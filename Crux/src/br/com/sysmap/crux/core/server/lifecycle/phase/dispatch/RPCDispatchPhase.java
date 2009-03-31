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
package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.json.JsonResult;
import br.com.sysmap.crux.core.server.json.JsonSerializer;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

/**
 * Dispatcher for handle crux RPC events at the server side.
 * @author Thiago
 *
 */
public class RPCDispatchPhase implements Phase
{
	private static final Log logger = LogFactory.getLog(RPCDispatchPhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		try 
		{
			DispatchData dispatchData = context.getDispatchData();
			String evtCall = dispatchData.getEvtCall();
			String[] call = RegexpPatterns.REGEXP_DOT.split(evtCall);
				
			Object controller = ControllerFactoryInitializer.getControllerFactory().getController(call[0]);
			bindControllerParameters(controller, dispatchData);

			Class<?>[] parametersTypes = {};
			Object[] parametersValues = {};
			Object result = dispatch(controller, call[1], parametersTypes, parametersValues);
			context.setCycleResult(new JsonResult(JsonResult.CODE_SUCCESS,JsonSerializer.objMarshall(result)));
			context.setDto(controller);
		} 
		catch (ValidateException e) 
		{
			context.interruptCycle(e.getLocalizedMessage());
		} 
		catch (Throwable e) 
		{
			context.setCycleResult(null);
			throw new PhaseException(e);
		}
	}
	
	protected Object dispatch(Object controller, String methodName, Class<?>[] parametersTypes, Object[] parametersValues) throws Exception 
	{
			Method method = controller.getClass().getMethod(methodName, parametersTypes);
			validateMethod(method, controller);
			return method.invoke(controller, parametersValues);
	}
	
	protected void bindControllerParameters(Object controller, DispatchData dispatchData)
	{
		for (String key: dispatchData.getParameters())
		{
			try
			{
				BeanUtils.copyProperty(controller, key, dispatchData.getParameter(key));
			}
			catch (Throwable e) 
			{
				if (logger.isInfoEnabled()) logger.info(messages.dispatchPhasePropertyNotBound(key));
			}
		}
	}

	protected void validateMethod(Method method, Object controller) throws Exception
	{
		Validate annot = method.getAnnotation(Validate.class);
		if (annot != null)
		{
			String validateMethod = annot.value();
			if (validateMethod == null || validateMethod.length() == 0)
			{
				validateMethod = "validate"+ method.getName();
			}
			try 
			{
				Method validate = controller.getClass().getMethod(validateMethod, new Class<?>[]{});
				validate.invoke(controller, new Object[]{});
			} 
			catch (Exception e) 
			{
				throw new ValidateException (e.getLocalizedMessage());
			} 
		}
	}
}
