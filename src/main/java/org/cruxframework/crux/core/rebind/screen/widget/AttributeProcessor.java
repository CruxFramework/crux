/*
 * Copyright 2010 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AttributeProcessor<C extends WidgetCreatorContext> extends AbstractProcessor
{
	/**
	 * @param widgetCreator
	 */
	public AttributeProcessor(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	public static abstract class NoParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public NoParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }
	}
	
	/**
	 * @param out
	 * @param context
	 * @param attributeValue
	 */
	public abstract void processAttribute(SourcePrinter out, C context, String attributeValue);
	
	/**
	 * Do not call this method.
	 * Work around to invoke processAttribute with reflection, once it declares a generic parameter and java
	 * fails in some cases (generic information is not completely available in runtime).
	 * 
	 * @param out
	 * @param context
	 * @param attributeValue
	 * @throws CruxGeneratorException
	 */
	@SuppressWarnings("unchecked")
	public final void processAttributeInternal(SourcePrinter out, WidgetCreatorContext context, String attributeValue) throws CruxGeneratorException
	{
		processAttribute(out, (C) context, attributeValue);
	}	
}
