package Control;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import Module.Chubbyer;
import Module.HostList;
import Protocol.EC;
import Protocol.SC;
import Util.Net;

/*
 * @Leung
 * ����ר���ڴ����ۺ������漰���е�����
 */
public class ClientTasker implements Callable<Object> {
	public String taskType;
	public int order;// �߳���ţ�������Ž�Ҫ���ʵ��������
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
				System.out.println("E_302:��ȡ����ʧ�ܣ��������»�ȡ");
				return this.getUseHoursRanking();
			}
		}
		if (taskType.equals(EC.E_303)) {
			ArrayList<String> chubbyerStrings = this.getUserScatters();
			if (chubbyerStrings != null)
				return chubbyerStrings;
			else {
				System.out.println("E_303:��ȡ����ʧ�ܣ��������»�ȡ");
				return this.getUserScatters();
			}
		}
		return null;
	}

	// ����������������
	public Object checkConnection() {
		Object returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.hosts[order].getServerIP();
				this.port = HostList.hosts[order].getPort();
				System.out.println(order + "��������" + serverIP + " �˿ڣ�" + port);
				this.socket = new Socket(serverIP, port);
				String data = SC.CHECK_CONNECTION;
				Net.sentData(socket, data);// ���ͱ�ʾ�������ӵ��ֶ�
				returnStr = Net.acceptData(socket);// �յ�����˵Ļ�Ӧ
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("�÷�����δ����");
				try {
					Thread.sleep(1000);// ��ͣ1����������
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("�÷�����δ����");
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
	 * ����EC-302��Լ������������˵�ʹ��ʱ�����а� ���ܵ������������硰{"name":"����","hours":3.2}�����͵��б���δ����
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUseHoursRanking() {
		try {
			// ��order��Ӧ���ѵǼǵķ�������������
			Object checkResult = this.checkConnection();
			if (checkResult != null) {
				String checkResultString = checkResult.toString().substring(0,
						3);
				if (checkResultString.equals(SC.SERVER_OK)) {
					// ����ķ��������Խ������񣬷��;������������
					String data = EC.E_302 + order;
					this.socket = new Socket(this.serverIP, this.port);
					Net.sentData(this.socket, data);
					System.out.println("�ѷ���E_302�������ڵȴ��������ݡ�����");
					return (ArrayList<String>) Net.acceptData(this.socket);
				} else {
					System.out.println("�÷�������æ");
				}
			} else {
				System.out.println("δ���ܵ���Ӧ");
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
	 * ����EC-303��Լ������������˵Ŀ��ؼ�ʱ��ڵ㣬 ���ܵ�������������
	 * {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}���͵��б�
	 */ 
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUserScatters(){
		try {
			// ��order��Ӧ���ѵǼǵķ�������������
			Object checkResult = this.checkConnection();
			if (checkResult != null) {
				String checkResultString = checkResult.toString().substring(0,
						3);
				if (checkResultString.equals(SC.SERVER_OK)) {
					// ����ķ��������Խ������񣬷��;������������
					String data = EC.E_303 + order;
					this.socket = new Socket(this.serverIP, this.port);
					Net.sentData(this.socket, data);
					System.out.println("�ѷ���E_303�������ڵȴ��������ݡ�����");
					return (ArrayList<String>) Net.acceptData(this.socket);
				} else {
					System.out.println("�÷�������æ");
				}
			} else {
				System.out.println("δ���ܵ���Ӧ");
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