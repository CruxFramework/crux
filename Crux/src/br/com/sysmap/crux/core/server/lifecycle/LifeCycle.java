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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.annotation.DispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.ParametersBindPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.PreDispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.PreParametersBindPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.PreRenderResponsePhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.RenderResponsePhase;

/**
 * 
 * @author Thiago
 *
 */
public class LifeCycle 
{
	private List<Phase> preParametersBindPhases = null;
	private Phase parametersBindPhase = null;
	private List<Phase> preDispatchPhases = null;
	private Phase dispatchPhase = null;
	private List<Phase> preRenderResponsePhases = null;
	private Phase renderResponsePhase = null;

	private static final Log logger = LogFactory.getLog(LifeCycle.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	ServletContext servletContext;
	
	private LifeCycle(Class<? extends BaseServlet> clazz, ServletContext servletContext)
	{
		this.servletContext = servletContext;
		registerPhases(clazz);
	}
	
	private void registerPhases(Class<? extends BaseServlet> clazz)
	{
		preParametersBindPhases = new ArrayList<Phase>();
		preDispatchPhases = new ArrayList<Phase>();
		preRenderResponsePhases = new ArrayList<Phase>();
		
		registerPreParametersBindPhases(clazz);
		registerParametersBindPhase(clazz);
		registerPreDispatchPhases(clazz);
		registerDispatchPhase(clazz);
		registerPreRenderResponsePhases(clazz);
		registerRenderResponsePhase(clazz);
		if (logger.isInfoEnabled()) logger.info(messages.lifeCycleRegisterPhases(clazz.getName()));
	}
	
	private <A extends Annotation> A getClassAnnotation(Class<? extends BaseServlet> clazz, Class<A> annotationClass)
	{
		A annotation = clazz.getAnnotation(annotationClass);
		
		Class<?> superClass = null;
		while (annotation == null && !(superClass = clazz.getSuperclass()).equals(BaseServlet.class))
		{
			annotation = superClass.getAnnotation(annotationClass);
		}
		if (annotation == null)
		{
			annotation = BaseServlet.class.getAnnotation(annotationClass);
		}
		return annotation;
	}
	
	private void registerPreParametersBindPhases(Class<? extends BaseServlet> clazz)
	{
		PreParametersBindPhase parametersBind = (PreParametersBindPhase)getClassAnnotation(clazz,PreParametersBindPhase.class);
		if (parametersBind != null)
		{
			Class<? extends Phase>[] classes = parametersBind.values();
			try 
			{
				for (Class<? extends Phase> phaseClass : classes) 
				{
					preParametersBindPhases.add(phaseClass.newInstance());
				}
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}

	private void registerParametersBindPhase(Class<? extends BaseServlet> clazz)
	{
		ParametersBindPhase parametersBind = (ParametersBindPhase) getClassAnnotation(clazz,ParametersBindPhase.class);
		if (parametersBind != null)
		{
			Class<? extends Phase> phaseClass = parametersBind.value();
			try 
			{
				parametersBindPhase = phaseClass.newInstance();
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}

	private void registerPreDispatchPhases(Class<? extends BaseServlet> clazz)
	{
		PreDispatchPhase dispatch = (PreDispatchPhase) getClassAnnotation(clazz,PreDispatchPhase.class);
		if (dispatch != null)
		{
			Class<? extends Phase>[] classes = dispatch.values();
			try 
			{
				for (Class<? extends Phase> phaseClass : classes) 
				{
					preDispatchPhases.add(phaseClass.newInstance());
				}
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}

	private void registerDispatchPhase(Class<? extends BaseServlet> clazz)
	{
		DispatchPhase dispatch = (DispatchPhase) getClassAnnotation(clazz,DispatchPhase.class);
		if (dispatch != null)
		{
			Class<? extends Phase> phaseClass = dispatch.value();
			try 
			{
				dispatchPhase = phaseClass.newInstance();
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}
	
	private void registerPreRenderResponsePhases(Class<? extends BaseServlet> clazz)
	{
		PreRenderResponsePhase dispatch = (PreRenderResponsePhase) getClassAnnotation(clazz,PreRenderResponsePhase.class);
		if (dispatch != null)
		{
			Class<? extends Phase>[] classes = dispatch.values();
			try 
			{
				for (Class<? extends Phase> phaseClass : classes) 
				{
					preRenderResponsePhases.add(phaseClass.newInstance());
				}
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}

	private void registerRenderResponsePhase(Class<? extends BaseServlet> clazz)
	{
		RenderResponsePhase dispatch = (RenderResponsePhase) getClassAnnotation(clazz,RenderResponsePhase.class);
		if (dispatch != null)
		{
			Class<? extends Phase> phaseClass = dispatch.value();
			try 
			{
				renderResponsePhase = phaseClass.newInstance();
			} 
			catch (Throwable e) 
			{
				logger.error(messages.lifeCycleRegisterPhaseError(e.getLocalizedMessage()), e);
			}
		}
	}

	/**
	 * Process all the phases registered in this lifeCycle object.
	 * @param request
	 * @param response
	 */
	void processRequest(HttpServletRequest request, HttpServletResponse response)
	{
		PhaseContext context = new PhaseContext(request, response);
		try
		{
			for (Phase phase : preParametersBindPhases) 
			{
				if (!context.isInterruptCycle())
					phase.execute(context);
			}
			if (parametersBindPhase != null && !context.isInterruptCycle())
			{
				parametersBindPhase.execute(context);
			}
			for (Phase phase : preDispatchPhases) 
			{
				if (!context.isInterruptCycle())
					phase.execute(context);
			}
			if (dispatchPhase != null  && !context.isInterruptCycle())
			{
				dispatchPhase.execute(context);
			}
			for (Phase phase : preRenderResponsePhases) 
			{
				if (!context.isInterruptCycle())
					phase.execute(context);
			}
		}
		catch (PhaseException e) 
		{
			if (logger.isInfoEnabled()) logger.info(messages.lifeCycleProcessRequestError(e.getLocalizedMessage()), e);
			context.setPhaseException(e);
		}
		try
		{
			if (renderResponsePhase != null)
			{
				renderResponsePhase.execute(context);
			}
		}
		catch (PhaseException e) 
		{
			logger.error(messages.lifeCycleProcessRequestError(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Create a LifeCycle object, based in the class annotations. Superclass 
	 * Annotations is used if not overwrote by subclasses
	 * @param clazz
	 * @return
	 */
	static LifeCycle getLifeCycle(Class<? extends BaseServlet> clazz, ServletContext context)
	{
		return new LifeCycle(clazz, context);
	}
}
