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
package br.com.sysmap.crux.core.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Helper class for create services and automatically associate them with 'rpc' 
 * entry point.
 * 
 * @author Thiago Bustamante
 */
public class Service 
{
	/**
	 * Creates a service and associate it with default crux entry point 
	 * for rpc calls.
	 * @param cls
	 * @return
	 */
	public static ServiceDefTarget create(Class<? extends RemoteService> cls)
	{
		ServiceDefTarget result = GWT.create(cls);
		
		if (result.getServiceEntryPoint() == null)
		{
			result.setServiceEntryPoint("crux/rpc");
		}
		return result;
	}
}
