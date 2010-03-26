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
package br.com.sysmap.crux.core.server.dispatch.st;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.sysmap.crux.core.client.rpc.st.CruxSynchronizerTokenService;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.user.server.Base64Utils;

/**
 * @author Thiago da Rosa de Bustamante - <code>thiago@sysmap.com.br</code>
 *
 */
public class CruxSynchronizerTokenServiceImpl implements CruxSynchronizerTokenService, CruxSynchronizerTokenHandler
{
	private static final String CRUX_SYNC_TOKEN_ATTR = "__CRUX_SYNC_TOKEN_";
	private static final String CRUX_SYNC_TOKEN_IN_USE_ATTR = "__CRUX_SYNC_TOKEN_IN_USE_";

	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static Lock readLock = readWriteLock.readLock();
	private static Lock writeLock = readWriteLock.writeLock();
	private static ServerMessages messages = MessagesFactory.getMessages(ServerMessages.class);
	private HttpServletRequest request;
	private HttpSession session;

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.RequestAware#setRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.SessionAware#setSession(javax.servlet.http.HttpSession)
	 */
	public void setSession(HttpSession session)
	{
		this.session = session;
	}		
	
	/**
	 * @see br.com.sysmap.crux.core.client.rpc.st.CruxSynchronizerTokenService#getSynchronizerToken(java.lang.String)
	 */
	public String getSynchronizerToken(String methodFullSignature)
	{
		writeLock.lock();
		try
		{
			if (createToken(methodFullSignature))
			{
				return getTokens().get(methodFullSignature);
			}
		}
		finally
		{
			writeLock.unlock();
		}
		
		return null;
	}

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#isMethodRunning(java.lang.String)
	 */
	public boolean isMethodRunning(String methodFullSignature)
	{
		readLock.lock();
		try
		{
			return getInUseTokens().containsKey(methodFullSignature);
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#startMethod(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public void startMethod(String methodFullSignature) throws InvalidTokenException
	{
		writeLock.lock();
		try
		{
			String expectedToken = getTokens().get(methodFullSignature);
			String receivedToken = request.getParameter(CRUX_SYNC_TOKEN_PARAM);
			if (expectedToken != null && receivedToken != null && expectedToken.equals(receivedToken))
			{
				getTokens().remove(methodFullSignature);
				getInUseTokens().put(methodFullSignature, true);
			}
			else
			{
				throw new InvalidTokenException(messages.synchronizerTokenServiceInvalidTokenError(methodFullSignature));
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#endMethod(java.lang.String)
	 */
	public void endMethod(String methodFullSignature)
	{
		writeLock.lock();
		try
		{
			getInUseTokens().remove(methodFullSignature);
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#getMethodDescription(java.lang.reflect.Method)
	 */
	public String getMethodDescription(Method method)
	{
		return ClassUtils.getMethodDescription(method);
	}

	/**
	 * Creates a new token for the requested method. If the method is already being processed 
	 * for the current user, return false and does not create the token.
	 * @param methodFullSignature
	 * @return true if the token was created. 
	 */
	private boolean createToken(String methodFullSignature)
	{
		if (!getInUseTokens().containsKey(methodFullSignature))
		{
			String token = generateRandomToken();
			getTokens().put(methodFullSignature, token);
			return true;
		}
		return false;
	}

	/**
	 * Generates a random 256 bit token, coded as Base64.
	 * @return
	 */
	private String generateRandomToken()
	{
		byte[] token = new byte[32];
		new Random().nextBytes(token);
		return Base64Utils.toBase64(token);
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getTokens()
	{
		Map<String, String> tokens = (Map<String, String>) this.session.getAttribute(CRUX_SYNC_TOKEN_ATTR); 
		if (tokens == null)
		{
			tokens = new HashMap<String, String>();
			session.setAttribute(CRUX_SYNC_TOKEN_ATTR, tokens);
		}
		
		return tokens;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Boolean> getInUseTokens()
	{
		Map<String, Boolean> inUsetokens = (Map<String, Boolean>) this.session.getAttribute(CRUX_SYNC_TOKEN_IN_USE_ATTR); 
		if (inUsetokens == null)
		{
			inUsetokens = new HashMap<String, Boolean>();
			session.setAttribute(CRUX_SYNC_TOKEN_IN_USE_ATTR, inUsetokens);
		}
		
		return inUsetokens;
	}
}
