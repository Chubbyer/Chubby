package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.security.auth.login.Configuration;

import Module.Chubbyer;
import Module.Host;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Protocol.SC;
import Util.ChubbyConfig;
import Util.MongoDBJDBC;
import Util.Net;

/*
 * @Leung
 * ���紦��ķ���ˣ���Ӧ�ͷ������������󣬷������ݴ�����
 */
public class SocketServer {
	private String localHostName;
	private String localIp;
	public static int dbPosition=0;// Ŀ�����ݿ�λ�ã�0��ʾ���أ�1��ʾĬ�ϵ�λ��
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status = SC.SERVER_OK;
	private int priority=10;//����Ȩ�������ɽ��ܵ����������
	private int maxLinks = 10;// ���ɽ��ܵ�������
	// �����˿ں�
	private int port = 10001;

	public SocketServer(int port) {
		this.port = port;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			localIp = addr.getHostAddress().toString();// ��ñ���IP
			localHostName = addr.getHostName().toString();// ��ñ�������
			MongoDBJDBC mongoer = new MongoDBJDBC("Log");
			Date nowDate = new Date();
			// ��鱾�����ݿ�����Ƿ��������򱾵����ݿ�д�뱾�β�������־
			if (mongoer.writeLog(localHostName, nowDate.toString())) {
				if (this.report())
					this.monitor();
			} else {
				if (this.localMongoException() == false)
					System.out.println("�����ݲ����ṩ����");
				else
					this.monitor();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��WorkStation�������");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��WorkStation�������");
		}
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
			Socket accpetSocket =null;
			System.out.println("�������Ѿ���");
			HeartBeat heartBeat=new HeartBeat(this.getIpInfo());
			heartBeat.start();
			while (true) {
				
				// �������
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
				int alreadyLinks = this.priority - this.maxLinks;
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
	 * ������ݷ�������ip��port��Ϣ
	 */
	public String getIpInfo() {
		String ipInfo="{\"ip\":\"" + this.localIp
				+ "\",\"port\":" + this.port + ",\"priority\":" + this.maxLinks
				+ "}";;
		return ipInfo;
	}

	/*
	 * ���ص�MongoDB���ݿ�����쳣ʱ���л���Ĭ�ϵ�MongoDB
	 */
	public boolean localMongoException() {
		try {
			System.out.println("�������ݿ�����쳣�������л���Ĭ�����ݿ������");
			MongoDBJDBC mongoer = new MongoDBJDBC(
					ChubbyConfig.DEFAULT_MONGODB_IP,
					ChubbyConfig.DEFAULT_MONGODB_PORT, "Log");
			Date nowDate = new Date();
			if (mongoer.writeLog(localHostName, nowDate.toString())) {
				System.out.println("�ɹ��л���Ĭ�����ݿ������");
				SocketServer.dbPosition=1;
				// ���±���
				if (this.report()) {
					return true;
				}else {
					System.out.println("��WorkStation�������");
					return false;
				}
			} else {
				System.out.println("�л�ʧ��");
				return false;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��WorkStation�������");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("�л�ʧ��");
		}
		return false;
	}

	/*
	 * ��WorkStation���汾�����ṩ���ݷ���
	 */
	public boolean report() throws UnknownHostException, IOException {
		System.out.println("������WorkStation���汾��״̬���������Chubbyϵͳ");
		while (true) {
			this.socket = new Socket(ChubbyConfig.STATION_IP,
					ChubbyConfig.STATION_PORT);
			String data = SC.CHUBBYER_REPORT + this.getIpInfo();
			Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
			String returnStr = (String) Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
			if (returnStr.equals(SC.CHUBBYER_REPORT)) {
				System.out.println("  ����Chubbyϵͳ�ɹ�");
				return true;
			}
		}
	}

	public static void main(String[] args) {
		SocketServer chubbyer = new SocketServer(10011);
		// chubbyer.monitor();
		// SocketServer chubbyer1=new SocketServer(10001);
		// chubbyer1.monitor();
		// SocketServer chubbyer2=new SocketServer(10002);
		// chubbyer2.monitor();
	}
}

/*
 * �μ��������Ե���
 */
class HeartBeat extends Thread {
	
	public String info;
	public HeartBeat(String info) {
		// TODO Auto-generated constructor stub
		this.info=info;
	}
	@Override
	public void run() {
		try {			
			while (true) {
				String receiveByUDP = Net.receiveDataByUDP(ChubbyConfig.HEART_BEAT_PORT);
				if (receiveByUDP.equals(SC.HEART_BEAT)) {
					System.out.println("�յ�������������");
					Socket socket = new Socket(ChubbyConfig.STATION_IP,
							ChubbyConfig.STATION_PORT);
					String data = SC.HEART_BEAT + this.info;
					Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
					String returnStr = (String) Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
					if (returnStr.equals(SC.HEART_BEAT)) {
						System.out.println("  ��������:��WorkStation���汾��״̬�ɹ�");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
