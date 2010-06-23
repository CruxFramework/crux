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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ClientInvokableGeneratorHelper extends AbstractRegisteredElementsGenerator
{	

	protected static GeneratorMessages coreMessages = MessagesFactory.getMessages(GeneratorMessages.class);
	
	
	/**
	 * Create objects for fields that are annotated with @Create
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	public static void generateAutoCreateFields(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter, String parentVariable)
	{
		generateAutoCreateFields(logger, controller, sourceWriter, parentVariable, new HashSet<String>());
	}
	
	/**
	 * 
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	public static void generateControllerUpdateObjectsFunction(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateControllerObjects(){");
		sourceWriter.indent();
		sourceWriter.println("Widget __wid = null;");
		generateControllerUpdateObjects(logger, "this", controller, sourceWriter);
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param autoBind
	 */
	public static void generateIsAutoBindEnabledMethod(SourceWriter sourceWriter, boolean autoBind)
	{
		sourceWriter.println("public boolean isAutoBindEnabled(){");
		sourceWriter.indent();
		sourceWriter.println("return "+autoBind+";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	public static void generateScreenUpdateWidgetsFunction(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateScreenWidgets(){");
		sourceWriter.indent();
		sourceWriter.println("Widget __wid = null;");
		sourceWriter.println("Object o = null;");
		generateScreenUpdateWidgets(logger, "this", controller, sourceWriter);
		sourceWriter.outdent();
		sourceWriter.println("}");
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
	protected static void generateFieldValueSet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable,  
			                           String valueVariable, SourceWriter sourceWriter)
	{
		generateFieldValueSet(logger, voClass, field, parentVariable, valueVariable, sourceWriter, true);
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
	protected static void generateFieldValueSet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable,  
			                           String valueVariable, SourceWriter sourceWriter, boolean allowProtected)
	{
		if ((Modifier.isPublic(field.getModifiers()) || (allowProtected && Modifier.isProtected(field.getModifiers()))))
		{
			sourceWriter.println(parentVariable+"."+field.getName()+"="+valueVariable+";");
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(setterMethodName, new Class<?>[]{field.getType()}) != null)
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
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param sourceWriter
	 */
	protected static String getFieldValueGet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable)
	{
		return getFieldValueGet(logger, voClass, field, parentVariable, true);
	}

	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param sourceWriter
	 * @param allowProtected
	 */
	protected static String getFieldValueGet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable, boolean allowProtected)
	{
		if ((Modifier.isPublic(field.getModifiers()) || (allowProtected && Modifier.isProtected(field.getModifiers()))))
		{
			return parentVariable+"."+field.getName();
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				Method method = voClass.getMethod(getterMethodName, new Class<?>[]{});
				if (method != null && (Modifier.isPublic(method.getModifiers()) || 
						               (allowProtected && Modifier.isProtected(method.getModifiers()))))
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
					Method method = voClass.getMethod(getterMethodName, new Class<?>[]{});
					if (method != null && (Modifier.isPublic(method.getModifiers()) || 
							               (allowProtected && Modifier.isProtected(method.getModifiers()))))
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
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	protected static boolean isPropertyVisibleToRead(Class<?> voClass, Field field)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			return true;
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				return (voClass.getMethod(getterMethodName, new Class<?>[]{}) != null);
			}
			catch (Exception e)
			{
				try
				{
					getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
					return (voClass.getMethod(getterMethodName, new Class<?>[]{}) != null);
				}
				catch (Exception e1)
				{
					return false;
				}
			}
		}
	}
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	protected static boolean isPropertyVisibleToWrite(Class<?> voClass, Field field)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			return true;
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				return (voClass.getMethod(setterMethodName, new Class<?>[]{field.getType()}) != null);
			}
			catch (Exception e)
			{
				return false;
			}
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param name
	 */
	private static void generateAutoBindHandlerForAllWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String name, boolean allowProtected)
	{
		String valueVariable = "__wid";
		sourceWriter.println(valueVariable + "= Screen.get(\""+name+"\");");
		sourceWriter.println("if ("+valueVariable+" != null){");
		sourceWriter.indent();
		sourceWriter.println("if ("+valueVariable+" instanceof HasFormatter){");
		sourceWriter.indent();
		generateHandleHasFormatterWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable, allowProtected);
		sourceWriter.outdent();
		sourceWriter.println("}else if ("+valueVariable+" instanceof HasValue){");
		sourceWriter.indent();
		generateHandleHasValueWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable, allowProtected);
		sourceWriter.outdent();
		sourceWriter.print("}");
		if (String.class.isAssignableFrom(type))
		{
			sourceWriter.println("else if ("+valueVariable+" instanceof HasText){");
			sourceWriter.indent();
			generateHandleHasTextWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * Create objects for fields that are annotated with @Create
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	private static void generateAutoCreateFields(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter, String parentVariable, Set<String> added)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (!added.contains(field.getName()))
			{
				if (field.getAnnotation(Create.class) != null)
				{
					added.add(field.getName());
					String fieldTypeName = getClassSourceName(field.getType());
					Class<?> type = getTypeForField(logger, field);
					String typeName = getClassSourceName(type);

					if (DataSource.class.isAssignableFrom(type))
					{
						String dsName = getDsName(type);
						sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=("+fieldTypeName+")"+
								br.com.sysmap.crux.core.client.screen.Screen.class.getName()+".createDataSource(\""+dsName+"\");");

						generateFieldValueSet(logger, controller, field, parentVariable, "_field"+field.getName(), sourceWriter);					
					}
					else
					{
						ParameterObject parameterObjectAnnot = type.getAnnotation(ParameterObject.class);
						if (parameterObjectAnnot != null)
						{
							generateParameterPopulation(logger, controller, parentVariable, sourceWriter, field);
						}
						else
						{
							sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=GWT.create("+typeName+".class);");
							generateFieldValueSet(logger, controller, field, parentVariable, "_field"+field.getName(), sourceWriter);
						}
					}
				}
				else if (field.getAnnotation(Parameter.class) != null)
				{
					generateParameterPopulation(logger, controller, parentVariable, sourceWriter, field);
				}
			}
		}

		if (controller.getSuperclass() != null)
		{
			generateAutoCreateFields(logger, controller.getSuperclass(), sourceWriter, parentVariable, added);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	private static void generateControllerUpdateObjects(TreeLogger logger, String controllerVariable, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				Class<?> type = field.getType();

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateDTOFieldPopulation(logger, controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateControllerUpdateObjects(logger, controllerVariable, controller.getSuperclass(), sourceWriter);
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
	private static void generateDTOFieldPopulation(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, parentVariable, voClass, field, sourceWriter, false, true);
	}
	
	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param logger
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	private static void generateDTOParameterPopulation(TreeLogger logger, String resultVariable, Class<?> voClass, SourceWriter sourceWriter)
	{
		boolean hasAtLeastOneField = false;
		for (Field field : voClass.getDeclaredFields()) 
		{
			if (isPropertyVisibleToWrite(voClass, field))
			{
				ParameterObject parameterObject = voClass.getAnnotation(ParameterObject.class);
				Parameter parameter = field.getAnnotation(Parameter.class); 
				if ((parameterObject != null && parameterObject.bindParameterByFieldName()) || parameter != null)
				{
					hasAtLeastOneField = true;
					generateDTOParameterPopulationField(logger, resultVariable, voClass, field, sourceWriter, false);
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
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param allowProtected
	 */
	private static void generateDTOParameterPopulationField(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean allowProtected)
	{
		Class<?> type = field.getType();
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
		
		if ((type.isPrimitive()) ||
                (Number.class.isAssignableFrom(type)) ||
                (Boolean.class.isAssignableFrom(type)) || 
                (Character.class.isAssignableFrom(type)) ||
                (CharSequence.class.isAssignableFrom(type)) ||
                (Date.class.isAssignableFrom(type)) ||
                (type.isEnum())) 
		{
			if (required)
			{
				
				sourceWriter.println("if (" +Window.class.getName()+".Location.getParameter(\""+name+"\")==null){");
				sourceWriter.indent();
				sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote(coreMessages.requiredParameterMissing(name))+");");
				sourceWriter.outdent();
				sourceWriter.println("}");
				
			}
			sourceWriter.println("if (" +Window.class.getName()+".Location.getParameter(\""+name+"\")!=null){");
			sourceWriter.indent();
			sourceWriter.println("try{");
			sourceWriter.indent();
			generateParameterBinding(logger, parentVariable, voClass, field, sourceWriter, type, name, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}catch(Throwable _e1){");
			sourceWriter.indent();
			sourceWriter.println("throw new "+ValidateException.class.getName()+"("+EscapeUtils.quote(coreMessages.errorReadingParameter(name))+");");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		else if (type.getAnnotation(ParameterObject.class) != null)
		{
			sourceWriter.println("if (" +getFieldValueGet(logger, voClass, field, parentVariable, allowProtected)+"==null){");
			sourceWriter.indent();
			generateFieldValueSet(logger, voClass, field, parentVariable, "new "+getClassSourceName(type)+"()", sourceWriter, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}");

			parentVariable = getFieldValueGet(logger, voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateDTOParameterPopulation(logger, parentVariable, type, sourceWriter);
			}
		}			
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 */
	private static void generateHandleHasFormatterWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasFormatter)"+valueVariable+").setUnformattedValue("
					            + getFieldValueGet(logger, voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, "("+getGenericDeclForType(type)+")"
					            + "((HasFormatter)"+valueVariable+").getUnformattedValue()", sourceWriter, allowProtected);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param valueVariable
	 */
	private static void generateHandleHasTextWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, 
			boolean populateScreen, Class<?> type, String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("o = " +getFieldValueGet(logger, voClass, field, parentVariable, allowProtected)+";");
			sourceWriter.println("((HasText)"+valueVariable+").setText(String.valueOf(o!=null?o:\"\"));");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, "((HasText)"+valueVariable+").getText()", sourceWriter, allowProtected);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 */
	private static void generateHandleHasValueWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String valueVariable, boolean allowProtected)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").setValue("
					            + getFieldValueGet(logger, voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, 
					"("+getGenericDeclForType(type)+")((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").getValue()", 
					sourceWriter, allowProtected);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param type
	 * @param name
	 */
	private static void generateParameterBinding(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, 
			Class<?> type, String name, boolean allowProtected)
	{
		generateFieldValueSet(logger, voClass, field, parentVariable, getParameterFromURL(type, name), sourceWriter, allowProtected);
	}
	
	/**
	 * 
	 * @param logger
	 * @param controller
	 * @param parentVariable 
	 * @param sourceWriter
	 * @param field
	 */
	private static void generateParameterPopulation(TreeLogger logger, Class<?> controller, String parentVariable, SourceWriter sourceWriter, Field field)
	{
		sourceWriter.println("try{");
		sourceWriter.indent();
		generateDTOParameterPopulationField(logger, parentVariable, controller, field, sourceWriter, true);
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
	 * @param logger
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	private static void generateScreenOrDTOPopulation(TreeLogger logger, String resultVariable, Class<?> voClass, SourceWriter sourceWriter, boolean populateScreen)
	{
		for (Field field : voClass.getDeclaredFields()) 
		{
			if ((populateScreen && isPropertyVisibleToRead(voClass, field)) || 
				(!populateScreen && isPropertyVisibleToWrite(voClass, field)))
			{
				ValueObject valueObject = voClass.getAnnotation(ValueObject.class);
				ScreenBind screenBind = field.getAnnotation(ScreenBind.class); 
				if (valueObject != null && valueObject.bindWidgetByFieldName() || screenBind != null)
				{
					generateScreenOrDTOPopulationField(logger, resultVariable, voClass, field, sourceWriter, populateScreen, false);
				}
			}
		}
	}	

	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	private static void generateScreenOrDTOPopulationField(TreeLogger logger, String parentVariable, Class<?> voClass, 
			                                        Field field, SourceWriter sourceWriter, boolean populateScreen, boolean allowProtected)
	{
		Class<?> type = field.getType();
		String name = null;
		if (field.getAnnotation(ScreenBind.class) != null)
		{
			name = field.getAnnotation(ScreenBind.class).value();
		}
		if (name == null || name.length() == 0)
		{
			name = field.getName();
		}
		
		if ((type.isPrimitive()) ||
                (Number.class.isAssignableFrom(type)) ||
                (Boolean.class.isAssignableFrom(type)) || 
                (Character.class.isAssignableFrom(type)) ||
                (CharSequence.class.isAssignableFrom(type)) ||
                (Date.class.isAssignableFrom(type)) ||
                (type.isEnum())) 
		{
			generateAutoBindHandlerForAllWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, name, allowProtected);
		}
		else if (type.getAnnotation(ValueObject.class) != null)
		{
			if (!populateScreen)
			{
				sourceWriter.println("if (" +getFieldValueGet(logger, voClass, field, parentVariable, allowProtected)+"==null){");
				sourceWriter.indent();
				generateFieldValueSet(logger, voClass, field, parentVariable, "new "+getClassSourceName(type)+"()", sourceWriter, allowProtected);
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
			parentVariable = getFieldValueGet(logger, voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateScreenOrDTOPopulation(logger, parentVariable, type, sourceWriter, populateScreen);
			}
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	private static void generateScreenUpdateWidgets(TreeLogger logger, String controllerVariable, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				Class<?> type = field.getType();

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateScreenWidgetPopulation(logger, controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateScreenUpdateWidgets(logger, controllerVariable, controller.getSuperclass(), sourceWriter);
		}
	}

	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	private static void generateScreenWidgetPopulation(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, parentVariable, voClass, field, sourceWriter, true, true);
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private static String getDsName(Class<?> type)
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
			dsName = type.getSimpleName();
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
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	private static String getGenericDeclForType(Class<?> type)
	{
		if (type.isEnum())
		{
			return "String";
		}
		else if (type.isPrimitive())
		{
			if (type.getName().equals("boolean"))
			{
				return "Boolean";
			}
			else if (type.getName().equals("char"))
			{
				return "Character";
			}
			else if (type.getName().equals("byte"))
			{
				return "Byte";
			}
			else if (type.getName().equals("short"))
			{
				return "Short";
			}
			else if (type.getName().equals("int"))
			{
				return "Integer";
			}
			else if (type.getName().equals("long"))
			{
				return "Long";
			}
			else if (type.getName().equals("float"))
			{
				return "Float";
			}
			else if (type.getName().equals("double"))
			{
				return "Double";
			}
			return "?";
		}
		else
		{
			return getClassSourceName(type);
		}
	}
	
	/**
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	private static String getParameterFromURL(Class<?> type, String name)
	{
		return ClassUtils.getParsingExpressionForSimpleType(Window.class.getName()+".Location.getParameter(\""+name+"\")", type);
	}
	
	/**
	 * Get the field type
	 * @param logger
	 * @param field
	 * @return
	 */
	private static Class<?> getTypeForField(TreeLogger logger, Field field)
	{
		Class<?> type = field.getType();
		if (type.getName().endsWith("Async"))
		{
			try 
			{
				type = Class.forName(type.getName().substring(0,type.getName().length()-5));
			} 
			catch (Exception e) 
			{
				logger.log(TreeLogger.DEBUG, "Error reading field super type."); 
			}
		}
		return type;
	}
}
