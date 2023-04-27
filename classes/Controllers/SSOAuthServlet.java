package Controllers;

import Dao.PostDao;
import Database.DbConnection;
import Model.Post;
import Model.User;
import Services.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import Dao.UserDao;
import java.util.Base64;
import javax.security.auth.login.LoginException;
import org.apache.catalina.realm.GenericPrincipal;
@WebServlet("/sso-auth")
public class SSOAuthServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String remoteuser=request.getRemoteUser();
		// PrintWriter out = response.getWriter();
		// response.setContentType("text/html");
		// String auth = request.getHeader("Authorization");
		System.out.println("cl= "+remoteuser);
		String clientName = "";
		// boolean clockflag=true;
		// 	if (auth == null) {
		// 		response.reset();
		// 		response.setHeader("WWW-Authenticate", "NEGOTIATE");
		// 		response.setContentLength(0);
		// 		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// 		response.flushBuffer();
		// 		return;
		// 	} else {
		// 		try{
		// 			clientName = Kerberos.authenticate(auth,"");
		// 		}catch(Throwable l){
		// 			Throwable cause = l.getCause();
		// 			System.out.println(cause);
		// 			if (cause != null) {
		// 				if(cause.getClass().getName().equals("javax.security.auth.login.LoginException")){
		// 					clockflag=false;
		// 				}
		// 			}
		// 		}
		// 	}
		// 	if(!clockflag){
		// 		out.print("<script>alert('Clock not Synchronized!!!');location.href='SSOAdLogin.jsp';</script>");
		// 		return;
		// 	}
		 if(remoteuser==null){
			response.sendRedirect("SSOAdLogin.jsp");
		 }else{
			// System.out.println(request.getUserPrincipal().getName());
			clientName=remoteuser;
			try{
				User user=new User();
                HttpSession session = request.getSession();
                UserDao userdao = new UserDao(DbConnection.getConnection());
                String email=clientName.substring(clientName.indexOf("\\")+1)+"@zohor.com";
				System.out.println(email);
                user = userdao.getSSOAdUser(email.trim().toLowerCase());
                session.setAttribute("auth", user);
                System.out.println(user);
                response.sendRedirect("user-home.jsp");
			}catch(Exception e){}
		 }
	}
}
