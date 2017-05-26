package View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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

import Control.Analyzer;
import Control.ClientTasker;
import Module.Chubbyer;
import Module.OrderChubbyer;
import Protocol.EC;
import Util.ChubbyerParser;

public class Overview extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Overview() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 */
	@SuppressWarnings("unused")
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
		// 获得所有同学的平均PC使用时间并排序
		if (optType.equals(EC.E_302)) {
			int threadNum = 3;// 开3个线程，i表示线程序号
			for (int i = 0; i < threadNum; i++) {
				comp.submit(new ClientTasker(EC.E_302, i + 1));
			}
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = new ArrayList<String>();
			for (int i = 0; i < threadNum; i++) {
				try {
					// 获得已完成任务的子线程的结果,每个线程返回的是一批同学的PC平均使用时间
					// 结果是基于JSON格式的描述
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<String> chubbyer = (ArrayList<String>) future
							.get();
					chubbyerString.addAll(chubbyer);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (chubbyerString != null) {
				// 按使用时间排序并加工成Chubbyer对象的列表
				chubbyers = ChubbyerParser
						.sortChubbyersForRanking(chubbyerString);
				// 把ArrayList转换成JSON
				String jsonStr = null;
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<Double> hours = new ArrayList<Double>();
				for (Chubbyer chubbyer : chubbyers) {
					names.add("\"" + chubbyer.day + "\"");
					hours.add(chubbyer.point);
				}
				jsonStr = "{" + "\"names\"" + ":" + names + "," + "\"hours\""
						+ ":" + hours + "}";
				// 向前端发送JSON串
				out.println(jsonStr);
			}else {
				out.println("null");
			}
		}
		// 获得所有同学的开关机时点
		if (optType.equals(EC.E_303)) {
			int threadNum = 3;// 开3个线程，i表示线程序号
			for (int i = 0; i < threadNum; i++) {
				comp.submit(new ClientTasker(EC.E_303, i + 1));
			}
			ArrayList<String> openPoints = new ArrayList<String>();
			ArrayList<String> closePoints = new ArrayList<String>();
			for (int i = 0; i < threadNum; i++) {
				try {
					// 获得已完成任务的子线程的结果,每个线程返回的是一批同学的PC平均使用时间
					// 结果是基于JSON格式的描述
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<String> chubbyerString = (ArrayList<String>) future
							.get();
					for (int j = 0; j < chubbyerString.size(); j++) {
						if (i < chubbyerString.size() / 2) {
							// 前半部分的数据是开机的节点
							openPoints.add(chubbyerString.get(i));
						} else {
							// 后半部分的数据是关机的节点
							closePoints.add(chubbyerString.get(i));
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 把ArrayList转换成JSON
			String jsonStr = null;
			jsonStr = "{" + "\"openPoints\":" + openPoints + ","
					+ "\"closePoints\":" + closePoints + "}";
			// 向前端发送JSON串
			out.println(jsonStr);
		}
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		out.close();
	}

}
