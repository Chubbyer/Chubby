package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import Module.Host;
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
	private String status=SC.SERVER_OK;
	// private Map<String, Host> hostsMap;
	// �����˿ں�
	private int port = 10001;

	public SocketServer(int port) {
		this.port = port;
	}
	//�����������󣬽��ܵ����������ת������
	@SuppressWarnings("unused")
	public void monitor() {
		String receiveData=null;
		String sendData;
		try {
			// ��������
			this.serverSocket = new ServerSocket(port);
			while (true) {
				// �������
				Socket accpetSocket=new Socket();
				accpetSocket = serverSocket.accept();
				//ת�ƿ���Ȩ������Ӧ������һ���߳�
				this.action(accpetSocket);
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
		String receiveData=null;
		String sendData;
	
		// ���տͻ��˷�������
		receiveData=Net.acceptData(accpetSocket);
		System.out.println("�˿ڣ�"+this.port+" �յ���"+receiveData);
		//�������ͻ������������
		String oType=receiveData.substring(0, 3);
		if(oType.equals(SC.CHECK_CONNECTION)){
			//�ͻ����������ӣ����ͱ���������״̬
			Net.sentData(accpetSocket, this.status);
			return;
		}
		if(oType.equals(EC.E_301)&&this.status.equals(SC.SERVER_OK)){
			System.out.println(receiveData.substring(3));
			//��������״̬��Ϊæ
			this.status=SC.SERVER_BUSY;
			//���EC.E_301������������
			User user=MongoDBJDBC.findUserInfo(receiveData.substring(3));
			if(user.getR_Flag()){
				//ֱ�Ӵ�ǰ׺ΪR_�ļ�������ȡ���ݼӹ������ͻ���
			}
			else if(user.getFlag()){
				//��ԭʼ�����ݿ⼯���ܷ�����������͸��ͻ���
			}else{
				//��ԭʼ���ļ���ʼ����
			}
			Net.sentData(accpetSocket, this.status);
		}
	}

	/*
	 * ����Ѿ����Ĵ����
	 */
	public int checkChubbyer() {
		Date start = new Date();
		// ��������
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				// �������
				socket = serverSocket.accept();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		SocketServer chubbyer=new SocketServer(10001);
		chubbyer.monitor();
//		SocketServer chubbyer1=new SocketServer(10001);
//		chubbyer1.monitor();
//		SocketServer chubbyer2=new SocketServer(10002);
//		chubbyer2.monitor();
	}
}
