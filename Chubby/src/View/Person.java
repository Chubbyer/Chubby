package View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Control.SocketClient;

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
		response.setContentType("text/html");
		
		String serach = request.getParameter("search");
		request.getSession().setAttribute("serachCondition", serach);
		System.out.println("Serach:"+serach);
//		SocketClient sClient = new SocketClient();
//		// System.out.println(sClient.checkConnection());
//		@SuppressWarnings("unchecked")
//		ArrayList<String> chubbyers = (ArrayList<String>) sClient
//				.getOneOverview("Leung");
//		System.out.println(chubbyers.size());
//		for (int i = 0; i < 50; i++) {
//			System.out.println(chubbyers.get(i));
//		}
//		System.out.println("Index");
		request.getRequestDispatcher("Person.jsp").forward(request, response);
	}
}
