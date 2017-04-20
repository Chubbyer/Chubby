package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import Module.Host;
import Module.HostList;
import Protocol.EC;
import Protocol.SC;
import Util.Net;

/*
 * @Leung
 * �����������ݷ���
 */
public class SocketClient {
	private Socket socket = null;
	private String serverIP = null;// ��������IP��ַ
	private int port;// �������˶˿ں�

	public SocketClient() {
	}

	// ����������������
	public String checkConnection() {
		//ע������nextHost�������û��
		this.serverIP = HostList.nextHost().getServerIP();
		System.out.println(this.serverIP);
		this.port = HostList.nextHost().getPort();
		System.out.println(this.port);
		try {
			socket = new Socket(serverIP, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = SC.CHECK_CONNECTION;
		Net.sentData(socket, data);
		return Net.acceptData(socket);
	}

	// ����Э��EC-300;����301����,id��ʾ��Ӧ�˵ı�ʶ��
	public String getOneOverview(String id) {
		// ��ǰ����������ʼ����ķ�����������
		for (int i = 0; i < HostList.hostsCount; i++) {
			if (this.checkConnection().equals(SC.SERVER_OK)) {
				String data = EC.E_301;
				Net.sentData(socket, data);
				System.out.println(Net.acceptData(socket));
				return Net.acceptData(socket);
			}
			
		}return null;
	}

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		System.out.println(sClient.checkConnection());
	}
}
