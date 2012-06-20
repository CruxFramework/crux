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
package org.cruxframework.crux.widgets.rebind;

import org.cruxframework.crux.widgets.client.dynatabs.Tab;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * generates a invoker for calling existing controllers/methods on a sibling tab.
 * @author Gesse S. F. Dafe
 */
@Deprecated
public class SiblingTabInvokerProxyCreator extends AbstractTabInvokerProxyCreator
{
	public SiblingTabInvokerProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
	protected String getTabMethodInvocationString()
	{
		return Tab.class.getName() + ".invokeOnSiblingTab";
	}	
}