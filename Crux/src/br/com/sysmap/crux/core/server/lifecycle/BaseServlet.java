/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
