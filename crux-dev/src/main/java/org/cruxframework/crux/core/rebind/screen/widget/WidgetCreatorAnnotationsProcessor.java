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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.json.JSONObject;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class WidgetCreatorAnnotationsProcessor
{
	private List<AttributeCreator> attributes;
	private List<EventCreator> events;
	private ChildrenProcessor children;
	
	/**
	 * Constructor
	 * @param type
	 * @param widgetCreator
	 */
	WidgetCreatorAnnotationsProcessor(Class<?> type, WidgetCreator<?> widgetCreator)
    {
		this.attributes = new AttributesAnnotationScanner(widgetCreator, type).scanAttributes();
		this.events = new EventsAnnotationScanner(widgetCreator, type).scanEvents();
		this.children = new ChildrenAnnotationScanner(widgetCreator, type).scanChildren();
    }
	
	/**
	 * @param out
	 * @param context
	 */
	void processAttributes(SourcePrinter out, WidgetCreatorContext context)
	{
		for (AttributeCreator creator : attributes)
        {
	        creator.createAttribute(out, context);
        }
	}

	/**
	 * @param out
	 * @param context
	 */
	void processEvents(SourcePrinter out, WidgetCreatorContext context)
	{
		for (EventCreator creator : events)
        {
	        creator.createEvent(out, context);
        }
	}

	/**
	 * @param out
	 * @param context
	 */
	void processChildren(SourcePrinter out, WidgetCreatorContext context)
	{
		if (children != null)
		{
			children.processChildren(out, context);
		}
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static interface AttributeCreator
	{
		void createAttribute(SourcePrinter out, WidgetCreatorContext context);
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static interface EventCreator
	{
		void createEvent(SourcePrinter out, WidgetCreatorContext context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class ChildrenProcessor
	{
		private Map<String, ChildProcessor> childrenProcessors = new HashMap<String, ChildProcessor>();
		
		abstract void processChildren(SourcePrinter out, WidgetCreatorContext context);

		
		/**
		 * @param out
		 * @param context
		 * @param childName
		 */
		void processChild(SourcePrinter out, WidgetCreatorContext context, String childName)
		{
			if (!childrenProcessors.containsKey(childName))
			{
				throw new CruxGeneratorException("Can not find ChildProcessor for ["+childName+"].");
			}
			childrenProcessors.get(childName).processChild(out, context);
		}
		
		/**
		 * @param childName
		 * @return
		 */
		boolean hasChildProcessor(String childName)
		{
			return childrenProcessors.containsKey(childName);
		}
		
		/**
		 * @param tagName
		 * @param childProcessor
		 */
		void addChildProcessor(String tagName, ChildProcessor childProcessor)
		{
			childrenProcessors.put(tagName, childProcessor);
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class ChildProcessor
	{
		private ChildrenProcessor childrenProcessor;
		abstract void processChild(SourcePrinter out, WidgetCreatorContext context);
		abstract void postProcessChild(SourcePrinter out, WidgetCreatorContext context);
		
		void processChildren(SourcePrinter out, WidgetCreatorContext context)
		{
			JSONObject childElement = context.getChildElement();
			if (childrenProcessor != null)
			{
				childrenProcessor.processChildren(out, context);
			}
			context.setChildElement(childElement);
			postProcessChild(out, context);
		}

		void setChildrenProcessor(ChildrenProcessor childrenProcessor)
		{
			this.childrenProcessor = childrenProcessor;
		}
	}
}
