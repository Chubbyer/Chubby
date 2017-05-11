package View;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String optType=request.getParameter("oType").trim();
		String serCnondition=(String) request.getSession().getAttribute("serachCondition");
		if(optType.equals(EC.E_301)){
			//����EC.E_301,����Ӧ��չʾ�ڵ�3��ͼ���У�ɢ��ͼ��
		}
		System.out.println(optType);
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
		request.setCharacterEncoding("utf-8");// ʶ������
		response.setCharacterEncoding("utf-8");// ʶ������
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String optType=request.getParameter("oType").trim();
		System.out.println(optType);
		out.println("ssasad:doPost");
		out.flush();
		out.close();
	}

}
