package View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Control.SocketClient;
import Module.User;
import Util.MongoDBJDBC;

public class Person extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Person() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * The doPost method of the servlet. <br>
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");

		String serach = request.getParameter("search");
		
		System.out.println("Serach:" + serach);
		MongoDBJDBC mongoer = new MongoDBJDBC("User");
		User user = mongoer.findUserInfo(serach);
		if (user != null){
			//ת������չʾ��ҳ��
			request.getSession().setAttribute("serachCondition", serach);
			request.getRequestDispatcher("Person.jsp").forward(request,
					response);
		}else {
			//ת����ҳ����ʾ��������֮ƥ�����Ϣ
			request.setAttribute("errorInfo", "��������֮ƥ�����Ϣ");
			request.getRequestDispatcher("index.jsp").forward(request,
					response);
		}
	}
}
