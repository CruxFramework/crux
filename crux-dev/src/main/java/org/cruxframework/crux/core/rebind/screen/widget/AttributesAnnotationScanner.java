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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorAnnotationsProcessor.AttributeCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute.SameAsType;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute.WidgetReference;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class AttributesAnnotationScanner
{
	private final WidgetCreator<?> widgetCreator;
	
	AttributesAnnotationScanner(WidgetCreator<?> widgetCreator)
    {
		this.widgetCreator = widgetCreator;
    }
	
	/**
	 * @param factoryClass
	 * @throws CruxGeneratorException
	 */
	List<AttributeCreator> scanAttributes() throws CruxGeneratorException
	{
		ArrayList<AttributeCreator> attributes = new ArrayList<AttributeCreator>();
		scanAttributes(widgetCreator.getClass(), widgetCreator.getWidgetClass(), attributes, new HashSet<String>());
		return attributes;
	}
	
	/**
	 * @param factoryClass
	 * @param attributes
	 * @param added
	 * @throws CruxGeneratorException
	 */
	void scanAttributes(Class<?> factoryClass, Class<?> targetUIClass, List<AttributeCreator> attributes, Set<String> added) throws CruxGeneratorException
	{
		try
        {
			TagAttributes attrs = factoryClass.getAnnotation(TagAttributes.class);
			if (attrs != null)
			{
				for (TagAttribute attr : attrs.value())
				{
					String attrName = attr.value();
					if (!added.contains(attrName))
					{
						added.add(attrName);
						if (isValidName(attrName))
						{
							attributes.add(createAttributeProcessor(targetUIClass, attr));
						}
						else
						{
							throw new CruxGeneratorException("Error generating widget factory. Invalid attribute name: ["+attrName+"].");
						}
					}
				}
			}
	        Class<?> superclass = factoryClass.getSuperclass();
	        if (superclass!= null && !superclass.equals(Object.class))
	        {
	        	scanAttributes(superclass, targetUIClass, attributes, added);
	        }
	        Class<?>[] interfaces = factoryClass.getInterfaces();
	        for (Class<?> interfaceClass : interfaces)
	        {
	        	scanAttributes(interfaceClass, targetUIClass, attributes, added);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @param factoryClass
	 * @param attr
	 * @return
	 */
	private AttributeCreator createAttributeProcessor(Class<?> targetUIClass, TagAttribute attr)
    {
		final String attrName = attr.value();
		final String setterMethod;
		boolean nestedProperty = false;
		boolean expressionDataBindingOnly = false;
		String widgetPropertyPath = (!StringUtils.isEmpty(attr.property()))?attr.property():attrName;
		if (!StringUtils.isEmpty(attr.method()))
		{
			setterMethod = attr.method();
			expressionDataBindingOnly = true;
		}
		else
		{
			nestedProperty = widgetPropertyPath.contains(".");
			if (nestedProperty)
			{
				String[] properties = RegexpPatterns.REGEXP_DOT.split(widgetPropertyPath);
				StringBuilder expression = new StringBuilder();
				for(int i=0; i< properties.length-1;i++)
				{
					expression.append(ClassUtils.getGetterMethod(properties[i])+"().");
				}
				expression.append(ClassUtils.getSetterMethod(properties[properties.length-1]));
				setterMethod = expression.toString();
			}
			else
			{
				setterMethod = ClassUtils.getSetterMethod(widgetPropertyPath);
			}
		}
		
		Class<?> type = attr.type();
		Class<?> widgetType = attr.widgetType().equals(SameAsType.class)?type:attr.widgetType();

    	final boolean hasProcessor = !(AttributeProcessor.NoProcessor.class.isAssignableFrom(attr.processor()));
		if (!hasProcessor && !(nestedProperty || ClassUtils.hasValidSetter(targetUIClass, setterMethod, widgetType)))
		{//TODO: implement method check for nested property.
			throw new CruxGeneratorException("Error generating widget factory. Widget does not have a valid setter for attribute: ["+attrName+"].");
		}
		final boolean isWidgetReferencedType = WidgetReference.class.isAssignableFrom(type);
		return doCreateAttributeProcessor(attrName, setterMethod, widgetType, 
												   widgetPropertyPath, 
												   expressionDataBindingOnly, isWidgetReferencedType, attr);
    }

	/**
	 * 
	 * @param attrName
	 * @param setterMethod
	 * @param widgetType
	 * @param widgetPropertyPath
	 * @param expressionDataBindingOnly
	 * @param isWidgetReferencedType
	 * @param attr
	 * @return
	 */
	private AttributeCreator doCreateAttributeProcessor(final String attrName, final String setterMethod, 
																 Class<?> widgetType, 
																 final String widgetPropertyPath, 
																 final boolean expressionDataBindingOnly, final boolean isWidgetReferencedType, 
																 TagAttribute attr)
    {
        try
        {
        	final boolean hasProcessor = !(AttributeProcessor.NoProcessor.class.isAssignableFrom(attr.processor()));
        	Class<?> processorClass = (hasProcessor?attr.processor():null);
        	final Method method = getAtributeProcessorMethod(processorClass);
        	final AttributeProcessor<?> processor = hasProcessor
        											?(AttributeProcessor<?>) processorClass.getConstructor(new Class<?>[]{WidgetCreator.class}).newInstance(widgetCreator)
        											:null;
        		
    		final boolean supportsDataBinding = attr.supportsDataBinding();
    		final boolean isStringExpression = String.class.isAssignableFrom(widgetType);
    		final boolean supportsI18N = isStringExpression && attr.supportsI18N();
    		final boolean dataBindingTargetsAttributes = attr.dataBindingTargetsAttributes();
    		final boolean isEnumExpression = widgetType.isEnum();
    		final boolean isPrimitiveExpression = widgetType.isPrimitive();
    		final boolean supportsResources = attr.supportsResources();
    		final String typeName = widgetType.getCanonicalName();
    		final Device[] supportedDevices = attr.supportedDevices();
    		final ProcessingTime processingTime = attr.processingTime();
        	
        	return new AttributeCreator()
        	{
        		public void createAttribute(SourcePrinter out, WidgetCreatorContext context)
        		{
        			if (widgetCreator.isCurrentDeviceSupported(supportedDevices))
        			{
        				String attrValue = context.readWidgetProperty(attrName);

        				if (supportsDataBinding && !isWidgetReferencedType)
        				{
        					PropertyBindInfo binding = widgetCreator.getObjectDataBinding(attrValue, widgetPropertyPath, dataBindingTargetsAttributes, 
        						context.getDataBindingProcessor());
        					if (!expressionDataBindingOnly && binding != null)
        					{
        						context.registerObjectDataBinding(binding);
        						return;
        					}
        					else
        					{
        						ExpressionDataBinding expressionBinding = widgetCreator.getExpressionDataBinding(attrValue, widgetCreator.getWidgetClassName(),
        							widgetPropertyPath, null, null,
        							context.getDataBindingProcessor(), setterMethod, typeName);
        						if (expressionBinding != null)
        						{
        							context.registerExpressionDataBinding(expressionBinding);
        							return;
        						}
        					}	
        				}
        				if (hasProcessor)
        				{
        					invokeAttributeProcessor(attrName, method, processor, processingTime, out, context, attrValue);
        				}
        				else
        				{
        					printAttributeExpression(setterMethod, isWidgetReferencedType, isStringExpression, supportsI18N, isEnumExpression,
                                isPrimitiveExpression, supportsResources, typeName, processingTime, out, context, attrValue);
        				}
        			}
        		}
        	};
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Error creating AttibuteProcessor.", e);
        }
    }

	/**
	 * @param processorClass
	 * @return
	 */
	private Method getAtributeProcessorMethod(Class<?> processorClass)
    {
		if (processorClass == null)
		{
			return null;
		}
	    try 
	    {
			return processorClass.getMethod("processAttributeInternal", new Class<?>[]{SourcePrinter.class, WidgetCreatorContext.class, String.class});
		}
	    catch (Exception e) 
		{
			return null;
		}
    }

	private String getExpression(WidgetCreatorContext context, 
		String typeName, 
		boolean isStringExpression, 
		boolean isEnumExpression, boolean isPrimitiveExpression, 
		boolean isWidgetReferencedType, String attrValue, 
		boolean supportsI18N, boolean supportsResources)
	{
		String expression = null;

		if (StringUtils.isEmpty(attrValue))
		{
			expression = null;
		}
		else if (isWidgetReferencedType)
		{
			if (!hasReferencedWidget(attrValue, context))
			{
				throw new CruxGeneratorException("There is no " + typeName + " named ["+attrValue+
					"] on the view ["+widgetCreator.getView().getId()+"]");
			}

			if (!typeName.equals(Widget.class.getCanonicalName()))
			{
				expression = "(" + typeName + ")" +widgetCreator.getViewVariable() + ".getWidget(" + EscapeUtils.quote(attrValue) + ")";
			}
			else
			{
				expression = widgetCreator.getViewVariable() + ".getWidget(" + EscapeUtils.quote(attrValue) + ")";
			}
		}
		else if (supportsI18N)
		{
			expression = widgetCreator.resolveI18NString(attrValue);
		}
		else if (supportsResources)
		{
			expression = widgetCreator.getResourceAccessExpression(attrValue);
		}
		else if (isStringExpression)
		{
			expression = EscapeUtils.quote(attrValue);
		}
		else if (isEnumExpression)
		{
			expression = typeName+".valueOf("+EscapeUtils.quote(attrValue)+")";
		}
		else if (isPrimitiveExpression)
		{
			expression = "("+typeName+")"+attrValue;
		}
		else
		{
			expression = attrValue;
		}
		//TODO: checar o tipo da expressao... se for boolean, integer, etc...fazer o parseXxx, para garantir que eh um valor valido... senao o erro gerado eh de dificil compreensao
		return expression;
	}
	
	private boolean hasReferencedWidget(String widgetId, WidgetCreatorContext context)
	{
		if (widgetCreator.getView().getWidget(widgetId) != null)
		{
			return true;
		}
		return hasReferencedWidget(widgetId, context, context.getWidgetElement());
	}
	
	private boolean hasReferencedWidget(String widgetId, WidgetCreatorContext context, JSONObject widgetElement)
	{
		JSONArray children = widgetCreator.ensureChildren(widgetElement, true, widgetId);
		if (children != null)
		{
			for (int i = 0; i < children.length(); i++)
            {
	            JSONObject child = children.optJSONObject(i);
	            if (child != null && widgetCreator.isWidget(child))
	            {
	            	String childId = child.optString("id");
	            	if (childId != null && childId.equals(widgetId))
	            	{
	            		return true;
	            	}
	            	if (hasReferencedWidget(widgetId, context, child))
	            	{
	            		return true;
	            	}
	            }
            }
		}
		return false;
	}

	private void invokeAttributeProcessor(final String attrName, final Method method, final AttributeProcessor<?> processor,
        final ProcessingTime processingTime, SourcePrinter out, WidgetCreatorContext context, String attrValue)
    {
		if (!StringUtils.isEmpty(attrValue))
		{
			try
			{
				switch (processingTime)
				{
					case afterAllWidgetsOnView:
						maybePrintWidgetDeclaration(context);
						
						method.invoke(processor, widgetCreator.getPostProcessingPrinter(), context, attrValue);
						break;
					default:
						method.invoke(processor, out, context, attrValue);
						break;
				}
			}
			catch (Exception e)
			{
				
				throw new CruxGeneratorException("Error running attribute processor for attribute ["+attrName+"], " +
					"from widget ["+context.getWidgetId()+"], on screen ["+widgetCreator.getView().getId()+"].", e);
			}
		}
    }

	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean isValidName(String name)
	{
		return name != null && name.length() > 0 && RegexpPatterns.REGEXP_WORD.matcher(name).matches() 
		                                         && !Character.isDigit(name.charAt(0));
	}

	private void maybePrintWidgetDeclaration(WidgetCreatorContext context)
    {
        if (!widgetCreator.isWidgetRegisteredForPostProcessing(context.getWidgetId()))
        {
        	widgetCreator.registerWidgetForPostProcessing(context.getWidgetId());
        	String widgetClassName = widgetCreator.getWidgetClassName();
        	String widgetDecl = "final "+widgetClassName+" " + context.getWidget() + " = ("+widgetClassName+")" + 
        						widgetCreator.getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");";
        	widgetCreator.printlnPostProcessing(widgetDecl);
        }
    }
	
	private void printAttributeExpression(final String setterMethod, final boolean isWidgetReferencedType, final boolean isStringExpression,
        final boolean supportsI18N, final boolean isEnumExpression, final boolean isPrimitiveExpression,
        final boolean supportsResources, final String typeName, final ProcessingTime processingTime, SourcePrinter out,
        WidgetCreatorContext context, String attrValue)
    {
        String expression = getExpression(context, typeName, isStringExpression, isEnumExpression, 
        	isPrimitiveExpression, isWidgetReferencedType, attrValue, supportsI18N, supportsResources);
        if (expression != null)
        {
        	switch (processingTime)
        	{
        		case afterAllWidgetsOnView:
        			maybePrintWidgetDeclaration(context);

        			widgetCreator.printlnPostProcessing(context.getWidget()+"."+setterMethod+"("+expression+");");
        			break;
        		default:
        			out.println(context.getWidget()+"."+setterMethod+"("+expression+");");
        			break;
        	}
        }
    }	
}
