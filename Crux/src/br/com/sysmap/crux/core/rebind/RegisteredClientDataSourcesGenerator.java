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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.datasource.Bindable;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.DataSoureExcpetion;
import br.com.sysmap.crux.core.client.datasource.EditableDataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalBindableEditableScrollableDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalBindablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalBindableScrollableDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.Metadata;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceColumns;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.client.screen.ScreenBindableObject;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;
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

public class RegisteredClientDataSourcesGenerator extends AbstractRegisteredClientInvokableGenerator
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
		try
		{
			if (!added.containsKey(dataSource) && DataSources.getDataSource(dataSource)!= null)
			{
				String genClass = generateDataSourceClass(logger,screen,sourceWriter,DataSources.getDataSource(dataSource));
				added.put(dataSource, genClass);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSource, e.getLocalizedMessage()), e);
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
				
		ColumnsData columnsData = generateDataSourceClassConstructor(logger, sourceWriter, dataSourceClass, className);	
		
		boolean autoBind = true;
		br.com.sysmap.crux.core.client.datasource.annotation.DataSource annot = 
				dataSourceClass.getAnnotation(br.com.sysmap.crux.core.client.datasource.annotation.DataSource.class);

		if (annot != null)
		{
			autoBind = annot.autoBind();
		}
		if (RemoteDataSource.class.isAssignableFrom(dataSourceClass))
		{
			generateFetchFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
		}
		else if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
		{
			generateLoadFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
		}
		generateUpdateFunction(logger, screen, dataSourceClass, sourceWriter, columnsData);
		generateScreenUpdateWidgetsFunction(logger, screen, dataSourceClass, sourceWriter);
		generateControllerUpdateObjectsFunction(logger, screen, dataSourceClass, sourceWriter);
		generateGetBindedObjectFunction(logger, screen, dataSourceClass, sourceWriter, columnsData);
		
		sourceWriter.println("}");
		return className;
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
	 * @param screen
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateUpdateFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, ColumnsData columnsData)
	{
		try
		{
			
			Class<?> recordType = getRecordTypeFromClass(logger, dataSourceClass);
			String recordTypeDeclaration = getParameterDeclaration(recordType);
			
			Class<?> dataType;
			
			if(Bindable.class.isAssignableFrom(dataSourceClass))
			{
				dataType = getDtoTypeFromClass(logger, dataSourceClass);			
			}
			else
			{
				dataType = recordType;
			}
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			 
			
			boolean isRemote = RemoteDataSource.class.isAssignableFrom(dataSourceClass);
			
			if (isRemote)
			{
				sourceWriter.println("public void updateData(int startRecord, int endRecord, "+dataTypeDeclaration+"[] data){");
			}
			else
			{
				sourceWriter.println("public void updateData("+dataTypeDeclaration+"[] data){");
			}
			
			if (recordType.isAssignableFrom(dataType))
			{
				if (isRemote)
				{
					sourceWriter.println("update(startRecord, endRecord, data);");
				}
				else
				{
					sourceWriter.println("update(data);");
				}
			}
			else
			{
				sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[(data!=null?data.length:0)];");
				sourceWriter.println("for (int i=0; i<data.length; i++){");
				sourceWriter.print("ret[i] = new "+recordTypeDeclaration+"(");
				
				if (EditableDataSourceRecord.class.isAssignableFrom(recordType) && 
					EditableDataSource.class.isAssignableFrom(dataSourceClass))
				{
					sourceWriter.print("this,");
				}
				sourceWriter.print(getIdentifierDeclaration(logger, dataType, columnsData.identifier, "data[i]"));
				sourceWriter.println(");");

				for (String name:  columnsData.names)
				{
					sourceWriter.println("ret[i].addValue("+
							getFieldValueGet(logger, dataType, dataType.getDeclaredField(name),	"data[i]", false)+
							");");
				}
				
				sourceWriter.println("}");
				if (isRemote)
				{
					sourceWriter.println("update(startRecord, endRecord, ret);");
				}
				else
				{
					sourceWriter.println("update(ret);");
				}
			}
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
	private void generateGetBindedObjectFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, ColumnsData columnsData)
	{
		try
		{
			
			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);			
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			 
			sourceWriter.println("public "+dataTypeDeclaration+" getBindedObject(){");
			sourceWriter.println(dataTypeDeclaration+" ret = new "+dataTypeDeclaration+"();");
			sourceWriter.println("DataSourceRecord record = getRecord();");

			for (int i=0; i < columnsData.names.length; i++)
			{
				String name = columnsData.names[i];
				Class<?> type = (columnsData.types.length > 0? columnsData.types[i]:String.class);
				Field field = dataType.getDeclaredField(name);
				generateFieldValueSet(logger, dataType, field, "ret", 
									"("+getParameterDeclaration(type)+")record.get("+i+")", 
									sourceWriter, false);
			}

			Field field = dataType.getDeclaredField(columnsData.identifier);
			Class<?> type = field.getType();
			generateFieldValueSet(logger, dataType, field, "ret", 
					"("+getParameterDeclaration(type)+")record.getIdentifier()", 
					sourceWriter, false);
			
			sourceWriter.println("return ret;");
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
			result.append("+"+getFieldValueGet(logger, dataType, dataType.getDeclaredField(identifier[i]), parentVariable, false));
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
	private ColumnsData generateDataSourceClassConstructor(TreeLogger logger, SourceWriter sourceWriter, Class<? extends DataSource<?>> dataSourceClass, String className)
	{
		ColumnsData ret = new ColumnsData();
		sourceWriter.println("public "+className+"(){");
		sourceWriter.println("this.metadata = new Metadata();");
		
		DataSourceColumns columnsAnnot = dataSourceClass.getAnnotation(DataSourceColumns.class);
		DataSourceBinding typeAnnot = dataSourceClass.getAnnotation(DataSourceBinding.class);
		
		boolean isBindable = Bindable.class.isAssignableFrom(dataSourceClass) && typeAnnot != null;
		if(columnsAnnot == null && !isBindable)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceNoMetaInformation(dataSourceClass.getName()), null);
		}
		else if(columnsAnnot != null && isBindable)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceConflictingMetaInformation(dataSourceClass.getName()), null);
		}
		else if(columnsAnnot != null)
		{
			ret.identifier = columnsAnnot.identifier();
			generateMetadataPopulationBlockFromColumns(logger, sourceWriter, columnsAnnot, dataSourceClass.getName(), ret);
		}
		else
		{
			ret.identifier = typeAnnot.identifier();
			generateMetadataPopulationBlockFromType(logger, sourceWriter, typeAnnot, getDtoTypeFromClass(logger, dataSourceClass), 
											dataSourceClass.getName(), ret);
		}
		generateAutoCreateFields(logger, dataSourceClass, sourceWriter, "this");
		sourceWriter.println("}");
		
		return ret;
	}

	/**
	 * 
	 * @param logger
	 * @param dataSourceClass
	 * @return
	 */
	private Class<?> getDtoTypeFromClass(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass)
	{
		try
		{
			Type superClass = dataSourceClass.getGenericSuperclass();
			while (superClass != null)
			{
				if (superClass instanceof ParameterizedType)
				{
					ParameterizedType parameterizedType = (ParameterizedType)superClass;
					Type rawType = parameterizedType.getRawType();
					if (LocalBindableEditableScrollableDataSource.class.equals(rawType) || 
						LocalBindableScrollableDataSource.class.equals(rawType) || 	
						LocalBindablePagedDataSource.class.equals(rawType) || 	
						LocalBindableEditablePagedDataSource.class.equals(rawType))
					{
						return (Class<?>)parameterizedType.getActualTypeArguments()[0];
					}
				}
				superClass = ((Class<?>)superClass).getGenericSuperclass();
			}
		}
		catch (Exception e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
		return null;
	}

	/**
	 * 
	 * @param logger
	 * @param dataSourceClass
	 * @return
	 */
	private Class<?> getRecordTypeFromClass(TreeLogger logger, Class<? extends DataSource<?>> dataSourceClass)
	{
		try
		{
			Method method = dataSourceClass.getMethod("getRecord", new Class[]{});
			return method.getReturnType();
		}
		catch (Exception e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getName(), e.getLocalizedMessage()), e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param typeAnnot
	 */
	@SuppressWarnings("unchecked")
	private void generateMetadataPopulationBlockFromType(TreeLogger logger, SourceWriter sourceWriter, 
							DataSourceBinding typeAnnot, Class<?> dtoType, String dataSourceClassName, ColumnsData columnsData)
	{
		List<String> names = new ArrayList<String>();
		List<Class<? extends Comparable<?>>> types = new ArrayList<Class<? extends Comparable<?>>>();
		
		String[] includeFields;
		String[] excludeFields;
		if (typeAnnot != null)
		{
			includeFields = typeAnnot.includeFields();
			excludeFields = typeAnnot.excludeFields();  
		}
		else
		{
			includeFields = new String[0];
			excludeFields = new String[0];
		}
		
		Field[] declaredFields = dtoType.getDeclaredFields();
		
		for (Field field : declaredFields)
		{
			if (mustInclude(field, includeFields, excludeFields, dtoType))
			{
				names.add(field.getName());
				types.add((Class<? extends Comparable<?>>) field.getType());
			}
		}
		columnsData.names = names.toArray(new String[0]);
		columnsData.types = (Class<? extends Comparable<?>>[]) types.toArray(new Class[0]);
		
		generateMetadaPopulationBlock(logger, sourceWriter, dataSourceClassName, columnsData);
	}

	/**
	 * 
	 * @param field
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	private boolean mustInclude(Field field, String[] includeFields, String[] excludeFields, Class<?> dtoType)
	{
		boolean mustInclude = Comparable.class.isAssignableFrom(field.getType());
		if (mustInclude)
		{
			mustInclude = isPropertyVisibleToRead(dtoType, field) && isPropertyVisibleToWrite(dtoType, field);
		}
		if (mustInclude)
		{
			mustInclude = chekWhiteList(field, includeFields, excludeFields);
		}
		if (mustInclude)
		{
			mustInclude = checkBlackList(field, excludeFields);
		}
		
		return mustInclude;
	}

	/**
	 * 
	 * @param field
	 * @param excludeFields
	 * @return
	 */
	private boolean checkBlackList(Field field, String[] excludeFields)
	{
		boolean mustInclude = true;
		if (excludeFields != null && excludeFields.length > 0)
		{
			boolean isInBlackList = false;
			for (String string : excludeFields)
			{
				if (field.getName().equals(string))
				{
					isInBlackList = true;
					break;
				}
			}
			mustInclude = !isInBlackList;
		}
		return mustInclude;
	}

	/**
	 * 
	 * @param field
	 * @param includeFields
	 * @param excludeFields
	 * @return
	 */
	private boolean chekWhiteList(Field field, String[] includeFields, String[] excludeFields)
	{
		boolean mustInclude = true;
		if (includeFields != null && includeFields.length > 0)
		{
			boolean isInWhiteList = false;
			for (String string : excludeFields)
			{
				if (field.getName().equals(string))
				{
					isInWhiteList = true;
					break;
				}
			}
			mustInclude = isInWhiteList;
		}
		return mustInclude;
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param columnsAnnot
	 * @param dataSourceClassName
	 */
	private void generateMetadataPopulationBlockFromColumns(TreeLogger logger, SourceWriter sourceWriter, 
							DataSourceColumns columnsAnnot, String dataSourceClassName, ColumnsData columnsData)
	{
		columnsData.names = columnsAnnot.names();
		columnsData.types = columnsAnnot.types();
		
		generateMetadaPopulationBlock(logger, sourceWriter, dataSourceClassName, columnsData);
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
		if (columnsData.types.length > 0 && (columnsData.names.length != columnsData.types.length))
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceInvalidMetaInformation(dataSourceClassName), null);
		}
		
		for (int i=0; i<columnsData.names.length; i++)
		{
			Class<? extends Comparable<?>> type;
			if (columnsData.types.length > 0)
			{
				type = columnsData.types[i];
			}
			else
			{
				type = String.class;
			}
			sourceWriter.println("metadata.addColumn(new ColumnMetadata<"+getParameterDeclaration(type)+">(\""+columnsData.names[i]+"\"));");
		}
	}	
	
	private static class ColumnsData
	{
		private String[] names;
		private Class<? extends Comparable<?>>[] types;
		private String identifier;
	}
}
