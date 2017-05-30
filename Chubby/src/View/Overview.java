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

import Control.ClientTasker;
import Module.Chubbyer;
import Protocol.EC;
import Util.ChubbyerParser;
import Util.TimeOut;
import Util.Timing;

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

		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		// ���ڴ����߳�������̳߳�
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		PrintWriter out = response.getWriter();
		String optType = request.getParameter("oType").trim();
		// �������ͬѧ��ƽ��PCʹ��ʱ�䲢����
		if (optType.equals(EC.E_302)) {
			int threadNum = 3;// ��3���̣߳�i��ʾ�߳����
			for (int i = 0; i < threadNum; i++) {
				comp.submit(new ClientTasker(EC.E_302, i + 1));
			}
			ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
			ArrayList<String> chubbyerString = new ArrayList<String>();
			TimeOutHandle timeOutHandle = new TimeOutHandle(out, 15 * 1000);
			timeOutHandle.startTiming();
			for (int i = 0; i < threadNum; i++) {
				try {
					// ����������������̵߳Ľ��,ÿ���̷߳��ص���һ��ͬѧ��PCƽ��ʹ��ʱ��
					// ����ǻ���JSON��ʽ������
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
			timeOutHandle.closeTiming();
			if (chubbyerString != null) {
				// ��ʹ��ʱ�����򲢼ӹ���Chubbyer������б�
				chubbyers = ChubbyerParser
						.sortChubbyersForRanking(chubbyerString);
				// ��ArrayListת����JSON
				String jsonStr = null;
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<Double> hours = new ArrayList<Double>();
				for (Chubbyer chubbyer : chubbyers) {
					names.add("\"" + chubbyer.day + "\"");
					hours.add(chubbyer.point);
				}
				jsonStr = "{" + "\"names\"" + ":" + names + "," + "\"hours\""
						+ ":" + hours + "}";
				// ��ǰ�˷���JSON��
				out.println(jsonStr);
			} else {
				out.println("null");
			}
		}
		// �������ͬѧ�Ŀ��ػ�ʱ��
		if (optType.equals(EC.E_303)) {
			int threadNum = 3;// ��3���̣߳�i��ʾ�߳����
			for (int i = 0; i < threadNum; i++) {
				comp.submit(new ClientTasker(EC.E_303, i + 1));
			}
			ArrayList<String> openPoints = new ArrayList<String>();
			ArrayList<String> closePoints = new ArrayList<String>();
			TimeOutHandle timeOutHandle = new TimeOutHandle(out, 15 * 1000);
			timeOutHandle.startTiming();
			for (int i = 0; i < threadNum; i++) {
				try {
					// ����������������̵߳Ľ��,ÿ���̷߳��ص���һ��ͬѧ��PCƽ��ʹ��ʱ��
					// ����ǻ���JSON��ʽ������
					Future<Object> future = comp.take();
					@SuppressWarnings("unchecked")
					ArrayList<String> chubbyerString = (ArrayList<String>) future
							.get();
					timeOutHandle.closeTiming();
					for (int j = 0; j < chubbyerString.size(); j++) {
						if (i < chubbyerString.size() / 2) {
							// ǰ�벿�ֵ������ǿ����Ľڵ�
							openPoints.add(chubbyerString.get(i));
						} else {
							// ��벿�ֵ������ǹػ��Ľڵ�
							closePoints.add(chubbyerString.get(i));
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// ��ArrayListת����JSON
			if (openPoints.size() > 0 && closePoints.size() > 0) {
				String jsonStr = null;
				jsonStr = "{" + "\"openPoints\":" + openPoints + ","
						+ "\"closePoints\":" + closePoints + "}";
				// ��ǰ�˷���JSON��
				out.println(jsonStr);
			}else
				out.println("null");
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
