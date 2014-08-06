/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.server.dispatch;

import java.lang.reflect.Method;

import org.cruxframework.crux.core.i18n.LocaleResolver;
import org.cruxframework.crux.core.i18n.LocaleResolverInitializer;
import org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler;
import org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandlerFactory;
import org.cruxframework.crux.core.server.dispatch.st.InvalidTokenException;
import org.cruxframework.crux.core.shared.rpc.st.UseSynchronizerToken;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;

/**
 * 
 * @author Thiago Bustamante
 */
public class RemoteServiceServlet extends com.google.gwt.user.server.rpc.RemoteServiceServlet
{
	private static final long serialVersionUID = -5471459247489132091L;

	/**
	 * @see com.google.gwt.user.server.rpc.RemoteServiceServlet#processCall(java.lang.String)
	 */
	@Override
	public String processCall(String payload) throws SerializationException 
	{
		boolean localeInitializedByServlet = false;
		try 
		{
			localeInitializedByServlet = initUserLocaleResolver();
			Object service = getServiceForRequest(payload);
			RPCRequest rpcRequest = RPC.decodeRequest(payload, service.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);

			//TODO: criar um ponto de injecao de comportamento aki.... para permitir que plugins sejam criados (ex: seguranca, logs, etc)
			CruxSynchronizerTokenHandler handler = CruxSynchronizerTokenHandlerFactory.getCruxSynchronizerTokenHandler(getThreadLocalRequest());

			boolean useToken = checkSynchonizerToken(rpcRequest, handler);
			try
			{
				return RPC.invokeAndEncodeResponse(service, rpcRequest.getMethod(),
						rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
			}
			finally
			{
				if (useToken)
				{
					String methodFullSignature = handler.getMethodDescription(rpcRequest.getMethod());
					handler.endMethod(methodFullSignature);
				}
			}
		}
		catch (IncompatibleRemoteServiceException ex) 
		{
			log("An IncompatibleRemoteServiceException was thrown while processing this call.",ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
		finally
		{
			if (localeInitializedByServlet)
			{
				clearUserLocaleResolver();
			}
		}
	}
	
	/**
	 * @param rpcRequest
	 * @param handler
	 * @return
	 * @throws IncompatibleRemoteServiceException
	 */
	protected boolean checkSynchonizerToken(RPCRequest rpcRequest, CruxSynchronizerTokenHandler handler) throws IncompatibleRemoteServiceException
	{
		Method method = rpcRequest.getMethod();
		if (method.getAnnotation(UseSynchronizerToken.class) != null)
		{
			String methodFullSignature = handler.getMethodDescription(method);
			if (!handler.isMethodRunning(methodFullSignature))
			{
				try
				{
					handler.startMethod(methodFullSignature);
					return true;
				}
				catch (InvalidTokenException e)
				{
					throw new IncompatibleRemoteServiceException(e.getLocalizedMessage(), e);
				}
			}
			else
			{
				throw new IncompatibleRemoteServiceException("Invalid Synchronizer Token for method ["+methodFullSignature+"]. Possible CSRF attack.");
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	protected boolean initUserLocaleResolver()
	{
		if (LocaleResolverInitializer.getLocaleResolver() == null)
		{
			LocaleResolverInitializer.createLocaleResolverThreadData();
			LocaleResolver resolver = LocaleResolverInitializer.getLocaleResolver();
			resolver.initializeUserLocale(getThreadLocalRequest());
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	protected void clearUserLocaleResolver()
	{
		LocaleResolverInitializer.clearLocaleResolverThreadData();
	}

	/**
	 * Return the service that will handle this request
	 * @param encodedRequest
	 * @return
	 * @throws IncompatibleRemoteServiceException
	 */
	protected Object getServiceForRequest(String encodedRequest) throws IncompatibleRemoteServiceException
	{
		try 
		{
			if (!ServiceFactoryInitializer.isFactoryInitialized())
			{
				ServiceFactoryInitializer.initialize(getServletContext());
			}
			
			// We don't need to verify or parse the encodedRequest because it will be already done by
			// RPC.decodeRequest. So, just read the interface name directly
			String serviceIntfName = RegexpPatterns.REGEXP_PIPE.split(encodedRequest)[5];			
			Object service = ServiceFactoryInitializer.getServiceFactory().getService(serviceIntfName);
			if (service instanceof RequestAware)
			{
				((RequestAware)service).setRequest(getThreadLocalRequest());
			}
			if (service instanceof ResponseAware)
			{
				((ResponseAware)service).setResponse(getThreadLocalResponse());
			}
			if (service instanceof SessionAware)
			{
				((SessionAware)service).setSession(getThreadLocalRequest().getSession());
			}
			return service;
		} 
		catch (Throwable e) 
		{
			throw new IncompatibleRemoteServiceException(e.getLocalizedMessage(), e);
		} 
	}
}
