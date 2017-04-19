package Control;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import Util.Net;

public class SocketClient {
	 public static void main(String[] args) {
         Socket socket = null;
         InputStream is = null;
         OutputStream os = null;
         //��������IP��ַ
         String serverIP = "127.0.0.1";
         //�������˶˿ں�
         int port = 10000;
         //��������
         String data = "Hello";
         try {
                  //��������
                  socket = new Socket(serverIP,port);
                  //��������
                  Net.sentData(socket, data);
                  //��������
                  String ac=Net.acceptData(socket);
                  //�����������
                  System.out.println("������������" + ac);
         } catch (Exception e) {
                  e.printStackTrace(); //��ӡ�쳣��Ϣ
         }finally{
                  try {
                           //�ر���������
                           is.close();
                           os.close();
                           socket.close();
                  } catch (Exception e2) {}
         }
}
}
