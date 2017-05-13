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
 * 此类专用于处理综合事务，涉及所有的数据
 */
public class ClientTasker implements Callable<Object> {
	public String taskType;
	public int order;// 序号，这意味着将要访问的主机序号
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
		if(taskType.equals(EC.E_302)){
			return this.getUseHoursRanking();
		}
		return null;
	}

	// 检查与服务器的连接
	public Object checkConnection() {
		Object returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.hosts[order].getServerIP();
				this.port = HostList.hosts[order].getPort();
				System.out.println("正在请求：" + serverIP + " 端口：" + port);
				this.socket = new Socket(serverIP, port);
				String data = SC.CHECK_CONNECTION;
				Net.sentData(socket, data);// 发送表示请求连接的字段
				returnStr = Net.acceptData(socket);// 收到服务端的回应
				break;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("该服务器未启用");
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("该服务器未启用");
				continue;
			}
		}
		return returnStr;
	}

	/*
	 * 根据EC-302的约定，获得所有人的使用时间排行榜
	 * 接受到的数据是例如“{"name":"梁健","hours":3.2}”类型的列表，未排序
	 */
	public ArrayList<String> getUseHoursRanking() {
		try {
			// 向order对应的已登记的服务器请求连接
			Object checkResult = this.checkConnection();
			if (checkResult != null) {
				String checkResultString = checkResult.toString().substring(0,
						3);
				if (checkResultString.equals(SC.SERVER_OK)) {
					// 请求的服务器可以接受任务，发送具体的任务类型
					String data = EC.E_302;
					this.socket = new Socket(this.serverIP, this.port);
					Net.sentData(this.socket, data);
					System.out.println("已发送请求，正在等待接受数据···");
					return (ArrayList<String>) Net.acceptData(this.socket);
				} else {
					System.out.println("该服务器正忙");
				}
			} else {
				System.out.println("未接受到回应");
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
