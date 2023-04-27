package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.RequestDao;
import Database.DbConnection;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;


@WebServlet("/cancel-friend-request")
public class CancelFriendRequestServlet extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		int senderid=((User)request.getSession().getAttribute("auth")).getUserId();
		int receiverid=(Integer.parseInt(request.getParameter("receiverid")));
		try {
			RequestDao requestDao=new RequestDao(DbConnection.getConnection());
			requestDao.deleteRequest(senderid,receiverid);
			out.print("<script>window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
