package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import Module.Host;
import Protocol.SC;
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
	private int port = 10000;

	public SocketServer(int port) {
		this.port = port;
	}
	//
	@SuppressWarnings("unused")
	public void monitor() {
		String receiveData=null;
		String sendData;
		try {
			// ��������
			this.serverSocket = new ServerSocket(port);
			while (true) {
				// �������
				this.socket = serverSocket.accept();
				// ���տͻ��˷�������
				receiveData=Net.acceptData(socket);
				System.out.println("�˿ڣ�"+this.port+" �յ���"+receiveData);
				if(receiveData.equals(SC.CHECK_CONNECTION))
					Net.sentData(socket, this.status);
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	 * ���ͻ��������
	 */
	public String swapInfo(String data) {
		return data;

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
		SocketServer chubbyer=new SocketServer(10000);
		chubbyer.monitor();
//		SocketServer chubbyer1=new SocketServer(10001);
//		chubbyer1.monitor();
//		SocketServer chubbyer2=new SocketServer(10002);
//		chubbyer2.monitor();
	}
}
