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
package org.cruxframework.crux.smartfaces.rebind.list;

import java.util.Set;

import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ExpressionDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBindPathFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasPagedDataProviderFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.list.AbstractComboBox.OptionsRenderer;
import org.cruxframework.crux.smartfaces.client.list.ComboBox;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
//TODO Create another factory to separate the two combo types
@DeclarativeFactory(targetWidget = ComboBox.class, id = "comboBox", library = Constants.LIBRARY_NAME, 
					description = "Combobox component that uses a data provider to display a list of items or widgets")
@TagChildren({ 
	@TagChild(HasPagedDataProviderFactory.PagedDataProviderChildren.class), 
	@TagChild(value = AbstractComboBoxFactory.OptionsProcessor.class, autoProcess = false) 
})
public class AbstractComboBoxFactory extends AbstractPageableFactory<WidgetCreatorContext> implements HasBindPathFactory<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		JSONObject optionsRendererChild = null;
		JSONObject dataChild = null;
		JSONArray children = ensureChildren(context.getWidgetElement(), false, context.getWidgetId());
		for (int i=0; i< children.length(); i++)
		{
			JSONObject child = children.optJSONObject(i);
			if (getChildName(child).startsWith("options"))
			{
				optionsRendererChild = child;
			}
			else
			{
				dataChild = child;
			}
		}
		
		JClassType dataObject = getDataObject(context.getWidgetId(), dataChild);
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String className = getWidgetClassName()+"<"+dataObjectName+">";

		String comboBoxRenderer = createVariableName("comboBoxRenderer");
		String comboBoxRendererClassName = OptionsRenderer.class.getCanonicalName() + "<" + getValueType() + "," + dataObjectName + ">";
		
		
		out.print("final " + comboBoxRendererClassName + " " + comboBoxRenderer + " = ");
		
		if (!generateOptionRendererCreation(out, context, optionsRendererChild, dataObject, comboBoxRendererClassName))
		{
        	throw new CruxGeneratorException("Invalid child tag on widget ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}

		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+comboBoxRenderer+");");
	}

	protected String getValueType()
    {
	    return "String";
    }

	protected boolean generateOptionRendererCreation(SourcePrinter out, WidgetCreatorContext context, 
											JSONObject optionRendererElement, JClassType dataObject, 
											String comboBoxRendererClassName)
	{
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		DataObject dataObjectAnnotation = dataObject.getAnnotation(DataObject.class);
		if (dataObjectAnnotation == null)
		{
			throw new CruxGeneratorException("Invaid dataObject: "+dataObject.getQualifiedSourceName());
		}
		String dataObjectAlias = dataObjectAnnotation.value();
		JSONObject child = ensureFirstChild(optionRendererElement, true, context.getWidgetId());
		String dataObjectVariable = createVariableName("value");
		String bindingContextVariable = createVariableName("context");

		String labelAttr = optionRendererElement.optString("label");
		String valueAttr = optionRendererElement.optString("value");
		Set<String> converterDeclarations = new HashSet<String>();
		out.println("new " + comboBoxRendererClassName + "(){");
		generateBindingContextDeclaration(out, bindingContextVariable);
		
		String labelExpression = getExpression(dataObjectAlias, dataObjectVariable, bindingContextVariable, labelAttr, converterDeclarations);	
		String valueExpression = getExpression(dataObjectAlias, dataObjectVariable, bindingContextVariable, valueAttr, converterDeclarations);	

		if (child != null)
		{
			String childName = getChildName(child);
			
			if (childName.equals("widget"))
			{
				Set<String> converters = generateWidgetCreationForCellByTemplate(out, context, child, dataObject, bindingContextVariable);
				converterDeclarations.addAll(converters);
			}
			else if (childName.equals("widgetFactory"))
			{
				generateWidgetCreationForCellOnController(out, context, child, dataObject);
			}
			else
			{
				return false;
			}
		}
		else
		{
			out.println("@Override public "+IsWidget.class.getCanonicalName()+" createWidget(" + 
											dataObjectName + " " + dataObjectVariable + "){");
			out.println("return new " + Label.class.getCanonicalName() + "(" + labelExpression + ");");
			out.println("}");
		}
		out.println("@Override public " + getValueType() + " getValue(" + dataObjectName + " " + dataObjectVariable + "){");
		out.println("return " + valueExpression + ";");
		out.println("}");
		
		out.println("@Override public String getLabel(" + dataObjectName + " " + dataObjectVariable + "){");
		out.println("return " + labelExpression + ";");
		out.println("}");
		
		for (String converterDeclaration : converterDeclarations)
		{
			out.println(converterDeclaration);
		}
		out.println("};");
		return true;
	}

	private String getExpression(String dataObjectAlias, String dataObjectVariable, String bindingContextVariable, String labelAttr, Set<String> converterDeclarations)
    {
	    String labelExpression = null;
		PropertyBindInfo binding = getObjectDataBinding(labelAttr, null);
		if (binding != null)
		{
			labelExpression = binding.getDataObjectReadExpression(dataObjectVariable);
			String converterDeclaration = binding.getConverterDeclaration();
			if (converterDeclaration != null)
			{
				converterDeclarations.add(converterDeclaration);
			}
		}
		else
		{
			ExpressionDataBinding expressionBinding = getExpressionDataBinding(labelAttr, null);
			if (expressionBinding != null)
			{
				labelExpression = expressionBinding.getExpression(bindingContextVariable, dataObjectVariable, dataObjectAlias);
				// TODO converters
			}
			else
			{
				labelExpression = getDeclaredMessage(labelAttr);
			}
		}
		 return labelExpression;
    }
	
	@TagConstraints(tagName = "options", minOccurs = "1", maxOccurs = "1")
	@TagAttributesDeclaration({ 
		@TagAttributeDeclaration(value="value", required=true), 
		@TagAttributeDeclaration(value="label", required=true)
	})
	@TagChildren({
		@TagChild(value=WidgetListChildCreator.class, autoProcess=false)
	})
	public static class OptionsProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
	}
	
	@TagConstraints(minOccurs = "0", maxOccurs = "1")
	@TagChildren({
		@TagChild(WidgetFactoryChildCreator.class),
		@TagChild(WidgetFactoryControllerChildCreator.class)
	})
	public static class WidgetListChildCreator extends ChoiceChildProcessor<WidgetCreatorContext>{}
	
}