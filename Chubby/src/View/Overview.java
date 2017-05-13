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
		if(optType.equals(EC.E_302)){
			int threadNum = 3;// 开3个线程
			for (int i = 0; i < threadNum; i++) {
				comp.submit(new ClientTasker(EC.E_302, i));
			}
			ArrayList<Chubbyer> chubbyers=new ArrayList<Chubbyer>();
			ArrayList<String> chubbyersString=new ArrayList<String>();
			for (int i = 0; i < threadNum; i++) {
				try {
					// 获得已完成任务的子线程的结果
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<String> chubbyer = (ArrayList<String>) future
							.get();
					chubbyersString.addAll(chubbyer);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
