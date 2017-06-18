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

		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		// ���ڴ����߳�������̳߳�
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Object> comp = new ExecutorCompletionService<>(
				executor);
		PrintWriter out = response.getWriter();
		String optType = request.getParameter("oType").trim();

		if (optType.equals(EC.E_301_1)) {
			// ����EC.E_301_1,����Ӧ��չʾ�ڵ�1��ͼ���У���״ͼ��
			System.out.println("���ڴ���" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
//			TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//			timeOutHandle.start();// ��������ʱ
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
				// chubbyerStrings������ͼ������ݻ���
				if (chubbyerStrings != null) {
//					timeOutHandle.closeTiming();// ȡ������ʱ
					request.getSession().setAttribute("chubbyerStrings",
							chubbyerStrings);
					// �ӹ�getOneOverview�����Ľ����������ҳ����չʾEC-301_1����Ľ��,�õ�ÿ��ʹ�ö���Сʱ

					chubbyers = ChubbyerParser.getUseTime(chubbyerStrings);
					chubbyers = ChubbyerParser.remoneRepChubbyers(chubbyers);
					chubbyers = ChubbyerParser.supplementChubbyers(chubbyers);
					// ��ArrayListת����JSON
					ArrayList<String> days = new ArrayList<String>();
					ArrayList<Double> points = new ArrayList<Double>();
					
					for (Chubbyer chubbyer : chubbyers) {
						days.add("\"" + chubbyer.day + "\"");
						points.add(chubbyer.point);
					}
					//System.out.println(points);
					jsonStr = "{\"days\":" + days + "," + "\"points\":"
							+ points + "}";
					// ��ǰ�˷���JSON��
					// out.println(jsonStr);
					System.out.println(optType + "�������");
				} else {
					System.out.println(optType + "δ�������");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EC.E_301_1������ת������");
			} finally {
				System.out.println("�ѷ���");
				out.println(jsonStr);
			}
		}
		if (optType.equals(EC.E_301_2)) {
			// ����EC.E_301_2,����Ӧ��չʾ�ڵ�2��ͼ���У���ͼ��
			System.out.println("���ڴ���" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			String jsonStr = "{\"morning\":0,\"afternoon\":0,\"evening\":0}";
			try {
				@SuppressWarnings("unchecked")
				ArrayList<String> chubbyerStrings = (ArrayList<String>) request
						.getSession().getAttribute("chubbyerStrings");
				ArrayList<Double> timeDistribut = ChubbyerParser
						.getUseHoursDistribut(chubbyerStrings);
				// ת����JSON
				jsonStr = "{\"morning\":" + timeDistribut.get(0) + ","
						+ "\"afternoon\":" + timeDistribut.get(1) + ","
						+ "\"evening\":" + timeDistribut.get(2) + "}";
				// ��ǰ�˷���JSON��
				// out.println(jsonStr);
				System.out.println(optType + "�������");
			} catch (NullPointerException e) {
				// TODO: handle exception
				// ��sessionΪ�յ�ʱ��
//				TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//				timeOutHandle.start();// ��������ʱ
				String result = this.handEC_301_2(serCnondition, comp);
				if (result != null) {
//					timeOutHandle.closeTiming();
					jsonStr=result;
				}
			} finally {
				System.out.println("�ѷ���");
				out.println(jsonStr);
			}
		}
		if (optType.equals(EC.E_301_3)) {
			// ����EC.E_301_3,����Ӧ��չʾ�ڵ�3��ͼ���У�ɢ��ͼ��
			System.out.println("���ڴ���" + optType);
			String serCnondition = (String) request.getSession().getAttribute(
					"serachCondition");
			String jsonStr = "{\"openPoints\":[],\"closePoints\":[]}";
			;
			try {
				ArrayList<Chubbyer> chubbyers = new ArrayList<Chubbyer>();
				@SuppressWarnings("unchecked")
				ArrayList<String> chubbyerString = (ArrayList<String>) request
						.getSession().getAttribute("chubbyerStrings");
				// �ӹ�getOneOverview�����Ľ����������ҳ����չʾEC-301_3����Ľ��,�õ����ػ�ʱ���
				chubbyers = ChubbyerParser.getUseTimeScatter(chubbyerString);
				// ��ArrayListת����JSON
				ArrayList<String> openPoints = new ArrayList<String>();
				ArrayList<String> closePoints = new ArrayList<String>();
				for (int i = 0; i < chubbyers.size(); i++) {
					if (i < chubbyers.size() / 2) {
						// ǰ�벿�ֵ������ǿ����Ľڵ�
						openPoints.add(chubbyers.get(i).toString());
					} else {
						// ��벿�ֵ������ǹػ��Ľڵ�
						closePoints.add(chubbyers.get(i).toString());
					}
				}
				// {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
				jsonStr = "{\"openPoints\":" + openPoints + ",\"closePoints\":"
						+ closePoints + "}";
				// ��ǰ�˷���JSON��
				//out.println(jsonStr);
				System.out.println(optType + "�������");
				// System.out.println(jsonStr);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				// ��sessionΪ�յ�ʱ��
//				TimeOutHandle timeOutHandle = new TimeOutHandle(out, 10 * 1000);
//				timeOutHandle.start();// ��������ʱ
				String result = this.handEC_301_3(serCnondition, comp);
				if (result != null) {
//					timeOutHandle.closeTiming();
					jsonStr = result;
				}
			} finally {
				System.out.println("�ѷ���");
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
		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String optType = request.getParameter("oType").trim();
		System.out.println(optType);
		out.println("ssasad:doPost");
		out.flush();
		out.close();
	}

	/*
	 * ���´�������
	 */
	@SuppressWarnings({ "null", "unchecked" })
	public String handEC_301_2(String ser, CompletionService<Object> comp) {
		System.out.println("�������´���EC_301_2");
		String serCnondition = ser;
		SocketClient client = new SocketClient(serCnondition, EC.E_301_2);
		comp.submit(client);
		Future<Object> future = null;
		try {
			// future = comp.take();
			ArrayList<Double> timeDistribut;
			timeDistribut = (ArrayList<Double>) future.get();
			// ת����JSON
			String jsonStr = null;
			jsonStr = "{\"morning\":" + timeDistribut.get(0) + ","
					+ "\"afternoon\":" + timeDistribut.get(1) + ","
					+ "\"evening\":" + timeDistribut.get(2) + "}";
			System.out.println("EC_301_2���´������");
			return jsonStr;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("EC.E_301_2������ת������");
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/*
	 * ���´�������
	 */
	public String handEC_301_3(String ser, CompletionService<Object> comp) {
		System.out.println("�������´���EC_301_3");
		String serCnondition = ser;
		SocketClient client = new SocketClient(serCnondition, EC.E_301_3);
		comp.submit(client);
		Future<Object> future;
		try {
			future = comp.take();
			@SuppressWarnings("unchecked")
			ArrayList<Chubbyer> chubbyers = (ArrayList<Chubbyer>) future.get();
			// ��ArrayListת����JSON
			String jsonStr = null;
			ArrayList<String> openPoints = new ArrayList<String>();
			ArrayList<String> closePoints = new ArrayList<String>();
			for (int i = 0; i < chubbyers.size(); i++) {
				if (i < chubbyers.size() / 2) {
					// ǰ�벿�ֵ������ǿ����Ľڵ�
					openPoints.add(chubbyers.get(i).toString());
				} else {
					// ��벿�ֵ������ǹػ��Ľڵ�
					closePoints.add(chubbyers.get(i).toString());
				}
			}
			// {"openPoints":[['2017/05/11',11.2],['2017/05/11',11.2]],"closePoints":[['2017/05/11',12.2],['2017/05/11',13.2]]}
			jsonStr = "{\"openPoints\":" + openPoints + ",\"closePoints\":"
					+ closePoints + "}";
			// ��ǰ�˷���JSON��
			System.out.println("EC_301_3���´������");
			return jsonStr;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("EC.E_301_3������ת������");
		}catch (Exception e) {
			return null;
		}
		return null;
	}
}

/*
 * �������ָ����ʱ�仹û��ý�����Զ���ǰ�������
 */
class TimeOutHandle extends Thread {
	public Timing timing;
	public long delay;// ����ʱʱ��
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
		System.out.println("��ʱ��ʼ");
		while (true) {
			if ((System.currentTimeMillis() - start) > delay) {
				if (timingFlag) {
					System.out.println("����ʱ������");
					out.println("null");
					out.flush();
					break;
					// out.close();
				}
			}
		}
	}

}
