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
import org.opensaml.xml.signature.Signature;
import org.opensaml.saml2.core.Assertion;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.XMLObject;
import java.util.*;

public class ADFSSAMLGetResponse {

    public static String receiveSAMLResponse(String responseString) {
       try {

            String responseXml = new String(Base64.decodeBase64(responseString), "UTF-8");

            /* Generating SAML Response object from XML string */
            DefaultBootstrap.bootstrap();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());
            System.out.println("responsexml=" + responseXml);

            Document document = docBuilder.parse(is);
            System.out.println("document=" + document);
            Element element = document.getDocumentElement();
            System.out.println("element=" + element);


            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            System.out.println("unmarshaller=" + unmarshaller);
            // XMLObject xmlObj = unmarshaller.unmarshall(element);
           Signature signature=((Response) unmarshaller.unmarshall(element)).getSignature();
            Response response = (Response) unmarshaller.unmarshall(element);
			

            /* Validating the signature on the response */
            if (!validateSignature(signature)) {
                return "false";
            } else {
               
                System.out.println("\n\nverification====>success\n\n");
                List<Assertion> assertions = response.getAssertions();
                String username="";
                // Iterate over the assertions and print the attribute values
                for (Assertion samlAssertion : assertions) {
                    List<AttributeStatement> attributeStatements = samlAssertion.getAttributeStatements();
                    for (AttributeStatement attributeStatement : attributeStatements) {
                        List<Attribute> attributes = attributeStatement.getAttributes();
                        // username=attributes.get(0).getAttributeValues().get(0).getDOM().getTextContent();
                        for (Attribute attribute : attributes) {
                            // System.out.println(attribute.getName() + ":");
                            List<XMLObject> attributeValues = attribute.getAttributeValues();
                            for (XMLObject attributeValue : attributeValues) {
                                System.out.println("\t" + attributeValue.getDOM().getTextContent());
                                username=attributeValue.getDOM().getTextContent();
                                if(!username.isEmpty()){
                                System.out.println("asdasd "+username);
                                    return username;
                                }
                            }
                        }
                    }
                }
                return username;
            }
        } catch (Exception e) {
            return e + "";
        }
    }

    private static boolean validateSignature(Signature signature) {
        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
        System.out.println("signature= "+signature);
        try {
            profileValidator.validate(signature);
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
            sigValidator.validate(signature);
        } catch (Exception e) {
            /*
             * Indicates signature was not cryptographically valid, or possibly a processing
             * error.
             */
            e.printStackTrace();
            return false;
            // throw e;
        }
        return true;
    }

    private static Credential getVerificationCredential() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\adfs.cert"));
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


// package Services;

// import java.io.IOException;
// import java.io.PrintWriter;

// import Dao.RequestDao;
// import Database.DbConnection;
// import Model.User;
// import jakarta.servlet.*;
// import jakarta.servlet.annotation.*;
// import jakarta.servlet.http.*;

// import java.io.BufferedInputStream;
// import java.io.ByteArrayInputStream;
// import java.io.FileInputStream;
// import java.security.cert.CertificateFactory;
// import java.security.cert.X509Certificate;

// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import org.opensaml.xml.ConfigurationException;
// import org.xml.sax.SAXException;

// import org.apache.commons.codec.binary.Base64;
// import org.opensaml.Configuration;
// import org.opensaml.DefaultBootstrap;
// import org.opensaml.saml2.core.LogoutRequest;
// import org.opensaml.saml2.core.Response;
// import org.opensaml.saml2.core.Subject;
// import org.opensaml.security.SAMLSignatureProfileValidator;
// import org.opensaml.xml.XMLObject;
// import org.opensaml.xml.io.Unmarshaller;
// import javax.xml.bind.ValidationException;
// import javax.xml.bind.UnmarshalException;
// import javax.xml.parsers.ParserConfigurationException;
// import org.opensaml.xml.io.UnmarshallerFactory;
// import org.opensaml.xml.security.credential.Credential;
// import org.opensaml.xml.security.x509.BasicX509Credential;
// import org.opensaml.xml.signature.SignatureValidator;
// import org.opensaml.xml.signature.Signature;
// import org.w3c.dom.Document;
// import org.w3c.dom.Element;

// public class ADFSSAMLGetResponse {

//     public static String receiveSAMLResponse(String responseString) {
//         /* Getting the response string from HTTP Request object */

//         /* Decoding Base64 response string to get the XML string */try {

//             String responseXml = new String(Base64.decodeBase64(responseString), "UTF-8");

//             /* Generating SAML Response object from XML string */
//             DefaultBootstrap.bootstrap();
//             DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//             documentBuilderFactory.setNamespaceAware(true);
//             DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
//             ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());

//             Document document = docBuilder.parse(is);


//             System.out.println("document=" + document);
//             Element element = document.getDocumentElement();
//             System.out.println("element=" + element);


//             UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
//             Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
//             System.out.println("unmarshaller=" + unmarshaller);
//             // XMLObject xmlObj = unmarshaller.unmarshall(element);
//             //Response response = (Response) unmarshaller.unmarshall(element);
//             //System.out.println("response=" + unmarshaller);
//             Signature signature=((LogoutRequest) unmarshaller.unmarshall(element)).getSignature();
// 			System.out.println("Signature="+signature);
//             /* Validating the signature on the response */
//             if (!validateSignature(signature)) {
//                 return "false";
//             } else {
//                 // /* If validation was successful, get the username from the response. */
//                 // Subject subject = response.getAssertions().get(0).getSubject();
//                 // String username = subject.getNameID().getValue();
//                 // Decrypt decrypter = new Decrypt();
// 			    System.out.println(is);
//                 return "username";
//             }
//         } catch (Exception e) {
//             return e + "";
//         }
//     }

//     private static boolean validateSignature(Signature signature) {
//         try {
// 			//to check the rules
// 			SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();  
//             profileValidator.validate(signature);
// 			Credential verificationCredential = getVerificationCredential();
			
// 			//to check the whole response integrity
// 			SignatureValidator signValidator = new SignatureValidator(verificationCredential);
//             signValidator.validate(signature);
//         } catch (Exception e) { 
//             System.out.println(e); 
//             return false; 
//         }
//         return true;
//     } 

//     private static Credential getVerificationCredential() {
//         try {
//             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
//                     "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\adfs.cert"));
//             CertificateFactory cf = CertificateFactory.getInstance("X509");
//             X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
//             BasicX509Credential x509Credential = new BasicX509Credential();
//             x509Credential.setPublicKey(cert.getPublicKey());
//             x509Credential.setEntityCertificate(cert);
//             Credential credential = x509Credential;
//             System.out.println("cert= "+cert);
//             System.out.println("issuer= "+cert.getSignature());
//             System.out.println("algname= "+cert.getSigAlgName());
//             System.out.println("pubkey= "+cert.getPublicKey());
//             System.out.println("name= "+cert.getIssuerAlternativeNames());
//             return credential;
//         } catch (Exception e) {
//             System.out.println(e);
//             e.printStackTrace();
//             return null;
//         }
//     }
// }
