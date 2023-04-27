package Services;

import java.io.IOException;
import java.io.PrintWriter;

import Dao.RequestDao;
import Database.DbConnection;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.opensaml.xml.ConfigurationException;
import org.xml.sax.SAXException;

import org.apache.commons.codec.binary.Base64;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.parsers.ParserConfigurationException;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.SignatureValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SAMLGetResponse {

    public static String receiveSAMLResponse(String responseString) {
       try {

            String responseXml = new String(Base64.decodeBase64(responseString), "UTF-8");

            DefaultBootstrap.bootstrap();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());

            Document document = docBuilder.parse(is);
            System.out.println("document=" + document);
            Element element = document.getDocumentElement();
            System.out.println("element=" + element);
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();

            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            System.out.println("unmarshaller=" + unmarshaller);
            Response response = (Response) unmarshaller.unmarshall(element);
            System.out.println("response=" + unmarshaller);

            /* Validating the signature on the response */
            if (!validateSignature(response)) {
                return "false";
            } else {
                // /* If validation was successful, get the username from the response. */
                Subject subject = response.getAssertions().get(0).getSubject();
                String username = subject.getNameID().getValue();
                return username;
            }
        } catch (Exception e) {
            return e + "";
        }
    }

    private static boolean validateSignature(Response response) {
        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
        System.out.println("signature= "+response.getSignature());
        try {
            profileValidator.validate(response.getSignature());
        } catch (Exception e) {
            /* Indicates signature did not conform to SAML Signature profile */
            e.printStackTrace();
            return false;
            // throw e;
        }

        Credential verificationCredential = getVerificationCredential();
        System.out.println(verificationCredential);
        SignatureValidator sigValidator = new SignatureValidator(verificationCredential);
        try {
            sigValidator.validate(response.getSignature());
        } catch (Exception e) {
           
            e.printStackTrace();
            return false;
            // throw e;
        }
        return true;
    }

    private static Credential getVerificationCredential() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\okta.cert"));
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
            BasicX509Credential x509Credential = new BasicX509Credential();
            x509Credential.setPublicKey(cert.getPublicKey());
            x509Credential.setEntityCertificate(cert);
            Credential credential = x509Credential;
            System.out.println("cert= "+cert);
            System.out.println("issuer= "+cert.getSignature());
            System.out.println("algname= "+cert.getSigAlgName());
            System.out.println("pubkey= "+cert.getPublicKey());
            System.out.println("name= "+cert.getIssuerAlternativeNames());
            return credential;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }
}
