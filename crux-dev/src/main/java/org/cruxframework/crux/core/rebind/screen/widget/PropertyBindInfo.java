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
	
	protected boolean boundToAttribute;
	protected String getUiObjectExpression;
	protected boolean nativeElement = false;
	protected boolean nativeWrapperOrElement = false;
	protected JClassType uiObjectType;
	protected String widgetClassName;
	protected String widgetPropertyPath;

	public PropertyBindInfo(String widgetPropertyPath, boolean boundToAttribute, String bindPath, JClassType widgetType, JClassType dataObjectType, 
							JClassType converterType, String dataObject, String converterParams,
							JClassType uiObjectType, String getUiObjectExpression)  throws NoSuchFieldException
    {
		super(bindPath, dataObjectType, converterType, dataObject, converterParams);
		this.widgetPropertyPath = widgetPropertyPath;
		this.boundToAttribute = boundToAttribute;
		this.uiObjectType = uiObjectType==null?widgetType:uiObjectType;
		this.nativeElement = this.uiObjectType.getQualifiedSourceName().equals(Element.class.getCanonicalName());
		this.nativeWrapperOrElement = nativeElement || this.uiObjectType.getQualifiedSourceName().equals(NativeWrapper.class.getCanonicalName());
		this.getUiObjectExpression = getUiObjectExpression;
		if (widgetType != null)
		{
			this.widgetClassName = widgetType.getQualifiedSourceName();
		}
    }

	public String getDataObjectReadExpression(String contextVariable, String resultVariable, String collectionDataObjectRef, String collectionItemVar)
	{
        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionItemVar.equals(dataObject);
        String dataObjectVariable = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
        String result = "";
        if (!isCollectionObjectReference)
    	{
    		result = getDataObjectClassName()+" "+dataObjectVariable+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n";
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

	public String getReadExpression(String dataObjectVariable) throws NoSuchFieldException
	{
		String readExpression = buildReadExpression(widgetPropertyPath);
		return getExpression(readExpression, dataObjectVariable);
	}
	
	public String getUiObjectClassName()
	{
		return uiObjectType.getParameterizedQualifiedSourceName();
	}

	public String getUiObjectExpression()
	{
		return getUiObjectExpression;
	}
	
	/**
	 * Retrieve the reference to the variable that represents the target ui element from this binding
	 * @param widgetVar
	 * @return
	 */
	public String getUIObjectVar(String widgetVar)
	{
		if (!hasUiObjectExpression())
		{
			return widgetVar;
		}
		
		String uiObjectVar = MessageFormat.format(getUiObjectExpression, widgetVar);
		return uiObjectVar;
	}

	public String getWidgetClassName()
	{
		return widgetClassName;
	}
	
	public String getWriteExpression(String dataObjectVariable) throws NoSuchFieldException
	{
		String writeExpression = buildWriteExpression(widgetPropertyPath, null);
		return getExpression(writeExpression, dataObjectVariable);
	}
	
	public String getWriteExpression(String contextVariable, String widgetVar, 
						String collectionObjectReference, String collectionItemVar, 
						String uiObjectVariable) throws NoSuchFieldException
	{
		String writeExpression = buildWriteExpression(widgetPropertyPath, uiObjectVariable);
		
        boolean isCollectionObjectReference = collectionObjectReference != null && collectionItemVar.equals(dataObject);
        String dataObjectVariable = isCollectionObjectReference?collectionObjectReference:ViewFactoryCreator.createVariableName(dataObject);
        boolean isReferenced = !isCollectionObjectReference;
        String result;
        if (isReferenced)
        {
        	result = getDataObjectClassName()+" "+dataObjectVariable+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n"
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
	
	/**
	 * If this databinding reference an ui element that is not the widget bound to this binding, 
	 * then an expression is used to retrieve the target ui element
	 * @return
	 */
	public boolean hasUiObjectExpression()
    {
	    return !StringUtils.isEmpty(getUiObjectExpression);
    }

	public boolean isBoundToAttribute()
    {
	    return boundToAttribute;
    }
	
	public boolean isNativeElement()
	{
		return nativeElement;
	}

	/**
	 * Expression to read FROM widget and write TO dataObject
	 * @param widgetPropertyPath
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String buildReadExpression(String widgetPropertyPath) throws NoSuchFieldException
    {
		StringBuilder getExpression = new StringBuilder();
		String uiObjectVar = getUIObjectVar(WIDGET_VAR_REF);
		boolean createAuxiliaryVariable = hasUiObjectExpression();
		String uiObjectVariable = createAuxiliaryVariable?ViewFactoryCreator.createVariableName("uiObjectVariable"):uiObjectVar;
		
		if (nativeWrapperOrElement)
		{
			String propertyGetter = DataBindingNativeTypeResolver.resolveTypeForProperty(widgetPropertyPath).getGetter();
			getExpression.append(uiObjectVariable+"."+propertyGetter+"(" + EscapeUtils.quote(widgetPropertyPath) + ")");
		}
		else
		{
			JClassUtils.buildGetValueExpression(getExpression, uiObjectType, widgetPropertyPath, uiObjectVariable, false, true, true);
		}
		StringBuilder result = new StringBuilder();
		if (createAuxiliaryVariable)
		{
			result.append(getUiObjectClassName() + " " + uiObjectVariable + " = " + uiObjectVar + ";\n");
		}
		result.append("if ("+uiObjectVariable + " != null){\n"); 
		result.append(buildDataObjectWriteExpression(getExpression.toString()));
		result.append("\n}");
		return result.toString();
    }
	
	/**
	 * Expression to read FROM dataObject and write TO widget
	 * 
	 * @param widgetPropertyPath
	 * @param uiObjectVariable
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String buildWriteExpression(String widgetPropertyPath, String uiObjectVariable) throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();
		
		String uiObjectVar = getUIObjectVar(WIDGET_VAR_REF);
		boolean hasUiObjectVariable = !StringUtils.isEmpty(uiObjectVariable);
		boolean createAuxiliaryVariable = hasUiObjectExpression() && !hasUiObjectVariable;

		uiObjectVariable = createAuxiliaryVariable?ViewFactoryCreator.createVariableName("uiObjectVariable"):
								(hasUiObjectVariable?uiObjectVariable:uiObjectVar);
		if (createAuxiliaryVariable)
		{
			writeExpression.append(getUiObjectClassName() + " " + uiObjectVariable + " = " + uiObjectVar + ";\n");
		}
		writeExpression.append("if ("+uiObjectVariable + " != null)\n");
		if (nativeWrapperOrElement)
		{
			String propertySetter = DataBindingNativeTypeResolver.resolveTypeForProperty(widgetPropertyPath).getSetter();
			writeExpression.append(uiObjectVariable+"."+propertySetter+"(" + 
								EscapeUtils.quote(widgetPropertyPath) + "," + getDataObjectReadExpression() + ");");
		}
		else
		{
			JClassUtils.buildSetValueExpression(writeExpression, uiObjectType, widgetPropertyPath, 
				uiObjectVariable, getDataObjectReadExpression());
		}
		return writeExpression.toString();
	}
}