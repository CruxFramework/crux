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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ExpressionDataBinding
{
	protected static String WIDGET_VAR_REF = "widget";

	private Set<String> dataObjects = new HashSet<String>();
	private StringBuilder expression = new StringBuilder();
	private String widgetClassName;
	private JClassType widgetType;
	private String widgetPropertyPath;

	public static enum LogicalOperations{IS, EMPTY, NULL, FILLED, POSITIVE, NEGATIVE, ZERO}
	
	
	public ExpressionDataBinding(JClassType widgetType, String widgetPropertyPath)
    {
		this.widgetType = widgetType;
		this.widgetPropertyPath = widgetPropertyPath;
		this.widgetClassName = widgetType.getQualifiedSourceName();
    }
	
	public String getWriteExpression(String contextVariable) throws NoSuchFieldException
	{
		return getWriteExpression(contextVariable, WIDGET_VAR_REF, null, null);
	}
	
	public String getWriteExpression(String contextVariable, String widgetVar, String collectionDataObjectRef, String collectionDataObject) throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();

		JClassUtils.buildSetValueExpression(writeExpression, widgetType, widgetPropertyPath, widgetVar, expression.toString());

		for (String dataObject : dataObjects)
        {
	        String dataObjectReference = "${"+dataObject+"}";
	        int index;
	        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionDataObject.equals(dataObject);
	        String dataObjectVar = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
	        boolean isReferenced = false;
	        while ((index = writeExpression.indexOf(dataObjectReference)) >= 0)
	        {
	        	isReferenced = !isCollectionObjectReference;
	        	writeExpression.replace(index, index+dataObjectReference.length(), dataObjectVar);
	        }
	        if (isReferenced)
	        {
	        	String dataObjectClassName = DataObjects.getDataObject(dataObject);
	        	writeExpression.insert(0, dataObjectClassName+" "+dataObjectVar+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n");
	        }
        }

		return writeExpression.toString();
	}
	
	public String getExpression(String contextVariable, String collectionDataObjectRef, String collectionDataObject)
	{
		StringBuilder result = new StringBuilder();
		
		result.append(expression);
		for (String dataObject : dataObjects)
        {
	        String dataObjectReference = "${"+dataObject+"}";
	        int index;
	        boolean isCollectionObjectReference = collectionDataObjectRef != null && collectionDataObject.equals(dataObject);
	        String dataObjectVar = isCollectionObjectReference?collectionDataObjectRef:ViewFactoryCreator.createVariableName(dataObject);
	        boolean isReferenced = false;
	        while ((index = result.indexOf(dataObjectReference)) >= 0)
	        {
	        	isReferenced = !isCollectionObjectReference;
	        	result.replace(index, index+dataObjectReference.length(), dataObjectVar);
	        }
	        if (isReferenced)
	        {
	        	String dataObjectClassName = DataObjects.getDataObject(dataObject);
	        	result.insert(0, dataObjectClassName+" "+dataObjectVar+" = "+contextVariable+".getDataObject("+EscapeUtils.quote(dataObject)+");\n");
	        }
        }

		return result.toString();
	}
	
	public Iterator<String> iterateDataObjects()
	{
		return dataObjects.iterator();
	}

	public String getWidgetClassName()
	{
		return widgetClassName;
	}

	public void addStringConstant(String constant)
	{
		if (expression.length() > 0)
		{
			expression.append(" + ");
		}
		expression.append(EscapeUtils.quote(constant));
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
    }
	
	public void addLogicalBinding(List<ExpressionPart> expressionParts, LogicalOperations logicalOperations, boolean negate)
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
			
			if (logicalOperations == LogicalOperations.NULL)
			{
				addLogicalBindingForNullOperation(expressionPart, readExpression);
			}
			else if (logicalOperations == LogicalOperations.FILLED)
			{
				addLogicalBindingForFilledOperation(expressionPart, readExpression);
			}
			else if (logicalOperations == LogicalOperations.EMPTY)
			{
				addLogicalBindingForEmptyOperation(expressionPart, readExpression);
			}
			else if (logicalOperations == LogicalOperations.IS)
			{
				expression.append(readExpression);
			}
			else
			{
				addLogicalBindingForNumericOperations(expressionPart, logicalOperations, readExpression);
			}
	        
			dataObjects.add(expressionPart.getDataObject());
        }
		
		if (negate)
		{
			expression.append(")");
		}

	}

	private void addLogicalBindingForNumericOperations(ExpressionPart expressionPart, LogicalOperations logicalOperations, String readExpression)
    {
		expression.append(readExpression);
	    if (!JClassUtils.isNumeric(expressionPart.getType()))
	    {
	    	throw new CruxGeneratorException("Invalid expression. property ["+widgetPropertyPath+"] "
	    			+ "of widget ["+widgetType.getQualifiedSourceName()+"] is not a numeric type.");
	    }
	    
	    if (logicalOperations == LogicalOperations.POSITIVE)
	    {
	    	expression.append(" > 0");
	    }
	    else if (logicalOperations == LogicalOperations.NEGATIVE)
	    {
	    	expression.append(" < 0");
	    }
	    else if (logicalOperations == LogicalOperations.ZERO)
	    {
	    	expression.append(" == 0");
	    }
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
    }
}
