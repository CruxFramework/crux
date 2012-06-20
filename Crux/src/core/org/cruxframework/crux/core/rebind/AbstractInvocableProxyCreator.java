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
package org.cruxframework.crux.core.rebind;

import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.controller.ParameterObject;
import org.cruxframework.crux.core.client.controller.ScreenBind;
import org.cruxframework.crux.core.client.controller.ValueObject;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.rebind.screen.parameter.ParameterBindGenerator;
import org.cruxframework.crux.core.rebind.screen.parameter.ParameterBindGeneratorInitializer;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Base class for all proxy creators that will produce crux invocable objects.
 * A Crux Invocable object is this that can be dispatched from the crux engine, 
 * like Controllers or DataSources.  
 * @author Thiago da Rosa de Bustamante
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractInvocableProxyCreator extends AbstractSerializableProxyCreator
{
	protected JClassType invocableClassType;
	
	public AbstractInvocableProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseProxyType, JClassType invocableClassType)
    {
	    super(logger, context, baseProxyType);
	    this.invocableClassType = invocableClassType;
    }
	
	/**
	 * Create objects for fields that are annotated with @Create
	 * @param sourceWriter
	 * @param parentVariable
	 * @param isAutoBindEnabled
	 */
	@Deprecated
	protected void generateAutoCreateFields(SourceWriter sourceWriter, String parentVariable, boolean isAutoBindEnabled)
	{
		generateAutoCreateFields(invocableClassType, sourceWriter, parentVariable, new HashSet<String>());
		if (isAutoBindEnabled)
		{
			sourceWriter.println("updateScreenWidgets();");
		}		
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
	        			
	        			JClassType dataSourceType = classType.getOracle().getType(DataSource.class.getCanonicalName());
	        			if (type instanceof JClassType && dataSourceType.isAssignableFrom((JClassType)type))
	        			{
	        				String dsName = getDsName((JClassType)type);
	        				sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=("+fieldTypeName+")"+
	        						org.cruxframework.crux.core.client.screen.Screen.class.getName()+".createDataSource(\""+dsName+"\");");

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
	        					sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=GWT.create("+type.getQualifiedSourceName()+".class);");
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
		generateScreenOrDTOPopulationField(parentVariable, voClass, field, sourceWriter, false, true, new HashSet<String>());
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
		JClassUtils.generateFieldValueSet(voClass, field, parentVariable, valueVariable, sourceWriter, true);
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
		String valueVariableName = "__valueVariable";
		JPrimitiveType isPrimitiveType = type.isPrimitive();
		
		
		if (populateScreen)
		{
			sourceWriter.println("((HasFormatter)"+valueVariable+").setUnformattedValue("
					            + JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			sourceWriter.println("Object "+valueVariableName+" = ((HasFormatter)"+valueVariable+").getUnformattedValue();");
			if (isPrimitiveType != null)
			{
				sourceWriter.println("if ("+valueVariableName+" == null){");
				sourceWriter.println(valueVariableName+" = (" +isPrimitiveType.getQualifiedBoxedSourceName()+ ")" +(isPrimitiveType == JPrimitiveType.BOOLEAN?"false":"0")+ ";");
				sourceWriter.println("}");
			}
			
			JClassUtils.generateFieldValueSet(voClass, field, parentVariable, "("+JClassUtils.getGenericDeclForType(type)+")"
					            + valueVariableName, sourceWriter, allowProtected);
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
			sourceWriter.println("o = " +JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected)+";");
			sourceWriter.println("((HasText)"+valueVariable+").setText(String.valueOf(o!=null?o:\"\"));");
		}
		else
		{
			JClassUtils.generateFieldValueSet(voClass, field, parentVariable, "((HasText)"+valueVariable+").getText()", sourceWriter, allowProtected);
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
			sourceWriter.println("((HasValue<"+JClassUtils.getGenericDeclForType(type)+">)"+valueVariable+").setValue("
					            + JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected)+");");
		}
		else
		{
			JClassUtils.generateFieldValueSet(voClass, field, parentVariable, 
					"("+JClassUtils.getGenericDeclForType(type)+")((HasValue<"+JClassUtils.getGenericDeclForType(type)+">)"+valueVariable+").getValue()", 
					sourceWriter, allowProtected);
		}
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
		ParameterBindGenerator parameterBindGenerator = ParameterBindGeneratorInitializer.getParameterBindGenerator();
		parameterBindGenerator.generate(parentVariable, classType, field, sourceWriter, logger);
	}
	
	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 * @param populateScreen
	 * @param dtoLooping
	 */
	private void generateScreenOrDTOPopulation(String resultVariable, JClassType voClass, SourceWriter sourceWriter, 
			boolean populateScreen, Set<String> dtoLooping)
	{
		dtoLooping.add(voClass.getQualifiedSourceName());
		for (JField field : voClass.getFields()) 
		{
			if ((populateScreen && JClassUtils.isPropertyVisibleToRead(voClass, field)) || 
				(!populateScreen && JClassUtils.isPropertyVisibleToWrite(voClass, field)))
			{
				ValueObject valueObject = voClass.getAnnotation(ValueObject.class);
				ScreenBind screenBind = field.getAnnotation(ScreenBind.class); 
				if (valueObject != null && valueObject.bindWidgetByFieldName() || screenBind != null)
				{
					if (dtoLooping.contains(field.getType().getQualifiedSourceName()))
					{
						throw new CruxGeneratorException("Error Generating value binding code for DTO ["+voClass.getQualifiedSourceName()+"], " +
								"Field ["+field.getName()+"]. Circular Reference.");
					}
					generateScreenOrDTOPopulationField(resultVariable, voClass, field, sourceWriter, populateScreen, false, dtoLooping);
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
			                                        JField field, SourceWriter sourceWriter, 
			                                        boolean populateScreen, boolean allowProtected, 
			                                        Set<String> dtoLooping)
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
		
		if (JClassUtils.isSimpleType(type)) 
		{
			generateAutoBindHandlerForAllWidgets(parentVariable, voClass, field, sourceWriter, populateScreen, type, name, allowProtected);
		}
		else if (type instanceof JClassType && ((JClassType)type).getAnnotation(ValueObject.class) != null)
		{
			sourceWriter.println("if (" +JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected)+"==null){");
			sourceWriter.indent();

			JClassUtils.generateFieldValueSet(voClass, field, parentVariable, "new "+type.getParameterizedQualifiedSourceName()+"()", sourceWriter, allowProtected);
			sourceWriter.outdent();
			sourceWriter.println("}");
			parentVariable = JClassUtils.getFieldValueGet(voClass, field, parentVariable, allowProtected);
			if (parentVariable != null)
			{
				generateScreenOrDTOPopulation(parentVariable, (JClassType)type, sourceWriter, populateScreen, dtoLooping);
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
		generateScreenOrDTOPopulationField(parentVariable, voClass, field, sourceWriter, true, true, new HashSet<String>());
	}
	
	
	/**
	 * Return the DataSource name associated with a DataSource subclass
	 * @param type
	 * @return
	 */
	private String getDsName(JClassType type)
	{
		String dsName = null;
		org.cruxframework.crux.core.client.datasource.annotation.DataSource annot = 
			type.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.DataSource.class);
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
		return JClassUtils.getFieldValueGet(voClass, field, parentVariable, true);
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
}
