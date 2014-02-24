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
package org.cruxframework.crux.core.rebind.rpc;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.rpc.ProxyCreator;
import com.google.gwt.user.rebind.rpc.ServiceInterfaceProxyGenerator;

/**
 * This class overrides, through CruxProxyCreator, the GWT Service Generator to add a wrapper class around the original generated class. 
 * 
 * <p>
 * The wrapper has two goals:<br>
 *  - Point all requests that does not inform an endPoint to the Crux FrontController Servlet.<br>
 *  - Handle security issues like SynchronizationToken for sensitive methods.  
 * </p>
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxServiceGenerator extends ServiceInterfaceProxyGenerator
{
	@Override
	protected ProxyCreator createProxyCreator(JClassType remoteService)
	{
		return new CruxProxyCreator(remoteService);
	}
}
