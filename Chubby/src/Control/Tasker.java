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
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		// ��User��Ϣ�⵱��ȥ����ƥ��userId��User
		User user = mongoer.findUserInfo(userId);
		ArrayList<String> chubbyers = this.getAllChubbyers(user);
		ArrayList<String> finalChubbyers = new ArrayList<String>();
		// ����ʱ��˳��
		for (int i = chubbyers.size(); i > 0; i--) {
			finalChubbyers.add(chubbyers.get(i - 1));
		}
		if (eType.equals(EC.E_301)) {
			this.complete = true;
			// ���뷢���ܹ����л��Ķ��������finalChubbyers�ǻ���JSON��ʽ�������б�����
			System.out.println("E_301�����Ѵ�����ϣ����ڷ��͡�����");
			if (Net.sentData(socket, finalChubbyers))
				// System.out.println(finalChubbyers.size());
				System.out.println("E_301�ѷ��ͳɹ�");
			// ��finalChubbyersд��R_����
			// mongoer=new MongoDBJDBC(user.getHost());
			// mongoer.insertChubbyers(user.getHost(), finalChubbyers);
			return true;
		}
		if (eType.equals(EC.E_302)) {
			// EC-302��������301����Ľ��֮�Ͻ��еģ���finalChubbyers�ٽ��мӹ�
			// 302������ǵõ�ĳ��User��finalChubbyers֮��õ���ƽ��ʹ��ʱ��
			// ������Խ��SocketClient����EC-301_1�Ĳ���
			ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
			// �õ�ÿ��ʹ�ö���Сʱ
			chubbyerList = ChubbyerParser.getUseTime(finalChubbyers);
			// ��chubbyerList������ƽ�����ٷ���ĳ����ƽ��ʹ��ʱ��
			double average = 0;
			for (int i = 0; i < chubbyerList.size(); i++) {
				average += chubbyerList.get(i).point;
			}
			//System.out.println("Sum:"+average+" days:"+chubbyerList.size());
			average = Math.round(average / chubbyerList.size() * 10) / 10.0;
			Chubbyer userEC_302 = new Chubbyer(userId, average);
			//System.out.println(userEC_302.point);
			return userEC_302;
		}
		if (eType.equals(EC.E_303)) {
			// EC-303��������301����Ľ��֮�Ͻ��еģ���finalChubbyers�ٽ��мӹ�
			// 303������ǵõ�ĳ��User��finalChubbyers֮��õ��俪�ػ��ڵ�
			// ������Խ��SocketClient����EC-301_3�Ĳ���
			ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
			// �õ�ÿ��Ŀ��ػ�ʱ��
			chubbyerList = ChubbyerParser.getUseTimeScatter(finalChubbyers);
			return chubbyerList;
		}
		return complete;
	}

	/*
	 * ִ������E_301
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAllChubbyers(User user) {
		// ������յĽ��
		ArrayList<String> chubbyers = new ArrayList<String>();
		// �����̳߳�
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);

		MongoDBJDBC mongoer;
		if (user.getR_Flag()) {
			// ֱ�Ӵ�ǰ׺ΪR_�ļ�������ȡ���ݼӹ������ͻ���,�����漰���������Ƚ�С
			mongoer = new MongoDBJDBC(user.getHost());// ����user��������
			chubbyers = mongoer.findAllChubbyers(user.getHost());

		} else if (user.getFlag()) {
			// ��ԭʼ�����ݿ⼯���ܷ�����������͸��ͻ���,�����漰���������Ƚϴ�
			chubbyers = this.getChubbyers(user, comp);

		} else {
			// ��ԭʼ���ļ���ʼ����,�����漰���������Ƚϴ�
			// �Ƚ��ļ��������ݿ�
			mongoer = new MongoDBJDBC(user.getHost());// ����user��������
			SAXParser saxParser = new SAXParser(mongoer, user.getHost(),
					"Security");
			SAXParser.writeToMongo(saxParser);// ִ�н�����Ὣuser.getFlag()=true
			// �ٴ����ݿ������ͬuser.getFlag()==trueʱ
			chubbyers = this.getChubbyers(user, comp);
		}
		// chubbyers = this.sortChubbyers(orderChubbyers);
		return chubbyers;
	}

	/*
	 * ����10��Analyzer�̴߳���E_301���� ���е��̶߳��������󷵻����ս�� �������յĽ����������Ҫ�Ľ����д��R_����
	 * �´ξͿ���ֱ�Ӵ�R_���϶�ȡ��� Ϊ�˽�Լʱ�䣬д��R_���ϵĲ�������ͻ��˷��ͽ�����д�� ��getAllChubbys��������
	 */
	public ArrayList<String> getChubbyers(User user,
			CompletionService<Object> comp) {
		// oType=2
		// ������̵߳Ľ��:OrderChubbyer<String>
		ArrayList<OrderChubbyer<String>> orderChubbyers = new ArrayList<OrderChubbyer<String>>();
		int threadNum = 10;// ��10���߳�
		for (int i = 0; i < threadNum; i++) {
			comp.submit(new Analyzer(EC.E_301, user, 2, i));
		}
		for (int i = 0; i < threadNum; i++) {
			try {
				// ����������������̵߳Ľ��
				Future<Object> future = comp.take();
				@SuppressWarnings("unchecked")
				OrderChubbyer<String> orderChubbyer = (OrderChubbyer<String>) future
						.get();
				orderChubbyers.add(orderChubbyer);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("���ս��ܵ�" + orderChubbyers.size() + "���߳̽��");
		// System.out.println("˳��");
		// for (OrderChubbyer<String> orderChubbyer : orderChubbyers) {
		// System.out.println("Order:" + orderChubbyer.order);
		// }
		ArrayList<String> finallChubbyers = this.sortChubbyers(orderChubbyers);
		return finallChubbyers;
	}

	/*
	 * ÿ���������̷߳��صĽ��������ģ�������Ӧ�����̵߳�������� ���շ��صĽ���������̵߳Ľ���ĺϲ�
	 */
	public ArrayList<String> sortChubbyers(
			ArrayList<OrderChubbyer<String>> orderChubbyers) {
		ArrayList<String> al = new ArrayList<String>();
		int minIndex = 0;
		int size = orderChubbyers.size();
		for (int i = 0; i < size - 1; i++) {
			// ÿһ���ҵ��̳߳����߳������С���Ǹ�
			int minOrder = orderChubbyers.get(0).order;
			for (int j = 0; j < orderChubbyers.size(); j++) {
				if (orderChubbyers.get(j).order < minOrder) {
					minOrder = orderChubbyers.get(j).order;
					minIndex = j;
				}
			}
			//System.out.println("size:" + orderChubbyers.size());
			//System.out.println("minIndex:" + minIndex);
			al.addAll(orderChubbyers.get(minIndex).chubbyers);
			orderChubbyers.remove(minIndex);
			minIndex = 0;
		}
		// ʣ�µ����һ�������߳���������Ǹ�
		al.addAll(orderChubbyers.get(0).chubbyers);
		// System.out.println(al.size());
		return al;
	}

	public static void main(String[] args) {
		Chubbyer chubbyer=new Chubbyer("AA", 2.8);
		System.out.println("{\"name\":\""+chubbyer.day+"\",\"hours\":"+chubbyer.point+"}");
//		ExecutorService executor = Executors.newCachedThreadPool();
//		CompletionService<Object> comp = new ExecutorCompletionService<>(
//				executor);
//		Tasker taskE_302 = new Tasker("����",
//				EC.E_302);
//		comp.submit(taskE_302);
		// ArrayList<String> chubbyers = tasker.getAllChubbyers("qq");
		// System.out.println(chubbyers.size());
		// for (String chubbyer : chubbyers) {
		// System.out.println(chubbyer);
		// }
		// int minIndex = 0;
		// int size = orderChubbyers.size();
		// for (int i = 0; i < size - 1; i++) {
		// // ÿһ���ҵ��̳߳����߳������С���Ǹ�
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
		// // ʣ�µ����һ�������߳���������Ǹ�
		// al.addAll(orderChubbyers.get(0).chubbyers);
		// System.out.println(al.size());
	}
}