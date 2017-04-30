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
 * 用于请求数据服务
 */
public class SocketClient {
	private Socket socket = null;
	private String serverIP = null;// 服务器端IP地址
	private int port;// 服务器端端口号

	public SocketClient() {
	}

	// 检查与服务器的连接
	public Object checkConnection() {
		Object returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.nextHost().getServerIP();
				this.port = HostList.nextHost().getPort();
				System.out.println("正在请求："+serverIP+" 端口："+port);
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

	// 根据协议EC-300;请求301操作,id表示对应人的标识号
	public Object getOneOverview(String id) {
		// 当前服务器（初始适配的服务器）就绪
		try {
			for (int i = 0; i < HostList.hostsCount; i++) {
				// 随机向已登记的服务器请求连接
				Object checkResult= this.checkConnection();
				if (checkResult != null) {
					String checkResultString = checkResult.toString()
							.substring(0, 3);
					if (checkResultString.equals(SC.SERVER_OK)) {
						// 请求的服务器可以接受任务，发送具体的任务类型
						String data = EC.E_301 + id;
						this.socket = new Socket(this.serverIP, this.port);
						Net.sentData(this.socket, data);
						System.out.println("已发送请求，正在等待接受数据···");
						return Net.acceptData(this.socket);
					}else {
						System.out.println("该服务器正忙");
						continue;
					}
				} else {
					System.out.println("未接受到回应");
				}
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

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		//System.out.println(sClient.checkConnection());
		@SuppressWarnings("unchecked")
		ArrayList<String> chubbyers = (ArrayList<String>) sClient.getOneOverview("Leung");
		System.out.println(chubbyers.size());
		for (int i = 0; i < 50; i++) {
			System.out.println(chubbyers.get(i));
		}
	
		//System.out.println(sClient.getOneOverview("Leung"));

	}
}
