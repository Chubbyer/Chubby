package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import Module.Chubbyer;
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
	public Object checkConnection() {
		Object returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.nextHost().getServerIP();
				System.out.println(serverIP);
				this.port = HostList.nextHost().getPort();
				System.out.println(port);
				this.socket = new Socket(serverIP, port);
				String data = SC.CHECK_CONNECTION;
				Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
				returnStr = Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				continue;
			}
		}
		return returnStr;
	}

	// ����Э��EC-300;����301����,id��ʾ��Ӧ�˵ı�ʶ��
	public Object getOneOverview(String id) {
		// ��ǰ����������ʼ����ķ�����������
		try {
			for (int i = 0; i < HostList.hostsCount; i++) {
				// ������ѵǼǵķ�������������
				Object checkResult= this.checkConnection();
				if (checkResult != null) {
					String checkResultString = checkResult.toString()
							.substring(0, 3);
					if (checkResultString.equals(SC.SERVER_OK)) {
						// ����ķ��������Խ������񣬷��;������������
						String data = EC.E_301 + id;
						this.socket = new Socket(this.serverIP, this.port);
						Net.sentData(this.socket, data);
						// System.out.println(Net.acceptData(this.socket));
						return Net.acceptData(this.socket);
					}else {
						continue;
					}
				} else {
					System.out.println("δ���ܵ���Ӧ");
				}
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		//System.out.println(sClient.checkConnection());
		@SuppressWarnings("unchecked")
		ArrayList<String> chubbyers = (ArrayList<String>) sClient.getOneOverview("Leung");
		System.out.println(chubbyers.size());
		for (String chubbyer : chubbyers) {
			System.out.println(chubbyer);
		}
		//System.out.println(sClient.getOneOverview("Leung"));

	}
}
