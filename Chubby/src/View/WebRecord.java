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
import Module.User;
import Module.WebAnalyzer;
import Protocol.EC;
import Util.ChubbyConfig;
import Util.ChubbyerParser;
import Util.MongoDBJDBC;

public class WebRecord extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WebRecord() {
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
		PrintWriter out = response.getWriter();
		String serCnondition = (String) request.getSession().getAttribute(
				"serachCondition");
		String optType = request.getParameter("oType").trim();
		System.out.println(optType);
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		User user = mongoer.findUserInfo(serCnondition);
		mongoer = new MongoDBJDBC(user.getHost());
		if(optType.equals("test")&&user.getWeb_Flag()){
			out.println("{\"status\":1}");			
		}
		if(optType.equals("test")&&!user.getWeb_Flag()){
			out.println("{\"status\":2}");//System.out.println("未找到相关用户的数据");
		}
		if (user.getWeb_Flag()) {
			// 上网时间
			if (optType.equals(EC.E_401_1)) {
				String jsonStr = "{\"days\":[],\"points\":[]}";
				ArrayList<Chubbyer> chubbyers = mongoer.findWebNodes(
						"WebOnline", "Day", "Usetime");
				chubbyers = WebAnalyzer.supplementChubbyers(chubbyers);
				// 把ArrayList转换成JSON
				ArrayList<String> days = new ArrayList<String>();
				ArrayList<Double> points = new ArrayList<Double>();
				for (Chubbyer chubbyer : chubbyers) {
					days.add("\"" + chubbyer.day + "\"");
					points.add(Math.round(chubbyer.point/60*10)/10.0);
				}
				jsonStr = "{\"days\":" + days + "," + "\"points\":" + points
						+ "}";
				out.println(jsonStr);
			}
			// 浏览器使用情况
			if (optType.equals(EC.E_401_2)) {
				String jsonStr = "{\"browserName\":[],\"visit_count\":[]}";
				ArrayList<Chubbyer> chubbyers = mongoer.findWebNodes(
						"WebBrowser", "Browser", "Visit_count");
				ArrayList<String> browserName = new ArrayList<String>();
				ArrayList<Double> visit_count = new ArrayList<Double>();

				for (Chubbyer chubbyer : chubbyers) {
					browserName.add("\"" + chubbyer.day + "\"");
					visit_count.add(chubbyer.point);
				}
				jsonStr = "{\"browserName\":" + browserName + ","
						+ "\"visit_count\":" + visit_count + "}";
				out.println(jsonStr);
			}
			// 网站类型
			if (optType.equals(EC.E_401_3)) {
				String jsonStr = "{\"Sites\":[],\"Counts\":[]}";
				ArrayList<Chubbyer> chubbyers = mongoer.findWebNodes(
						"WebNode1", "Site", "Counts");
				ArrayList<String> sites = new ArrayList<String>();
				ArrayList<Double> counts = new ArrayList<Double>();
				int countSum = 0, priorityCount = 0;
				for (int i = 0; i < chubbyers.size(); i++) {
					countSum += chubbyers.get(i).point;
					if (i < 20) {
						sites.add("\"" + chubbyers.get(i).day + "\"");
						counts.add(chubbyers.get(i).point);
						priorityCount += chubbyers.get(i).point;
					}
				}
				sites.add("\"其他\"");
				counts.add((double) (countSum - priorityCount));
				jsonStr = "{\"Sites\":" + sites + "," + "\"Counts\":" + counts
						+ "}";
				out.println(jsonStr);
			}
			// 网站类型
			if (optType.equals(EC.E_401_4)) {
				String jsonStr = "{\"Sites\":[],\"Counts\":[]}";
				ArrayList<Chubbyer> chubbyers = mongoer.findWebNodes(
						"WebNode2", "Site", "Counts");
				ArrayList<String> sites = new ArrayList<String>();
				ArrayList<Double> counts = new ArrayList<Double>();
				int countSum = 0, priorityCount = 0;
				for (int i = 0; i < chubbyers.size(); i++) {
					countSum += chubbyers.get(i).point;
					if (i < 20) {
						sites.add("\"" + chubbyers.get(i).day + "\"");
						counts.add(chubbyers.get(i).point);
						priorityCount += chubbyers.get(i).point;
					}
				}
				sites.add("\"其他\"");
				counts.add((double) (countSum - priorityCount));
				jsonStr = "{\"Sites\":" + sites + "," + "\"Counts\":" + counts
						+ "}";
				out.println(jsonStr);
			}
		}
		
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

}
