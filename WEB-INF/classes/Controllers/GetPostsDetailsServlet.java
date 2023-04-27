package Controllers;

import Model.User;
import Dao.UserDao;
import Dao.PostDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import Database.DbConnection;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import Model.Authenticator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/getpostdetails")
public class GetPostsDetailsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        PrintWriter out = response.getWriter();
        try {
            int userid=Integer.parseInt(request.getParameter("userid"));
            PostDao postdao=new PostDao(DbConnection.getConnection());
            JSONArray post_details=postdao.getAllPostsAsJSON(userid);
            out.println(post_details);
        }catch(Exception e){
            out.println("<script>alert('Error fetching Post details');history.back();</script>");
        }
    }
}
