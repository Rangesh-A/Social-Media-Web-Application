package Controllers;

import Model.User;
import Dao.UserDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import Database.DbConnection;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import Model.Authenticator;

@WebServlet("/SSOAdloginuser")
public class SSOADLoginServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String phone_email_username = request.getParameter("phone_email_username");
        String password = request.getParameter("password");
        System.out.println(phone_email_username+","+password);
        try {
                User user=new User();
                HttpSession session = request.getSession();
                UserDao userdao = new UserDao(DbConnection.getConnection());
                String email="";
                boolean valid=Authenticator.KerberosAuthenticate(phone_email_username,password);
                System.out.println(valid);
                if(valid){
                    email=phone_email_username;
                }else{
                    out.println("<script>alert('Invalid AD Login Credentials');history.back();</script>");
                    return;
                }
                System.out.println(email.trim().toLowerCase());
                user = userdao.getSSOAdUser(email.trim().toLowerCase());
                session.setAttribute("auth", user);
                System.out.println(user);
                response.sendRedirect("user-home.jsp");
        }catch(Exception e){
            // out.println("<script>alert('Invalid Login Credentials');history.back();</script>");
            out.println(e);
        }
    }
  
}
