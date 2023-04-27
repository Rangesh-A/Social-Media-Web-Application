package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.UserDao;
import Database.DbConnection;
import Services.LocalServices;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.util.HashMap;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		int user_id=((User)request.getSession().getAttribute("auth")).getUserId();
		String old_password=request.getParameter("old_password");
		String new_password=request.getParameter("new_password");
		
		int password_breach_count=LocalServices.isPasswordBreached(new_password);
		System.out.println("password_breach_count= "+password_breach_count);
		
		try {
			UserDao userdao=new UserDao(DbConnection.getConnection());
			if(password_breach_count==0&&userdao.changePassword(user_id,old_password,new_password)) {
				request.setAttribute("type","user");
				request.setAttribute("sub_type","updatepassword");
				request.setAttribute("userid",user_id);
				request.setAttribute("new_password",new_password);
				RequestDispatcher rd = request.getRequestDispatcher("/sync-to-chat");
				rd.include(request, response);
				request.setAttribute("success","Password Successfully changed");
			}else if(password_breach_count==0){
				request.setAttribute("error","Old password Wrong");
			}else {
				request.setAttribute("error","New password you entered is already "+password_breach_count+" times available in breached data");
			}
			RequestDispatcher rd=request.getRequestDispatcher("user-settings.jsp");
			rd.include(request, response);
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
