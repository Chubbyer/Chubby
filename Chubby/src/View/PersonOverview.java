package View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
			System.out.println("正在处理"+optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			SocketClient client = new SocketClient(serCnondition, EC.E_301_1);
			comp.submit(client);
			Future<Object> future;
			try {
				future = comp.take();
				@SuppressWarnings("unchecked")
				ArrayList<Chubbyer> chubbyers = (ArrayList<Chubbyer>) future.get();
				//把ArrayList转换成JSON
				String jsonStr=null;
				ArrayList<String> days=new ArrayList<String>();
				ArrayList<Double> points=new ArrayList<Double>();
				for (Chubbyer chubbyer : chubbyers) {
					days.add("\""+chubbyer.day+"\"");
					points.add(chubbyer.point);
				}
				jsonStr="{"+"\"days\""+":"+days+","+"\"points\""+":"+points+"}";
				//向前端发送JSON串
				out.println(jsonStr);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EC.E_301_1任务结果转换出错");
			}

		}
		if (optType.equals(EC.E_301_2)) {
			//处理EC.E_301_2,数据应该展示在第2个图表中（饼图）
			System.out.println("正在处理"+optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			SocketClient client = new SocketClient(serCnondition, EC.E_301_2);
			comp.submit(client);
			Future<Object> future;
			try {
				future = comp.take();
				@SuppressWarnings("unchecked")
				ArrayList<Chubbyer> chubbyers = (ArrayList<Chubbyer>) future.get();
				//把ArrayList转换成JSON
				String jsonStr=null;
				ArrayList<String> days=new ArrayList<String>();
				ArrayList<Double> points=new ArrayList<Double>();
				jsonStr="{"+"\"morning\""+":"+days+","+"\"afternoon\""+":"+points+","+"\"evening\""+":"+points+"}";
				//向前端发送JSON串
				out.println(jsonStr);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EC.E_301_2任务结果转换出错");
			}
		}
		if (optType.equals(EC.E_301_3)) {
			//处理EC.E_301_3,数据应该展示在第3个图表中（散点图）
			System.out.println("正在处理"+optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			SocketClient client = new SocketClient(serCnondition, EC.E_301_3);
			comp.submit(client);
			Future<Object> future;
			try {
				future = comp.take();
				@SuppressWarnings("unchecked")
				ArrayList<Chubbyer> chubbyers = (ArrayList<Chubbyer>) future.get();
				//把ArrayList转换成JSON
				String jsonStr=null;
				ArrayList<String> openPoints=new ArrayList<String>();
				ArrayList<String> closePoints=new ArrayList<String>();			
				for (int i = 0; i <chubbyers.size(); i++) {
					if(i<chubbyers.size()/2){
						//前半部分的数据是开机的节点
						openPoints.add(chubbyers.get(i).toString());
					}else {
						//后半部分的数据是关机的节点
						closePoints.add(chubbyers.get(i).toString());
					}
				}
				//{"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
				jsonStr="{"+"\"openPoints\""+":"+openPoints+","+"\"closePoints\""+":"+closePoints+"}";
				//向前端发送JSON串
				out.println(jsonStr);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EC.E_301_3任务结果转换出错");
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

}
