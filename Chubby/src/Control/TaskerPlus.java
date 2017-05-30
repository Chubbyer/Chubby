package Control;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
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

/*
 * @Leung
 * 此类专用于EC-302与EC-303综合任务 
 * 这里需要同时处理多个Tasker返回的数据
 * 并最终返回给请求端
 */
public class TaskerPlus implements Callable<Object> {
	public boolean complete = false;
	private Socket socket;
	private String eType;
	private int order;

	public TaskerPlus(Socket socket, String eType, int order) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.eType = eType;
		this.order = order;
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		// 将order所对应的那部分用户的信息搜索出来
		// 把这部分的用户平均使用时间放在chubbyers之中
		ArrayList<String> chubbyers = new ArrayList<String>();
		ArrayList<User> users = new ArrayList<User>();
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
		int userStart = (order - 1) * 10;// 这里的数字待定
		int userEnd = order * 10;
		users = mongoer.findUsersInfo(userStart, userEnd);
		if (eType.equals(EC.E_302)) {
			if (users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					// 启动EC.E_302所描述的任务
					Tasker taskE_302 = new Tasker(users.get(i).getName(),
							EC.E_302);
					comp.submit(taskE_302);
				}
				for (int i = 0; i < users.size(); i++) {
					// 获得已完成任务的子线程的结果,其结果是一个Chubbyer对象并将其转换成一个json对象
					// 它表示某个人平均每天使用多少小时
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					Chubbyer chubbyer = (Chubbyer) future.get();
					chubbyers.add("{\"name\":\"" + chubbyer.day
							+ "\",\"hours\":" + chubbyer.point + "}");
				}
				this.complete = true;
				// 必须发送能够序列化的对象，这里的finalChubbyers是基于JSON格式的数组列表描述
				System.out.println("E_302任务已处理完毕，正在发送···");
				if (Net.sentData(socket, chubbyers) == true)
					System.out.println("E_302已发送成功");
				return true;
			}
			else
				System.out.println("没有找到302任务对应的用户");
		}
		if (eType.equals(EC.E_303)) {
			if (users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					// 启动EC.E_303所描述的任务
					Tasker taskE_303 = new Tasker(users.get(i).getName(),
							EC.E_303);
					comp.submit(taskE_303);
				}
				ArrayList<String> openPoints=new ArrayList<String>(); 
				ArrayList<String> closePoints=new ArrayList<String>(); 
				for (int i = 0; i < users.size(); i++) {
					// 获得已完成任务的子线程的结果,其结果是一个Chubbyer对象的列表
					// 其中一个Chubbyer对象表示某天在什么时候开机或关机
					// 列表中的前半部分是开机时点，后半部分是关机时点
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<Chubbyer> chubbyer = (ArrayList<Chubbyer>) future.get();
					for (int j = 0; j < chubbyer.size(); j++) {
						if(i<chubbyer.size()/2){
							//前半部分的数据是开机的节点
							openPoints.add(chubbyers.get(i).toString());
						}else {
							//后半部分的数据是关机的节点
							closePoints.add(chubbyers.get(i).toString());
						}
					}
				}
				openPoints.addAll(closePoints);
				this.complete = true;
				// 必须发送能够序列化的对象，这里的finalChubbyers是基于JSON格式的数组列表描述
				System.out.println("E_303任务已处理完毕，正在发送···");
				if (Net.sentData(socket, openPoints) == true)
					System.out.println("E_303已发送成功");
				return true;
			}
			else
				System.out.println("没有找到302任务对应的用户");
		}
		return complete;
	}

}
