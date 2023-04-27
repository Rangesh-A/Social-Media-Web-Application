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

@WebServlet("/Adloginuser")
public class ADLoginServlet extends HttpServlet {


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        PrintWriter out = response.getWriter();
        String phone_email_username = request.getParameter("phone_email_username");
        String password = request.getParameter("password");


        // env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        // env.put(Context.PROVIDER_URL, "ldap://192.168.0.102:389");  
        // // env.put(Context.PROVIDER_URL, "ldap://192.168.10.161:389");
        // env.put(Context.SECURITY_PRINCIPAL, "CN="+phone_email_username+",OU=Zoho,DC=zohor,DC=com");
        // // env.put(Context.SECURITY_PRINCIPAL, "CN=sas,O=Zoho,DC=zohor,DC=com");
        // env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // DirContext ctx = new InitialDirContext(env);
            //  String searchFilter = "(&(objectClass=user)(sAMAccountName="+phone_email_username+"))";
            // String[] requiredAttributes = {"cn", "mail","telephoneNumber"};
            // SearchControls controls = new SearchControls();
            // controls.setReturningAttributes(requiredAttributes);
            // controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // NamingEnumeration<SearchResult> results = ctx.search("CN="+phone_email_username+",OU=Zoho,DC=zohor,DC=com", searchFilter, controls);
            // if (results.hasMore()) {
                // SearchResult searchResult = (SearchResult) results.next();
                // Attributes attributes = searchResult.getAttributes();
                // String name=attributeToString(attributes.get("cn"));
                // String email=attributeToString(attributes.get("mail"));
                // String phone=attributeToString(attributes.get("telephoneNumber"));
                User user=new User();
                HttpSession session = request.getSession();
                UserDao userdao = new UserDao(DbConnection.getConnection());
                // email=email.trim();
                // System.out.println("--"+email+"--");
                String email="";
                boolean valid=Authenticator.authenticate(phone_email_username,password);
                System.out.println(valid);
                if(valid){
                    email=phone_email_username;
                }else{
                    out.println("<script>alert('Invalid AD Login Credentialss');history.back();</script>");
                    return;
                }
                user = userdao.getAdUser(email.trim());
                session.setAttribute("auth", user);
                System.out.println(user);
                response.sendRedirect("user-home.jsp");
            // }
        }catch(Exception e){
            e.printStackTrace();
            out.println("<script>alert('Invalid Login Credentials');history.back();</script>");
            // out.println(e);
        }
    }
    public static String attributeToString(Attribute att)throws Exception{
         if(att != null){
                    NamingEnumeration attributeValues = att.getAll();
                    StringBuilder sb = new StringBuilder();
                    while(attributeValues.hasMore()){
                        sb.append(attributeValues.next().toString() + "\n");
                    }
                    String attributeValue = sb.toString();
                    return attributeValue;
                }
            return null;
    } 
}
