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
 * �����������ݽ���
 */
public class Net {
	private static OutputStream os;
	private static InputStream is;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;

	// ��������,���Ͷ���ʱ�����ͷ�����ܷ��Ķ���Ҫ��һģһ���ģ���������ͬһ����
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

	// �������ݣ��ӿͻ��˷��͸��������Ĵ󲿷����ַ���
	public static Object acceptData(Socket socket) {
		try {
			is = socket.getInputStream();
			ois = new ObjectInputStream(new BufferedInputStream(is));
			Object obj = ois.readObject();
			// byte[] b = new byte[1024];
			// int n = is.read(b);
			// return new String(b, 0, n);
			//ois.close();//TCP����ͨ����һ���������ӵ�ͨ�ţ����ܹ�
			return obj;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// �����������
		// System.out.println("������������" + new String(b, 0, n));
	}

	public static void main(String[] args) {
		Date d = new Date();
		System.out.println(System.currentTimeMillis());
	}
}
