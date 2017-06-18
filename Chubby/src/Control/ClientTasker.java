package Control;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import Protocol.EC;
import Protocol.SC;
import Util.ChubbyConfig;
import Util.Net;

/*
 * @Leung
 * ����ר���ڴ����ۺ������漰���е�����
 */
public class ClientTasker implements Callable<Object> {
	public String taskType;
	public int order;// �߳���ţ�������ű����ʵ����ݷ�����Ӧ�ô����Ĳ�������
	public Socket socket;// ���뱣�������ݷ�����������
	public String serverIP;
	public int port;

	public ClientTasker(String taskType, int order) {
		// TODO Auto-generated constructor stub
		this.taskType = taskType;
		this.order = order;
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		if (taskType.equals(EC.E_302)) {
			ArrayList<String> chubbyerStrings = this.getUseHoursRanking();
			if (chubbyerStrings != null)
				return chubbyerStrings;
			else {
				System.out.println("E_302:��ȡ����ʧ�ܣ�");
				return null;
			}
		}
		if (taskType.equals(EC.E_303)) {
			ArrayList<String> chubbyerStrings = this.getUserScatters();
			if (chubbyerStrings != null)
				return chubbyerStrings;
			else {
				System.out.println("E_303:��ȡ����ʧ�ܣ�");
				return null;
			}
		}
		return null;
	}

	// ����վ����һ�����ŵ����ݷ�����
	public Object getAvailableHost() {
		Object returnStr = null;
		int requestTimes = 5;
		while (true) {
			try {
				String serverIP = ChubbyConfig.STATION_IP;
				int port = ChubbyConfig.STATION_PORT;
				System.out.println("��������վ������õ����ݷ�����");
				this.socket = new Socket(serverIP, port);
				String data = SC.CLIENT_REQUEST+EC.E_302;
				Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
				returnStr = Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				requestTimes--;
				System.out.println("�ù���վδ����");
				if (requestTimes < 0) {
					break;
				}
				try {
					Thread.sleep(1000);// ��ͣ1����������
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				requestTimes--;
				System.out.println("�ù���վδ����");
				if (requestTimes < 0) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}
				continue;
			}
		}
		return returnStr;
	}

	/*
	 * ����EC-302��Լ������������˵�ʹ��ʱ�����а� ���ܵ������������硰{"name":"����","hours":3.2}�����͵��б�δ����
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUseHoursRanking() {
		try {
			// �ӹ���վ��ÿ��õķ���������
			ArrayList<String> availableHost = (ArrayList<String>) this
					.getAvailableHost();
			if (availableHost != null) {
				// ����ķ��������Խ������񣬷��;������������
				this.serverIP = availableHost.get(0);
				this.port = Integer.parseInt(availableHost.get(1));
				// ����ķ��������Խ������񣬷��;������������
				String data = EC.E_302 + order;
				this.socket = new Socket(this.serverIP, this.port);
				Net.sentData(this.socket, data);
				System.out.println("�ѷ���E_302�������ڵȴ��������ݡ�����");
				return (ArrayList<String>) Net.acceptData(this.socket);
			} else {
				System.out.println("δ�ҵ����õ����ݷ�����");
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
	 * ����EC-303��Լ������������˵Ŀ��ؼ�ʱ��ڵ㣬 ���ܵ������������� ǰ�벿�ֱ�ʾ���ǿ���ʱ��ڵ㣬��벿���ǹػ� [[
	 * '2017/05/11',11.2],['2017/05/11',11.2]],[['2017/05/11',12.2],['2017/05/11',13.2]]���͵��б�
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUserScatters() {
		try {
			// ��order��Ӧ���ѵǼǵķ�������������
			// �ӹ���վ��ÿ��õķ���������
			ArrayList<String> availableHost = (ArrayList<String>) this
					.getAvailableHost();
			if (availableHost != null) {
				// ����ķ��������Խ������񣬷��;������������
				this.serverIP = availableHost.get(0);
				this.port = Integer.parseInt(availableHost.get(1));
				// ����ķ��������Խ������񣬷��;������������
				String data = EC.E_303 + order;
				this.socket = new Socket(this.serverIP, this.port);
				Net.sentData(this.socket, data);
				System.out.println("�ѷ���E_303�������ڵȴ��������ݡ�����");
				return (ArrayList<String>) Net.acceptData(this.socket);
			} else {
				System.out.println("δ�ҵ����õ����ݷ�����");
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
}
