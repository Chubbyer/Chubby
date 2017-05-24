package Control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import Module.Chubbyer;
import Module.Host;
import Module.OrderChubbyer;
import Module.User;
import Protocol.EC;
import Protocol.SC;
import Util.MongoDBJDBC;
import Util.Net;

/*
 * @Leung
 * 网络处理的服务端，响应客服机的数据请求，返回数据处理结果
 */
public class SocketServer {
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private String status = SC.SERVER_OK;
	private int maxLinks = 10;// 最大可接受的连接数
	private ArrayList<FutureTask> futureTasks = new ArrayList<FutureTask>();
	// 监听端口号
	private int port = 10001;

	public SocketServer(int port) {
		this.port = port;
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
			System.out.println("服务器已就绪");
			while (true) {
				// 获得连接
				Socket accpetSocket = new Socket();
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
				int alreadyLinks = 10 - this.maxLinks;
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
	 * 发送或接受数据
	 */
	public void action(Socket accpetSocket) {
		Object receiveData = null;
		String sendData;

		// 接收客户端发送内容
		receiveData = Net.acceptData(accpetSocket);
		System.out.println("端口：" + this.port + " 收到：" + receiveData);
		// 解析出客户端请求的类型
		String oType = receiveData.toString().substring(0, 3);
		// System.out.println(oType);
		if (oType.equals(SC.CHECK_CONNECTION)) {
			// 客户端请求连接，发送本服务器的状态
			Net.sentData(accpetSocket, this.status);
			return;
		}
		// 所有的任务都从这里转发
		if (oType.equals(EC.E_301) && this.status.equals(SC.SERVER_OK)) {
			// 附加的数据
			String additional = receiveData.toString().substring(3);
			// 将本机的状态置为忙
			this.status = SC.SERVER_BUSY;
			// 启动EC.E_301所描述的任务
			// Tasker taskE_301 = new Tasker(additional);
			// FutureTask<Object> futureTask=new FutureTask<Object>(taskE_301);
			// new Thread(futureTask).start();
			ArrayList<String> al = new ArrayList<String>();
			al.add("First");
			al.add("Second");// this.status
			Net.sentData(accpetSocket, al);
		}
	}

	/*
	 * 检查已就绪的处理机
	 */
	public int checkChubbyer() {

		return 0;
	}

	public static void main(String[] args) {
		SocketServer chubbyer = new SocketServer(10001);
		chubbyer.monitor();
		// SocketServer chubbyer1=new SocketServer(10001);
		// chubbyer1.monitor();
		// SocketServer chubbyer2=new SocketServer(10002);
		// chubbyer2.monitor();
	}
}

class Session {
	public Tasker tasker;
	public FutureTask<Object> futureTask;

	public Session(Tasker tasker, FutureTask<Object> futureTask) {
		// TODO Auto-generated constructor stub

	}
}
