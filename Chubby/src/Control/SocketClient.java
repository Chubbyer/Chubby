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
 * 用于请求数据服务
 */
public class SocketClient implements Callable<Object> {
	private Socket socket = null;
	private String serverIP = null;// 服务器端IP地址
	private int port;// 服务器端端口号
	public String serCondition;// 搜索条件
	public String taskType;// 任务类型

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
		// 任务分发
		if (this.taskType.equals(EC.E_301)) {
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			return chubbyerString;
		}
		if (this.taskType.equals(EC.E_301_1)) {
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			// 加工getOneOverview函数的结果，方便在页面上展示EC-301_1任务的结果,得到每天使用多少小时
			chubbyers = ChubbyerParser.getUseTime(chubbyerString);
			chubbyers = ChubbyerParser.remoneRepChubbyers(chubbyers);
			chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
			System.out.println("SocketClient已返回数据");
			return chubbyers;
		}
		if (this.taskType.equals(EC.E_301_2)) {
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			ArrayList<Double> useHours = ChubbyerParser
					.getUseHoursDistribut(chubbyerString);
			return useHours;
		}
		if (this.taskType.equals(EC.E_301_3)) {
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = (ArrayList<String>) this
					.getOneOverview(serCondition);
			// 加工getOneOverview函数的结果，方便在页面上展示EC-301_3任务的结果,得到开关机时间点
			chubbyers = ChubbyerParser.getUseTimeScatter(chubbyerString);
			// chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
			System.out.println("SocketClient已返回数据");
			return chubbyers;
		}
		return null;
	}

	// 检查与服务器的连接
	public Object getAvailableHost() {
		Object returnStr = null;
		int requestTimes = 5;
		while (true) {
			try {
				String serverIP = ChubbyConfig.STATION_IP;
				int port = ChubbyConfig.STATION_PORT;
				System.out.println("正在向工作站请求可用的数据服务器");
				this.socket = new Socket(serverIP, port);
				String data = SC.CLIENT_REQUEST+EC.E_301;
				Net.sentData(socket, data);// 发送表示请求连接的字段
				returnStr = Net.acceptData(socket);// 收到服务端的回应
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				requestTimes--;
				System.out.println("该工作站未启用");
				if (requestTimes < 0) {
					break;
				}
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				requestTimes--;
				System.out.println("该工作站未启用");
				if (requestTimes < 0) {
					break;
				}
				continue;
			}
		}
		return returnStr;
	}

	// 根据协议EC-300;请求301操作,id表示对应人的标识号
	public Object getOneOverview(String id) {
		// 当前服务器（初始适配的服务器）就绪
		try {
			// 从工作组获得可用的服务器请求
			@SuppressWarnings("unchecked")
			ArrayList<String> availableHost = (ArrayList<String>) this
					.getAvailableHost();
			if (availableHost != null) {
				// 请求的服务器可以接受任务，发送具体的任务类型
				this.serverIP = availableHost.get(0);
				this.port = Integer.parseInt(availableHost.get(1));
				System.out.println("可用：" + this.serverIP + " " + this.port);
				String data = EC.E_301 + id;
				this.socket = new Socket(this.serverIP, this.port);
				Net.sentData(this.socket, data);
				System.out.println("已发送请求，正在等待接受数据···");
				return Net.acceptData(this.socket);
			} else {
				System.out.println("未找到可用的数据服务器");
				return null;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接异常");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接异常");
			return null;
		}
	}

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();

		try {
			System.out.println(sClient.getAvailableHost());
			Thread.sleep(2000);
			System.out.println(sClient.getAvailableHost());
			Thread.sleep(2000);
			System.out.println(sClient.getAvailableHost());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(sClient.checkConnection());
		// @SuppressWarnings("unchecked")
		// ArrayList<String> chubbyers = (ArrayList<String>) sClient
		// .getOneOverview("Leung");
		// System.out.println(chubbyers.size());
		// // for (int i = 0; i < 50; i++) {
		// // System.out.println(chubbyers.get(i));
		// // }
		// ArrayList<Chubbyer> chubbyerList = new ArrayList<Chubbyer>();
		// // 得到每天的开关机时点
		// chubbyerList = ChubbyerParser.getUseTimeScatter(chubbyers);
		// for (int i = 0; i < chubbyerList.size() / 2; i++) {
		// System.out.print("OP:" + chubbyerList.get(i).day + " ");
		// System.out.println(chubbyerList.get(i).point);
		// System.out.print("CP:"
		// + chubbyerList.get(i + chubbyerList.size() / 2).day + " ");
		// System.out
		// .println(chubbyerList.get(i + chubbyerList.size() / 2).point);
		// }
		// System.out.println(sClient.getOneOverview("Leung"));

	}

}
