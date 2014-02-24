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
package org.cruxframework.crux.gwt.rebind;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.screen.RequiresResizeFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.gwt.client.LayoutAnimationEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;

class AbstractLayoutPanelContext extends WidgetCreatorContext
{
	int animationDuration = 0;
	ArrayList<String> childProcessingAnimations;

	/**
	 * @param context
	 * @param command
	 */
	protected void addChildWithAnimation(String command)
	{
		childProcessingAnimations.add(command);
	}
}


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="animationDuration", type=Integer.class, processor=AbstractLayoutPanelFactory.AnimationDurationAttributeParser.class)
})
@TagEventsDeclaration({
	@TagEventDeclaration("onAnimationComplete"), 
	@TagEventDeclaration("onAnimationStep") 
})
public abstract class AbstractLayoutPanelFactory<C extends AbstractLayoutPanelContext> 
			    extends ComplexPanelFactory<C> implements RequiresResizeFactory
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class AnimationDurationAttributeParser extends AttributeProcessor<AbstractLayoutPanelContext>
	{
		public AnimationDurationAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
		public void processAttribute(SourcePrinter out, AbstractLayoutPanelContext context, String attributeValue)
		{
			context.animationDuration = Integer.parseInt(attributeValue);
			context.childProcessingAnimations = new ArrayList<String>();			
		}
	}
	
	@Override
    public void postProcess(SourcePrinter out, C context) throws CruxGeneratorException
    {
    	String widget = context.getWidget();
		
		if (context.animationDuration > 0)
		{
			String onAnimationComplete =context.readWidgetProperty("onAnimationComplete");
			String onAnimationStep =context.readWidgetProperty("onAnimationStep");
			
			if (!StringUtils.isEmpty(onAnimationComplete) || !StringUtils.isEmpty(onAnimationStep))
			{
				String layoutAnimationEvent = createVariableName("evt");
				String eventClassName = LayoutAnimationEvent.class.getCanonicalName()+"<"+getWidgetClassName()+">";

				String widgetClassName = getWidgetClassName();
				printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
				printlnPostProcessing(eventClassName+" "+layoutAnimationEvent+" = new "+eventClassName+"("+widget+", "+context.getWidgetId()+");");

				runChildProcessingAnimations(context.childProcessingAnimations);
				
				printlnPostProcessing(widget+".animate("+context.animationDuration+", new "+AnimationCallback.class.getCanonicalName()+"(){");
				printlnPostProcessing("public void onAnimationComplete(){");
				printlnPostProcessing("if (onAnimationComplete != null){");
				EvtProcessor.printPostProcessingEvtCall(onAnimationComplete, "onAnimationComplete", LayoutAnimationEvent.class, layoutAnimationEvent, this);
				printlnPostProcessing("}");
				printlnPostProcessing("}");
				printlnPostProcessing("public void onLayout(Layer layer, double progress){");
				printlnPostProcessing("if (onAnimationStep != null){");
				EvtProcessor.printPostProcessingEvtCall(onAnimationStep, "onAnimationStep", LayoutAnimationEvent.class, layoutAnimationEvent, this);
				printlnPostProcessing("}");
				printlnPostProcessing("}");
				printlnPostProcessing("});");				
			}
			else
			{
				runChildProcessingAnimations(context.childProcessingAnimations);
				printlnPostProcessing(widget+".animate("+context.animationDuration+");");
			}
		}		
    }
	
	/**
	 * 
	 * @param childProcessingAnimations
	 */
	protected void runChildProcessingAnimations(ArrayList<String> childProcessingAnimations)
	{
		for (int i=0; i<childProcessingAnimations.size(); i++)
		{
			String command = childProcessingAnimations.get(i);
			printlnPostProcessing(command);
		}
	}

	/**
	 * @param sizeUnit
	 * @return
	 */
	public static Unit getUnit(String sizeUnit)
	{
		Unit unit;
		if (!StringUtils.isEmpty(sizeUnit))
		{
			unit = Unit.valueOf(sizeUnit);
		}
		else
		{
			unit = Unit.PX;
		}
		return unit;
	}
}
