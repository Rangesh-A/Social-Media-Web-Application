package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.RequestDao;
import Database.DbConnection;
import Model.FriendRequest;
import Model.User;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;


@WebServlet("/send-friend-request")
public class SendFriendRequestServlet extends HttpServlet {
public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		FriendRequest frnd_request=new FriendRequest();
		frnd_request.setSenderId(((User)request.getSession().getAttribute("auth")).getUserId());
		frnd_request.setReceiverId(Integer.parseInt(request.getParameter("receiverid")));
		frnd_request.setTime(LocalServices.getCurrentDateTime());
		frnd_request.setIsReacted(false);
		try {
			RequestDao requestDao=new RequestDao(DbConnection.getConnection());
			requestDao.insertRequest(frnd_request);
			out.print("<script> window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
