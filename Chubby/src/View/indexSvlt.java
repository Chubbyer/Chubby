package View;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
/*
 * @Leung
 * ������ҳ���߼�����
 */
public class indexSvlt extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public indexSvlt() {
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
		request.getRequestDispatcher("index.jsp").forward(request,
				response);
	
	}

	/**
	 * The doPost method of the servlet. <br>
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//doGet(request, response);
		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		request.getRequestDispatcher("index.jsp").forward(request,
				response);
	}

}
