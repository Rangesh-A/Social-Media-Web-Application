import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Context;
import org.apache.catalina.authenticator.AuthenticatorBase;
import waffle.apache.NegotiateAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

public class HomeJspFilter implements Filter {
  private Context context;
  private AuthenticatorBase authenticator;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    context = (Context) filterConfig.getServletContext().getAttribute("org.apache.catalina.core.ApplicationContext");
    authenticator = new NegotiateAuthenticator();
    authenticator.setContainer(context);
    authenticator.start();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      if (httpRequest.getRequestURI().endsWith("/home.jsp")) {
        Request catalinaRequest = (Request) request.getAttribute("org.apache.catalina.connector.REQUEST");
        Response catalinaResponse = (Response) response.getAttribute("org.apache.catalina.connector.RESPONSE");
        authenticator.invoke(catalinaRequest, catalinaResponse);
        return;
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    authenticator.stop();
    authenticator = null;
    context = null;
  }
}