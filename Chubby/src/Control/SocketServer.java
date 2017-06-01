package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.security.auth.login.Configuration;

import Module.Chubbyer;
import Module.Host;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Protocol.SC;
import Util.ChubbyConfig;
import Util.MongoDBJDBC;
import Util.Net;

/*
 * @Leung
 * 网络处理的服务端，响应客服机的数据请求，返回数据处理结果
 */
public class SocketServer {
	private String localHostName;
	private String localIp;
	public static int dbPosition=0;// 目标数据库位置，0表示本地，1表示默认的位置
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status = SC.SERVER_OK;
	private int priority=10;//优先权，与最大可接受的连接数相等
	private int maxLinks = 10;// 最大可接受的连接数
	// 监听端口号
	private int port = 10001;

	public SocketServer(int port) {
		this.port = port;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			localIp = addr.getHostAddress().toString();// 获得本机IP
			localHostName = addr.getHostName().toString();// 获得本机名称
			MongoDBJDBC mongoer = new MongoDBJDBC("Log");
			Date nowDate = new Date();
			// 检查本机数据库服务是否启动并向本地数据库写入本次操作的日志
			if (mongoer.writeLog(localHostName, nowDate.toString())) {
				if (this.report())
					this.monitor();
			} else {
				if (this.localMongoException() == false)
					System.out.println("本机暂不能提供服务");
				else
					this.monitor();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("向WorkStation报告出错");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("向WorkStation报告出错");
		}
	}

