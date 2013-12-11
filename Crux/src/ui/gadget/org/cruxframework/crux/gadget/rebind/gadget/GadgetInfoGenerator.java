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
package org.cruxframework.crux.gadget.rebind.gadget;

import org.cruxframework.crux.core.rebind.AbstractGenerator;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetInfoGenerator extends AbstractGenerator
{
	@Override
    protected AbstractProxyCreator createProxy(TreeLogger logger, GeneratorContext ctx, JClassType baseIntf) throws UnableToCompleteException
    {
	    return new GadgetInfoProxyCreator(logger, ctx, baseIntf);
    }
}
