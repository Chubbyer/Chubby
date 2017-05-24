package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import Module.Chubbyer;
import Module.Host;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Protocol.SC;
import Util.MongoDBJDBC;
import Util.Net;

/*
 * @Leung
 * ���紦��ķ���ˣ���Ӧ�ͷ������������󣬷������ݴ�����
 */
public class SocketServer {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status = SC.SERVER_OK;
	private int maxLinks = 10;// ���ɽ��ܵ�������
	private ArrayList<FutureTask> futureTasks = new ArrayList<FutureTask>();
	// �����˿ں�
	private int port = 10001;

	public SocketServer(int port) {
		this.port = port;
	}

	// �����������󣬽��ܵ����������ת������
	@SuppressWarnings("unused")
	public void monitor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		Object receiveData = null;
		String sendData;
		try {
			// ��������
			this.serverSocket = new ServerSocket(port);
			System.out.println("�������Ѿ���");
			while (true) {
				// �������
				Socket accpetSocket = new Socket();
				accpetSocket = serverSocket.accept();

				// ���տͻ��˷�������
				receiveData = Net.acceptData(accpetSocket);
				System.out.println("��ǰ�ɽ��ܵ���������" + this.maxLinks);
				System.out.println("�˿ڣ�" + this.port + " �յ���" + receiveData);
				// �������ͻ������������
				String oType = receiveData.toString().substring(0, 3);
				System.out.println("�ͻ�������" + oType + "#����");
				if (oType.equals(SC.CHECK_CONNECTION)) {
					// �ͻ����������ӣ����ͱ���������״̬
					Net.sentData(accpetSocket, this.status);
					continue;
				}
				// ���е����񶼴�����ת��
				if (oType.equals(EC.E_301) && this.status.equals(SC.SERVER_OK)) {
					// ���ӵ����ݣ�һ��Ϊ������ѧ�Ż�������
					String additional = receiveData.toString().substring(3);
					System.out.println("����ת��E_301(" + additional + ")���񡤡���");
					// ��������״̬��Ϊæ
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;
					// ����EC.E_301������������
					Tasker taskE_301 = new Tasker(accpetSocket, additional,
							EC.E_301);
					comp.submit(taskE_301);
					System.out.println("���ڴ���E_301(" + additional + ")���񡤡���");
				}
				if (oType.equals(EC.E_302) && this.status.equals(SC.SERVER_OK)) {
					// ���ӵ����ݣ�����Ӧ����1����2����3
					// ���ǽ����е��û��ֳ������ݣ�1��2��3��ʾҪ����ڼ�����
					String additional = receiveData.toString().substring(3);
					int order = Integer.parseInt(additional);
					System.out.println("����ת��E_302���񡤡���");
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;// ��������״̬��Ϊæ
					// ����EC.E_302������������
					TaskerPlus taskE_302 = new TaskerPlus(accpetSocket,
							EC.E_302, order);
					comp.submit(taskE_302);
					System.out.println("���ڴ���E_302:(" + additional + ")���񡤡���");
				}
				if (oType.equals(EC.E_303) && this.status.equals(SC.SERVER_OK)) {
					// ������302������ͬ
					// ���ӵ����ݣ�����Ӧ����1����2����3
					// ���ǽ����е��û��ֳ������ݣ�1��2��3��ʾҪ����ڼ�����
					String additional = receiveData.toString().substring(3);
					int order = Integer.parseInt(additional);
					System.out.println("����ת��E_303���񡤡���");
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;// ��������״̬��Ϊæ
					// ����EC.E_302������������
					TaskerPlus taskE_303 = new TaskerPlus(accpetSocket,
							EC.E_303, order);
					comp.submit(taskE_303);
					System.out.println("���ڴ���E_303:(" + additional + ")���񡤡���");
				}
				// ����������Ƿ���ִ����
				int alreadyLinks = 10 - this.maxLinks;
				for (int i = 0; i < alreadyLinks; i++) {
					try {
						// ��������߳��Ƿ�ִ����
						Future<Object> future = comp.take();
						boolean isComplet = (boolean) future.get();
						if (isComplet == true)
							this.maxLinks++;

					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	 * ���ͻ��������
	 */
	public void action(Socket accpetSocket) {
		Object receiveData = null;
		String sendData;

		// ���տͻ��˷�������
		receiveData = Net.acceptData(accpetSocket);
		System.out.println("�˿ڣ�" + this.port + " �յ���" + receiveData);
		// �������ͻ������������
		String oType = receiveData.toString().substring(0, 3);
		// System.out.println(oType);
		if (oType.equals(SC.CHECK_CONNECTION)) {
			// �ͻ����������ӣ����ͱ���������״̬
			Net.sentData(accpetSocket, this.status);
			return;
		}
		// ���е����񶼴�����ת��
		if (oType.equals(EC.E_301) && this.status.equals(SC.SERVER_OK)) {
			// ���ӵ�����
			String additional = receiveData.toString().substring(3);
			// ��������״̬��Ϊæ
			this.status = SC.SERVER_BUSY;
			// ����EC.E_301������������
			// Tasker taskE_301 = new Tasker(additional);
			// FutureTask<Object> futureTask=new FutureTask<Object>(taskE_301);
			// new Thread(futureTask).start();
			ArrayList<String> al = new ArrayList<String>();
			al.add("First");
			al.add("Second");// this.status
			Net.sentData(accpetSocket, al);
		}
	}

	/*
	 * ����Ѿ����Ĵ����
	 */
	public int checkChubbyer() {

		return 0;
	}

	public static void main(String[] args) {
		SocketServer chubbyer = new SocketServer(10001);
		chubbyer.monitor();
		// SocketServer chubbyer1=new SocketServer(10001);
		// chubbyer1.monitor();
		// SocketServer chubbyer2=new SocketServer(10002);
		// chubbyer2.monitor();
	}
}

class Session {
	public Tasker tasker;
	public FutureTask<Object> futureTask;

	public Session(Tasker tasker, FutureTask<Object> futureTask) {
		// TODO Auto-generated constructor stub

	}
}
