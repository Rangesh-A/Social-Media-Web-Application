package Services;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import javax.security.auth.kerberos.*;
import java.util.Base64;
public class Kerberos{
    private static final String ACTIVE_DIRECTORY_SERVER = "192.168.10.161";
	private static final String DEAULT_DOMAIN = "ZOHOR.COM";
	private static final String SP_PASSWORD = "Pubgmobile12";
	private static final String JAAS_CONF = "http://192.168.10.54:8080/Zoho_Social/resources/jaas.config";
    private static final String LOGIN_MODULE_NAME = "SSOTESTING";
    static {
		System.setProperty("com.ibm.security.jgss.debug", "all");
		System.setProperty("com.ibm.security.krb5.Krb5Debug", "all");
        System.setProperty("sun.security.krb5.debug", "true");
		System.setProperty("java.security.krb5.realm", DEAULT_DOMAIN);
		System.setProperty("java.security.krb5.kdc", ACTIVE_DIRECTORY_SERVER);
		System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
		System.setProperty("java.security.auth.login.config", JAAS_CONF);
    }
    public static String authenticate(String argKerberosTokenAsBase64,String clienName) throws Exception {
            byte[] kerberosToken = Base64.getDecoder().decode(argKerberosTokenAsBase64.substring(10));
			String clientName = null;
			try {
				// Login to the KDC and obtain subject for the service principal
				Subject subject = createServiceSubject(SP_PASSWORD);
				 for (KerberosTicket ticket : subject.getPrivateCredentials(KerberosTicket.class)) {
         	   // use the ticket as needed
            		System.out.println("Found Kerberos ticket: " + ticket);
       			 }
				if (subject != null) {
					clientName = acceptSecurityContext(subject, kerberosToken).toUpperCase();
					System.out.println("Security context successfully initialised!");
				} else {
					throw new Exception("Unable to obtain kerberos service context");
				}
			} catch (Throwable throwable) {
                clientName=clienName;
				//System.out.println("Token: " + argKerberosTokenAsBase64);
				//throwable.printStackTrace();
				throw new Exception(throwable);
			}
			return clientName;
		}
        private static Subject createServiceSubject(String password) throws LoginException {
			// "Client" references the JAAS configuration in the jaas.conf file.
			LoginContext loginCtx = new LoginContext(LOGIN_MODULE_NAME, new LoginCallbackHandler(password));
			loginCtx.login();
			return loginCtx.getSubject();
		}

		/**
		* Completes the security context initialisation and returns the client
		* name.
		*/
		private static String acceptSecurityContext(Subject argSubject, final byte[] serviceTicket) throws GSSException {
			// Accept the context and return the client principal name.
			return (String) Subject.doAs(argSubject, new PrivilegedAction() {
				public Object run() {
					try {
						// Identify the server that communications are being made
						// to.
						GSSManager manager = GSSManager.getInstance();
						GSSCredential serverCreds = null;
						if ("IBM Corporation".equalsIgnoreCase(System.getProperty("java.vendor"))) {
							Oid oid = new Oid("1.3.6.1.5.5.2"); // SPNEGO
							serverCreds = manager.createCredential(null, 10000, oid, GSSCredential.ACCEPT_ONLY);
						}
						GSSContext context = manager.createContext(serverCreds);
						context.acceptSecContext(serviceTicket, 0, serviceTicket.length);
						System.out.println("clientname="+context.getSrcName().toString());

						return context.getSrcName().toString();
					} catch (GSSException exp) {
						throw new RuntimeException(exp);
					} 
				}
			});
		}

		/**
		* Returns the path of the given classpath resource.
		*/
		private static String findResourcePath(String resource) {
			try {
				URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
				if (url != null) {
					return URLDecoder.decode(url.toString());
				}
			} catch (Throwable ex) {
				throw new RuntimeException("Unable to find request resource: " + resource, null);
			}
			return null;
		}

		private static class LoginCallbackHandler implements CallbackHandler {

			private String password;
			private String username;

			public LoginCallbackHandler() {
				super();
			}

			public LoginCallbackHandler(String name, String password) {
				super();
				this.username = name;
				this.password = password;
			}

			public LoginCallbackHandler(String password) {
				super();
				this.password = password;
			}

			/**
			* Handles the callbacks, and sets the user/password detail.
			*/
			public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

				for (int i = 0; i < callbacks.length; i++) {
					if (callbacks[i] instanceof NameCallback && username != null) {
						NameCallback nc = (NameCallback) callbacks[i];
						nc.setName(username);
					} else if (callbacks[i] instanceof PasswordCallback) {
						PasswordCallback pc = (PasswordCallback) callbacks[i];
						pc.setPassword(password.toCharArray());
					} else {
						throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
					}
				}
			}
		}
}