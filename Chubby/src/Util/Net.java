package Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/*
 * @Leung
 * 用于网络数据交换
 */
public class Net {
	private static OutputStream os;
	private static InputStream is;

	// 发送数据
	public static boolean sentData(Socket socket, String data) {
		try {
			os = socket.getOutputStream();
			os.write(data.getBytes());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 接收数据
	public static String acceptData(Socket socket) {
		try {
			is = socket.getInputStream();
			byte[] b = new byte[1024];
			int n = is.read(b);
			return new String(b, 0, n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// 输出反馈数据
		//System.out.println("服务器反馈：" + new String(b, 0, n));
	}
	public static void main(String[] args) {
		Date d = new Date();
		System.out.println(System.currentTimeMillis());
	}
}
