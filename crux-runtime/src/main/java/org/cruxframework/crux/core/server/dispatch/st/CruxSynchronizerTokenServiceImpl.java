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
package org.cruxframework.crux.core.server.dispatch.st;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.cruxframework.crux.core.shared.rpc.st.CruxSynchronizerTokenService;
import org.cruxframework.crux.core.utils.ClassUtils;

import com.google.gwt.user.server.Base64Utils;

/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxSynchronizerTokenServiceImpl implements CruxSynchronizerTokenService, CruxSynchronizerTokenHandler
{
	private static final String EXPECTED_TOKENS_ATT = "__CRUX_SYNC_TOKEN_";
	private static final String PROCESSING_TOKENS_ATT = "__CRUX_SYNC_TOKEN_IN_USE_";

	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static Lock readLock = readWriteLock.readLock();
	private static Lock writeLock = readWriteLock.writeLock();
	private HttpServletRequest request;
	private HttpSession session;

	/**
	 * @see org.cruxframework.crux.core.server.dispatch.RequestAware#setRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	/**
	 * @see org.cruxframework.crux.core.server.dispatch.SessionAware#setSession(javax.servlet.http.HttpSession)
	 */
	public void setSession(HttpSession session)
	{
		this.session = session;
	}		
	
	/**
	 * @see org.cruxframework.crux.core.shared.rpc.st.CruxSynchronizerTokenService#getSynchronizerToken(java.lang.String)
	 */
	public String getSynchronizerToken(String methodFullSignature)
	{
		writeLock.lock();
		try
		{
			if (createToken(methodFullSignature))
			{
				return getExpectedToken(methodFullSignature);
			}
		}
		finally
		{
			writeLock.unlock();
		}
		
		return null;
	}

	/**
	 * @see org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#isMethodRunning(java.lang.String)
	 */
	public boolean isMethodRunning(String methodFullSignature)
	{
		readLock.lock();
		try
		{
			return getProcessingTokens().containsKey(methodFullSignature);
		}
		finally
		{
			readLock.unlock();
		}
	}

	/**
	 * @see org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#startMethod(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public void startMethod(String methodFullSignature) throws InvalidTokenException
	{
		writeLock.lock();
		try
		{
			String expectedToken = getExpectedToken(methodFullSignature);
			String receivedToken = request.getParameter(CRUX_SYNC_TOKEN_PARAM);
			if (expectedToken != null && receivedToken != null && expectedToken.equals(receivedToken))
			{
				unregisterExpectedToken(methodFullSignature);
				registerProcessingToken(methodFullSignature);
			}
			else
			{
				throw new InvalidTokenException("Invalid Synchronizer Token for method ["+methodFullSignature+"]. Possible CSRF attack.");
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}

	private void registerProcessingToken(String methodFullSignature) 
	{
		getProcessingTokens().put(methodFullSignature, true);
		forceProcessingTokensReplication();
	}
	
	private void unregisterProcessingToken(String methodFullSignature) 
	{
		getProcessingTokens().remove(methodFullSignature);
		forceProcessingTokensReplication();
	}

	private void unregisterExpectedToken(String methodFullSignature) 
	{
		getExpectedTokens().remove(methodFullSignature);
		forceExpectedTokensReplication();
	}
	

	private void registerExpectedToken(String methodFullSignature, String token) 
	{
		getExpectedTokens().put(methodFullSignature, token);
		forceExpectedTokensReplication();
	}
	
	private String getExpectedToken(String methodSignature)
	{
		 return getExpectedTokens().get(methodSignature);
	}
	
	/**
	 * @see org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#endMethod(java.lang.String)
	 */
	public void endMethod(String methodFullSignature)
	{
		writeLock.lock();
		try
		{
			unregisterProcessingToken(methodFullSignature);
		}
		finally
		{
			writeLock.unlock();
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.server.dispatch.st.CruxSynchronizerTokenHandler#getMethodDescription(java.lang.reflect.Method)
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
		if (!isMethodRunning(methodFullSignature))
		{
			String token = generateRandomToken();
			registerExpectedToken(methodFullSignature, token);
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
	 * This is only necessary because Google AppEngine does not replicates sessions the same way other containers do.
	 * The replication only occurs when you call the <code>setAttribute</code> method on the <code>session</code> object, 
	 * storing an object different than the previous one stored for the desired key.
	 * <a href="https://developers.google.com/appengine/docs/java/config/appconfig#Enabling_Sessions">AppEngine Documentation</a>  
	 */
	private void forceProcessingTokensReplication() 
	{
		Map<String, Boolean> processing = getProcessingTokens();
		Map<String, Boolean> clone = new HashMap<String, Boolean>();
		clone.putAll(processing);
		session.setAttribute(PROCESSING_TOKENS_ATT, clone);
	}

	/**
	 * This is only necessary because Google AppEngine does not replicates sessions the same way other containers do.
	 * The replication only occurs when you call the <code>setAttribute</code> method on the <code>session</code> object, 
	 * storing an object different than the previous one stored for the desired key.
	 * <a href="https://developers.google.com/appengine/docs/java/config/appconfig#Enabling_Sessions">AppEngine Documentation</a>  
	 */
	private void forceExpectedTokensReplication() 
	{
		Map<String, String> expected = getExpectedTokens();
		Map<String, String> clone = new HashMap<String, String>();
		clone.putAll(expected);
		session.setAttribute(EXPECTED_TOKENS_ATT, clone);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getExpectedTokens()
	{
		Map<String, String> tokens = (Map<String, String>) this.session.getAttribute(EXPECTED_TOKENS_ATT); 
		if (tokens == null)
		{
			tokens = new HashMap<String, String>();
			session.setAttribute(EXPECTED_TOKENS_ATT, tokens);
		}
		return tokens;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Boolean> getProcessingTokens()
	{
		Map<String, Boolean> inUsetokens = (Map<String, Boolean>) this.session.getAttribute(PROCESSING_TOKENS_ATT); 
		if (inUsetokens == null)
		{
			inUsetokens = new HashMap<String, Boolean>();
			session.setAttribute(PROCESSING_TOKENS_ATT, inUsetokens);
		}
		return inUsetokens;
	}
}
