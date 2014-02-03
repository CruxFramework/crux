/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.rest;

import org.cruxframework.crux.core.client.rest.RestProxy.TargetRestService;
import org.cruxframework.crux.core.rebind.AbstractGenerator;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * This class creates a client proxy for calling rest services
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxRestProxyGenerator extends AbstractGenerator
{
	@Override
    protected AbstractProxyCreator createProxy(TreeLogger logger, GeneratorContext ctx, JClassType baseIntf) throws UnableToCompleteException
    {
		TargetRestService restService = baseIntf.getAnnotation(TargetRestService.class);
		if (restService != null)
		{
			return new CruxRestProxyCreatorFromServeMetadata(logger, ctx, baseIntf);
		}
		return new CruxRestProxyCreatorFromClientMetadata(logger, ctx, baseIntf);
    }
}
