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
package br.com.sysmap.crux.core.rebind.datasource;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.datasource.ColumnMetadata;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.Metadata;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.client.screen.ScreenBindableObject;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractInvocableProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataSourceProxyCreator extends AbstractInvocableProxyCreator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static final String DATASOURCE_PROXY_SUFFIX = "_DataSourceProxy";
	
	private final ColumnsData columnsData;
	private final JClassType dataSourceClass;
	private final JClassType dtoType;
	private final JClassType recordType;

	private final boolean isAutoBindEnabled;

	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public DataSourceProxyCreator(TreeLogger logger, GeneratorContext context, JClassType dataSourceClass)
	{
		super(logger, context, null, dataSourceClass);
		this.dataSourceClass = dataSourceClass;
		this.dtoType = getDtoTypeFromClass();
		this.recordType = getRecordTypeFromClass();
		DataSource dsAnnot = dataSourceClass.getAnnotation(DataSource.class);
		this.isAutoBindEnabled = (dsAnnot == null || dsAnnot.autoBind());
		this.columnsData = new ColumnsData();
		this.columnsData.identifier = getDataSourceIdentifier();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyContructor(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public " + getProxySimpleName() + "() {");
		srcWriter.indent();
		srcWriter.println("this.metadata = new Metadata();");
		generateMetadataPopulationBlock(srcWriter);
		generateAutoCreateFields(srcWriter, "this");
		srcWriter.outdent();
		srcWriter.println("}");
	}	
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		try
        {
	        JClassType remoteDsType = dataSourceClass.getOracle().getType(RemoteDataSource.class.getCanonicalName());
	        JClassType localDsType = dataSourceClass.getOracle().getType(LocalDataSource.class.getCanonicalName());
	        if (remoteDsType.isAssignableFrom(dataSourceClass))
	        {
	        	generateFetchFunction(srcWriter);
	        }
	        else if (localDsType.isAssignableFrom(dataSourceClass))
	        {
	        	generateLoadFunction(srcWriter);
	        }

	        generateUpdateFunction(srcWriter);
	        generateGetBoundObjectFunction(srcWriter);
	        generateGetValueFunction(srcWriter);

	        generateScreenUpdateWidgetsFunction(dataSourceClass, srcWriter);
	        generateControllerUpdateObjectsFunction(dataSourceClass, srcWriter);
	        generateIsAutoBindEnabledMethod(srcWriter, isAutoBindEnabled);
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	
	
	/**
	 * @param sourceWriter
	 */
	private void generateGetValueFunction(SourceWriter sourceWriter)
	{
		try
		{
			sourceWriter.println("public Object getValue(String columnName, "+recordType.getParameterizedQualifiedSourceName()+" dataSourceRecord){");
			sourceWriter.indent();

			sourceWriter.println(dtoType.getParameterizedQualifiedSourceName() + " recordObject = dataSourceRecord.getRecordObject();");
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
				generateGetValueForColumn(dtoType, columnName, sourceWriter);
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
	 * @param baseClass
	 * @param columnName
	 * @param sourceWriter
	 */
	private void generateGetValueForColumn(JClassType baseClass, String columnName, SourceWriter sourceWriter)
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
	    	try
	    	{
	    		JField field = ClassUtils.getDeclaredField(baseClass, firstProperty);
	    		if (!(field.getType() instanceof JClassType))
	    		{
	    			throw new CruxGeneratorException(messages.errorGeneratingRegisteredDataSourceInvalidColumn(baseClass.getName(), columnName));
	    		}
	    		generateGetValueForColumn((JClassType)field.getType(), columnName, sourceWriter);
	    	}
	    	catch (NoSuchFieldException e) 
	    	{
	    		throw new CruxGeneratorException(messages.errorGeneratingRegisteredDataSourceInvalidColumn(baseClass.getName(), columnName));
	    	}
	    	
	    	sourceWriter.outdent();
	    	sourceWriter.println("}");
	    }
    }
	
	/**
	 * 
	 * @param sourceWriter
	 */
	private void generateGetBoundObjectFunction(SourceWriter sourceWriter)
	{
		try
		{
			sourceWriter.println("public "+dtoType.getParameterizedQualifiedSourceName()+
					                   " getBoundObject("+recordType.getParameterizedQualifiedSourceName()+" record){");
			sourceWriter.indent();
			sourceWriter.println("if (record == null) return null;");
			sourceWriter.println("return record.getRecordObject();");
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
	 * @param sourceWriter
	 */
	private void generateUpdateFunction(SourceWriter sourceWriter)
	{
		try
		{
			String recordTypeDeclaration = recordType.getQualifiedSourceName();
			
			sourceWriter.println("public void updateData("+dtoType.getParameterizedQualifiedSourceName()+"[] data){");
			sourceWriter.indent();
			sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[(data!=null?data.length:0)];");
			sourceWriter.println("for (int i=0; i<data.length; i++){");
			sourceWriter.indent();
			sourceWriter.print("ret[i] = new "+recordType.getParameterizedQualifiedSourceName()+"(this,");
			sourceWriter.print(getIdentifierDeclaration("data[i]"));
			sourceWriter.println(");");
			sourceWriter.println("ret[i].setRecordObject(data[i]);");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("update(ret);");
			sourceWriter.outdent();
			sourceWriter.println("}");

			sourceWriter.println("public void updateData(java.util.List<"+dtoType.getParameterizedQualifiedSourceName()+"> data){");
			sourceWriter.indent();
			sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[(data!=null?data.size():0)];");
			sourceWriter.println("for (int i=0; i<data.size(); i++){");
			sourceWriter.indent();
			sourceWriter.print("ret[i] = new "+recordType.getParameterizedQualifiedSourceName()+"(this,");
			sourceWriter.print(getIdentifierDeclaration("data.get(i)"));
			sourceWriter.println(");");
			sourceWriter.println("ret[i].setRecordObject(data.get(i));");
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
	 * @param parentVariable
	 * @return
	 */
	private String getIdentifierDeclaration(String parentVariable) 
	{
		//TODO: tratar propriedades alinhadas... 
		String[] identifier = RegexpPatterns.REGEXP_COMMA.split(columnsData.identifier);
		StringBuilder result = new StringBuilder("\"\""); 

		for (int i = 0; i < identifier.length; i++)
		{
			JField field = ((JClassType)dtoType).findField(identifier[i]);
			if (field == null)
			{
				throw new CruxGeneratorException(messages.errorGeneratingRegisteredDataSourceCanNotFindIdentifier(dataSourceClass.getName(), identifier[i]));
			}
			result.append("+"+getFieldValueGet((JClassType)dtoType, field, parentVariable, false));
		}
		return result.toString();
	}	
	
	/**
	 * @param baseClass
	 * @param columnName
	 * @param sourceWriter
	 */
	private void generateGetValueForProperty(JClassType baseClass, String columnName, SourceWriter sourceWriter) 
    {
		try
        {
	        String baseTypeDeclaration = baseClass.getParameterizedQualifiedSourceName();
	        sourceWriter.print("ret = (("+baseTypeDeclaration+")ret).");
	        JField field = ClassUtils.getDeclaredField(baseClass, columnName);
	        if (field.isPublic())
	        {
	        	sourceWriter.print(columnName);
	        }
	        else
	        {
	        	sourceWriter.print(ClassUtils.getGetterMethod(columnName, baseClass)+"()");
	        }
	        sourceWriter.println(";");
        }
        catch (NoSuchFieldException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }
	
	/**
	 * 
	 * @param sourceWriter
	 */
	private void generateLoadFunction(SourceWriter sourceWriter)
	{		
		try
		{
			sourceWriter.println("public void load(){");
			sourceWriter.indent();
			if (isAutoBindEnabled)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.load();");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getQualifiedSourceName(), e.getLocalizedMessage()), e);
		}
	}	
	
	/**
	 * 
	 * @param sourceWriter
	 */
	private void generateFetchFunction(SourceWriter sourceWriter)
	{
		try
		{
			sourceWriter.println("public void fetch(int startRecord, int endRecord){");
			sourceWriter.indent();
			if (isAutoBindEnabled)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.fetch(startRecord, endRecord);");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSource(dataSourceClass.getQualifiedSourceName(), e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateTypeSerializers(SerializableTypeOracle, SerializableTypeOracle)
	 */
	@Override
	protected void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser, SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException
	{
	}
	
	/**
	 * @return
	 */
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		br.com.sysmap.crux.core.client.screen.Screen.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(),
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		ColumnMetadata.class.getCanonicalName(), 
    		Metadata.class.getCanonicalName() 
		};
	    return imports;
    }
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	protected String getProxyQualifiedName()
	{
		return dataSourceClass.getPackage().getName() + "." + getProxySimpleName();
	}

	/**
	 * @return the simple name of the proxy object.
	 */
	protected String getProxySimpleName()
	{
		return ClassUtils.getSourceName(dataSourceClass) + DATASOURCE_PROXY_SUFFIX;
	}
	
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected SourceWriter getSourceWriter()
	{
		JPackage pkg = dataSourceClass.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		composerFactory.setSuperclass(dataSourceClass.getParameterizedQualifiedSourceName());
		composerFactory.addImplementedInterface(ScreenBindableObject.class.getCanonicalName());

		return composerFactory.createSourceWriter(context, printWriter);
	}
	
	/**
	 * @param dtoType
	 * @param names
	 * @param types
	 */
	private void findDataSourceColumns(JClassType dtoType, List<String> names, List<JType> types, String parentField)
    {
	    JField[] declaredFields = ClassUtils.getDeclaredFields(dtoType);
		
		for (JField field : declaredFields)
		{
			if (isFullAccessibleField(field, dtoType))
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
				JType columnType = field.getType();
				types.add(columnType);
				if (columnType instanceof JClassType && isComplexCustomType((JClassType) columnType))
				{
					findDataSourceColumns((JClassType)columnType, names, types, columnName);
				}
			}
		}
    }

	/**
	 * @param sourceWriter
	 */
	private void generateMetadataPopulationBlock(SourceWriter sourceWriter)
	{
		List<String> names = new ArrayList<String>();
		List<JType> types = new ArrayList<JType>();
		
		findDataSourceColumns(dtoType, names, types, null);
		columnsData.names = names.toArray(new String[0]);
		columnsData.types = types.toArray(new JType[0]);
		
		if (columnsData.names.length != columnsData.types.length)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredDataSourceInvalidMetaInformation(dataSourceClass.getQualifiedSourceName()), null);
		}
		
		try
		{
			JClassType comparableType = dataSourceClass.getOracle().getType(Comparable.class.getCanonicalName());
			for (int i=0; i<columnsData.names.length; i++)
			{
				boolean sortable = (columnsData.types[i] instanceof JPrimitiveType || comparableType.isAssignableFrom((JClassType)columnsData.types[i]));
				sourceWriter.println("metadata.addColumn(new ColumnMetadata<"+getParameterDeclarationWithPrimitiveWrappers(columnsData.types[i])+">(\""+columnsData.names[i]+"\","+sortable+"));");
			}
		}
		catch (NotFoundException e)
		{
			throw new CruxGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
    private String getDataSourceIdentifier()
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
	private JClassType getDtoTypeFromClass()
	{
		return getTypeFromMethodClass("getBoundObject");
	}

	/**
	 * @param methodName
	 * @return
	 */
	private JClassType getTypeFromMethodClass(String methodName)
    {
		JType returnType = ClassUtils.getReturnTypeFromMethodClass(dataSourceClass, methodName, new JType[]{});
		if (returnType == null)
		{
			throw new CruxGeneratorException(messages.errorGeneratingRegisteredDataSourceInvalidBoundObject(dataSourceClass.getName()));
		}
		return (JClassType) returnType;
    }
	
	/**
	 * @return
	 */
	private JClassType getRecordTypeFromClass()
	{
		return getTypeFromMethodClass("getRecord");
	}
	
	/**
	 * @param parameterClass
	 * @return
	 */
	private String getParameterDeclarationWithPrimitiveWrappers(JType parameterClass)
	{
		if (parameterClass.isPrimitive() != null)
		{
			return parameterClass.isPrimitive().getQualifiedBoxedSourceName();
		}
		
		return parameterClass.getParameterizedQualifiedSourceName();
	}
	
	/**
	 * @param type
	 * @return
	 */
	private boolean isComplexCustomType(JClassType type)
    {
		try
        {
	        JClassType charSequenceType = type.getOracle().getType(CharSequence.class.getCanonicalName());
	        JClassType dateType = type.getOracle().getType(Date.class.getCanonicalName());
	        JClassType collectionType = type.getOracle().getType(Collection.class.getCanonicalName());
	        JClassType mapType = type.getOracle().getType(Map.class.getCanonicalName());
	        JClassType numberType = type.getOracle().getType(Number.class.getCanonicalName());
	        JClassType booleanType = type.getOracle().getType(Boolean.class.getCanonicalName());
	        JClassType characterType = type.getOracle().getType(Character.class.getCanonicalName());
	        return (type.isPrimitive() == null
	        		&& type.isAnnotation() == null
	        		&& type.isArray() == null 
	        		&& type.isEnum() == null
	        		&& !charSequenceType.isAssignableFrom(type)
	        		&& !numberType.isAssignableFrom(type)
	        		&& !booleanType.isAssignableFrom(type)
	        		&& !characterType.isAssignableFrom(type)
	        		&& !dateType.isAssignableFrom(type) && !collectionType.isAssignableFrom(type) && !mapType.isAssignableFrom(type));
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class ColumnsData
	{
		private String identifier;
		private String[] names;
		private JType[] types;
	}	
}
