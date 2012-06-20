package org.cruxframework.crux.gadgets.server.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.common.uri.UriBuilder;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.LockedDomainService;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.servlet.ProxyServlet;
import org.apache.shindig.gadgets.servlet.ServletUtil;
import org.apache.shindig.gadgets.uri.ProxyUriManager;

import com.google.inject.Inject;

public class SSOProxyServlet extends ProxyServlet
{
    private static final long serialVersionUID = -2506038373173780280L;
    private static final Logger LOG = Logger.getLogger(SSOProxyServlet.class.getName());

    private transient SSOProxyHandler ssoProxyHandler;
    private transient ProxyUriManager proxyUriManager;
    private transient LockedDomainService lockedDomainService;

    @Inject
    public void setSSOProxyHandler(SSOProxyHandler ssoProxyHandler)
    {
        checkInitialized();
        this.ssoProxyHandler = ssoProxyHandler;
    }
    
    @Inject
    public void setProxyUriManager(ProxyUriManager proxyUriManager) {
      checkInitialized();
      this.proxyUriManager = proxyUriManager;
    }
    
    @Inject
    public void setLockedDomainService(LockedDomainService lockedDomainService) {
      checkInitialized();
      this.lockedDomainService = lockedDomainService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse servletResponse) throws IOException
    {
    	if (request.getHeader("If-Modified-Since") != null) {
    		servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    		return;
    	}

    	Uri reqUri = new UriBuilder(request).toUri();
    	HttpResponse response = null;
    	try {
    		// Parse request uri:
    		ProxyUriManager.ProxyUri proxyUri = proxyUriManager.process(reqUri);

    		// TODO: Consider removing due to redundant logic.
    		String host = request.getHeader("Host");
    		if (!lockedDomainService.isSafeForOpenProxy(host)) {
    			// Force embedded images and the like to their own domain to avoid XSS
    			// in gadget domains.
    			Uri resourceUri = proxyUri.getResource();
    			String msg = "Embed request for url " +
    			(resourceUri != null ? resourceUri.toString() : "n/a") + " made to wrong domain " + host;
    			LOG.info(msg);
    			throw new GadgetException(GadgetException.Code.INVALID_PARAMETER, msg,
    					HttpResponse.SC_BAD_REQUEST);
    		}

    		response = ssoProxyHandler.fetch(request, proxyUri);
    	} catch (GadgetException e) {
    		response = ServletUtil.errorResponse(new GadgetException(e.getCode(), e.getMessage(),
    				HttpServletResponse.SC_BAD_REQUEST));
    	}

    	ServletUtil.copyResponseToServlet(response, servletResponse);
    }
}
