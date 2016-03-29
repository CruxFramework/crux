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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.screen.binding.NativeWrapper;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dom.client.Element;
import com.ibm.icu.text.MessageFormat;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ExpressionDataBinding
{
	protected static String WIDGET_VAR_REF = "widget";

	private RebindContext context;
	private Set<String> convertersDeclarations = new HashSet<String>();
	private Set<String> dataObjects = new HashSet<String>();
	private StringBuilder expression = new StringBuilder();
	private JType expressionType;
	private String getUiObjectExpression;
	private JClassType uiObjectType;
	private String widgetClassName;
	private String widgetPropertyPath;
	private JClassType widgetType;
	private String setterMethod;

	public ExpressionDataBinding(RebindContext context, JClassType widgetType, String widgetPropertyPath, 
								 JClassType uiObjectType, String getUiObjectExpression, String setterMethod)
    {
		this.context = context;
		this.widgetType = widgetType;
		this.widgetPropertyPath = widgetPropertyPath;
		this.setterMethod = setterMethod;
		this.uiObjectType = uiObjectType==null?widgetType:uiObjectType;
		this.getUiObjectExpression = getUiObjectExpression;
		this.widgetClassName = widgetType.getQualifiedSourceName();
    }

	public void addLogicalBinding(List<ExpressionPart> expressionParts, LogicalOperation logicalOperation, boolean negate)
	{
		assert (expression.length() == 0):"Invalid Expression. Can not add logical binnding on a multi expression binding";
		
		if (negate)
		{
			expression.append("!(");
		}
		
		boolean first = true;
		for (ExpressionPart expressionPart : expressionParts)
        {
			if (!first)
			{
				expression.append(" && ");
			}
			first = false;
			
			String readExpression = expressionPart.getExpression(expressionPart.getDataObjectReadExpression(), "${"+expressionPart.getDataObject()+"}");
			
			if (logicalOperation == LogicalOperation.NULL)
			{
				addLogicalBindingForNullOperation(expressionPart, readExpression);
			}
			else if (logicalOperation == LogicalOperation.FILLED)
			{
				addLogicalBindingForFilledOperation(expressionPart, readExpression);
			}
			else if (logicalOperation == LogicalOperation.EMPTY)
			{
				addLogicalBindingForEmptyOperation(expressionPart, readExpression);
			}
			else if (logicalOperation == LogicalOperation.IS)
			{
				expression.append(readExpression);
			}
			else
			{
				addLogicalBindingForNumericOperations(expressionPart, logicalOperation, readExpression);
			}
	        
			dataObjects.add(expressionPart.getDataObject());
			appendConverterDeclaration(expressionPart);
        }
		
		if (negate)
		{
			expression.append(")");
		}
		expressionType = JPrimitiveType.BOOLEAN;
	}
	
	public void addReadBinding(ExpressionPart expressionPart)
    {
		if (expression.length() > 0)
		{
			expression.append(" + ");
		}
		dataObjects.add(expressionPart.getDataObject());
		//expressionParts.add(expressionPart);
		expression.append(expressionPart.getExpression(expressionPart.getDataObjectReadExpression(), "${"+expressionPart.getDataObject()+"}"));
		appendConverterDeclaration(expressionPart);
		
		if (expressionType == null)
		{
			expressionType = expressionPart.getType();
		}
		else if (!expressionType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			JPrimitiveType primitive = expressionPart.getType().isPrimitive();
			if (primitive != null)
			{
				if (primitive == JPrimitiveType.BOOLEAN || primitive == JPrimitiveType.CHAR)
				{
					expressionType = primitive;
				}
				else if (primitive == JPrimitiveType.VOID)
				{
					expressionType = context.getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName());;
				}
				else
				{
					expressionType = JPrimitiveType.DOUBLE;
				}
			}
		}
    }
	
	public void addStringConstant(String constant, boolean needsQuote)
	{
		if (constant == null || constant.length() == 0)
		{
			return;
		}
		if (expression.length() > 0)
		{
			expression.append(" + ");
		}
		if (needsQuote)
		{
			expression.append(EscapeUtils.quote(constant));
		}
		else
		{
			expression.append(constant);
		}
		expressionType = context.getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName());
	}
	
	public Set<String> getConverterDeclarations()
    {
	    return convertersDeclarations;
    }
	
	public String getExpression(String resultVariable, String contextVariable, String collectionDataObjectRef, String collectionItemVar)
	{
		StringBuilder result = new StringBuilder();
		
		result.append(resultVariable + " = " + expression);
		for (String dataObject : dataObjects)
        {
	        String dataObjectReference = "${"+dataObject+"}";
	        int index;
	        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionItemVar.equals(dataObject);
	        String dataObjectVar = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
	        boolean isReferenced = false;
	        while ((index = result.indexOf(dataObjectReference)) >= 0)
	        {
	        	isReferenced = !isCollectionObjectReference;
	        	result.replace(index, index+dataObjectReference.length(), dataObjectVar);
	        }
	        if (isReferenced)
	        {
	        	String dataObjectClassName = context.getDataObjects().getDataObject(dataObject);
	        	result.insert(0, dataObjectClassName+" "+dataObjectVar+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n");
	        }
        }

		return result.toString();
	}
	
	public JType getType()
	{
		return expressionType;
	}
	
	public String getWidgetClassName()
	{
		return widgetClassName;
	}

	public String getWriteExpression(String contextVariable) throws NoSuchFieldException
	{
		return getWriteExpression(contextVariable, WIDGET_VAR_REF, null, null);
	}

	public String getWriteExpression(String contextVariable, String widgetVar, String collectionDataObjectRef, String collectionItemVar) 
		throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();

		
		String uiObjectVar = getUIObjectVar(widgetVar);
		String uiObjectVariable = ViewFactoryCreator.createVariableName("uiObjectVariable");
		writeExpression.append(uiObjectType.getParameterizedQualifiedSourceName() + " " + uiObjectVariable + " = " + uiObjectVar + ";\n");
		writeExpression.append("if ("+uiObjectVariable + " != null)\n");
		if (!StringUtils.isEmpty(setterMethod))
		{
			writeExpression.append(uiObjectVariable+"."+setterMethod+"("+expression.toString()+");");

		}
        else
        {
	        boolean nativeWrapper = uiObjectType.getQualifiedSourceName().equals(NativeWrapper.class.getCanonicalName()) ||
	        						uiObjectType.getQualifiedSourceName().equals(Element.class.getCanonicalName());
	        if (nativeWrapper)
	        {
				String propertySetter = DataBindingNativeTypeResolver.resolveTypeForProperty(widgetPropertyPath).getSetter();
	        	writeExpression.append(uiObjectVariable+"."+propertySetter+"("+EscapeUtils.quote(widgetPropertyPath)+","+expression.toString()+");");
	        }
	        else
	        {
	        	JClassUtils.buildSetValueExpression(writeExpression, uiObjectType, widgetPropertyPath, uiObjectVariable, expression.toString());
	        }
        }

		for (String dataObject : dataObjects)
        {
	        String dataObjectReference = "${"+dataObject+"}";
	        int index;
	        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionItemVar.equals(dataObject);
	        String dataObjectVar = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
	        boolean isReferenced = false;
	        while ((index = writeExpression.indexOf(dataObjectReference)) >= 0)
	        {
	        	isReferenced = !isCollectionObjectReference;
	        	writeExpression.replace(index, index+dataObjectReference.length(), dataObjectVar);
	        }
	        if (isReferenced)
	        {
	        	String dataObjectClassName = context.getDataObjects().getDataObject(dataObject);
	        	writeExpression.insert(0, dataObjectClassName+" "+dataObjectVar+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n");
	        }
        }

		return writeExpression.toString();
	}
	
	public Iterator<String> iterateDataObjects()
	{
		return dataObjects.iterator();
	}

	private void addLogicalBindingForEmptyOperation(ExpressionPart expressionPart, String readExpression)
    {
	    JType type = expressionPart.getType();
		if (type.isArray() != null)
	    {
	    	expression.append(readExpression + " != null ? "+readExpression + ".length == 0 : true");
	    }
	    else if (JClassUtils.isCollection(type))
	    {
	    	expression.append(readExpression + " != null ? "+readExpression + ".size() == 0 : true");
	    }
	    else
	    {
	    	throw new CruxGeneratorException("Invalid expression. property ["+widgetPropertyPath+"] "
	    			+ "of widget ["+widgetType.getQualifiedSourceName()+"] is not a valid collection or array.");
	    }
		appendConverterDeclaration(expressionPart);
    }
	
	private void addLogicalBindingForFilledOperation(ExpressionPart expressionPart, String readExpression)
    {
		JType expressionType = expressionPart.getType();
		if (expressionType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			expression.append("!"+StringUtils.class.getCanonicalName()+".isEmpty("+readExpression+")");
		}
		else
		{
			String emptyValueForType = JClassUtils.getEmptyValueForType(expressionType);
			if(emptyValueForType == null)
			{
				throw new CruxGeneratorException("Invalid expression. property ["+widgetPropertyPath+"] "
						+ "of widget ["+widgetType.getQualifiedSourceName()+"] is void.");
			}
			expression.append(readExpression+" != "+emptyValueForType);
		}
		appendConverterDeclaration(expressionPart);
    }
	
	private void addLogicalBindingForNullOperation(ExpressionPart expressionPart, String readExpression)
    {
		JType expressionType = expressionPart.getType();
		if (expressionType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			expression.append(StringUtils.class.getCanonicalName()+".isEmpty("+readExpression+")");
		}
		else
		{
			String emptyValueForType = JClassUtils.getEmptyValueForType(expressionType);
			if(emptyValueForType == null)
			{
				throw new CruxGeneratorException("Invalid expression. property ["+widgetPropertyPath+"] "
						+ "of widget ["+widgetType.getQualifiedSourceName()+"] is void.");
			}
			expression.append(readExpression+" == "+emptyValueForType);
		}
		appendConverterDeclaration(expressionPart);
    }

	private void addLogicalBindingForNumericOperations(ExpressionPart expressionPart, LogicalOperation logicalOperation, String readExpression)
    {
		expression.append(readExpression);
	    if (!JClassUtils.isNumeric(expressionPart.getType()))
	    {
	    	throw new CruxGeneratorException("Invalid expression. property ["+widgetPropertyPath+"] "
	    			+ "of widget ["+widgetType.getQualifiedSourceName()+"] is not a numeric type.");
	    }
	    
	    if (logicalOperation == LogicalOperation.POSITIVE)
	    {
	    	expression.append(" > 0");
	    }
	    else if (logicalOperation == LogicalOperation.NEGATIVE)
	    {
	    	expression.append(" < 0");
	    }
	    else if (logicalOperation == LogicalOperation.ZERO)
	    {
	    	expression.append(" == 0");
	    }
		appendConverterDeclaration(expressionPart);
    }

	private void appendConverterDeclaration(ExpressionPart expressionPart)
    {
	    String converterDeclaration = expressionPart.getConverterDeclaration();
		if (converterDeclaration != null)
		{
			convertersDeclarations.add(converterDeclaration);
		}
    }

	private String getUIObjectVar(String widgetVar)
	{
		if (StringUtils.isEmpty(getUiObjectExpression))
		{
			return widgetVar;
		}
		String uiObjectVar = MessageFormat.format(getUiObjectExpression, widgetVar);
		return uiObjectVar;
	}

	public static enum LogicalOperation{EMPTY, FILLED, IS, NEGATIVE, NULL, POSITIVE, ZERO}
}
