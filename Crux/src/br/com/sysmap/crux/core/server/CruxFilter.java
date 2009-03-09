package br.com.sysmap.crux.core.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.sysmap.crux.core.rebind.CruxScreenBridge;

/**
 * Used to save the path to the current HTML page. This information is necessary
 * to generate the client handlers and formatters.
 * 
 * @author Thiago
 */
public class CruxFilter implements Filter 
{
	@Override
	public void init(FilterConfig arg0) throws ServletException 
	{
	}

	@Override
	public void destroy() 
	{
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
		throws IOException, ServletException 
	{
		HttpServletRequest request = (HttpServletRequest) req;
        String pathInfo = request.getPathInfo();
		if (pathInfo != null && pathInfo.length() > 0)
		{
			if (!pathInfo.endsWith("hosted.html"))
			{
				CruxScreenBridge.getInstance().registerLastPageRequested(pathInfo.substring(1));
			}
		}
		else
		{
			CruxScreenBridge.getInstance().registerLastPageRequested("");
		}
		chain.doFilter(req, resp);
	}
}
