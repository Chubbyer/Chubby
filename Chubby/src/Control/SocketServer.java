package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import Module.Host;

/*
 * @Leung
 * ���紦��ķ���ˣ����ڷַ����񣬻㼯��������������ݴ�����
 */
public class SocketServer {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	private Map<String, Host> hostsMap;
	// �����˿ں�
	private int port = 10000;

	private SocketServer(int port) {
		this.port = port;
	}

	/*
	 * ���ͻ��������
	 */
	public String swapInfo(String data) {
		String receiveData;
		String sendData;
		try {
			// ��������
			this.serverSocket = new ServerSocket(port);
			// �������
			this.socket = serverSocket.accept();
			// ���տͻ��˷�������
			this.is = socket.getInputStream();
			byte[] b = new byte[1024];
			int n = is.read(b);
			// ���
			System.out.println("�ͻ��˷�������Ϊ��" + new String(b, 0, n));
			// ��ͻ��˷��ͷ�������
			this.os = socket.getOutputStream();
			os.write(b, 0, n);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// �ر���������
				os.close();
				is.close();
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * ����Ѿ����Ĵ����
	 */
	public int checkChubbyer() {
		Date start=new Date();
		// ��������
		try {
			serverSocket = new ServerSocket(port);
			while(true){
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

		try {
			// ��������
			serverSocket = new ServerSocket(port);
			// �������
			socket = serverSocket.accept();
			// ���տͻ��˷�������
			is = socket.getInputStream();
			byte[] b = new byte[1024];
			int n = is.read(b);
			// ���
			System.out.println("�ͻ��˷�������Ϊ��" + new String(b, 0, n));
			// ��ͻ��˷��ͷ�������
			os = socket.getOutputStream();
			os.write(b, 0, n);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// �ر���������
				os.close();
				is.close();
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}
}
