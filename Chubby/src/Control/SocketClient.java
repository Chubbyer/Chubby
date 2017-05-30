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
import Util.ChubbyConfig;
import Util.ChubbyerParser;
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

	public SocketClient(String taskType) {
		this.taskType = taskType;
	}

	public SocketClient() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		// ����ַ�
		if (this.taskType.equals(EC.E_301)) {
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			return chubbyerString;
		}
		if (this.taskType.equals(EC.E_301_1)) {
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			// �ӹ�getOneOverview�����Ľ����������ҳ����չʾEC-301_1����Ľ��,�õ�ÿ��ʹ�ö���Сʱ
			chubbyers = ChubbyerParser.getUseTime(chubbyerString);
			chubbyers = ChubbyerParser.remoneRepChubbyers(chubbyers);
			chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
			System.out.println("SocketClient�ѷ�������");
			return chubbyers;
		}
		if (this.taskType.equals(EC.E_301_2)) {
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			ArrayList<Double> useHours = ChubbyerParser
					.getUseHoursDistribut(chubbyerString);
			System.out.println("SC:" + useHours);
			return useHours;
		}
		if (this.taskType.equals(EC.E_301_3)) {
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			// �ӹ�getOneOverview�����Ľ����������ҳ����չʾEC-301_3����Ľ��,�õ����ػ�ʱ���
			chubbyers = ChubbyerParser.getUseTimeScatter(chubbyerString);
			// chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
			System.out.println("SocketClient�ѷ�������");
			return chubbyers;
		}

		return null;
	}

	// ����������������
	public Object getAvailableHost() {
		Object returnStr = null;
		while (true) {
			try {
				String serverIP = ChubbyConfig.STATION_IP;
				int port = ChubbyConfig.STATION_PORT;
				System.out.println("��������վ������õ����ݷ�����");
				this.socket = new Socket(serverIP, port);
				String data = SC.CLIENT_REQUEST;
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
				// �ӹ������ÿ��õķ���������
				ArrayList<String> availableHost = (ArrayList<String>) this
						.getAvailableHost();
				if (availableHost != null) {
					// ����ķ��������Խ������񣬷��;������������
					this.serverIP=availableHost.get(0);
					this.port=Integer.parseInt(availableHost.get(1));
					String data = EC.E_301 + id;
					this.socket = new Socket(this.serverIP, this.port);
					Net.sentData(this.socket, data);
					System.out.println("�ѷ����������ڵȴ��������ݡ�����");
					return Net.acceptData(this.socket);
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

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		// System.out.println(sClient.checkConnection());
		@SuppressWarnings("unchecked")
		ArrayList<String> chubbyers = (ArrayList<String>) sClient
				.getOneOverview("Leung");
		System.out.println(chubbyers.size());
		// for (int i = 0; i < 50; i++) {
		// System.out.println(chubbyers.get(i));
		// }
		ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
		// �õ�ÿ��Ŀ��ػ�ʱ��
		chubbyerList = ChubbyerParser.getUseTimeScatter(chubbyers);
		for (int i = 0; i < chubbyerList.size() / 2; i++) {
			System.out.print("OP:" + chubbyerList.get(i).day + " ");
			System.out.println(chubbyerList.get(i).point);
			System.out.print("CP:"
					+ chubbyerList.get(i + chubbyerList.size() / 2).day + " ");
			System.out
					.println(chubbyerList.get(i + chubbyerList.size() / 2).point);
		}
		// System.out.println(sClient.getOneOverview("Leung"));

	}

}
