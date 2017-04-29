package Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;

	// 发送数据,发送对象时，发送方与接受方的对象要是一模一样的，必须来自同一个类
	public static boolean sentData(Socket socket, Object data) {
		try {
			os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(data);
			oos.flush();
			//oos.close();
			// os.write(((String) data).getBytes());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 接收数据，从客户端发送给服务器的大部分是字符串
	public static Object acceptData(Socket socket) {
		try {
			is = socket.getInputStream();
			ois = new ObjectInputStream(new BufferedInputStream(is));
			Object obj = ois.readObject();
			// byte[] b = new byte[1024];
			// int n = is.read(b);
			// return new String(b, 0, n);
			//ois.close();//TCP网络通信是一个保持连接的通信，不能关
			return obj;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// 输出反馈数据
		// System.out.println("服务器反馈：" + new String(b, 0, n));
	}

	public static void main(String[] args) {
		Date d = new Date();
		System.out.println(System.currentTimeMillis());
	}
}
