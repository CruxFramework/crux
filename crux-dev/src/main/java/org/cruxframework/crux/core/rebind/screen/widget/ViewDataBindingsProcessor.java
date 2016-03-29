/*
 * Copyright 2014 cruxframework.org.
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

import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.binding.ExpressionBinder;
import org.cruxframework.crux.core.client.screen.binding.ExpressionBinder.BindingContext;
import org.cruxframework.crux.core.client.screen.binding.PropertyBinder;
import org.cruxframework.crux.core.client.utils.DOMUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.DataBindingProcessor;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewDataBindingsProcessor implements DataBindingProcessor
{
	private ViewFactoryCreator viewFactory;

	public ViewDataBindingsProcessor(ViewFactoryCreator viewFactory)
    {
		this.viewFactory = viewFactory;
    }
	
	@Override
    public String getDataObjectAlias(String dataObject)
    {
	    return dataObject;
    }
	
	@Override
    public String getNativeUiObjectExpression(String elementId)
    {
	    return DOMUtils.class.getCanonicalName() + ".getElementById({0}," + EscapeUtils.quote(elementId) + ")";
    }

	@Override
    public void processBindings(SourcePrinter out, WidgetCreatorContext context)
    {
		processDataObjectBindings(out, context);
		processDataExpressionBindings(out, context);
    }
	
	/**
	 * Retrieve the variable name for the dataObjectBinder associated with the given alias.
	 * @param dataObjectAlias
	 * @param out
	 * @return
	 */
	protected String getDataObjectBinderVariable(String dataObjectAlias, SourcePrinter out)
	{
		return viewFactory.getDataObjectBinderVariable(dataObjectAlias, out);
	}

	/**
	 * Process any dataObject binding expression on this widget
	 * @param out
	 * @param context
	 * @param dataObjectBinderVariables 
	 */
	protected void processDataExpressionBindings(SourcePrinter out, WidgetCreatorContext context)
    {
		Iterator<ExpressionDataBinding> expressionBindings = context.iterateExpressionBindings();
		
		try
		{
			while (expressionBindings.hasNext())
			{
				ExpressionDataBinding expressionBinding = expressionBindings.next();

				String expressionBinder = ViewFactoryCreator.createVariableName("expressionBinder");
				out.println(ExpressionBinder.class.getCanonicalName() + " " + expressionBinder + " = "
						+ "new " + ExpressionBinder.class.getCanonicalName() + "<"+expressionBinding.getWidgetClassName()+">(){");

				for (String converterDeclaration: expressionBinding.getConverterDeclarations())
				{
					out.println(converterDeclaration);
				}

				out.println("public void updateExpression(" + BindingContext.class.getCanonicalName() +" context){");
				out.println(expressionBinding.getWriteExpression("context"));
				out.println("}");

				out.println("};");

				Iterator<String> dataObjects = expressionBinding.iterateDataObjects();

				while (dataObjects.hasNext())
				{
					String dataObjectAlias = dataObjects.next();
					String dataObjectBinder = getDataObjectBinderVariable(dataObjectAlias, out);

					out.println(dataObjectBinder + ".addExpressionBinder(" + EscapeUtils.quote(context.getWidgetId()) 
							+ ", " + expressionBinder + ");");
				}
			}
		}
		catch(NoSuchFieldException e)
		{
			throw new CruxGeneratorException("Error processing data binding expression.", e);
		}
    }

	/**
	 * Process any dataObject binding on this widget
	 * @param out
	 * @param context
	 * @return 
	 */
	protected void processDataObjectBindings(SourcePrinter out, WidgetCreatorContext context)
    {
		Iterator<String> dataObjects = context.iterateObjectDataBindingObjects();
		
		while (dataObjects.hasNext())
		{
			String dataObjectAlias = dataObjects.next();
			ObjectDataBinding dataBindingInfo = context.getObjectDataBinding(dataObjectAlias);
			
			String dataObjectClassName = dataBindingInfo.getDataObjectClassName();
			String dataObjectBinder = getDataObjectBinderVariable(dataObjectAlias, out);
			Iterator<PropertyBindInfo> propertyBindings = dataBindingInfo.iterateBindings();
			
			while (propertyBindings.hasNext())
			{
				PropertyBindInfo bind = propertyBindings.next(); 
				out.println(dataObjectBinder + ".addPropertyBinder(" + EscapeUtils.quote(context.getWidgetId()) + 
						", new " + PropertyBinder.class.getCanonicalName() + "<" + dataObjectClassName + ", "+ bind.getWidgetClassName() +">(){");
				String converterDeclaration = bind.getConverterDeclaration();
				if (converterDeclaration != null)
				{
					out.println(converterDeclaration);
				}

				out.println("public void copyTo(" + dataObjectClassName + " dataObject){");
				out.println(bind.getWriteExpression("dataObject"));
				out.println("}");
				
				out.println("public void copyFrom(" + dataObjectClassName + " dataObject){");
				out.println(bind.getReadExpression("dataObject"));
				out.println("}");
				
				if (!StringUtils.isEmpty(bind.getUiObjectExpression()))
				{
					out.println("public "+Element.class.getCanonicalName()+" getUiElement(){");
					
					if (bind.isNativeElement())
					{
						out.println("return " + bind.getUIObjectVar(PropertyBindInfo.WIDGET_VAR_REF) + ";");
					}
					else
					{
						out.println("return " + bind.getUIObjectVar(PropertyBindInfo.WIDGET_VAR_REF) + ".getElement();");
					}
					out.println("}");
				}
				out.println("}, "+bind.isBoundToAttribute()+");");
			}
		}
    }
}