package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.UserDao;
import Database.DbConnection;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;


@WebServlet("/deactivate-account")
public class DeactivateAccountServlet extends HttpServlet {
public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		int user_id=((User)request.getSession().getAttribute("auth")).getUserId();
		try {
			UserDao userdao=new UserDao(DbConnection.getConnection());
			userdao.deleteUser(user_id);
			request.getSession().invalidate();
			out.print("<script>alert('Account Successfully deactivated');location.href='index.jsp';</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
