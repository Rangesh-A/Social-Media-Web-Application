/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available     under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package Filters;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

import waffle.servlet.spi.SecurityFilterProvider;
import waffle.util.AuthorizationHeader;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * A negotiate security filter provider.
 *
 * @author dblock[at]dblock[dot]org
 */
 @WebFilter(filterName = "LoggedInFilter")
public class CustomSecurityFilter implements SecurityFilterProvider {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSecurityFilter.class);

    /** The Constant WWW_AUTHENTICATE. */
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /** The Constant PROTOCOLS. */
    private static final String PROTOCOLS = "http";

    /** The Constant NEGOTIATE. */
    private static final String NEGOTIATE = "Negotiate";

    /** The protocols. */
    private List<String> protocols = new ArrayList<>();

    /** The auth. */
    private final IWindowsAuthProvider auth;

   
    public CustomSecurityFilter(final IWindowsAuthProvider newAuthProvider) {
        this.auth = newAuthProvider;
        this.protocols.add(CustomSecurityFilter.NEGOTIATE);
    }


    public List<String> getProtocols() {
        return this.protocols;
    }


    public void setProtocols(final List<String> values) {
        this.protocols = values;
    }

 
    public void sendUnauthorized(final HttpServletResponse response) {
        final Iterator<String> protocolsIterator = this.protocols.iterator();
        while (protocolsIterator.hasNext()) {
            response.addHeader(WWW_AUTHENTICATE, protocolsIterator.next());
        }
    }

  
    public boolean isPrincipalException(final HttpServletRequest request) {
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        LOGGER.debug("authorization: {}, ntlm post: {}", authorizationHeader, Boolean.valueOf(ntlmPost));
        return ntlmPost;
    }


    public IWindowsIdentity doFilter(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
        //Custom NTLM Token Disable
        if(isNTLMToken(authorizationHeader)) {
            response.setHeader("Connection", "keep-alive");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return null;
        }

        // maintain a connection-based session for NTLM tokens
        final String connectionId = NtlmServletRequest.getConnectionId(request);
        final String securityPackage = authorizationHeader.getSecurityPackage();
        LOGGER.debug("security package: {}, connection id: {}", securityPackage, connectionId);

        if (ntlmPost) {
            // type 2 NTLM authentication message received
            this.auth.resetSecurityToken(connectionId);
        }

        final byte[] tokenBuffer = authorizationHeader.getTokenBytes();
        LOGGER.debug("token buffer: {} byte(s)", Integer.valueOf(tokenBuffer.length));
        final IWindowsSecurityContext securityContext = this.auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);

        final byte[] continueTokenBytes = securityContext.getToken();
        if (continueTokenBytes != null && continueTokenBytes.length > 0) {
            final String continueToken = BaseEncoding.base64().encode(continueTokenBytes);
            LOGGER.debug("continue token: {}", continueToken);
            response.addHeader(WWW_AUTHENTICATE, securityPackage + " " + continueToken);
        }

        LOGGER.debug("continue required: {}", Boolean.valueOf(securityContext.isContinue()));
        if (securityContext.isContinue() || ntlmPost) {
            response.setHeader("Connection", "keep-alive");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return null;
        }

        final IWindowsIdentity identity = securityContext.getIdentity();
        securityContext.dispose();
        return identity;
    }

    private boolean isNTLMToken(AuthorizationHeader authorizationHeader) {
        String decodedToken = new String(Base64.getDecoder().decode(authorizationHeader.getToken()));
        return decodedToken.contains("NTLM")
                || decodedToken.contains("ntlm");
    }

 
    public boolean isSecurityPackageSupported(final String securityPackage) {
        for (final String protocol : this.protocols) {
            if (protocol.equalsIgnoreCase(securityPackage)) {
                return true;
            }
        }
        return false;
    }


    public void initParameter(final String parameterName, final String parameterValue) {
        if (parameterName.equals(PROTOCOLS)) {
            this.protocols = new ArrayList<>();
            final String[] protocolNames = parameterValue.split("\\s+");
            for (String protocolName : protocolNames) {
                protocolName = protocolName.trim();
                if (protocolName.length() > 0) {
                    LOGGER.debug("init protocol: {}", protocolName);
                    if (protocolName.equals(NEGOTIATE)) {
                        this.protocols.add(protocolName);
                    } else {
                        LOGGER.error("unsupported protocol: {}", protocolName);
                        throw new RuntimeException("Unsupported protocol: " + protocolName);
                    }
                }
            }
        } else {
            throw new InvalidParameterException(parameterName);
        }
    }
}