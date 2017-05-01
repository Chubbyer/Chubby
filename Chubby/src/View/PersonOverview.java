package View;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		PrintWriter out = response.getWriter();
		out.println("ssasad");
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

		response.setContentType("text/html");
		String serach = request.getParameter("search");
		System.out.println(serach);
		request.getRequestDispatcher("Person.jsp").forward(request, response);
	}

}
