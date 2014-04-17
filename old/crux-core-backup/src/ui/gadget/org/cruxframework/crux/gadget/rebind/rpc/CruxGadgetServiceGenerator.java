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
package org.cruxframework.crux.gadget.rebind.rpc;

import org.cruxframework.crux.core.rebind.rpc.CruxServiceGenerator;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.rpc.ProxyCreator;

/**
 * This class overrides, through CruxGadgetProxyCreator, the Crux Service Generator to add a wrapper class around the original generated class. 
 * 
 * <p>
 * The wrapper redirects the original request through a proxy, as described on:<br>
 *  http://code.google.com/p/gwt-google-apis/wiki/GadgetsFAQ#How_can_I_get_GWT_RPC_to_work_in_a_Gadget?
 * </p>
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxGadgetServiceGenerator extends CruxServiceGenerator
{
	@Override
	protected ProxyCreator createProxyCreator(JClassType remoteService)
	{
		return new CruxGadgetProxyCreator(remoteService);
	}
}
