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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.DataWidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasBindPathFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasPagedDataProviderFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.list.AbstractComboBox;
import org.cruxframework.crux.smartfaces.client.list.AbstractComboBox.OptionsRenderer;
import org.cruxframework.crux.smartfaces.client.list.ComboBox;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.IsWidget;

class ComboBoxContext extends WidgetCreatorContext
{
	JClassType dataObject;
	String valuePath;
	String labelPath;
	String expression;
	String dataObjectVariable;
	String valueType  = "String";
}
//TODO Create another factory to separate the two combo types
@DeclarativeFactory(targetWidget = ComboBox.class, id = "comboBox", library = Constants.LIBRARY_NAME, description = "Combobox component that uses a data provider to display a list of item or widgets")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="isFilterable",defaultValue="false", type=Boolean.class,required=false,description="If true, the user can type on textbox to apply a filter"),
	@TagAttributeDeclaration(value="width", type=String.class)
})
@TagChildren({ 
	@TagChild(HasPagedDataProviderFactory.PagedDataProviderChildren.class), 
	@TagChild(value = AbstractComboBoxFactory.OptionsProcessor.class, autoProcess = false) 
})
public class AbstractComboBoxFactory extends AbstractPageableFactory<ComboBoxContext> implements HasBindPathFactory<ComboBoxContext>
{
	@Override
	public ComboBoxContext instantiateContext()
	{
		return new ComboBoxContext();
	}

	@Override
	public void instantiateWidget(SourcePrinter out, ComboBoxContext context) throws CruxGeneratorException
	{
		JSONObject optionsRendererChild = null;
		JSONObject dataChild = null;
		JSONArray children = ensureChildren(context.getWidgetElement(), false, context.getWidgetId());

		for (int i = 0; i < children.length(); i++)
		{
			JSONObject child = children.optJSONObject(i);
			if (getChildName(child).equals("optionsRenderer"))
			{
				optionsRendererChild = child;
			} else
			{
				dataChild = child;
			}
		}

		context.dataObject = getDataObject(context.getWidgetId(), dataChild);
		String dataObjectName = context.dataObject.getParameterizedQualifiedSourceName();
		String className = getWidgetClassName() + "<" + dataObjectName + ">";

		String comboBoxRenderer = createVariableName("comboBoxRenderer");
		String comboBoxRendererClassName = OptionsRenderer.class.getCanonicalName() + "<" + context.valueType + "," + context.dataObject.getParameterizedQualifiedSourceName() + ">";
		out.print("final " + comboBoxRendererClassName + " " + comboBoxRenderer + " = ");

		generateOptionRendererCreation(out, context, optionsRendererChild, context.dataObject, comboBoxRendererClassName);
		// TODO por uma validacao pra ver se achou um optionsRenderer
		out.println("final " + className + " " + context.getWidget() + " = new " + className + "(" + comboBoxRenderer + ");");
		
		String width = context.readChildProperty("width");
		
		if(!width.isEmpty())
		{
			out.println(context.getWidget() +".setWidth("+EscapeUtils.quote(width)+");");
		}
		else
		{
			out.println(context.getWidget() +".setWidth(\"150px\");");
		}
		
	}

	protected void generateOptionRendererCreation(SourcePrinter out, ComboBoxContext context, JSONObject optionRendererElement, JClassType dataObject, String comboBoxRendererClassName)
	{
		context.expression = null;
		context.dataObjectVariable = createVariableName("value");

		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		out.println("new " + comboBoxRendererClassName + "(){");
		out.println("@Override public " + IsWidget.class.getCanonicalName() + " createWidget(" + dataObjectName + " " + context.dataObjectVariable + "){");

		JSONObject displayWidgetElement = ensureFirstChild(optionRendererElement, true, context.getWidgetId());
		if (displayWidgetElement != null)
		{
			createDisplayWidget(out, context, displayWidgetElement);
		}

		String labelPath = optionRendererElement.optString("labelPath");
		String labelConverter = optionRendererElement.optString("labelConverter");
		JClassType widgetClassType = getContext().getTypeOracle().findType(Label.class.getCanonicalName());
		String labelExpression = getExpression(out, context, widgetClassType, labelPath, labelConverter, context.dataObjectVariable);

		String valuePath = optionRendererElement.optString("valuePath");
		String valueConverter = optionRendererElement.optString("valueConverter");
		String valueExpression = context.expression;
		if (valueExpression == null)
		{
			JClassType comboBoxClassType = getContext().getTypeOracle().findType(ComboBox.class.getCanonicalName());
			valueExpression = getExpression(out, context, comboBoxClassType, valuePath, valueConverter, context.dataObjectVariable);
		}

		String styleName = AbstractComboBox.LABEL_ITEM;
		
		if (displayWidgetElement == null)
		{
			out.println(Label.class.getCanonicalName() +" labelItemCombo = new " + Label.class.getCanonicalName() + "(" + labelExpression + ");");
			out.println("labelItemCombo.setStyleName("+EscapeUtils.quote(styleName)+");");
			out.println(" return labelItemCombo;");
		}
		out.println("}");

		out.println("@Override public " + context.valueType + " getValue(" + context.dataObject.getParameterizedQualifiedSourceName() + " " + context.dataObjectVariable + "){");
		out.println("return " + valueExpression + ";");
		out.println("}");

		out.println("@Override public String getLabel(" + context.dataObject.getParameterizedQualifiedSourceName() + " " + context.dataObjectVariable + "){");
		out.println("return " + labelExpression + ";");
		out.println("}");

		out.println("};");
	}

