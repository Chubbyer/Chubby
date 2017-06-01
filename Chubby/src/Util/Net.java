package Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import Control.SocketServer;

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
			// oos.close();
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
			// ois.close();//TCP����ͨ����һ���������ӵ�ͨ�ţ����ܹ�
			return obj;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// �����������
		// System.out.println("������������" + new String(b, 0, n));
	}

	/*
	 * ͨ��UDP��ָ����ַ�Ͷ˿ڷ�������
	 */
	public static void sendDataByUDP(String ip, int port, String info) {
		try {
			DatagramSocket socket = new DatagramSocket();
			// BufferedReader keyReader = new BufferedReader(new
			// InputStreamReader(System.in));
			DatagramPacket packet = null;
			packet = new DatagramPacket(info.getBytes(),
					info.getBytes().length, InetAddress.getByName(ip), port);
			socket.send(packet);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ͨ��UDP��ָ���˿ڽ�������
	 */
	public static String receiveDataByUDP(int port) {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			String receive = null;
			while (true) {
				socket.receive(packet);
				receive = new String(buf, 0, packet.getLength());
				if (receive != null)
					break;
			}
			socket.close();
			return receive;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Date d = new Date();
		// System.out.println(Net.receiveDataByUDP(9090));
		// Net.sendDataByUDP("172.16.70.201", 9090, "hello");
		SocketServer chubbyer2 = new SocketServer(10012);
	}
}
