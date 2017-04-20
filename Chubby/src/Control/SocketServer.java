package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import Module.Host;
import Protocol.SC;
import Util.Net;

/*
 * @Leung
 * 网络处理的服务端，响应客服机的数据请求，返回数据处理结果
 */
public class SocketServer {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status=SC.SERVER_OK;
	// private Map<String, Host> hostsMap;
	// 监听端口号
	private int port = 10000;

	public SocketServer(int port) {
		this.port = port;
	}
	//
	@SuppressWarnings("unused")
	public void monitor() {
		String receiveData=null;
		String sendData;
		try {
			// 建立连接
			this.serverSocket = new ServerSocket(port);
			while (true) {
				// 获得连接
				this.socket = serverSocket.accept();
				// 接收客户端发送内容
				receiveData=Net.acceptData(socket);
				System.out.println("端口："+this.port+" 收到："+receiveData);
				if(receiveData.equals(SC.CHECK_CONNECTION))
					Net.sentData(socket, this.status);
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	 * 发送或接受数据
	 */
	public String swapInfo(String data) {
		return data;

	}

	/*
	 * 检查已就绪的处理机
	 */
	public int checkChubbyer() {
		Date start = new Date();
		// 建立连接
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				// 获得连接
				socket = serverSocket.accept();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		SocketServer chubbyer=new SocketServer(10000);
		chubbyer.monitor();
//		SocketServer chubbyer1=new SocketServer(10001);
//		chubbyer1.monitor();
//		SocketServer chubbyer2=new SocketServer(10002);
//		chubbyer2.monitor();
	}
}
