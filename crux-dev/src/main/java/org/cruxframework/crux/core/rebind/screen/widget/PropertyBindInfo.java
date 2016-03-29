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

import org.cruxframework.crux.core.client.screen.binding.NativeWrapper;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dom.client.Element;
import com.ibm.icu.text.MessageFormat;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PropertyBindInfo extends BindInfo
{
	protected static String WIDGET_VAR_REF = "widget";
	
	protected String widgetClassName;
	protected String writeExpression;
	protected String readExpression;
	protected boolean boundToAttribute;
	protected String getUiObjectExpression;
	protected boolean nativeWrapperOrElement = false;
	protected boolean nativeElement = false;

	public PropertyBindInfo(String widgetPropertyPath, boolean boundToAttribute, String bindPath, JClassType widgetType, JClassType dataObjectType, 
							JClassType converterType, String dataObject, String converterParams,
							JClassType uiObjectType, String getUiObjectExpression) throws NoSuchFieldException
    {
		super(bindPath, dataObjectType, converterType, dataObject, converterParams);
		this.boundToAttribute = boundToAttribute;
		uiObjectType = uiObjectType==null?widgetType:uiObjectType;
		this.nativeElement = uiObjectType.getQualifiedSourceName().equals(Element.class.getCanonicalName());
		this.nativeWrapperOrElement = nativeElement || uiObjectType.getQualifiedSourceName().equals(NativeWrapper.class.getCanonicalName());
		this.getUiObjectExpression = getUiObjectExpression;
		if (widgetType != null)
		{
			this.widgetClassName = widgetType.getQualifiedSourceName();
			if (!StringUtils.isEmpty(widgetPropertyPath))
			{
				this.readExpression = getReadExpression(widgetPropertyPath, uiObjectType, dataObjectType, bindPath);
				this.writeExpression = getWriteExpression(widgetPropertyPath, uiObjectType);
			}
		}
    }

	public String getWriteExpression(String dataObjectVariable)
	{
		return getExpression(writeExpression, dataObjectVariable);
	}

	public String getWriteExpression(String contextVariable, String widgetVar, String collectionObjectReference, String collectionItemVar)
	{
		
        boolean isCollectionObjectReference = collectionObjectReference != null && collectionItemVar.equals(dataObject);
        String dataObjectVariable = isCollectionObjectReference?collectionObjectReference:ViewFactoryCreator.createVariableName(dataObject);
        boolean isReferenced = !isCollectionObjectReference;
        String result;
        if (isReferenced)
        {
        	result = dataObjectClassName+" "+dataObjectVariable+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n"
        		+ "if (" + dataObjectVariable + " != null){\n"
        		+ getExpression(writeExpression.replace(WIDGET_VAR_REF, widgetVar), dataObjectVariable)
        		+ "\n}";
        }
        else
        {
        	result = getExpression(writeExpression.replace(WIDGET_VAR_REF, widgetVar), dataObjectVariable);
        }
		
		return result;
	}

	public String getReadExpression(String dataObjectVariable)
	{
		return getExpression(readExpression, dataObjectVariable);
	}
	
	public String getDataObjectReadExpression(String contextVariable, String resultVariable, String collectionDataObjectRef, String collectionItemVar)
	{
        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionItemVar.equals(dataObject);
        String dataObjectVariable = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
        String result = "";
        if (!isCollectionObjectReference)
    	{
    		result = dataObjectClassName+" "+dataObjectVariable+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n";
    	}
        result += "if (" + dataObjectVariable + " != null){\n"
			+ resultVariable + " = " + getExpression(getDataObjectReadExpression(), dataObjectVariable) + ";"
			+ "\n} else {\n"
    		+ resultVariable + " = null;\n"
			+ "}";

    	return result;
	}

	public String getDataObjectWriteExpression(String dataObjectVar, String newValue)
	{
		return getExpression(getDataObjectWriteExpression(newValue), dataObjectVar);
	}
	
	public String getWidgetClassName()
	{
		return widgetClassName;
	}

	public boolean isBoundToAttribute()
    {
	    return boundToAttribute;
    }
	
	public String getUiObjectExpression()
	{
		return getUiObjectExpression;
	}
	
	public boolean isNativeElement()
	{
		return nativeElement;
	}
	
	/**
	 * Expression to read FROM widget and write TO dataObject
	 * @param widgetPropertyPath
	 * @param widgetType
	 * @param dataObjectType
	 * @param bindPath
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String getReadExpression(String widgetPropertyPath, JClassType widgetType, JClassType dataObjectType, 
				String bindPath) throws NoSuchFieldException
    {
		StringBuilder getExpression = new StringBuilder();
		String uiObjectVar = getUIObjectVar(WIDGET_VAR_REF);
		String uiObjectVariable = ViewFactoryCreator.createVariableName("uiObjectVariable");
		
		if (nativeWrapperOrElement)
		{
			String propertyGetter = DataBindingNativeTypeResolver.resolveTypeForProperty(widgetPropertyPath).getGetter();
			getExpression.append(uiObjectVariable+"."+propertyGetter+"(" + EscapeUtils.quote(widgetPropertyPath) + ")");
		}
		else
		{
			JClassUtils.buildGetValueExpression(getExpression, widgetType, widgetPropertyPath, uiObjectVariable, false, true, true);
		}
		return widgetType.getParameterizedQualifiedSourceName() + " " + uiObjectVariable + " = " + uiObjectVar + ";\n"
			    +"if ("+uiObjectVariable + " != null){\n" 
				+getDataObjectWriteExpression(dataObjectType, bindPath, getExpression.toString())
				+"\n}";
    }

	/**
	 * Expression to read FROM dataObject and write TO widget
	 * 
	 * @param widgetPropertyPath
	 * @param widgetType
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String getWriteExpression(String widgetPropertyPath, JClassType widgetType) throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();
		
		String uiObjectVar = getUIObjectVar(WIDGET_VAR_REF);
		String uiObjectVariable = ViewFactoryCreator.createVariableName("uiObjectVariable");
		writeExpression.append(widgetType.getParameterizedQualifiedSourceName() + " " + uiObjectVariable + " = " + uiObjectVar + ";\n");
		writeExpression.append("if ("+uiObjectVariable + " != null)\n");
		if (nativeWrapperOrElement)
		{
			String propertySetter = DataBindingNativeTypeResolver.resolveTypeForProperty(widgetPropertyPath).getSetter();
			writeExpression.append(uiObjectVariable+"."+propertySetter+"(" + 
								EscapeUtils.quote(widgetPropertyPath) + "," + getDataObjectReadExpression() + ");");
		}
		else
		{
			JClassUtils.buildSetValueExpression(writeExpression, widgetType, widgetPropertyPath, 
				uiObjectVariable, getDataObjectReadExpression());
		}
		return writeExpression.toString();
	}
	
	protected String getUIObjectVar(String widgetVar)
	{
		if (StringUtils.isEmpty(getUiObjectExpression))
		{
			return widgetVar;
		}
		
		String uiObjectVar = MessageFormat.format(getUiObjectExpression, widgetVar);
		return uiObjectVar;
	}
}