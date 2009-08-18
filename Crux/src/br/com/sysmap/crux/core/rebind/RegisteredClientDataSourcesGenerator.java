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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.datasource.Bindable;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSource;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.Metadata;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceColumns;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.core.client.datasource.local.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.remote.RemoteDataSource;
import br.com.sysmap.crux.core.client.screen.ScreenBindableObject;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

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
			generateFetchDataFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
			generateFetchFunction(logger, screen, dataSourceClass, sourceWriter, columnsData);
		}
		if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
		{
			generateLoadDataFunction(logger, screen, dataSourceClass, sourceWriter, autoBind);
			generateLoadFunction(logger, screen, dataSourceClass, sourceWriter, columnsData);
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
	private void generateLoadDataFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, boolean autoBind)
	{		
		try
		{
			Method method = dataSourceClass.getMethod("loadData", new Class[]{});
			Class<?> returnType = method.getReturnType();
			
			String returnDeclaration = getParameterDeclaration(returnType);
			sourceWriter.println("public "+returnDeclaration+" loadData(){");
			if (autoBind)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println(returnDeclaration+" ret = super.loadData();");
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
	private void generateLoadFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, ColumnsData columnsData)
	{
		try
		{
			Method method = dataSourceClass.getMethod("load", new Class[]{});
			Class<?> returnType = method.getReturnType();
			Class<?> returnTypeComponent = returnType.getComponentType();
			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);
			
			String returnDeclaration = getParameterDeclaration(returnType);
			String returnTypeComponentDeclaration = getParameterDeclaration(returnTypeComponent);
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			
			generateGenericLoadDataFunction(logger, sourceWriter, columnsData, returnTypeComponent, dataType, 
											dataSourceClass, returnDeclaration, returnTypeComponentDeclaration, 
											dataTypeDeclaration, "load()", "loadData()");
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
	 * @param columnsData
	 * @param returnType
	 * @param dataType
	 * @param returnDeclaration
	 * @param returnTypeComponentDeclaration
	 * @param dataTypeDeclaration
	 * @throws NoSuchFieldException
	 */
	private void generateGenericLoadDataFunction(TreeLogger logger, SourceWriter sourceWriter, ColumnsData columnsData, 
												 Class<?> returnTypeComponent, Class<?> dataType, Class<?> dataSourceClass, 
												 String returnDeclaration, String returnTypeComponentDeclaration, 
												 String dataTypeDeclaration, String loadFunctionDeclaration, 
												 String loadDataFunctionCall) throws NoSuchFieldException
	{
		sourceWriter.println("public "+returnDeclaration+" "+loadFunctionDeclaration+"{");
		if (returnTypeComponent.isAssignableFrom(dataType))
		{
			sourceWriter.println("return "+loadDataFunctionCall+";");
		}
		else
		{
			sourceWriter.println(dataTypeDeclaration+"[] data = "+loadDataFunctionCall+";");
			sourceWriter.println(returnDeclaration+" ret = new "+returnTypeComponentDeclaration+"[(data!=null?data.length:0)];");
			sourceWriter.println("for (int i=0; i<data.length; i++){");
			sourceWriter.print("ret[i] = new "+returnTypeComponentDeclaration+"(");
			
			if (EditableDataSourceRecord.class.isAssignableFrom(returnTypeComponent) && 
				EditableDataSource.class.isAssignableFrom(dataSourceClass))
			{
				sourceWriter.print("this,");
			}
			sourceWriter.print(getIdentifierDelcaration(logger, dataType, columnsData.identifier, "data[i]"));
			sourceWriter.println(");");

			for (String name:  columnsData.names)
			{
				sourceWriter.println("ret[i].addValue("+
						getFieldValueGet(logger, dataType, dataType.getDeclaredField(name),	"data[i]", false)+
						");");
			}
			
			sourceWriter.println("}");
			sourceWriter.println("return ret;");
		}
		sourceWriter.println("}");
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
	private String getIdentifierDelcaration(TreeLogger logger, Class<?> dataType, String identifiers, String parentVariable) throws NoSuchFieldException
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
	private void generateFetchDataFunction(TreeLogger logger, Screen screen, Class<? extends DataSource<?>> dataSourceClass, 
			SourceWriter sourceWriter, boolean autoBind)
	{
		try
		{
			Method method = dataSourceClass.getMethod("fetchData", new Class[]{Integer.TYPE, Integer.TYPE});
			Class<?> returnType = method.getReturnType();
			
			String returnDeclaration = getParameterDeclaration(returnType);
			sourceWriter.println("public "+returnDeclaration+" fetchData(int startRecord, int endRecord){");
			if (autoBind)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println(returnDeclaration+" ret = super.fetchData(startRecord, endRecord);");
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
			SourceWriter sourceWriter, ColumnsData columnsData)
	{
		try
		{
			Method method = dataSourceClass.getMethod("fetch", new Class[]{Integer.TYPE, Integer.TYPE});
			Class<?> returnType = method.getReturnType();
			Class<?> returnTypeComponent = returnType.getComponentType();
			Class<?> dataType = getDtoTypeFromClass(logger, dataSourceClass);
			
			String returnDeclaration = getParameterDeclaration(returnType);
			String returnTypeComponentDeclaration = getParameterDeclaration(returnTypeComponent);
			String dataTypeDeclaration = getParameterDeclaration(dataType);
			
			generateGenericLoadDataFunction(logger, sourceWriter, columnsData, returnTypeComponent, dataType, 
											dataSourceClass, returnDeclaration, returnTypeComponentDeclaration, 
											dataTypeDeclaration, "fetch(int start, int end)", "fetchData(start, end)");
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
			if (LocalDataSource.class.isAssignableFrom(dataSourceClass))
			{
				Method method = dataSourceClass.getMethod("loadData", new Class[]{});
				return method.getReturnType().getComponentType();
			}
			if (RemoteDataSource.class.isAssignableFrom(dataSourceClass))
			{
				Method method = dataSourceClass.getMethod("fetchData", new Class[]{Integer.TYPE, Integer.TYPE});
				return method.getReturnType().getComponentType();
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