	// 监听连接请求，接受到任务请求后转发任务
	@SuppressWarnings("unused")
	public void monitor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		Object receiveData = null;
		String sendData;
		try {
			// 建立连接
			this.serverSocket = new ServerSocket(port);
			Socket accpetSocket =null;
			System.out.println("服务器已就绪");
			HeartBeat heartBeat=new HeartBeat(this.getIpInfo());
			heartBeat.start();
			while (true) {
				
				// 获得连接
				accpetSocket = serverSocket.accept();
				// 接收客户端发送内容
				receiveData = Net.acceptData(accpetSocket);
				System.out.println("当前可接受的连接数：" + this.maxLinks);
				System.out.println("端口：" + this.port + " 收到：" + receiveData);
				// 解析出客户端请求的类型
				String oType = receiveData.toString().substring(0, 3);
				System.out.println("客户端请求：" + oType + "#操作");
				if (oType.equals(SC.CHECK_CONNECTION)) {
					// 客户端请求连接，发送本服务器的状态
					Net.sentData(accpetSocket, this.status);
					continue;
				}
				// 所有的任务都从这里转发
				if (oType.equals(EC.E_301) && this.status.equals(SC.SERVER_OK)) {
					// 附加的数据，一般为姓名或学号或主机名
					String additional = receiveData.toString().substring(3);
					System.out.println("正在转发E_301(" + additional + ")任务···");
					// 将本机的状态置为忙
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;
					// 启动EC.E_301所描述的任务
					Tasker taskE_301 = new Tasker(accpetSocket, additional,
							EC.E_301);
					comp.submit(taskE_301);
					System.out.println("正在处理E_301(" + additional + ")任务···");
				}
				if (oType.equals(EC.E_302) && this.status.equals(SC.SERVER_OK)) {
					// 附加的数据，这里应该是1或者2或者3
					// 我们将所有的用户分成三部份，1、2、3表示要处理第几部分
					String additional = receiveData.toString().substring(3);
					int order = Integer.parseInt(additional);
					System.out.println("正在转发E_302任务···");
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;// 将本机的状态置为忙
					// 启动EC.E_302所描述的任务
					TaskerPlus taskE_302 = new TaskerPlus(accpetSocket,
							EC.E_302, order);
					comp.submit(taskE_302);
					System.out.println("正在处理E_302:(" + additional + ")任务···");
				}
				if (oType.equals(EC.E_303) && this.status.equals(SC.SERVER_OK)) {
					// 几乎与302任务相同
					// 附加的数据，这里应该是1或者2或者3
					// 我们将所有的用户分成三部份，1、2、3表示要处理第几部分
					String additional = receiveData.toString().substring(3);
					int order = Integer.parseInt(additional);
					System.out.println("正在转发E_303任务···");
					if (--this.maxLinks == 0)
						this.status = SC.SERVER_BUSY;// 将本机的状态置为忙
					// 启动EC.E_302所描述的任务
					TaskerPlus taskE_303 = new TaskerPlus(accpetSocket,
							EC.E_303, order);
					comp.submit(taskE_303);
					System.out.println("正在处理E_303:(" + additional + ")任务···");
				}
				// 最后检查任务是否已执行完
				int alreadyLinks = this.priority - this.maxLinks;
				for (int i = 0; i < alreadyLinks; i++) {
					try {
						// 检查任务线程是否执行完
						Future<Object> future = comp.take();
						boolean isComplet = (boolean) future.get();
						if (isComplet == true)
							this.maxLinks++;

					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	 * 获得数据服务器的ip、port信息
	 */
	public String getIpInfo() {
		String ipInfo="{\"ip\":\"" + this.localIp
				+ "\",\"port\":" + this.port + ",\"priority\":" + this.maxLinks
				+ "}";;
		return ipInfo;
	}

	/*
	 * 本地的MongoDB数据库出现异常时，切换到默认的MongoDB
	 */
	public boolean localMongoException() {
		try {
			System.out.println("本地数据库服务异常，正在切换至默认数据库服务器");
			MongoDBJDBC mongoer = new MongoDBJDBC(
					ChubbyConfig.DEFAULT_MONGODB_IP,
					ChubbyConfig.DEFAULT_MONGODB_PORT, "Log");
			Date nowDate = new Date();
			if (mongoer.writeLog(localHostName, nowDate.toString())) {
				System.out.println("成功切换至默认数据库服务器");
				SocketServer.dbPosition=1;
				// 重新报告
				if (this.report()) {
					return true;
				}else {
					System.out.println("向WorkStation报告出错");
					return false;
				}
			} else {
				System.out.println("切换失败");
				return false;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("向WorkStation报告出错");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("切换失败");
		}
		return false;
	}

	/*
	 * 向WorkStation报告本机能提供数据服务
	 */
	public boolean report() throws UnknownHostException, IOException {
		System.out.println("正在向WorkStation报告本机状态并申请加入Chubby系统");
		while (true) {
			this.socket = new Socket(ChubbyConfig.STATION_IP,
					ChubbyConfig.STATION_PORT);
			String data = SC.CHUBBYER_REPORT + this.getIpInfo();
			Net.sentData(socket, data);// 发送表示请求连接的字段
			String returnStr = (String) Net.acceptData(socket);// 收到服务端的回应
			if (returnStr.equals(SC.CHUBBYER_REPORT)) {
				System.out.println("  加入Chubby系统成功");
				return true;
			}
		}
	}

	public static void main(String[] args) {
		SocketServer chubbyer = new SocketServer(10011);
		// chubbyer.monitor();
		// SocketServer chubbyer1=new SocketServer(10001);
		// chubbyer1.monitor();
		// SocketServer chubbyer2=new SocketServer(10002);
		// chubbyer2.monitor();
	}
}

/*
 * 参加心跳测试的类
 */
class HeartBeat extends Thread {
	
	public String info;
	public HeartBeat(String info) {
		// TODO Auto-generated constructor stub
		this.info=info;
	}
	@Override
	public void run() {
		try {			
			while (true) {
				String receiveByUDP = Net.receiveDataByUDP(ChubbyConfig.HEART_BEAT_PORT);
				if (receiveByUDP.equals(SC.HEART_BEAT)) {
					System.out.println("收到心跳测试请求");
					Socket socket = new Socket(ChubbyConfig.STATION_IP,
							ChubbyConfig.STATION_PORT);
					String data = SC.HEART_BEAT + this.info;
					Net.sentData(socket, data);// 发送表示请求连接的字段
					String returnStr = (String) Net.acceptData(socket);// 收到服务端的回应
					if (returnStr.equals(SC.HEART_BEAT)) {
						System.out.println("  心跳测试:向WorkStation报告本机状态成功");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
