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
 * 此类专用于处理综合事务，涉及所有的数据
 */
public class ClientTasker implements Callable<Object> {
	public String taskType;
	public int order;// 线程序号，这代表着被访问的数据服务器应该处理哪部分数据
	public Socket socket;// 用与保持与数据服务器的链接
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
				System.out.println("E_302:获取数据失败！");
				return null;
			}
		}
		if (taskType.equals(EC.E_303)) {
			ArrayList<String> chubbyerStrings = this.getUserScatters();
			if (chubbyerStrings != null)
				return chubbyerStrings;
			else {
				System.out.println("E_303:获取数据失败！");
				return null;
			}
		}
		return null;
	}

	// 向工作站请求一个最优的数据服务器
	public Object getAvailableHost() {
		Object returnStr = null;
		int requestTimes = 5;
		while (true) {
			try {
				String serverIP = ChubbyConfig.STATION_IP;
				int port = ChubbyConfig.STATION_PORT;
				System.out.println("正在向工作站请求可用的数据服务器");
				this.socket = new Socket(serverIP, port);
				String data = SC.CLIENT_REQUEST+EC.E_302;
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
				try {
					Thread.sleep(1000);// 暂停1秒后继续请求
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
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
	 * 根据EC-302的约定，获得所有人的使用时间排行榜 接受到的数据是例如“{"name":"梁健","hours":3.2}”类型的列表，未排序
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUseHoursRanking() {
		try {
			// 从工作站获得可用的服务器请求
			ArrayList<String> availableHost = (ArrayList<String>) this
					.getAvailableHost();
			if (availableHost != null) {
				// 请求的服务器可以接受任务，发送具体的任务类型
				this.serverIP = availableHost.get(0);
				this.port = Integer.parseInt(availableHost.get(1));
				// 请求的服务器可以接受任务，发送具体的任务类型
				String data = EC.E_302 + order;
				this.socket = new Socket(this.serverIP, this.port);
				Net.sentData(this.socket, data);
				System.out.println("已发送E_302请求，正在等待接受数据···");
				return (ArrayList<String>) Net.acceptData(this.socket);
			} else {
				System.out.println("未找到可用的数据服务器");
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
		return null;
	}

	/*
	 * 根据EC-303的约定，获得所有人的开关几时间节点， 接受到的数据是例如 前半部分表示的是开机时间节点，后半部分是关机 [[
	 * '2017/05/11',11.2],['2017/05/11',11.2]],[['2017/05/11',12.2],['2017/05/11',13.2]]类型的列表
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUserScatters() {
		try {
			// 向order对应的已登记的服务器请求连接
			// 从工作站获得可用的服务器请求
			ArrayList<String> availableHost = (ArrayList<String>) this
					.getAvailableHost();
			if (availableHost != null) {
				// 请求的服务器可以接受任务，发送具体的任务类型
				this.serverIP = availableHost.get(0);
				this.port = Integer.parseInt(availableHost.get(1));
				// 请求的服务器可以接受任务，发送具体的任务类型
				String data = EC.E_303 + order;
				this.socket = new Socket(this.serverIP, this.port);
				Net.sentData(this.socket, data);
				System.out.println("已发送E_303请求，正在等待接受数据···");
				return (ArrayList<String>) Net.acceptData(this.socket);
			} else {
				System.out.println("未找到可用的数据服务器");
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
		return null;
	}
}
