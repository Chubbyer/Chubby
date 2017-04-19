package Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/*
 * @Leung
 * �����������ݽ���
 */
public class Net {
	private static OutputStream os;
	private static InputStream is;

	// ��������
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

	// ��������
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

		// �����������
		//System.out.println("������������" + new String(b, 0, n));
	}
	public static void main(String[] args) {
		Date d = new Date();
		System.out.println(System.currentTimeMillis());
	}
}
