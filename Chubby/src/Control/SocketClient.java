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
		//注意这里nextHost结果可能没变
		this.serverIP = HostList.nextHost().getServerIP();
		System.out.println(this.serverIP);
		this.port = HostList.nextHost().getPort();
		System.out.println(this.port);
		try {
			socket = new Socket(serverIP, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = SC.CHECK_CONNECTION;
		Net.sentData(socket, data);
		return Net.acceptData(socket);
	}

	// 根据协议EC-300;请求301操作,id表示对应人的标识号
	public String getOneOverview(String id) {
		// 当前服务器（初始适配的服务器）就绪
		for (int i = 0; i < HostList.hostsCount; i++) {
			if (this.checkConnection().equals(SC.SERVER_OK)) {
				String data = EC.E_301;
				Net.sentData(socket, data);
				System.out.println(Net.acceptData(socket));
				return Net.acceptData(socket);
			}
			
		}return null;
	}

	public static void main(String[] args) {
		SocketClient sClient = new SocketClient();
		System.out.println(sClient.checkConnection());
	}
}
