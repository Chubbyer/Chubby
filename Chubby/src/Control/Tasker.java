package Control;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Module.Chubbyer;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Util.ChubbyerParser;
import Util.MongoDBJDBC;
import Util.Net;
import Util.SAXParser;

/*
 * @Leung
 * 数据服务器端的任务处理器
 */
public class Tasker implements Callable<Object> {

	private String userId;
	public boolean complete = false;
	private Socket socket;
	private String eType;

	public Tasker(Socket socket, String userId, String eType) {
		this.socket = socket;
		this.userId = userId;
		this.eType = eType;
	}

	public Tasker(String userId, String eType) {
		this.userId = userId;
		this.eType = eType;
	}

	public Tasker() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
		// 到User信息库当中去找能匹配userId的User
		User user = mongoer.findUserInfo(userId);
		if (user == null) {
			System.out.println("在本地未找到" + userId + "用户");
			return true;// 线程任务结束
		}
		ArrayList<String> chubbyers = this.getAllChubbyers(user);
		ArrayList<String> finalChubbyers = null;
		// 调整时间顺序，改为从以前到现在
		if (chubbyers != null) {
			finalChubbyers = new ArrayList<String>();
			for (int i = chubbyers.size(); i > 0; i--)
				finalChubbyers.add(chubbyers.get(i - 1));
		}
		if (eType.equals(EC.E_301)) {
			this.complete = true;
			// 必须发送能够序列化的对象，这里的finalChubbyers是基于JSON格式的数组列表描述
			System.out.println("E_301任务已处理完毕，正在发送···");
			if (finalChubbyers != null) {
				// System.out.println(finalChubbyers.size());
				Net.sentData(socket, finalChubbyers);
				System.out.println("E_301已发送成功" + chubbyers.size() + "条记录");
			} else
				Net.sentData(socket, null);
			return true;
		}
		if (eType.equals(EC.E_302)) {
			// EC-302任务是在301任务的结果之上进行的，对finalChubbyers再进行加工
			// 302任务就是得到某个User的finalChubbyers之后得到其平均使用时间
			// 这里可以借鉴SocketClient对于EC-301_1的操作
			ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
			// 得到每天使用多少小时
			chubbyerList = ChubbyerParser.getUseTime(finalChubbyers);
			// 对chubbyerList加总求平均，再返回某个人平均使用时间
			double average = 0;
			for (int i = 0; i < chubbyerList.size(); i++) {
				average += chubbyerList.get(i).point;
			}
			// System.out.println("Sum:"+average+" days:"+chubbyerList.size());
			average = Math.round(average / chubbyerList.size() * 10) / 10.0;
			Chubbyer userEC_302 = new Chubbyer(userId, average);
			// System.out.println(userEC_302.point);
			return userEC_302;
		}
		if (eType.equals(EC.E_303)) {
			// EC-303任务是在301任务的结果之上进行的，对finalChubbyers再进行加工
			// 303任务就是得到某个User的finalChubbyers之后得到其开关机节点
			// 这里可以借鉴SocketClient对于EC-301_3的操作
			ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
			// 得到每天的开关机时点
			chubbyerList = ChubbyerParser.getUseTimeScatter(finalChubbyers);
			return chubbyerList;
		}
		return complete;
	}

	/*
	 * 执行任务E_301
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAllChubbyers(User user) {
		// 存放最终的结果
		ArrayList<String> chubbyers = new ArrayList<String>();
		// 创建线程池
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		MongoDBJDBC mongoer;
		if (user.getR_Flag()) {
			// 直接从前缀为R_的集合中提取数据加工发给客户端,这里涉及的数据量比较小
			mongoer = MongoDBJDBC.createMongoger(user.getHost());// 传入user的主机名
			chubbyers = mongoer.findAllChubbyers(user.getHost());
		} else if (user.getFlag()) {
			// 从原始的数据库集合总分析出结果发送给客户端,这里涉及的数据量比较大
			chubbyers = this.getChubbyers(user, comp);
		} else {
			// 从原始的文件开始分析,这里涉及的数据量比较大
			// 先将文件读入数据库
			System.out.println(user.getHost());
			mongoer = MongoDBJDBC.createMongoger(user.getHost());// 传入user的主机名
			SAXParser saxParser = new SAXParser(mongoer, user.getHost(),
					"Security");
			boolean fileFlag = SAXParser.writeToMongo(saxParser);
			if (fileFlag)// 执行结束后会将user.getFlag()=true
				// 再从数据库分析，同user.getFlag()==true时
				chubbyers = this.getChubbyers(user, comp);
			else
				return null;
		}
		return chubbyers;
	}

	/*
	 * 创建10个Analyzer线程处理E_301任务 所有的线程都完成任务后返回最终结果 并把最终的结果（我们想要的结果）写入R_集合
	 * 下次就可以直接从R_集合读取结果 为了节约时间，写入R_集合的操作在向客户端发送结束后写入 被getAllChubbys方法调用
	 */
	public ArrayList<String> getChubbyers(User user,
			CompletionService<Object> comp) {
		// oType=2
		// 存放子线程的结果:OrderChubbyer<String>
		ArrayList<OrderChubbyer<String>> orderChubbyers = new ArrayList<OrderChubbyer<String>>();
		int threadNum = 10;// 开10个线程
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
		// 到User信息库当中去找能匹配userId的User
		User newUser = mongoer.findUserInfo(user.getHost());
		for (int i = 0; i < threadNum; i++) {
			comp.submit(new Analyzer(EC.E_301, newUser, 2, i));
		}
		for (int i = 0; i < threadNum; i++) {
			try {
				// 获得已完成任务的子线程的结果
				Future<Object> future = comp.take();
				@SuppressWarnings("unchecked")
				OrderChubbyer<String> orderChubbyer = (OrderChubbyer<String>) future
						.get();
				if (orderChubbyer != null)
					orderChubbyers.add(orderChubbyer);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (orderChubbyers != null) {
			System.out.println("最终接受到" + orderChubbyers.size() + "个线程结果");
			ArrayList<String> finallChubbyers = this
					.sortChubbyers(orderChubbyers);
			// 把finalChubbyers写入R_集合
			if (finallChubbyers != null) {
				MongoDBJDBC mg = MongoDBJDBC.createMongoger(user.getHost());
				mg.insertChubbyers(user.getHost(), finallChubbyers);
			}
			return finallChubbyers;
		}
		System.out.println("未获得结果");
		return null;
	}

	/*
	 * 每个子任务线程返回的结果是无序的，在这里应按照线程的序号排序 最终返回的结果是所有线程的结果的合并
	 */
	public ArrayList<String> sortChubbyers(
			ArrayList<OrderChubbyer<String>> orderChubbyers) {
		ArrayList<String> al = new ArrayList<String>();
		int minIndex = 0;
		int size = orderChubbyers.size();
		if (size > 0) {
			for (int i = 0; i < size - 1; i++) {
				// 每一次找到线程池里线程序号最小的那个
				int minOrder = orderChubbyers.get(0).order;
				for (int j = 0; j < orderChubbyers.size(); j++) {
					if (orderChubbyers.get(j).order < minOrder) {
						minOrder = orderChubbyers.get(j).order;
						minIndex = j;
					}
				}
				// System.out.println("size:" + orderChubbyers.size());
				// System.out.println("minIndex:" + minIndex);
				al.addAll(orderChubbyers.get(minIndex).chubbyers);
				orderChubbyers.remove(minIndex);
				minIndex = 0;
			}
			// 剩下的最后一个就是线程序号最大的那个
			al.addAll(orderChubbyers.get(0).chubbyers);
			// System.out.println(al.size());
			return al;
		}
		return null;
	}

	public static void main(String[] args) {
		Chubbyer chubbyer = new Chubbyer("AA", 2.8);
		System.out.println("{\"name\":\"" + chubbyer.day + "\",\"hours\":"
				+ chubbyer.point + "}");
		// ExecutorService executor = Executors.newCachedThreadPool();
		// CompletionService<Object> comp = new ExecutorCompletionService<>(
		// executor);
		// Tasker taskE_302 = new Tasker("梁健",
		// EC.E_302);
		// comp.submit(taskE_302);
		// ArrayList<String> chubbyers = tasker.getAllChubbyers("qq");
		// System.out.println(chubbyers.size());
		// for (String chubbyer : chubbyers) {
		// System.out.println(chubbyer);
		// }
		// int minIndex = 0;
		// int size = orderChubbyers.size();
		// for (int i = 0; i < size - 1; i++) {
		// // 每一次找到线程池里线程序号最小的那个
		// int minOrder = orderChubbyers.get(0).order;
		// for (int j = 0; j < orderChubbyers.size(); j++) {
		// if (orderChubbyers.get(j).order < minOrder) {
		// minOrder = orderChubbyers.get(j).order;
		// minIndex = j;
		// }
		// }
		// al.addAll(orderChubbyers.get(minIndex).chubbyers);
		// orderChubbyers.remove(minIndex);
		// minIndex = 0;
		// }
		// // 剩下的最后一个就是线程序号最大的那个
		// al.addAll(orderChubbyers.get(0).chubbyers);
		// System.out.println(al.size());
	}
}
