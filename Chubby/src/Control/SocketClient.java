package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

import org.json.JSONException;
import org.json.JSONObject;

import Module.Chubbyer;
import Module.Host;
import Module.HostList;
import Protocol.EC;
import Protocol.SC;
import Util.Net;
import Util.TimeParser;

/*
 * @Leung
 * �����������ݷ���
 */
public class SocketClient implements Callable<Object> {
	private Socket socket = null;
	private String serverIP = null;// ��������IP��ַ
	private int port;// �������˶˿ں�
	public String serCondition;// ��������
	public String taskType;// ��������

	public SocketClient(String serCondition, String taskType) {
		this.serCondition = serCondition;
		this.taskType = taskType;
	}

	public SocketClient() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		if (this.taskType.equals(EC.E_301)) {

		}
		return null;
	}

	// ����������������
	public Object checkConnection() {
		Object returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.nextHost().getServerIP();
				this.port = HostList.nextHost().getPort();
				System.out.println("��������" + serverIP + " �˿ڣ�" + port);
				this.socket = new Socket(serverIP, port);
				String data = SC.CHECK_CONNECTION;
				Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
				returnStr = Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("�÷�����δ����");
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("�÷�����δ����");
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
				Object checkResult = this.checkConnection();
				if (checkResult != null) {
					String checkResultString = checkResult.toString()
							.substring(0, 3);
					if (checkResultString.equals(SC.SERVER_OK)) {
						// ����ķ��������Խ������񣬷��;������������
						String data = EC.E_301 + id;
						this.socket = new Socket(this.serverIP, this.port);
						Net.sentData(this.socket, data);
						System.out.println("�ѷ����������ڵȴ��������ݡ�����");
						return Net.acceptData(this.socket);
					} else {
						System.out.println("�÷�������æ");
						continue;
					}
				} else {
					System.out.println("δ���ܵ���Ӧ");
				}
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("�����쳣");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("�����쳣");
			return null;
		}
		return null;
	}

	/*
	 * �ӹ�getOneOverview�����Ľ����������ҳ����չʾEC-301����Ľ��
	 */
	public ArrayList<Chubbyer> getOpenChubbyers(ArrayList<String> chubbyerString) {
		ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
		ArrayList<String> openDays = new ArrayList<String>();
		ArrayList<String> openPoints = new ArrayList<String>();
		
		System.out.println(chubbyerString.size());
		JSONObject jsonObj;
//		try {
//			for (int i = 0; i < chubbyerString.size(); i++) {
//				jsonObj = new JSONObject(chubbyers.get(i));
//				openDays.add(TimeParser.getChubbyerString(jsonObj.getString("ot"))
//						.substring(0, 10));
//				openPoints.add(TimeParser
//						.getChubbyerString(jsonObj.getString("ct")).substring(
//								11));
//				 System.out.println(chubbyers.get(i));
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Client�˵�JSON��������");
//		}

		return null;

	}

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		// System.out.println(sClient.checkConnection());
		@SuppressWarnings("unchecked")
		ArrayList<String> chubbyers = (ArrayList<String>) sClient
				.getOneOverview("Leung");
		System.out.println(chubbyers.size());
		for (int i = 0; i < 50; i++) {
			System.out.println(chubbyers.get(i));
		}

		// System.out.println(sClient.getOneOverview("Leung"));

	}

}
