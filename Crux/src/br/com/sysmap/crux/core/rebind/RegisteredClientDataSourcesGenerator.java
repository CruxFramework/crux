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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.datasource.Bindable;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.Metadata;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceColumns;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceType;
import br.com.sysmap.crux.core.client.datasource.local.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.remote.RemoteDataSource;
import br.com.sysmap.crux.core.client.screen.ScreenBindableObject;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
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
		
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		Map<String, String> dataSourcesClassNames = new HashMap<String, String>();
		for (Screen screen : screens)
		{
			generateDataSourcesForScreen(logger, sourceWriter, screen, dataSourcesClassNames);
		}
		generateConstructor(logger, sourceWriter, implClassName, dataSourcesClassNames);
		sourceWriter.println("private java.util.Map<String,DataSource<?>> dataSources = new java.util.HashMap<String,DataSource<?>>();");

		sourceWriter.println("public DataSource<?> getDataSource(String id){");
		sourceWriter.println("return dataSources.get(id);");
		sourceWriter.println("}");

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
	private void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, String implClassName, 
			Map<String, String> dataSourcesClassNames) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		for (String dataSource : dataSourcesClassNames.keySet()) 
		{
			sourceWriter.println("dataSources.put(\""+dataSource+"\", new " + dataSourcesClassNames.get(dataSource) + "());");
		}
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
				
		generateDataSourceClassConstructor(logger, sourceWriter, dataSourceClass, className);	
		
		br.com.sysmap.crux.core.client.datasource.annotation.DataSource annot = 
				dataSourceClass.getAnnotation(br.com.sysmap.crux.core.client.datasource.annotation.DataSource.class);
		if (annot != null && annot.autoBind())
		{
			if (RemoteDataSource.class.isAssignableFrom(dataSourceClass))
			{
				generateFetchFunction(logger, screen, dataSourceClass, sourceWriter);
			}
			if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
			{
				generateLoadFunction(logger, screen, dataSourceClass, sourceWriter);
			}
		}
		generateScreenUpdateWidgetsFunction(logger, screen, dataSourceClass, sourceWriter);
		generateControllerUpdateObjectsFunction(logger, screen, dataSourceClass, sourceWriter);
		
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
			SourceWriter sourceWriter)
	{		
		try
		{
			Method method = dataSourceClass.getMethod("loadData", new Class[]{});
			Class<?> returnType = method.getReturnType();
			
			String returnDeclaration = getParameterDeclaration(returnType);
			sourceWriter.println("public "+returnDeclaration+" loadData(){");
			sourceWriter.println("updateControllerObjects();");
			sourceWriter.println(returnDeclaration+" ret = super.loadData();");
			sourceWriter.println("updateScreenWidgets();");
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
	 * @param screen
	 * @param dataSourceClass
	 * @param sourceWriter
	 */
	private void generateFetchFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter)
	{
		try
		{
			Method method = dataSourceClass.getMethod("fetchData", new Class[]{Integer.TYPE, Integer.TYPE});
			Class<?> returnType = method.getReturnType();
			
			String returnDeclaration = getParameterDeclaration(returnType);
			sourceWriter.println("public "+returnDeclaration+" fetchData(int startRecord, int endRecord){");
			sourceWriter.println("updateControllerObjects();");
			sourceWriter.println(returnDeclaration+" ret = super.fetchData(startRecord, endRecord);");
			sourceWriter.println("updateScreenWidgets();");
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
	 * @param sourceWriter
	 * @param dataSourceClass
	 * @param className
	 */
	private void generateDataSourceClassConstructor(TreeLogger logger, SourceWriter sourceWriter, Class<? extends DataSource<?>> dataSourceClass, String className)
	{
		sourceWriter.println("public "+className+"(){");
		sourceWriter.println("this.metadata = new Metadata();");
		
		DataSourceColumns columnsAnnot = dataSourceClass.getAnnotation(DataSourceColumns.class);
		DataSourceType typeAnnot = dataSourceClass.getAnnotation(DataSourceType.class);
		
		boolean isBindable = Bindable.class.isAssignableFrom(dataSourceClass);
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
			generateMetadataPopulationBlockFromColumns(logger, sourceWriter, columnsAnnot, dataSourceClass.getName());
		}
		else
		{
			generateMetadataPopulationBlockFromType(logger, sourceWriter, typeAnnot, getDtoTypeFromClass(logger, dataSourceClass), dataSourceClass.getName());
		}
		sourceWriter.println("}");
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
			if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
			{
				Method method = dataSourceClass.getMethod("loadData", new Class[]{});
				return method.getReturnType();
			}
			if (RemoteDataSource.class.isAssignableFrom(dataSourceClass))
			{
				Method method = dataSourceClass.getMethod("fetchData", new Class[]{Integer.TYPE, Integer.TYPE});
				return method.getReturnType();
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
	 * @param sourceWriter
	 * @param typeAnnot
	 */
	@SuppressWarnings("unchecked")
	private void generateMetadataPopulationBlockFromType(TreeLogger logger, SourceWriter sourceWriter, 
							DataSourceType typeAnnot, Class<?> dtoType, String dataSourceClassName)
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
		
		generateMetadaPopulationBlock(logger, sourceWriter, dataSourceClassName, names, types);
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
			//TODO: checar por escrita tbm para editabledatasources
			mustInclude = isPropertyVisibleToRead(dtoType, field);
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
							DataSourceColumns columnsAnnot, String dataSourceClassName)
	{
		List<String> names = Arrays.asList(columnsAnnot.names());
		List<Class<? extends Comparable<?>>> types = Arrays.asList(columnsAnnot.types());
		
		generateMetadaPopulationBlock(logger, sourceWriter, dataSourceClassName, names, types);
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
				List<String> names, List<Class<? extends Comparable<?>>> types)
	{
		if (types.size() > 0 && (names.size() != types.size()))
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceInvalidMetaInformation(dataSourceClassName), null);
		}
		
		for (int i=0; i<names.size(); i++)
		{
			Class<? extends Comparable<?>> type;
			if (types.size() > 0)
			{
				type = types.get(i);
			}
			else
			{
				type = String.class;
			}
			if (!Comparable.class.isAssignableFrom(type))
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceInvalidColumnType(dataSourceClassName, type.getName()), null);
			}
			sourceWriter.println("metadata.addColumn(new ColumnMetadata<"+getParameterDeclaration(type)+">(\""+names.get(i)+"\"));");
		}
	}	
}
