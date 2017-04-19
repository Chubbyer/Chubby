package Control;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
	public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;
        //�����˿ں�
        int port = 10000;
        try {
                 //��������
                 serverSocket = new ServerSocket(port);
                 //�������
                 socket = serverSocket.accept();
                 //���տͻ��˷�������
                 is = socket.getInputStream();
                 byte[] b = new byte[1024];
                 int n = is.read(b);
                 //���
                 System.out.println("�ͻ��˷�������Ϊ��" + new String(b,0,n));
                 //��ͻ��˷��ͷ�������
                 os = socket.getOutputStream();
                 os.write(b, 0, n);
        } catch (Exception e) {
                 e.printStackTrace();
        }finally{
                 try{
                          //�ر���������
                          os.close();
                          is.close();
                          socket.close();
                          serverSocket.close();
                 }catch(Exception e){}
        }
}
}
