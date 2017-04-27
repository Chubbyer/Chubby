package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

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
	public String checkConnection() {
		// 注意这里nextHost结果可能没变
		String returnStr = null;
		while (true) {
			try {
				this.serverIP = HostList.nextHost().getServerIP();
				System.out.println(serverIP);
				this.port = HostList.nextHost().getPort();
				System.out.println(port);
				this.socket = new Socket(serverIP, port);
				String data = SC.CHECK_CONNECTION;
				Net.sentData(socket, data);// 发送表示请求连接的字段
				// Net.sentData(socket, "301|Leung");
				returnStr = Net.acceptData(socket);// 收到服务端的回应
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

	// 根据协议EC-300;请求301操作,id表示对应人的标识号
	public String getOneOverview(String id) {
		// 当前服务器（初始适配的服务器）就绪
		try {
			for (int i = 0; i < HostList.hostsCount; i++) {
				// 随机向已登记的服务器请求连接
				if (this.checkConnection().equals(SC.SERVER_OK)) {
					// 请求的服务器可以接受任务，发送具体的任务类型
					// 任务类型与目标用户的标识用|隔开
					String data = EC.E_301 + id;
					this.socket=new Socket(this.serverIP, this.port);
					Net.sentData(this.socket, data);
					//System.out.println(Net.acceptData(this.socket));
					return Net.acceptData(this.socket);
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
		// System.out.println(sClient.checkConnection());
		sClient.getOneOverview("Leung");
	}
}
