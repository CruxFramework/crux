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

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.DataSoureExcpetion;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.Metadata;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.client.screen.ScreenBindableObject;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.GenericUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class RegisteredClientDataSourcesGenerator extends AbstractRegisteredElementsGenerator
{
	@Override
	protected void generateClass(TreeLogger logger, GeneratorContext context,JClassType classType, List<Screen> screens) 
	{
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImplementedInterface(RegisteredDataSources.class.getName());
		composer.addImport(Metadata.class.getName());
		composer.addImport(Widget.class.getName());
		composer.addImport(GWT.class.getName());
		composer.addImport(br.com.sysmap.crux.core.client.screen.Screen.class.getName());
		composer.addImport(HasValue.class.getName());
		composer.addImport(HasText.class.getName());
		composer.addImport(HasFormatter.class.getName());
		composer.addImport(DataSoureExcpetion.class.getName());
		composer.addImport(DataSourceRecord.class.getName());	
	
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		Map<String, String> dataSourcesClassNames = new HashMap<String, String>();
		for (Screen screen : screens)
		{
			generateDataSourcesForScreen(logger, sourceWriter, screen, dataSourcesClassNames);
		}
		generateGetdataSourceMethod(logger, sourceWriter, implClassName, dataSourcesClassNames);

		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param implClassName
	 * @param dataSourcesClassNames
	 */
	private void generateGetdataSourceMethod(TreeLogger logger, SourceWriter sourceWriter, String implClassName, 
			Map<String, String> dataSourcesClassNames) 
	{
		sourceWriter.println("public DataSource<?> getDataSource(String id){");
		boolean first = true;
		for (String dataSource : dataSourcesClassNames.keySet()) 
		{
			if (!first)
			{
				sourceWriter.print("else ");
			}
			else
			{
				first = false;
			}
			sourceWriter.println("if(\""+dataSource+"\".equals(id)){");
			sourceWriter.println("return new " + dataSourcesClassNames.get(dataSource) + "();");
			sourceWriter.println("}");
		}
		sourceWriter.println("throw new DataSoureExcpetion("+EscapeUtils.quote(messages.errorGeneratingRegisteredDataSourceNotFound())+"+id);");
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param screen
	 * @param dataSourcesClassNames
	 */
	private void generateDataSourcesForScreen(TreeLogger logger, SourceWriter sourceWriter, Screen screen, 
			Map<String, String> dataSourcesClassNames)
	{
		Iterator<String> dataSources = screen.iterateDataSources();
		
		while (dataSources.hasNext())
		{
			String dataSource = dataSources.next();
			generateDataSourceClassBlock(logger, screen, sourceWriter, dataSource, dataSourcesClassNames);
		}		
	}
	
	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param sourceWriter
	 * @param dataSource
	 * @param added
	 */
	private void generateDataSourceClassBlock(TreeLogger logger, Screen screen, SourceWriter sourceWriter, String dataSource, 
			Map<String, String> added)
	{
		if (!added.containsKey(dataSource) && DataSources.getDataSource(dataSource)!= null)
		{
			String genClass = generateDataSourceClass(logger,screen,sourceWriter,DataSources.getDataSource(dataSource));
			added.put(dataSource, genClass);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param sourceWriter
	 * @param dataSourceClass
	 * @return
	 */
	private String generateDataSourceClass(TreeLogger logger, Screen screen, SourceWriter sourceWriter, 
						Class<? extends DataSource<?>> dataSourceClass)
	{
		String className = dataSourceClass.getSimpleName()+"Wrapper";
		sourceWriter.println("public class "+className+" extends " + getClassSourceName(dataSourceClass)
				         + " implements "+ScreenBindableObject.class.getName()+"{");
		sourceWriter.indent();		
		ColumnsData columnsData = generateDataSourceClassConstructor(logger, sourceWriter, dataSourceClass, className);	
		
		br.com.sysmap.crux.core.client.datasource.annotation.DataSource annot = 
				dataSourceClass.getAnnotation(br.com.sysmap.crux.core.client.datasource.annotation.DataSource.class);
		boolean autoBind = (annot == null || annot.autoBind());

		if (RemoteDataSource.class.isAssignableFrom(dataSourceClass))
		{
			generateFetchFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
		}
		else if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
		{
			generateLoadFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
		}
		generateUpdateFunction(logger, dataSourceClass, sourceWriter, columnsData);
		ClientInvokableGeneratorHelper.generateScreenUpdateWidgetsFunction(logger, dataSourceClass, sourceWriter);
		ClientInvokableGeneratorHelper.generateControllerUpdateObjectsFunction(logger, dataSourceClass, sourceWriter);
		generateGetBoundObjectFunction(logger, screen, dataSourceClass, sourceWriter);
		generateGetValueFunction(logger, dataSourceClass, columnsData, sourceWriter);
		ClientInvokableGeneratorHelper.generateIsAutoBindEnabledMethod(sourceWriter, autoBind);
		
		sourceWriter.outdent();		
		sourceWriter.println("}");
		return className;
	}

	private void generateGetValueFunction(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass,
			ColumnsData columnsData, SourceWriter sourceWriter)
	{
		try
		{
			Class<?> recordType = getRecordTypeFromClass(logger, dataSourceClass);
			String recordTypeDeclaration = getParameterDeclaration(recordType);

			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);			
			String dataTypeDeclaration = getParameterDeclaration(dataType);

			sourceWriter.println("public Object getValue(String columnName, "+recordTypeDeclaration+"<"+dataTypeDeclaration+"> dataSourceRecord){");
			sourceWriter.indent();

			sourceWriter.println(dataTypeDeclaration + " recordObject = dataSourceRecord.getRecordObject();");
			sourceWriter.println("Object ret = recordObject;");

			sourceWriter.println("if (recordObject == null){");
			sourceWriter.indent();
			sourceWriter.println("return null;");
			sourceWriter.outdent();
			sourceWriter.println("}");

			for (String columnName : columnsData.names)
            {
				sourceWriter.println("else if (columnName.equals(\""+columnName+"\")){");
				sourceWriter.indent();
				generateGetValueForColumn(logger, dataType, columnName, sourceWriter);
				sourceWriter.outdent();
				sourceWriter.println("}");
            }
			sourceWriter.println("else {");
			sourceWriter.indent();
			sourceWriter.println("ret = null;");
			sourceWriter.outdent();
			sourceWriter.println("}");
			
			sourceWriter.println("return ret;");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
    }

	/**
	 * @param logger
	 * @param baseClass
	 * @param columnName
	 * @param sourceWriter
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	private void generateGetValueForColumn(TreeLogger logger, Class<?> baseClass, String columnName, SourceWriter sourceWriter)
	             throws SecurityException, NoSuchFieldException
    {
	    if (columnName.indexOf('.') < 0)
	    {
	    	generateGetValueForProperty(baseClass, columnName, sourceWriter);
	    }
	    else
	    {
	    	String firstProperty = columnName.substring(0,columnName.indexOf('.'));
	    	generateGetValueForProperty(baseClass, firstProperty, sourceWriter);
		    sourceWriter.println("if (ret != null){");
			sourceWriter.indent();
			
	    	columnName = columnName.substring(columnName.indexOf('.')+1);
	    	Field field = baseClass.getDeclaredField(firstProperty);
	    	generateGetValueForColumn(logger, field.getType(), columnName, sourceWriter);
	    	
	    	sourceWriter.outdent();
	    	sourceWriter.println("}");
	    }
	    
    }

	/**
	 * @param baseClass
	 * @param columnName
	 * @param sourceWriter
	 * @throws NoSuchFieldException
	 */
	private void generateGetValueForProperty(Class<?> baseClass, String columnName, SourceWriter sourceWriter) throws NoSuchFieldException
    {
		String baseTypeDeclaration = getParameterDeclaration(baseClass);
	    sourceWriter.print("ret = (("+baseTypeDeclaration+")ret).");
	    Field field = baseClass.getDeclaredField(columnName);
	    if (Modifier.isPublic(field.getModifiers()))
	    {
	    	sourceWriter.print(columnName);
	    }
	    else
	    {
	    	sourceWriter.print(ClassUtils.getGetterMethod(columnName)+"()");
	    }
	    sourceWriter.println(";");
    }

	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateLoadFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, boolean autoBind)
	{		
		try
		{
			sourceWriter.println("public void load(){");
			if (autoBind)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.load();");
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateUpdateFunction(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, ColumnsData columnsData)
	{
		try
		{
			Class<?> recordType = getRecordTypeFromClass(logger, dataSourceClass);
			String recordTypeDeclaration = getParameterDeclaration(recordType);
			
			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);			
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			 		
			sourceWriter.println("public void updateData("+dataTypeDeclaration+"[] data){");
			sourceWriter.indent();
			sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[(data!=null?data.length:0)];");
			sourceWriter.println("for (int i=0; i<data.length; i++){");
			sourceWriter.indent();
			sourceWriter.print("ret[i] = new "+recordTypeDeclaration+"(this,");
			sourceWriter.print(getIdentifierDeclaration(logger, dataType, columnsData.identifier, "data[i]"));
			sourceWriter.println(");");
			sourceWriter.println("ret[i].setRecordObject(data[i]);");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("update(ret);");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateGetBoundObjectFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter)
	{
		try
		{
			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);			
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			 
			Class<?> recordType = getRecordTypeFromClass(logger, dataSourceClass);			
			String recordTypeDeclaration = getParameterDeclaration(recordType);

			sourceWriter.println("public "+dataTypeDeclaration+" getBoundObject("+recordTypeDeclaration+"<"+dataTypeDeclaration+"> record){");
			sourceWriter.indent();
			sourceWriter.println("if (record == null) return null;");
			sourceWriter.println("return record.getRecordObject();");
			sourceWriter.outdent();
			sourceWriter.println("}");//TODO: clonar o objeto ou remover isso daqui
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param dataType
	 * @param identifiers
	 * @param parentVariable
	 * @return
	 * @throws NoSuchFieldException
	 */
	private String getIdentifierDeclaration(TreeLogger logger, Class<?> dataType, String identifiers, String parentVariable) throws NoSuchFieldException
	{
		String[] identifier = RegexpPatterns.REGEXP_COMMA.split(identifiers);
		StringBuilder result = new StringBuilder("\"\""); 

		for (int i = 0; i < identifier.length; i++)
		{
			result.append("+"+ClientInvokableGeneratorHelper.getFieldValueGet(logger, dataType, ClassUtils.getDeclaredField(dataType, identifier[i]), parentVariable, false));
		}
		return result.toString();
	}
	
	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateFetchFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, boolean autoBind)
	{
		try
		{
			sourceWriter.println("public void fetch(int startRecord, int endRecord){");
			if (autoBind)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.fetch(startRecord, endRecord);");
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param dataSourceClass
	 * @param className
	 */
	private ColumnsData generateDataSourceClassConstructor(TreeLogger logger, SourceWriter sourceWriter, 
			Class<? extends DataSource<?>> dataSourceClass, String className)
	{
		ColumnsData ret = new ColumnsData();
		sourceWriter.println("public "+className+"(){");
		sourceWriter.println("this.metadata = new Metadata();");
		
		ret.identifier = getDataSourceIdentifier(dataSourceClass);
		generateMetadataPopulationBlockFromType(logger, sourceWriter, getDtoTypeFromClass(logger, dataSourceClass), 
				dataSourceClass.getName(), ret);
		ClientInvokableGeneratorHelper.generateAutoCreateFields(logger, dataSourceClass, sourceWriter, "this");
		sourceWriter.println("}");
		
		return ret;
	}

	@SuppressWarnings("deprecation")
    private String getDataSourceIdentifier(Class<? extends DataSource<?>> dataSourceClass)
    {
		DataSourceRecordIdentifier idAnnotation = 
			dataSourceClass.getAnnotation(DataSourceRecordIdentifier.class);
		if (idAnnotation != null)
		{
			return idAnnotation.value();
		}

		br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding typeAnnot = 
			dataSourceClass.getAnnotation(br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding.class);
		return typeAnnot.identifier();
    }

	/**
	 * 
	 * @param logger
	 * @param dataSourceClass
	 * @return
	 */
	private Class<?> getDtoTypeFromClass(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass)
	{
		Class<?> returnType = GenericUtils.resolveReturnType(dataSourceClass, "getBoundObject", new Class[]{});
		if (returnType == null)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceCanNotRealizeGenericType(dataSourceClass.getName()));
		}
		return returnType;
	}
	
	/**
	 * 
	 * @param logger
	 * @param dataSourceClass
	 * @return
	 */
	private Class<?> getRecordTypeFromClass(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass)
	{
		Class<?> returnType = GenericUtils.resolveReturnType(dataSourceClass, "getRecord", new Class[]{});
		if (returnType == null)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceCanNotRealizeGenericType(dataSourceClass.getName()));
		}
		return returnType;
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 */
	private void generateMetadataPopulationBlockFromType(TreeLogger logger, SourceWriter sourceWriter, 
							Class<?> dtoType, String dataSourceClassName, ColumnsData columnsData)
	{
		List<String> names = new ArrayList<String>();
		List<Class<?>> types = new ArrayList<Class<?>>();
		
		findDataSourceColumns(dtoType, names, types, null);
		columnsData.names = names.toArray(new String[0]);
		columnsData.types = types.toArray(new Class[0]);
		
		generateMetadaPopulationBlock(logger, sourceWriter, dataSourceClassName, columnsData);
	}

	/**
	 * @param dtoType
	 * @param names
	 * @param types
	 */
	private void findDataSourceColumns(Class<?> dtoType, List<String> names, List<Class<?>> types, String parentField)
    {
	    Field[] declaredFields = ClassUtils.getDeclaredFields(dtoType);
		
		for (Field field : declaredFields)
		{
			if (mustInclude(field, dtoType))
			{
				String columnName;
				if (StringUtils.isEmpty(parentField))
				{
					columnName = field.getName();
				}
				else
				{
					columnName = parentField+"."+field.getName();
				}
				names.add(columnName);
				Class<?> columnType = field.getType();
				types.add(columnType);
				if (isComplexCustomType(columnType))
				{
					findDataSourceColumns(columnType, names, types, columnName);
				}
			}
		}
    }

	/**
	 * @param type
	 * @return
	 */
	private boolean isComplexCustomType(Class<?> type)
    {
		return (!type.isPrimitive() && !type.isAnnotation() && !type.isArray() && !CharSequence.class.isAssignableFrom(type)
				&& !Number.class.isAssignableFrom(type) && !Character.class.isAssignableFrom(type) && !Boolean.class.isAssignableFrom(type)
				&& !Date.class.isAssignableFrom(type) && !Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type)
				&& !type.isEnum());
    }

	/**
	 * 
	 * @param field
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	private boolean mustInclude(Field field, Class<?> dtoType)
	{
		boolean mustInclude = ClientInvokableGeneratorHelper.isFullAccessibleField(field, dtoType);

		return mustInclude;
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param dataSourceClassName
	 * @param names
	 * @param types
	 */
	private void generateMetadaPopulationBlock(TreeLogger logger, SourceWriter sourceWriter, String dataSourceClassName, 
			                                  ColumnsData columnsData)
	{
		if (columnsData.names.length != columnsData.types.length)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceInvalidMetaInformation(dataSourceClassName), null);
		}
		
		for (int i=0; i<columnsData.names.length; i++)
		{
			boolean sortable = Comparable.class.isAssignableFrom(columnsData.types[i]);
			sourceWriter.println("metadata.addColumn(new ColumnMetadata<"+getParameterDeclarationWithPrimitiveWrappers(columnsData.types[i])+">(\""+columnsData.names[i]+"\","+sortable+"));");
		}
	}	
	
	/**
	 * @param parameterClass
	 * @return
	 */
	private String getParameterDeclarationWithPrimitiveWrappers(Class<?> parameterClass)
	{
		if (parameterClass.isPrimitive())
		{
			return getParameterDeclaration(ClassUtils.getReflectionEquivalentTypeForPrimities(parameterClass));
		}
		
		return getParameterDeclaration(parameterClass);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class ColumnsData
	{
		private String[] names;
		private Class<?>[] types;
		private String identifier;
	}
}
