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
package org.cruxframework.crux.gadget.server.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RPCServletUtils;

/**
 * This class overrides the Crux RemoteServiceServlet to to accept requests from the gadget proxy. 
 * 
 * <p>
 * The servlet is adapted to work as described on:<br>
 *  http://code.google.com/p/gwt-google-apis/wiki/GadgetsFAQ#How_can_I_get_GWT_RPC_to_work_in_a_Gadget?
 * </p>
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class GadgetRemoteServiceServlet extends org.cruxframework.crux.core.server.dispatch.RemoteServiceServlet
{
	private static final long serialVersionUID = 1209850549607480950L;

	@Override
	protected String readContent(HttpServletRequest request) throws ServletException, IOException
	{
//		return RPCServletUtils.readContent(request, null, "UTF-8");//iGoogle sends 'null' on charset
		return RPCServletUtils.readContent(request, null, null);
	}
	
	@Override
	protected void checkPermutationStrongName() throws SecurityException
	{
	}
}
