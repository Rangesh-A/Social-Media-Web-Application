package Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import Services.SAMLGetResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/assertion-consumer")
public class AssertionConsumerServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        String responseString = (String) request.getParameter("SAMLResponse");
        request.getSession().setAttribute("SSOtoken",responseString);
        String username = SAMLGetResponse.receiveSAMLResponse(responseString);
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

    // public static String receiveSAMLResponse(String responseString) {
    /* Getting the response string from HTTP Request object */

    /* Decoding Base64 response string to get the XML string */
    // try {

    // String responseXml = new String(Base64.decodeBase64(responseString),
    // "UTF-8");

    // /* Generating SAML Response object from XML string */
    // DefaultBootstrap.bootstrap();
    // DocumentBuilderFactory documentBuilderFactory =
    // DocumentBuilderFactory.newInstance();
    // documentBuilderFactory.setNamespaceAware(true);
    // DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
    // ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());
    // Document document = docBuilder.parse(is);
    // Element element = document.getDocumentElement();

    // UnmarshallerFactory unmarshallerFactory =
    // Configuration.getUnmarshallerFactory();

    // Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);

    // XMLObject xmlObj = unmarshaller.unmarshall(element);
    // Response response = (Response) xmlObj;

    // // /* Validating the signature on the response */
    // // // validateSignature(response);

    // // /* If validation was successful, get the username from the response. */
    // Subject subject = response.getAssertions().get(0).getSubject();
    // String username = subject.getNameID().getValue();
    // return responseString;
    // } catch (Exception e) {
    // return "ee";
    // }
    // }
}