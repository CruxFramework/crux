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

import javax.servlet.http.HttpSession;

import org.cruxframework.crux.core.server.dispatch.RequestAware;
import org.cruxframework.crux.core.server.dispatch.SessionAware;


/**
 * @author Thiago da Rosa de Bustamante 
 * 
 */
public interface CruxSynchronizerTokenHandler extends SessionAware, RequestAware
{
	void startMethod(String methodFullSignature) throws InvalidTokenException;
	void endMethod(String methodFullSignature);
	boolean isMethodRunning(String methodFullSignature);
	void setSession(HttpSession session);
	String getMethodDescription(Method method);
}