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
package org.cruxframework.crux.core.rebind.screen.parameter;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.controller.ParameterObject;
import org.cruxframework.crux.core.client.event.ValidateException;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
//TODO: @Legacy
public class ParameterBindGeneratorImpl implements ParameterBindGenerator
{
	@Override
    public void generate(String parentVariable, JClassType classType, JField field, SourcePrinter sourceWriter, TreeLogger logger)
    {
		sourceWriter.println("try{");
		generateDTOParameterPopulationField(parentVariable, classType, field, sourceWriter, true, logger);
		sourceWriter.println("}catch("+ValidateException.class.getName() + " _e){");
		sourceWriter.println(Crux.class.getName()+".getValidationErrorHandler().handleValidationError(_e.getMessage());");
		sourceWriter.println("}");
    }

	/**
	 * 
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param allowProtected
	 */
	private void generateDTOParameterPopulationField(String parentVariable, JClassType voClass, JField field, SourcePrinter sourceWriter, boolean allowProtected, TreeLogger logger)
	{
		JType type = field.getType();
		String name = null;
		boolean required = false;
		Parameter parameterAnnot = field.getAnnotation(Parameter.class);
		if (parameterAnnot != null)
		{
			name = parameterAnnot.value();
			required = parameterAnnot.required();
		}
		if (name == null || name.length() == 0)
		{
			name = field.getName();
		}
		
		if (JClassUtils.isSimpleType(type)) 
		{
			if (required)
			{
				
				sourceWriter.println("if ("+StringUtils.class.getName()+".isEmpty(" +Window.class.getName()+".Location.getParameter(\""+name+"\"))){");
				sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote("Required parameter ["+name+"] is missing.")+");");
				sourceWriter.println("}");
				
			}
			sourceWriter.println("if (!"+StringUtils.class.getName()+".isEmpty(" +Window.class.getName()+".Location.getParameter(\""+name+"\"))){");
			sourceWriter.println("try{");
			generateParameterBinding(parentVariable, voClass, field, sourceWriter, type, name, allowProtected);
			sourceWriter.println("}catch(Throwable _e1){");
			sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote("Error parsing parameter ["+name+"].")+");");
			sourceWriter.println("}");
			sourceWriter.println("}");
		}
		else if (type instanceof JClassType && ((JClassType)type).getAnnotation(ParameterObject.class) != null)
		{
			sourceWriter.println("if (" +JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected)+"==null){");
			JClassUtils.generateFieldValueSet(voClass, field, parentVariable, "new "+type.getParameterizedQualifiedSourceName()+"()", sourceWriter, allowProtected);
			sourceWriter.println("}");

			parentVariable = JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateDTOParameterPopulation(parentVariable, (JClassType)type, sourceWriter, logger);
			}
		}			
	}

	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	private void generateDTOParameterPopulation(String resultVariable, JClassType voClass, SourcePrinter sourceWriter, TreeLogger logger)
	{
		boolean hasAtLeastOneField = false;
		for (JField field : voClass.getFields()) 
		{
			if (JClassUtils.isPropertyVisibleToWrite(voClass, field, true))
			{
				ParameterObject parameterObject = voClass.getAnnotation(ParameterObject.class);
				Parameter parameter = field.getAnnotation(Parameter.class); 
				if ((parameterObject != null && parameterObject.bindParameterByFieldName()) || parameter != null)
				{
					hasAtLeastOneField = true;
					generateDTOParameterPopulationField(resultVariable, voClass, field, sourceWriter, false, logger);
				}
			}
		}
		if (!hasAtLeastOneField)
		{
			logger.log(TreeLogger.ERROR, "Parameter Object ["+voClass.getName()+"] has no valid field for binding.");
		}
	}	
	
	/**
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param type
	 * @param name
	 * @param allowProtected
	 */
	private void generateParameterBinding(String parentVariable, JClassType voClass, JField field, SourcePrinter sourceWriter, 
			JType type, String name, boolean allowProtected)
	{
		JClassUtils.generateFieldValueSet(voClass, field, parentVariable, getParameterFromURL(type, name), sourceWriter, allowProtected);
	}	
	
	/**
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	private String getParameterFromURL(JType type, String name)
	{
		try
        {
	        return JClassUtils.getParsingExpressionForSimpleType(Window.class.getName()+".Location.getParameter(\""+name+"\")", type);
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	
}
