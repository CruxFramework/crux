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
package org.cruxframework.crux.core.rebind.screen.widget.creator.direction;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;

import com.google.gwt.i18n.shared.AnyRtlDirectionEstimator;
import com.google.gwt.i18n.shared.FirstStrongDirectionEstimator;
import com.google.gwt.i18n.shared.WordCountDirectionEstimator;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DirectionEstimatorAttributeParser<C extends WidgetCreatorContext> extends AttributeProcessor<C>
{
	public DirectionEstimatorAttributeParser(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	@Override
    public void processAttribute(SourcePrinter out, C context, String attributeValue)
    {
		DirectionEstimator estimator = DirectionEstimator.valueOf(attributeValue);
		
		switch (estimator) 
		{
			case anyRtl:
				out.println(context.getWidget()+".setDirectionEstimator("+AnyRtlDirectionEstimator.class.getCanonicalName()+".get());");
				break;
			case firstStrong: 
				out.println(context.getWidget()+".setDirectionEstimator("+FirstStrongDirectionEstimator.class.getCanonicalName()+".get());");
				break;
			case wordCount: 
				out.println(context.getWidget()+".setDirectionEstimator("+WordCountDirectionEstimator.class.getCanonicalName()+".get());");
				break;
				
			default:
				out.println(context.getWidget()+".setDirectionEstimator(true);");
		}
    }
}
