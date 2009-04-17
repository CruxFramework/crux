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
package br.com.sysmap.crux.core.server.dispatch;

import java.lang.reflect.Method;

import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.SerializationPolicy;

/**
 * 
 * @author Thiago Bustamante
 */
public class RemoteServiceServlet extends com.google.gwt.user.server.rpc.RemoteServiceServlet
{
	private static final long serialVersionUID = -5471459247489132091L;

	/**
	 * Override RemoteServiceServlet to 
	 * {@link SerializationPolicy}
	 */
	@Override
	public String processCall(String payload) throws SerializationException 
	{
		try 
		{
			Object controller = getControllerForRequest(payload);
			RPCRequest rpcRequest = RPC.decodeRequest(payload, controller.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);
			try
			{
				validateMethod(rpcRequest.getMethod(), controller);
				return RPC.invokeAndEncodeResponse(controller, rpcRequest.getMethod(),
						rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
			} 
			catch (ValidateException ex) 
			{
				return RPC.encodeResponseForFailure(rpcRequest.getMethod(), ex);
			} 
		}
		catch (IncompatibleRemoteServiceException ex) 
		{
			log("An IncompatibleRemoteServiceException was thrown while processing this call.",ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}
	  
	/**
	 * Return the controller that will handle this request
	 * @param encodedRequest
	 * @return
	 * @throws IncompatibleRemoteServiceException
	 */
	protected Object getControllerForRequest(String encodedRequest) throws IncompatibleRemoteServiceException
	{
		try 
		{
			// We don't need to verify or parser the encodedRequest because it will be already done by
			// RPC.decodeRequest. So, just read the interface name directly
			String serviceIntfName = RegexpPatterns.REGEXP_PIPE.split(encodedRequest)[5];
			return ControllerFactoryInitializer.getControllerFactory().getController(serviceIntfName);
		} 
		catch (Throwable e) 
		{
			throw new IncompatibleRemoteServiceException(e.getLocalizedMessage(), e);
		} 
	}
	
	/**
	 * If controller specifies a validate method, run it before invoke the method itself
	 * @param method
	 * @param controller
	 * @throws ValidateException
	 */
	protected void validateMethod(Method method, Object controller) throws ValidateException
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
