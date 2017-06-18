package Chubbys;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import Protocol.EC;
import Protocol.SC;
import Util.ChubbyConfig;
import Util.Net;
import Util.TimeOut;
import Util.Timing;

/*
 * author@Leung
 * 2017-05-27,�����Ŀ���Ǹ����������ݷ���ϵͳ���ȶ�
 * ּ��������ݷ���ϵͳ���ݴ�����
 */
public class WorkStation {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status = SC.SERVER_OK;
	private int port = 10001;// Ĭ�ϵļ����˿ں�
	private ArrayList<DataHost> hostList = new ArrayList<DataHost>();// ����ϵͳ�ķ�����

	public WorkStation(int port) {
		// TODO Auto-generated constructor stub
		this.port = port;
	}

	public void action() {
		Object receiveData = null;
		try {
			this.serverSocket = new ServerSocket(port);
			System.out.println("����վ�������Ѿ���");
			Socket accpetSocket = null;
			HeartBeat heartBeat = new HeartBeat(null, 30 * 1000);
			heartBeat.start();
			while (true) {
				if (this.hostList.size() > 0) {
					heartBeat.hostList = this.hostList;
					// this.hostList = null;
				}
				// �������
				accpetSocket = serverSocket.accept();
				// ���տͻ��˷�������
				receiveData = Net.acceptData(accpetSocket);
				String oType = receiveData.toString().substring(0, 3);
				if (oType.equals(SC.CLIENT_REQUEST) && this.hostList != null) {
					// ���ظ�������ͷ�������Ҫ���DS
					ArrayList<String> hostInfo = new ArrayList<String>();
					// ���ӵ����ݣ�һ��Ϊ��������
					String additional = receiveData.toString().substring(3);
					if (additional.equals(EC.E_302)
							|| additional.equals(EC.E_303)) {
						// �ͷ�������������302��303�����DS
						if (this.hostList.size() > 0
								&& this.hostList.get(0).priority >= 5) {
							hostInfo.add(this.hostList.get(0).ip);
							hostInfo.add(this.hostList.get(0).port + "");
							this.hostList.get(0).priority-=5;
						}
					} else {
						if (this.hostList.size() > 0) {
							hostInfo.add(this.hostList.get(0).ip);
							hostInfo.add(this.hostList.get(0).port + "");
							this.hostList.get(0).priority--;
						} else {
							hostInfo.add(ChubbyConfig.DEFAULT_DS_IP);
							hostInfo.add(ChubbyConfig.DEFAULT_DS_PORT + "");
						}
					}
					Net.sentData(accpetSocket, hostInfo);
					this.hostList = this.sortHostPriority(this.hostList);
					continue;
				}
				if (oType.equals(SC.CHUBBYER_REPORT)) {
					// ���ӵ����ݣ�eg:{"ip":"192.168.10.23","port":10001,"priority":12}
					String additional = receiveData.toString().substring(3);
					DataHost host = this.getDataHost(additional);
					System.out.println("�յ�" + host.ip + "����Chubbyϵͳ�ı���");
					// ����host����hostList֮�У������е�host�Ƴ�
					for (int i = 0; i < this.hostList.size(); i++) {
						if (host.ip.equals(hostList.get(i).ip)) {
							this.hostList.remove(i);
						}
					}
					this.hostList.add(host);
					if (Net.sentData(accpetSocket, SC.CHUBBYER_REPORT))
						System.out.println("   " + host.ip + "�ɹ�����Chubbyϵͳ");
					this.hostList = this.sortHostPriority(this.hostList);
					heartBeat.hostList = this.hostList;// �������������б�
					continue;
				}
				if (oType.equals(SC.HEART_BEAT)) {
					// ���ӵ����ݣ�eg:{"ip":"192.168.10.23","port":10001,"priority":12}
					String additional = receiveData.toString().substring(3);
					DataHost host = this.getDataHost(additional);
					if (Net.sentData(accpetSocket, SC.HEART_BEAT)
							&& host != null) {
						System.out.println("    �յ�" + host.ip + "���������Ա���");
						for (int i = 0; i < this.hostList.size(); i++) {
							if (this.hostList.get(i).ip.equals(host.ip)) {
								// ��������Ȩ
								this.hostList.get(i).priority = host.priority;
							} else {
								// ��������Ȩ
								this.hostList.get(i).priority--;
							}
						}
						this.hostList = this.sortHostPriority(this.hostList);
						heartBeat.hostList = this.hostList;
						continue;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataHost getDataHost(String hostString) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(hostString);
			String ip = jsonObj.getString("ip");
			int port = jsonObj.getInt("port");
			int priority = jsonObj.getInt("priority");
			return new DataHost(ip, port, priority);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * ���ѵǼǵķ�����������Ȩ���򣬰�����ȨС��0���ų�
	 */
	public ArrayList<DataHost> sortHostPriority(ArrayList<DataHost> hosts) {
		ArrayList<DataHost> dataHosts = new ArrayList<DataHost>();
		// �ų�����ȨС��0������
		for (int i = 0; i < hosts.size(); i++) {
			if (hosts.get(i).priority < 0) {
				System.out.println("�ѽ�" + hosts.get(i).ip + "�Ƴ�");
				hosts.remove(i);
			}
		}
		int maxIndex = 0;
		int size = hosts.size();
		if (size > 0) {
			for (int i = 0; i < size - 1; i++) {
				// ÿһ���ҵ��б���priority�����Ǹ�
				double maxPriority = hosts.get(0).priority;
				for (int j = 0; j < hosts.size(); j++) {
					if (hosts.get(j).priority > maxPriority) {
						maxPriority = hosts.get(j).priority;
						maxIndex = j;
					}
				}
				dataHosts.add(hosts.get(maxIndex));
				hosts.remove(maxIndex);
				maxIndex = 0;
			}
			// ʣ�µ����һ������priority�����Ǹ�
			dataHosts.add(hosts.get(0));
		}
		return dataHosts;
	}

	/*
	 * �������ԣ�ÿ��30�뷢���hostList���������ԣ����Chubby����ϵͳ�д������ݷ���������
	 * ͨ��UDP���Ͳ����������ݷ��������ܵ���ͨ��TCP���ر���
	 */
	public static void heartBeat(ArrayList<DataHost> hostList) {
		String testIp = null;
		// int testPort;
		if (hostList != null) {
			for (int i = 0; i < hostList.size(); i++) {
				testIp = hostList.get(i).ip;
				// testPort = hostList.get(i).port;
				Net.sendDataByUDP(testIp, ChubbyConfig.HEART_BEAT_PORT,
						SC.HEART_BEAT);
				System.out.println("    ����IP��" + testIp + "   �˿ڣ�"
						+ hostList.get(i).port + "  ������������   Priority:"
						+ hostList.get(i).priority);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		// System.out.println("��ʱ��ʼ");
		// ExecutorService executor = Executors.newCachedThreadPool();
		// CompletionService<Object> comp = new ExecutorCompletionService<>(
		// executor);
		// String string = "SS";
		// // comp.submit(new Timing(3000,string));
		// Timing timing = new Timing(3000, string);
		// timing.start();
		// System.out.println("____" + string.toString());
		DataHost host1 = new DataHost("109.12", 10001, 11);
		DataHost host2 = new DataHost("109.12", 10002, 12);
		DataHost host3 = new DataHost("109.12", 10003, 13);
		DataHost host4 = new DataHost("109.12", 10004, -1);
		ArrayList<DataHost> dataHosts = new ArrayList<DataHost>();
		dataHosts.add(host1);
		dataHosts.add(host2);
		dataHosts.add(host3);
		dataHosts.add(host4);
		// WorkStation ws = new WorkStation(10000);
		// dataHosts = ws.sortHostPriority(dataHosts);
		// for (DataHost dataHost : dataHosts) {
		// System.out.println(dataHost.port);
		// }
		// Net.sendDataByUDP("172.16.70.201", 9090, "sss");
		WorkStation ws = new WorkStation(10000);
		ws.action();
		// HeartBeat heartBeat = new HeartBeat(null, 5 * 1000);
		// heartBeat.start();
		// Thread.sleep(11000);
		// heartBeat.hostList = dataHosts;
	}
}

class DataHost {
	public String ip;
	public int port;
	public int priority;

	public DataHost(String ip, int port, int priority) {
		this.ip = ip;
		this.port = port;
		this.priority = priority;
	}
}

/*
 * ���ڿ�չ��������
 */
class HeartBeat extends Thread {
	public ArrayList<DataHost> hostList;// ��Ҫ�����Եķ�����
	public Timing timing;
	public long delay;// �������Եļ��ʱ��

	public HeartBeat(ArrayList<DataHost> hostList, long delay) {
		// TODO Auto-generated constructor stub
		this.hostList = hostList;
		this.delay = delay;
	}

	class hb implements TimeOut {

		@Override
		public void timeUp() {
			// TODO Auto-generated method stub

			if (hostList != null) {
				System.out.println("������������  " + (new Date()).toLocaleString());
				WorkStation.heartBeat(hostList);
			}
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		while (true) {
			if ((System.currentTimeMillis() - start) > delay) {
				timing = new Timing(delay, new hb());
				timing.start();
				start = System.currentTimeMillis();
			}
		}
	}
}
