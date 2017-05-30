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
 * ����ר����EC-302��EC-303�ۺ����� 
 * ������Ҫͬʱ������Tasker���ص�����
 * �����շ��ظ������
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
		// ��order����Ӧ���ǲ����û�����Ϣ��������
		// ���ⲿ�ֵ��û�ƽ��ʹ��ʱ�����chubbyers֮��
		ArrayList<String> chubbyers = new ArrayList<String>();
		ArrayList<User> users = new ArrayList<User>();
		MongoDBJDBC mongoer = MongoDBJDBC.createMongoger("User");
		int userStart = (order - 1) * 10;// ��������ִ���
		int userEnd = order * 10;
		users = mongoer.findUsersInfo(userStart, userEnd);
		if (eType.equals(EC.E_302)) {
			if (users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					// ����EC.E_302������������
					Tasker taskE_302 = new Tasker(users.get(i).getName(),
							EC.E_302);
					comp.submit(taskE_302);
				}
				for (int i = 0; i < users.size(); i++) {
					// ����������������̵߳Ľ��,������һ��Chubbyer���󲢽���ת����һ��json����
					// ����ʾĳ����ƽ��ÿ��ʹ�ö���Сʱ
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					Chubbyer chubbyer = (Chubbyer) future.get();
					chubbyers.add("{\"name\":\"" + chubbyer.day
							+ "\",\"hours\":" + chubbyer.point + "}");
				}
				this.complete = true;
				// ���뷢���ܹ����л��Ķ��������finalChubbyers�ǻ���JSON��ʽ�������б�����
				System.out.println("E_302�����Ѵ�����ϣ����ڷ��͡�����");
				if (Net.sentData(socket, chubbyers) == true)
					System.out.println("E_302�ѷ��ͳɹ�");
				return true;
			}
			else
				System.out.println("û���ҵ�302�����Ӧ���û�");
		}
		if (eType.equals(EC.E_303)) {
			if (users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					// ����EC.E_303������������
					Tasker taskE_303 = new Tasker(users.get(i).getName(),
							EC.E_303);
					comp.submit(taskE_303);
				}
				ArrayList<String> openPoints=new ArrayList<String>(); 
				ArrayList<String> closePoints=new ArrayList<String>(); 
				for (int i = 0; i < users.size(); i++) {
					// ����������������̵߳Ľ��,������һ��Chubbyer������б�
					// ����һ��Chubbyer�����ʾĳ����ʲôʱ�򿪻���ػ�
					// �б��е�ǰ�벿���ǿ���ʱ�㣬��벿���ǹػ�ʱ��
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<Chubbyer> chubbyer = (ArrayList<Chubbyer>) future.get();
					for (int j = 0; j < chubbyer.size(); j++) {
						if(i<chubbyer.size()/2){
							//ǰ�벿�ֵ������ǿ����Ľڵ�
							openPoints.add(chubbyers.get(i).toString());
						}else {
							//��벿�ֵ������ǹػ��Ľڵ�
							closePoints.add(chubbyers.get(i).toString());
						}
					}
				}
				openPoints.addAll(closePoints);
				this.complete = true;
				// ���뷢���ܹ����л��Ķ��������finalChubbyers�ǻ���JSON��ʽ�������б�����
				System.out.println("E_303�����Ѵ�����ϣ����ڷ��͡�����");
				if (Net.sentData(socket, openPoints) == true)
					System.out.println("E_303�ѷ��ͳɹ�");
				return true;
			}
			else
				System.out.println("û���ҵ�302�����Ӧ���û�");
		}
		return complete;
	}

}
