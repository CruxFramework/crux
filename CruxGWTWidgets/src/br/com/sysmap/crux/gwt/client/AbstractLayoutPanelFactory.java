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
package br.com.sysmap.crux.gwt.client;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.ComplexPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractLayoutPanelFactory<T extends ComplexPanel> extends ComplexPanelFactory<T>
{
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="animationDuration", type=Integer.class)
	})
	public void processAttributes(final WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		String animationDuration = context.getElement().getAttribute("_animationDuration");
		if (!StringUtils.isEmpty(animationDuration))
		{
			context.setAttribute("animationDuration", Integer.parseInt(animationDuration));
			context.setAttribute("animationCommands", new ArrayList<Command>());
		}
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onAnimationComplete"), 
		@TagEventDeclaration("onAnimationStep") 
	})
	public void processEvents(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public void postProcess(WidgetFactoryContext<T> context) throws InterfaceConfigException
    {
		final Element element = context.getElement();
		final T widget = context.getWidget();
		
		final List<Command> animationConstraints = (List<Command>) context.getAttribute("animationCommands");
		final Integer animationDuration = (Integer) context.getAttribute("animationDuration");
		if (animationDuration != null)
		{
			final Event onAnimationComplete = Events.getEvent("onAnimationComplete", element.getAttribute("_onAnimationComplete"));
			final Event onAnimationStep = Events.getEvent("onAnimationStep", element.getAttribute("_onAnimationStep"));
			if (onAnimationComplete != null  || onAnimationStep != null)
			{
				final LayoutAnimationEvent<T> animationEvent = new LayoutAnimationEvent<T>(widget, context.getWidgetId());
				addScreenLoadedHandler(new ScreenLoadHandler(){
					public void onLoad(ScreenLoadEvent screenLoadEvent)
					{
						setAnimationConstraints(animationConstraints);
						((AnimatedLayout)widget).animate(animationDuration, new AnimationCallback(){
							public void onAnimationComplete()
							{
								if (onAnimationComplete != null)
								{
									Events.callEvent(onAnimationComplete, animationEvent);
								}
							}
							public void onLayout(Layer layer, double progress)
							{
								if (onAnimationStep != null)
								{
									Events.callEvent(onAnimationStep, animationEvent);
								}
							}
						});
					}
				});
			}
			else
			{
				addScreenLoadedHandler(new ScreenLoadHandler(){
					public void onLoad(ScreenLoadEvent screenLoadEvent)
					{
						setAnimationConstraints(animationConstraints);
						((AnimatedLayout)widget).animate(animationDuration);
					}
				});
			}
		}		
    }
	
	/**
	 * 
	 * @param animationConstraints
	 */
	protected void setAnimationConstraints(List<Command> animationConstraints)
	{
		for (Command command : animationConstraints)
		{
			command.execute();
		}
	}

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
