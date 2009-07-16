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

import br.com.sysmap.crux.core.i18n.LocaleResolver;
import br.com.sysmap.crux.core.i18n.LocaleResolverInitialiser;
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
		boolean localeInitializedByServlet = false;
		try 
		{
			localeInitializedByServlet = initUserLocaleResolver();
			Object service = getServiceForRequest(payload);
			RPCRequest rpcRequest = RPC.decodeRequest(payload, service.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);
			return RPC.invokeAndEncodeResponse(service, rpcRequest.getMethod(),
					rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
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
	 * 
	 */
	protected boolean initUserLocaleResolver()
	{
		if (LocaleResolverInitialiser.getLocaleResolver() == null)
		{
			LocaleResolverInitialiser.createLocaleResolverThreadData();
			LocaleResolver resolver = LocaleResolverInitialiser.getLocaleResolver();
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
		LocaleResolverInitialiser.clearLocaleResolverThreadData();
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
			
			// We don't need to verify or parser the encodedRequest because it will be already done by
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
