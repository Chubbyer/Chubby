package View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Control.SocketClient;
import Module.Chubbyer;
import Protocol.EC;
import Util.ChubbyerParser;
import Util.Timing;

public class PersonOverview extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public PersonOverview() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");// 识别中文
		response.setCharacterEncoding("utf-8");// 识别中文
		response.setContentType("text/html");
		// 用于处理线程任务的线程池
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		PrintWriter out = response.getWriter();
		String optType = request.getParameter("oType").trim();

		if (optType.equals(EC.E_301_1)) {
			// 处理EC.E_301_1,数据应该展示在第1个图表中（柱状图）
			System.out.println("正在处理" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
//			TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//			timeOutHandle.start();// 启动倒计时
			SocketClient client = new SocketClient(serCnondition, EC.E_301);
			comp.submit(client);
			Future<Object> future;
			String jsonStr = "{\"days\":[],\"points\":[]}";
			try {
				future = comp.take();
				ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
				@SuppressWarnings("unchecked")
				ArrayList<String> chubbyerStrings = (ArrayList<String>) future
						.get();
				// chubbyerStrings是三个图表的数据基础
				if (chubbyerStrings != null) {
//					timeOutHandle.closeTiming();// 取消倒计时
					request.getSession().setAttribute("chubbyerStrings",
							chubbyerStrings);
					// 加工getOneOverview函数的结果，方便在页面上展示EC-301_1任务的结果,得到每天使用多少小时

					chubbyers = ChubbyerParser.getUseTime(chubbyerStrings);
					chubbyers = ChubbyerParser.remoneRepChubbyers(chubbyers);
					chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
					// 把ArrayList转换成JSON
					ArrayList<String> days = new ArrayList<String>();
					ArrayList<Double> points = new ArrayList<Double>();
					
					for (Chubbyer chubbyer : chubbyers) {
						days.add("\"" + chubbyer.day + "\"");
						points.add(chubbyer.point);
					}
					//System.out.println(points);
					jsonStr = "{\"days\":" + days + "," + "\"points\":"
							+ points + "}";
					// 向前端发送JSON串
					// out.println(jsonStr);
					System.out.println(optType + "处理完毕");
				} else {
					System.out.println(optType + "未获得数据");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EC.E_301_1任务结果转换出错");
			} finally {
				System.out.println("已反馈");
				out.println(jsonStr);
			}
		}
		if (optType.equals(EC.E_301_2)) {
			// 处理EC.E_301_2,数据应该展示在第2个图表中（饼图）
			System.out.println("正在处理" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			String jsonStr = "{\"morning\":0,\"afternoon\":0,\"evening\":0}";
			try {
				@SuppressWarnings("unchecked")
				ArrayList<String> chubbyerStrings = (ArrayList<String>) request
						.getSession().getAttribute("chubbyerStrings");
				ArrayList<Double> timeDistribut = ChubbyerParser
						.getUseHoursDistribut(chubbyerStrings);
				// 转换成JSON
				jsonStr = "{\"morning\":" + timeDistribut.get(0) + ","
						+ "\"afternoon\":" + timeDistribut.get(1) + ","
						+ "\"evening\":" + timeDistribut.get(2) + "}";
				// 向前端发送JSON串
				// out.println(jsonStr);
				System.out.println(optType + "处理完毕");
			} catch (NullPointerException e) {
				// TODO: handle exception
				// 当session为空的时候
//				TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//				timeOutHandle.start();// 启动倒计时
				String result = this.handEC_301_2(serCnondition, comp);
				if (result != null) {
//					timeOutHandle.closeTiming();
					jsonStr=result;
				}
			} finally {
				System.out.println("已反馈");
				out.println(jsonStr);
			}
		}
		if (optType.equals(EC.E_301_3)) {
			// 处理EC.E_301_3,数据应该展示在第3个图表中（散点图）
			System.out.println("正在处理" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			String jsonStr = "{\"openPoints\":[],\"closePoints\":[]}";
			;
			try {
				ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
				@SuppressWarnings("unchecked")
				ArrayList<String> chubbyerString = (ArrayList<String>) request
						.getSession().getAttribute("chubbyerStrings");
				// 加工getOneOverview函数的结果，方便在页面上展示EC-301_3任务的结果,得到开关机时间点
				chubbyers = ChubbyerParser.getUseTimeScatter(chubbyerString);
				// 把ArrayList转换成JSON
				ArrayList<String> openPoints = new ArrayList<String>();
				ArrayList<String> closePoints = new ArrayList<String>();
				for (int i = 0; i < chubbyers.size(); i++) {
					if (i < chubbyers.size() / 2) {
						// 前半部分的数据是开机的节点
						openPoints.add(chubbyers.get(i).toString());
					} else {
						// 后半部分的数据是关机的节点
						closePoints.add(chubbyers.get(i).toString());
					}
				}
				// {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
				jsonStr = "{\"openPoints\":" + openPoints + ",\"closePoints\":"
						+ closePoints + "}";
				// 向前端发送JSON串
				//out.println(jsonStr);
				System.out.println(optType + "处理完毕");
				// System.out.println(jsonStr);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				// 当session为空的时候
//				TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//				timeOutHandle.start();// 启动倒计时
				String result = this.handEC_301_3(serCnondition, comp);
				if (result != null) {
//					timeOutHandle.closeTiming();
					jsonStr = result;
				}
			} finally {
				System.out.println("已反馈");
				out.println(jsonStr);
			}
		}
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");// 识别中文
		response.setCharacterEncoding("utf-8");// 识别中文
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String optType = request.getParameter("oType").trim();
		System.out.println(optType);
		out.println("ssasad:doPost");
		out.flush();
		out.close();
	}

	/*
	 * 重新处理任务
	 */
	@SuppressWarnings({ "null", "unchecked" })
	public String handEC_301_2(String ser, CompletionService<Object> comp) {
		System.out.println("正在重新处理EC_301_2");
		String serCnondition = ser;
		SocketClient client = new SocketClient(serCnondition, EC.E_301_2);
		comp.submit(client);
		Future<Object> future = null;
		try {
			// future = comp.take();
			ArrayList<Double> timeDistribut;
			timeDistribut = (ArrayList<Double>) future.get();
			// 转换成JSON
			String jsonStr = null;
			jsonStr = "{\"morning\":" + timeDistribut.get(0) + ","
					+ "\"afternoon\":" + timeDistribut.get(1) + ","
					+ "\"evening\":" + timeDistribut.get(2) + "}";
			System.out.println("EC_301_2重新处理完毕");
			return jsonStr;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("EC.E_301_2任务结果转换出错");
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/*
	 * 重新处理任务
	 */
	public String handEC_301_3(String ser, CompletionService<Object> comp) {
		System.out.println("正在重新处理EC_301_3");
		String serCnondition = ser;
		SocketClient client = new SocketClient(serCnondition, EC.E_301_3);
		comp.submit(client);
		Future<Object> future;
		try {
			future = comp.take();
			@SuppressWarnings("unchecked")
			ArrayList<Chubbyer> chubbyers = (ArrayList<Chubbyer>) future.get();
			// 把ArrayList转换成JSON
			String jsonStr = null;
			ArrayList<String> openPoints = new ArrayList<String>();
			ArrayList<String> closePoints = new ArrayList<String>();
			for (int i = 0; i < chubbyers.size(); i++) {
				if (i < chubbyers.size() / 2) {
					// 前半部分的数据是开机的节点
					openPoints.add(chubbyers.get(i).toString());
				} else {
					// 后半部分的数据是关机的节点
					closePoints.add(chubbyers.get(i).toString());
				}
			}
			// {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
			jsonStr = "{\"openPoints\":" + openPoints + ",\"closePoints\":"
					+ closePoints + "}";
			// 向前端发送JSON串
			System.out.println("EC_301_3重新处理完毕");
			return jsonStr;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("EC.E_301_3任务结果转换出错");
		}catch (Exception e) {
			return null;
		}
		return null;
	}
}

/*
 * 如果超过指定的时间还没获得结果将自动向前端输出空
 */
class TimeOutHandle extends Thread {
	public Timing timing;
	public long delay;// 倒计时时间
	public boolean timingFlag = true;
	public PrintWriter out;

	public TimeOutHandle(PrintWriter out, long delay) {
		// TODO Auto-generated constructor stub
		this.delay = delay;
		this.out = out;
	}

	public void closeTiming() {
		timingFlag = false;
	}

	public void run() {
		long start = System.currentTimeMillis();
		System.out.println("计时开始");
		while (true) {
			if ((System.currentTimeMillis() - start) > delay) {
				if (timingFlag) {
					System.out.println("请求超时！！！");
					out.println("null");
					out.flush();
					break;
					// out.close();
				}
			}
		}
	}

}
