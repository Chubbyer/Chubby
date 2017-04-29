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
import Util.MongoDBJDBC;
import Util.Net;

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

	public Tasker() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		Thread.sleep(3000);
		if (eType.equals(EC.E_301)) {
			ArrayList<String> chubbyers = this.getAllChubbyers(userId);
			this.complete = true;
			System.out.println("E_301任务已处理完毕，正在发送···");
			Net.sentData(socket, chubbyers);//必须发送能够序列化的对象
			System.out.println("E_301已发送完毕");
			return true;
		}
		return complete;

	}

	/*
	 * 执行任务E_301
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAllChubbyers(String userId) {
		ArrayList<String> chubbyers = new ArrayList<String>();
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		// User user = MongoDBJDBC.findUserInfo(userId);
		//
		// if (user.getR_Flag()) {
		// // 直接从前缀为R_的集合中提取数据加工发给客户端,这里涉及的数据量比较小
		// } else if (user.getFlag()) {
		// 从原始的数据库集合总分析出结果发送给客户端,这里涉及的数据量比较大
		// oType=2
		int threadNum = 10;// 开10个线程
		ArrayList<OrderChubbyer<String>> orderChubbyers = new ArrayList<OrderChubbyer<String>>();

		for (int i = 0; i < threadNum; i++) {
			comp.submit(new Analyzer(EC.E_301, null, 2, i));
		}
		for (int i = 0; i < threadNum; i++) {
			try {
				// 获得已完成任务的子线程的结果
				Future<Object> future = comp.take();
				OrderChubbyer<String> orderChubbyer = (OrderChubbyer<String>) future
						.get();
				orderChubbyers.add(orderChubbyer);

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// } else {
		// // 从原始的文件开始分析,这里涉及的数据量比较大
		// }
		chubbyers = this.sortChubbyers(orderChubbyers);
		return chubbyers;
	}

	/*
	 * 每个子任务线程返回的结果是无序的，在这里应按照线程的序号排序 最终返回的结果是所有线程的结果的合并
	 */
	public ArrayList<String> sortChubbyers(
			ArrayList<OrderChubbyer<String>> orderChubbyers) {
		ArrayList<String> al = new ArrayList<String>();
		int minIndex = 0;
		int size = orderChubbyers.size();
		for (int i = 0; i < size - 1; i++) {
			// 每一次找到线程池里线程序号最小的那个
			int minOrder = orderChubbyers.get(0).order;
			for (int j = 0; j < orderChubbyers.size(); j++) {
				if (orderChubbyers.get(j).order < minOrder) {
					minOrder = orderChubbyers.get(j).order;
					minIndex = j;
				}
			}
			al.addAll(orderChubbyers.get(minIndex).chubbyers);
			orderChubbyers.remove(minIndex);
			minIndex = 0;
		}
		// 剩下的最后一个就是线程序号最大的那个
		al.addAll(orderChubbyers.get(0).chubbyers);
		System.out.println(al.size());
		return al;
	}

	public static void main(String[] args) {
		Tasker tasker = new Tasker();
		ArrayList<String> chubbyers = tasker.getAllChubbyers("qq");
		System.out.println(chubbyers.size());
		for (String chubbyer : chubbyers) {
			System.out.println(chubbyer);
		}

	}
}
