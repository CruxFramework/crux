/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.cruxframework.crux.gadgets.server.servlet;

import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shindig.auth.AuthInfo;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.common.util.Utf8UrlCoder;
import org.apache.shindig.gadgets.AuthType;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.RequestPipeline;
import org.apache.shindig.gadgets.oauth.OAuthArguments;
import org.apache.shindig.gadgets.rewrite.ResponseRewriterRegistry;
import org.apache.shindig.gadgets.servlet.MakeRequestHandler;
import org.apache.shindig.gadgets.servlet.ServletUtil;
import org.apache.shindig.gadgets.uri.UriCommon.Param;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handles gadgets.io.makeRequest requests.
 *
 * Unlike ProxyHandler, this may perform operations such as OAuth or signed fetch.
 */
@Singleton
public class SSOMakeRequestHandler extends MakeRequestHandler{
	public static final String JOSSO_SINGLE_SIGN_ON_COOKIE = "JOSSO_SESSIONID";

	static final Set<String> BAD_HEADERS = ImmutableSet.of("HOST", "ACCEPT-ENCODING");
	
	@Inject
	public SSOMakeRequestHandler(RequestPipeline requestPipeline,
			ResponseRewriterRegistry contentRewriterRegistry) {
		super(requestPipeline, contentRewriterRegistry);
	}

	/**
	 * Generate a remote content request based on the parameters
	 * sent from the client.
	 * @throws GadgetException
	 */
	protected HttpRequest buildHttpRequest(HttpServletRequest request) throws GadgetException {
		String urlStr = request.getParameter(Param.URL.getKey());
		if (urlStr == null) {
			throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
					Param.URL.getKey() + " parameter is missing.", HttpResponse.SC_BAD_REQUEST);
		}

		Uri url = null;
		try {
			url = ServletUtil.validateUrl(Uri.parse(urlStr));
		} catch (IllegalArgumentException e) {
			throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
					"Invalid " + Param.URL.getKey() + " parameter", HttpResponse.SC_BAD_REQUEST);
		}

		SSOHttpRequest req = new SSOHttpRequest(url);
		req.setMethod(getParameter(request, METHOD_PARAM, "GET"));
		req.setContainer(getContainer(request));

		setPostData(request,req);
		setSsoCookie(request,req);

		String headerData = getParameter(request, HEADERS_PARAM, "");
		if (headerData.length() > 0) {
			String[] headerList = StringUtils.split(headerData, '&');
			for (String header : headerList) {
				String[] parts = StringUtils.splitPreserveAllTokens(header, '=');
				if (parts.length != 2) {
					throw new GadgetException(GadgetException.Code.INVALID_PARAMETER,
							"Malformed header param specified:" + header, HttpResponse.SC_BAD_REQUEST);
				}
				String headerName = Utf8UrlCoder.decode(parts[0]);
				if (!BAD_HEADERS.contains(headerName.toUpperCase())) {
					req.addHeader(headerName, Utf8UrlCoder.decode(parts[1]));
				}
			}
		}

		// Set the default content type  for post requests when a content type is not specified
		if ("POST".equals(req.getMethod()) && req.getHeader("Content-Type")==null) {
			req.addHeader("Content-Type", "application/x-www-form-urlencoded");
		}

		req.setIgnoreCache("1".equals(request.getParameter(Param.NO_CACHE.getKey())));

		if (request.getParameter(Param.GADGET.getKey()) != null) {
			req.setGadget(Uri.parse(request.getParameter(Param.GADGET.getKey())));
		}

		// If the proxy request specifies a refresh param then we want to force the min TTL for
		// the retrieved entry in the cache regardless of the headers on the content when it
		// is fetched from the original source.
		if (request.getParameter(Param.REFRESH.getKey()) != null) {
			try {
				req.setCacheTtl(Integer.parseInt(request.getParameter(Param.REFRESH.getKey())));
			} catch (NumberFormatException nfe) {
				// Ignore
			}
		}
		// Allow the rewriter to use an externally forced mime type. This is needed
		// allows proper rewriting of <script src="x"/> where x is returned with
		// a content type like text/html which unfortunately happens all too often
		req.setRewriteMimeType(request.getParameter(Param.REWRITE_MIME_TYPE.getKey()));

		// Figure out whether authentication is required
		AuthType auth = AuthType.parse(getParameter(request, AUTHZ_PARAM, null));
		req.setAuthType(auth);
		if (auth != AuthType.NONE) {
			req.setSecurityToken(extractAndValidateToken(request));
			req.setOAuthArguments(new OAuthArguments(auth, request));
		}

		ServletUtil.setXForwardedForHeader(request, req);
		return req;
	}

	public static void setSsoCookie(HttpServletRequest request, SSOHttpRequest req)
    {
	    // TODO create a white list for this
        Cookie cookie = null;
        Cookie cookies[] = request.getCookies();
        if (cookies == null)
            cookies = new Cookie[0];
        for (int i = 0; i < cookies.length; i++) {
            if (JOSSO_SINGLE_SIGN_ON_COOKIE.equals(cookies[i].getName())) {
                cookie = cookies[i];
                break;
            }
        }
        if (cookie != null) {
        	req.setSsoCookie(cookie.getValue());
        }
    }

	/**
	 * @param request
	 * @return A valid token for the given input.
	 */
	private SecurityToken extractAndValidateToken(HttpServletRequest request) throws GadgetException {
		SecurityToken token = new AuthInfo(request).getSecurityToken();
		if (token == null) {
			// TODO: Determine appropriate external error code for this.
			throw new GadgetException(GadgetException.Code.INVALID_SECURITY_TOKEN);
		}
		return token;
	}

	public static class SSOHttpRequest extends HttpRequest
	{
		
		private String ssoCookie;

		public SSOHttpRequest(HttpRequest request)
        {
	        super(request);
        }

		public SSOHttpRequest(Uri url)
        {
	        super(url);
        }

		public String getSsoCookie()
        {
        	return ssoCookie;
        }

		public void setSsoCookie(String ssoCookie)
        {
        	this.ssoCookie = ssoCookie;
        }
	}
}
