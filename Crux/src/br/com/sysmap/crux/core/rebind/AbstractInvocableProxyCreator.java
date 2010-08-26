/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.controller.ParameterObject;
import br.com.sysmap.crux.core.client.controller.ScreenBind;
import br.com.sysmap.crux.core.client.controller.ValueObject;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Base class for all proxy creators that will produce crux invocable objects.
 * A Crux Invocable object is this that can be dispatched from the crux engine, 
 * like Controllers or DataSources.  
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractInvocableProxyCreator extends AbstractSerializableProxyCreator
{
	protected JClassType invocableClassType;
	
	public AbstractInvocableProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseProxyType, JClassType invocableClassType)
    {
	    super(logger, context, baseProxyType);
	    this.invocableClassType = invocableClassType;
    }
	
	/**
	 * Create objects for fields that are annotated with @Create
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateAutoCreateFields(SourceWriter sourceWriter, String parentVariable)
	{
		generateAutoCreateFields(invocableClassType, sourceWriter, parentVariable, new HashSet<String>());
	}

	/**
	 * 
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateControllerUpdateObjectsFunction(JClassType controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateControllerObjects(){");
		sourceWriter.indent();
		sourceWriter.println("Widget __wid = null;");
		generateControllerUpdateObjects("this", controller, sourceWriter);
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param autoBind
	 */
	protected void generateIsAutoBindEnabledMethod(SourceWriter sourceWriter, boolean autoBind)
	{
		sourceWriter.println("public boolean isAutoBindEnabled(){");
		sourceWriter.indent();
		sourceWriter.println("return "+autoBind+";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateScreenUpdateWidgetsFunction(JClassType controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateScreenWidgets(){");
		sourceWriter.indent();
		sourceWriter.println("Widget __wid = null;");
		sourceWriter.println("Object o = null;");
		generateScreenUpdateWidgets("this", controller, sourceWriter);
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param name
	 * @param allowProtected
	 */
	private void generateAutoBindHandlerForAllWidgets(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, 
			boolean populateScreen, JType type,
			String name, boolean allowProtected)
	{
		try
        {
	        String valueVariable = "__wid";
	        sourceWriter.println(valueVariable + "= Screen.get(\""+name+"\");");
	        sourceWriter.println("if ("+valueVariable+" != null){");
	        sourceWriter.indent();
	        sourceWriter.println("if ("+valueVariable+" instanceof HasFormatter){");
	        sourceWriter.indent();
	        generateHandleHasFormatterWidgets(parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable, allowProtected);
	        sourceWriter.outdent();
	        sourceWriter.println("}else if ("+valueVariable+" instanceof HasValue){");
	        sourceWriter.indent();
	        generateHandleHasValueWidgets(parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable, allowProtected);
	        sourceWriter.outdent();
	        sourceWriter.print("}");
	        JClassType stringType = voClass.getOracle().getType(String.class.getCanonicalName());
	        
	        if (type instanceof JClassType && stringType.isAssignableFrom((JClassType) type))
	        {
	        	sourceWriter.println("else if ("+valueVariable+" instanceof HasText){");
	        	sourceWriter.indent();
	        	generateHandleHasTextWidgets(parentVariable, voClass, field, sourceWriter, populateScreen, valueVariable, allowProtected);
	        	sourceWriter.outdent();
	        	sourceWriter.println("}");
	        }
	        sourceWriter.outdent();
	        sourceWriter.println("}");
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	
	
	/**
	 * Create objects for fields that are annotated with @Create
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	private void generateAutoCreateFields(JClassType classType, SourceWriter sourceWriter, String parentVariable, Set<String> added)
	{
		try
        {
	        for (JField field : classType.getFields()) 
	        {
	        	if (!added.contains(field.getName()))
	        	{
	        		if (field.getAnnotation(Create.class) != null)
	        		{
	        			added.add(field.getName());
	        			String fieldTypeName = field.getType().getParameterizedQualifiedSourceName();
	        			JType type = getTypeForField(logger, field);
	        			String typeName = type.getParameterizedQualifiedSourceName();

	                	JClassType dataSourceType = classType.getOracle().getType(DataSource.class.getCanonicalName());
	        			if (type instanceof JClassType && dataSourceType.isAssignableFrom((JClassType)type))
	        			{
	        				String dsName = getDsName((JClassType)type);
	        				sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=("+fieldTypeName+")"+
	        						br.com.sysmap.crux.core.client.screen.Screen.class.getName()+".createDataSource(\""+dsName+"\");");

	        				generateFieldValueSet(classType, field, parentVariable, "_field"+field.getName(), sourceWriter);					
	        			}
	        			else
	        			{
	        				ParameterObject parameterObjectAnnot = null;
	        				if (type instanceof JClassType)
	        				{
	        					parameterObjectAnnot = ((JClassType)type).getAnnotation(ParameterObject.class);
	        				}
	        				if (parameterObjectAnnot != null)
	        				{
	        					generateParameterPopulation(classType, parentVariable, sourceWriter, field);
	        				}
	        				else
	        				{
	        					sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=GWT.create("+typeName+".class);");
	        					generateFieldValueSet(classType, field, parentVariable, "_field"+field.getName(), sourceWriter);
	        				}
	        			}
	        		}
	        		else if (field.getAnnotation(Parameter.class) != null)
	        		{
	        			generateParameterPopulation(classType, parentVariable, sourceWriter, field);
	        		}
	        	}
	        }

	        if (classType.getSuperclass() != null)
	        {
	        	generateAutoCreateFields(classType.getSuperclass(), sourceWriter, parentVariable, added);
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	
	
	/**
	 * 
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	private void generateControllerUpdateObjects(String controllerVariable, JClassType controller, SourceWriter sourceWriter)
	{
		for (JField field : controller.getFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				JType type = field.getType();

				if (type instanceof JClassType &&  ((JClassType)type).getAnnotation(ValueObject.class) != null)
				{
					generateDTOFieldPopulation(controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateControllerUpdateObjects(controllerVariable, controller.getSuperclass(), sourceWriter);
		}
	}	
	
	/**
	 * Generates the code for DTO field population from a screen widget.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	private void generateDTOFieldPopulation(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(parentVariable, voClass, field, sourceWriter, false, true);
	}	
	
	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	private void generateDTOParameterPopulation(String resultVariable, JClassType voClass, SourceWriter sourceWriter)
	{
		boolean hasAtLeastOneField = false;
		for (JField field : voClass.getFields()) 
		{
			if (isPropertyVisibleToWrite(voClass, field))
			{
				ParameterObject parameterObject = voClass.getAnnotation(ParameterObject.class);
				Parameter parameter = field.getAnnotation(Parameter.class); 
				if ((parameterObject != null && parameterObject.bindParameterByFieldName()) || parameter != null)
				{
					hasAtLeastOneField = true;
					generateDTOParameterPopulationField(resultVariable, voClass, field, sourceWriter, false);
				}
			}
		}
		if (!hasAtLeastOneField)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredObjectParameterObjectHasNoValidField(voClass.getName()));
		}
	}	

	/**
	 * 
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param allowProtected
	 */
	private void generateDTOParameterPopulationField(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, boolean allowProtected)
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
		
		if (isSimpleType(type)) 
		{
			if (required)
			{
				
				sourceWriter.println("if (" +Window.class.getName()+".Location.getParameter(\""+name+"\")==null){");
				sourceWriter.indent();
				sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote(messages.requiredParameterMissing(name))+");");
				sourceWriter.outdent();
				sourceWriter.println("}");
				
			}
			sourceWriter.println("if (" +Window.class.getName()+".Location.getParameter(\""+name+"\")!=null){");
			sourceWriter.indent();
			sourceWriter.println("try{");
			sourceWriter.indent();
			generateParameterBinding(parentVariable, voClass, field, sourceWriter, (JClassType)type, name, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}catch(Throwable _e1){");
			sourceWriter.indent();
			sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote(messages.errorReadingParameter(name))+");");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		else if (type instanceof JClassType && ((JClassType)type).getAnnotation(ParameterObject.class) != null)
		{
			sourceWriter.println("if (" +getFieldValueGet(voClass, field, parentVariable, allowProtected)+"==null){");
			sourceWriter.indent();
			generateFieldValueSet(voClass, field, parentVariable, "new "+type.getParameterizedQualifiedSourceName()+"()", sourceWriter, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}");

			parentVariable = getFieldValueGet(voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateDTOParameterPopulation(parentVariable, (JClassType)type, sourceWriter);
			}
		}			
	}
	
	/**
	 * Generates a property set block. First try to set the field directly, then try to use a javabean setter method.
	 * 
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param valueVariable
	 * @param sourceWriter
	 */
	private void generateFieldValueSet(JClassType voClass, JField field, String parentVariable,  
			                           String valueVariable, SourceWriter sourceWriter)
	{
		generateFieldValueSet(voClass, field, parentVariable, valueVariable, sourceWriter, true);
	}
	
	/**
	 * Generates a property set block. First try to set the field directly, then try to use a javabean setter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param valueVariable
	 * @param sourceWriter
	 */
	private void generateFieldValueSet(JClassType voClass, JField field, String parentVariable,  
			                           String valueVariable, SourceWriter sourceWriter, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			sourceWriter.println(parentVariable+"."+field.getName()+"="+valueVariable+";");
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(setterMethodName, new JType[]{field.getType()}) != null)
				{
					sourceWriter.println(parentVariable+"."+setterMethodName+"("+valueVariable+");");
				}
			}
			catch (Exception e)
			{
				logger.log(TreeLogger.ERROR, messages.registeredClientObjectPropertyNotFound(field.getName()));
			}
		}
	}
	
	/**
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 * @param allowProtected
	 */
	private void generateHandleHasFormatterWidgets(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, 
			boolean populateScreen, JType type, String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasFormatter)"+valueVariable+").setUnformattedValue("
					            + getFieldValueGet(voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			generateFieldValueSet(voClass, field, parentVariable, "("+getGenericDeclForType(type)+")"
					            + "((HasFormatter)"+valueVariable+").getUnformattedValue()", sourceWriter, allowProtected);
		}
	}	
	
	/**
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param valueVariable
	 * @param allowProtected
	 */
	private void generateHandleHasTextWidgets(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, 
			boolean populateScreen, String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("o = " +getFieldValueGet(voClass, field, parentVariable, allowProtected)+";");
			sourceWriter.println("((HasText)"+valueVariable+").setText(String.valueOf(o!=null?o:\"\"));");
		}
		else
		{
			generateFieldValueSet(voClass, field, parentVariable, "((HasText)"+valueVariable+").getText()", sourceWriter, allowProtected);
		}
	}
	
	/**
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 * @param allowProtected
	 */
	private void generateHandleHasValueWidgets(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, 
			boolean populateScreen, JType type, String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").setValue("
					            + getFieldValueGet(voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			generateFieldValueSet(voClass, field, parentVariable, 
					"("+getGenericDeclForType(type)+")((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").getValue()", 
					sourceWriter, allowProtected);
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
	private void generateParameterBinding(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter, 
			JClassType type, String name, boolean allowProtected)
	{
		generateFieldValueSet(voClass, field, parentVariable, getParameterFromURL(type, name), sourceWriter, allowProtected);
	}	
	
	/**
	 * 
	 * @param classType
	 * @param parentVariable 
	 * @param sourceWriter
	 * @param field
	 */
	private void generateParameterPopulation(JClassType classType, String parentVariable, SourceWriter sourceWriter, JField field)
	{
		sourceWriter.println("try{");
		sourceWriter.indent();
		generateDTOParameterPopulationField(parentVariable, classType, field, sourceWriter, true);
		sourceWriter.outdent();
		sourceWriter.println("}catch("+ValidateException.class.getName() + " _e){");
		sourceWriter.indent();
		sourceWriter.println(Crux.class.getName()+".getValidationErrorHandler().handleValidationError(_e.getMessage());");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	private void generateScreenOrDTOPopulation(String resultVariable, JClassType voClass, SourceWriter sourceWriter, boolean populateScreen)
	{
		for (JField field : voClass.getFields()) 
		{
			if ((populateScreen && isPropertyVisibleToRead(voClass, field)) || 
				(!populateScreen && isPropertyVisibleToWrite(voClass, field)))
			{
				ValueObject valueObject = voClass.getAnnotation(ValueObject.class);
				ScreenBind screenBind = field.getAnnotation(ScreenBind.class); 
				if (valueObject != null && valueObject.bindWidgetByFieldName() || screenBind != null)
				{
					generateScreenOrDTOPopulationField(resultVariable, voClass, field, sourceWriter, populateScreen, false);
				}
			}
		}
	}	
	
	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param allowProtected
	 */
	private void generateScreenOrDTOPopulationField(String parentVariable, JClassType voClass, 
			                                        JField field, SourceWriter sourceWriter, boolean populateScreen, boolean allowProtected)
	{
		JType type = field.getType();
		String name = null;
		if (field.getAnnotation(ScreenBind.class) != null)
		{
			name = field.getAnnotation(ScreenBind.class).value();
		}
		if (name == null || name.length() == 0)
		{
			name = field.getName();
		}
		
		if (isSimpleType(type)) 
		{
			generateAutoBindHandlerForAllWidgets(parentVariable, voClass, field, sourceWriter, populateScreen, type, name, allowProtected);
		}
		else if (type instanceof JClassType && ((JClassType)type).getAnnotation(ValueObject.class) != null)
		{
			if (!populateScreen)
			{
				sourceWriter.println("if (" +getFieldValueGet(voClass, field, parentVariable, allowProtected)+"==null){");
				sourceWriter.indent();
				
				generateFieldValueSet(voClass, field, parentVariable, "new "+type.getParameterizedQualifiedSourceName()+"()", sourceWriter, allowProtected);
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			parentVariable = getFieldValueGet(voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateScreenOrDTOPopulation(parentVariable, (JClassType)type, sourceWriter, populateScreen);
			}
		}
	}
	
	/**
	 * 
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	private void generateScreenUpdateWidgets(String controllerVariable, JClassType controller, SourceWriter sourceWriter)
	{
		for (JField field : controller.getFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				JType type = field.getType();

				if (type instanceof JClassType &&  ((JClassType)type).getAnnotation(ValueObject.class) != null)
				{
					generateScreenWidgetPopulation(controllerVariable, controller, field, sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateScreenUpdateWidgets(controllerVariable, controller.getSuperclass(), sourceWriter);
		}
	}
	
	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	private void generateScreenWidgetPopulation(String parentVariable, JClassType voClass, JField field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(parentVariable, voClass, field, sourceWriter, true, true);
	}
	
	
	/**
	 * Return the DataSource name associated with a DataSource subclass
	 * @param type
	 * @return
	 */
	private String getDsName(JClassType type)
	{
		String dsName = null;
		br.com.sysmap.crux.core.client.datasource.annotation.DataSource annot = 
			type.getAnnotation(br.com.sysmap.crux.core.client.datasource.annotation.DataSource.class);
		if (annot != null)
		{
			dsName = annot.value();
		}
		else
		{
			dsName = type.getSimpleSourceName();
			if (dsName.length() >1)
			{
				dsName = Character.toLowerCase(dsName.charAt(0)) + dsName.substring(1);
			}
			else
			{
				dsName = dsName.toLowerCase();
			}
		}
		return dsName;
	}

	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 */
	protected String getFieldValueGet(JClassType voClass, JField field, String parentVariable)
	{
		return getFieldValueGet(voClass, field, parentVariable, true);
	}
	
	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param allowProtected
	 */
	protected String getFieldValueGet(JClassType voClass, JField field, String parentVariable, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			return parentVariable+"."+field.getName();
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				JMethod method = voClass.getMethod(getterMethodName, new JType[]{});
				if (method != null && (method.isPublic() || (allowProtected && method.isProtected())))
				{
					return (parentVariable+"."+getterMethodName+"()");
				}
				else
				{
					logger.log(TreeLogger.ERROR, messages.registeredClientObjectPropertyNotFound(field.getName()));
				}
			}
			catch (Exception e)
			{
				try
				{
					getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
					JMethod method = voClass.getMethod(getterMethodName, new JType[]{});
					if (method != null && (method.isPublic() || (allowProtected && method.isProtected())))
					{
						return (parentVariable+"."+getterMethodName+"()");
					}
					else
					{
						logger.log(TreeLogger.ERROR, messages.registeredClientObjectPropertyNotFound(field.getName()));
					}
				}
				catch (Exception e1)
				{
					logger.log(TreeLogger.ERROR, messages.registeredClientObjectPropertyNotFound(field.getName()));
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	private String getGenericDeclForType(JType type)
	{
		if (type.isEnum() != null)
		{
			return "String";
		}
		else if (type.isPrimitive() != null)
		{
			JPrimitiveType jPrimitiveType = type.isPrimitive();
			return jPrimitiveType.getQualifiedBoxedSourceName();
		}
		else
		{
			return type.getParameterizedQualifiedSourceName();
		}
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
	        return ClassUtils.getParsingExpressionForSimpleType(Window.class.getName()+".Location.getParameter(\""+name+"\")", type);
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	
	
	/**
	 * Get the field type
	 * @param logger
	 * @param field
	 * @return
	 */
	private JType getTypeForField(TreeLogger logger, JField field)
	{
		JType type = field.getType();
		if (type.getSimpleSourceName().endsWith("Async"))
		{
			try 
			{
	        	String typeSourceName = type.getQualifiedSourceName();
				type = context.getTypeOracle().getType(typeSourceName.substring(0,typeSourceName.length()-5));
			} 
			catch (Exception e) 
			{
				logger.log(TreeLogger.DEBUG, "Error reading field super type."); 
			}
		}
		return type;
	}	
	
	/**
	 * Returns <code>true</code> is the given field has both a "get" and a "set" methods.
	 * @param clazz
	 * @param field
	 * @return
	 */
	private boolean hasGetAndSetMethods(JField field, JClassType clazz)
	{
		return hasGetMethod(field, clazz) && hasSetMethod(field, clazz);
	}
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "get" method.
	 * @param clazz
	 * @param field
	 * @return
	 */
	private boolean hasGetMethod(JField field, JClassType clazz)
	{
		String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
		}
		catch (Exception e)
		{
			try
			{
				getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
				return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
			}
			catch (Exception e1)
			{
				if (clazz.getSuperclass() == null)
				{
					return false;
				}
				else
				{
					return hasGetMethod(field, clazz.getSuperclass());
				}
			}
		}
	}	
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "set" method.
	 * @param field
	 * @param clazz
	 * @return
	 */
	private boolean hasSetMethod(JField field, JClassType clazz)
	{
		String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(setterMethodName, new JType[]{field.getType()}) != null);
		}
		catch (Exception e)
		{
			if (clazz.getSuperclass() == null)
			{
				return false;
			}
			else
			{
				return hasSetMethod(field, clazz.getSuperclass());
			}
		}
	}

	/**
	 * Verify if the given field is fully accessible.
	 * @param field
	 * @param clazz
	 * @return <code>true</code> if the field is public or has associated "get" and "set" methods.
	 */
	protected boolean isFullAccessibleField(JField field, JClassType clazz)
	{
		return field.isPublic() || hasGetAndSetMethods(field, clazz);
	}	
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	private boolean isPropertyVisibleToRead(JClassType voClass, JField field)
	{
		if (field.isPublic() || field.isProtected())
		{
			return true;
		}
		else
		{
			return hasGetMethod(field, voClass);
		}
	}
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	private boolean isPropertyVisibleToWrite(JClassType voClass, JField field)
	{
		if ((field.isPublic() || field.isProtected()) && !field.isFinal())
		{
			return true;
		}
		else
		{
			return hasSetMethod(field, voClass);
		}
	}
	
	/**
	 * @param type
	 * @return
	 */
	private boolean isSimpleType(JType type)
	{
		
		if (type instanceof JPrimitiveType)
		{
			return true;
		}
		else
		{
			try
            {
	            JClassType classType = (JClassType)type;

	            JClassType charSequenceType = classType.getOracle().getType(CharSequence.class.getCanonicalName());
	            JClassType dateType = classType.getOracle().getType(Date.class.getCanonicalName());
	            JClassType numberType = classType.getOracle().getType(Number.class.getCanonicalName());
	            JClassType booleanType = classType.getOracle().getType(Boolean.class.getCanonicalName());
	            JClassType characterType = classType.getOracle().getType(Character.class.getCanonicalName());

	            return (classType.isPrimitive() != null) ||
	            (numberType.isAssignableFrom(classType)) ||
	            (booleanType.isAssignableFrom(classType)) ||
	            (characterType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (dateType.isAssignableFrom(classType)) ||
	            (classType.isEnum() != null);
            }
            catch (NotFoundException e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }		
		}
	}
}
