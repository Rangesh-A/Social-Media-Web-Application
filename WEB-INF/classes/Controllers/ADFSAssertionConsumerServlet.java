package Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import Services.SAMLGetResponse;
import Services.ADFSSAMLGetResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/adfsassertion-consumer")
public class ADFSAssertionConsumerServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        String responseString = (String) request.getParameter("SAMLResponse");
        System.out.println(responseString);
        request.getSession().setAttribute("SSOtoken",responseString);
        String username = ADFSSAMLGetResponse.receiveSAMLResponse(responseString);
        System.out.println("Logged in as"+username);
        if (!username.equals("false")) {
            request.setAttribute("type", "sso");
            request.setAttribute("name", username);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/registeruser");
            requestDispatcher.include(request, response);
            response.sendRedirect("user-home.jsp");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

}