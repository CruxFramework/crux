package br.com.sysmap.crux.core.server.lifecycle;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.sysmap.crux.core.server.lifecycle.annotation.DispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.ParametersBindPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.RenderResponsePhase;

/**
 * 
 * @author Thiago
 */
@ParametersBindPhase
@DispatchPhase
@RenderResponsePhase
public abstract class BaseServlet extends HttpServlet
{
	private LifeCycle lifeCycle;

	@Override
	public final void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		lifeCycle = LifeCycle.getLifeCycle(getClass(), config.getServletContext());
	}
	
	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		lifeCycle.processRequest(request, response);
	}

	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

}
