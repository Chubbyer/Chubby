package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import Module.Host;

/*
 * @Leung
 * 网络处理的服务端，用于分发任务，汇集其他处理机的数据处理结果
 */
public class SocketServer {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	private Map<String, Host> hostsMap;
	// 监听端口号
	private int port = 10000;

	private SocketServer(int port) {
		this.port = port;
	}

	/*
	 * 发送或接受数据
	 */
	public String swapInfo(String data) {
		String receiveData;
		String sendData;
		try {
			// 建立连接
			this.serverSocket = new ServerSocket(port);
			// 获得连接
			this.socket = serverSocket.accept();
			// 接收客户端发送内容
			this.is = socket.getInputStream();
			byte[] b = new byte[1024];
			int n = is.read(b);
			// 输出
			System.out.println("客户端发送内容为：" + new String(b, 0, n));
			// 向客户端发送反馈内容
			this.os = socket.getOutputStream();
			os.write(b, 0, n);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流和连接
				os.close();
				is.close();
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * 检查已就绪的处理机
	 */
	public int checkChubbyer() {
		Date start=new Date();
		// 建立连接
		try {
			serverSocket = new ServerSocket(port);
			while(true){
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

		try {
			// 建立连接
			serverSocket = new ServerSocket(port);
			// 获得连接
			socket = serverSocket.accept();
			// 接收客户端发送内容
			is = socket.getInputStream();
			byte[] b = new byte[1024];
			int n = is.read(b);
			// 输出
			System.out.println("客户端发送内容为：" + new String(b, 0, n));
			// 向客户端发送反馈内容
			os = socket.getOutputStream();
			os.write(b, 0, n);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流和连接
				os.close();
				is.close();
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}
}
