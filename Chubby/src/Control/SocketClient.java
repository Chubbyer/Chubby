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
         //服务器端IP地址
         String serverIP = "127.0.0.1";
         //服务器端端口号
         int port = 10000;
         //发送内容
         String data = "Hello";
         try {
                  //建立连接
                  socket = new Socket(serverIP,port);
                  //发送数据
                  Net.sentData(socket, data);
                  //接收数据
                  String ac=Net.acceptData(socket);
                  //输出反馈数据
                  System.out.println("服务器反馈：" + ac);
         } catch (Exception e) {
                  e.printStackTrace(); //打印异常信息
         }finally{
                  try {
                           //关闭流和连接
                           is.close();
                           os.close();
                           socket.close();
                  } catch (Exception e2) {}
         }
}
}