	private void createDisplayWidget(SourcePrinter out, ComboBoxContext context, JSONObject displayWidgetElement)
	{
		String childName = getChildName(displayWidgetElement);

		if (childName.equals("displayWidget"))
		{
			JSONObject widgetElement = ensureFirstChild(displayWidgetElement, false, context.getWidgetId());
			String widgetClassName = getChildWidgetClassName(widgetElement);
			JClassType widgetClassType = getContext().getTypeOracle().findType(widgetClassName);

			ComboBoxWidgetConsumer consumer = new ComboBoxWidgetConsumer(getContext(), context, context.dataObject, context.dataObjectVariable, widgetClassType, getView().getId(), context.getWidgetId());

			String widgetId = createChildWidget(out, widgetElement, consumer, true, context);
			out.println("return " + widgetId + ";");
		} else
		{
			throw new CruxGeneratorException("Invalid child tag on widget [" + context.getWidgetId() + "]. View [" + getView().getId() + "]");
		}
	}

	private String getExpression(SourcePrinter out, ComboBoxContext context, JClassType widgetClassType, String bindPath, String bindConverter, String dataObjectVariable)
	{
		JClassType converterType = DataWidgetConsumer.getConverterType(out, getContext(), bindPath, bindConverter, context.dataObject, widgetClassType);
		String converterVariable = null;
		if (converterType != null)
		{
			converterVariable = createVariableName("__converter");
			out.println(converterType.getParameterizedQualifiedSourceName() + " " + converterVariable + " = new " + converterType.getParameterizedQualifiedSourceName() + "();");
		}

		try
		{
			return DataWidgetConsumer.getPropertyReadExpression(context.dataObject, dataObjectVariable, converterVariable, bindPath);
		} catch (NoSuchFieldException e)
		{
			throw new CruxGeneratorException("Invalid binding path [" + bindPath + "] on target dataobject [" + context.dataObject.getParameterizedQualifiedSourceName() + "]. Property not found. Widget [" + context.getWidgetId() + "] on View ["
					+ getView().getId() + "]");
		}
	}

	@TagConstraints(tagName = "optionsRenderer", minOccurs = "1", maxOccurs = "1")
	@TagAttributesDeclaration({ @TagAttributeDeclaration(required = true, value = "valuePath", type = String.class), @TagAttributeDeclaration(required = true, value = "labelPath", type = String.class), @TagAttributeDeclaration("labelConverter"),
			@TagAttributeDeclaration("valueConverter") })
	@TagChildren({ @TagChild(value = AbstractComboBoxFactory.DisplayWidgetProcessor.class) })
	public static class OptionsProcessor extends WidgetChildProcessor<ComboBoxContext>
	{
	}

	@TagConstraints(tagName = "displayWidget", minOccurs = "0", maxOccurs = "1")
	@TagChildren({ @TagChild(value = AbstractComboBoxFactory.WidgetProcessor.class) })
	public static class DisplayWidgetProcessor extends WidgetChildProcessor<ComboBoxContext>
	{
	}

	@TagConstraints(type = AnyWidget.class, description = "The widget used to render an option.", minOccurs = "0", maxOccurs = "1")
	public static class WidgetProcessor extends WidgetChildProcessor<ComboBoxContext>
	{
	}

	static class ComboBoxWidgetConsumer extends DataWidgetConsumer implements WidgetConsumer
	{
		private String dataObjectVariable;
		private JClassType dataObjectType;
		private JClassType widgetClassType;
		private GeneratorContext context;
		private String parentWidgetId;
		private String viewId;
		private ComboBoxContext factoryContext;

		public ComboBoxWidgetConsumer(GeneratorContext generatorContext, ComboBoxContext factoryContext, JClassType dataObjectType, String dataObjectVariable, JClassType widgetClassType, String viewId, String parentWidgetId)
		{
			this.context = generatorContext;
			this.factoryContext = factoryContext;
			this.dataObjectType = dataObjectType;
			this.dataObjectVariable = dataObjectVariable;
			this.widgetClassType = widgetClassType;
			this.viewId = viewId;
			this.parentWidgetId = parentWidgetId;
		}

		@Override
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem)
		{

			String bindPath = metaElem.optString("bindPath");
			String bindConverter = metaElem.optString("bindConverter");

			JClassType converterType = getConverterType(out, context, bindPath, bindConverter, dataObjectType, widgetClassType);
			String converterVariable = null;
			if (converterType != null)
			{
				converterVariable = ViewFactoryCreator.createVariableName("__converter");
				out.println(converterType.getParameterizedQualifiedSourceName() + " " + converterVariable + " = new " + converterType.getParameterizedQualifiedSourceName() + "();");
			}

			try
			{
				factoryContext.expression = getPropertyReadExpression(dataObjectType, dataObjectVariable, converterVariable, bindPath);
			} catch (NoSuchFieldException e)
			{
				throw new CruxGeneratorException("Invalid binding path [" + bindPath + "] on target dataobject [" + dataObjectType.getParameterizedQualifiedSourceName() + "]. Property not found. Widget [" + parentWidgetId + "] on View [" + viewId
						+ "]");
			}
		}
	}
}