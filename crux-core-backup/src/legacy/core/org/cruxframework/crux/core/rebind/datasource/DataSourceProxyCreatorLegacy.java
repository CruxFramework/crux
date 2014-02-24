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
package org.cruxframework.crux.core.rebind.datasource;

import java.io.PrintWriter;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.datasource.ColumnDefinition;
import org.cruxframework.crux.core.client.datasource.ColumnDefinitions;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord;
import org.cruxframework.crux.core.client.datasource.LocalDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractInvocableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Legacy(value=DataSourceProxyCreator.class)
public class DataSourceProxyCreatorLegacy extends AbstractInvocableProxyCreator
{
	@Legacy
	@Deprecated
	private final boolean isAutoBindEnabled;
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public DataSourceProxyCreatorLegacy(TreeLogger logger, GeneratorContext context, JClassType dataSourceClass)
	{
		super(logger, context, null, dataSourceClass);
		this.dataSourceClass = dataSourceClass;
		this.dtoType = getDtoTypeFromClass();
		this.recordType = getRecordTypeFromClass();
		DataSource dsAnnot = dataSourceClass.getAnnotation(DataSource.class);
		this.isAutoBindEnabled = (dsAnnot == null || dsAnnot.autoBind());
		this.identifier = getDataSourceIdentifier();
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected void generateProxyContructor(SourcePrinter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public " + getProxySimpleName() + "("+View.class.getCanonicalName()+" view) {");
		srcWriter.println("this.__view = view;");
		generateAutoCreateFields(srcWriter, "this", isAutoBindEnabled);
		createColumnDefinitions(srcWriter);
		srcWriter.println("}");
	}	


	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		super.generateProxyMethods(srcWriter);
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
	        generateCopyValueToWidgetMethod(srcWriter);
	        generateBindToWidgetMethod(srcWriter);
	        generateSetValueMethod(srcWriter);
	        
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
	 * 
	 * @param sourceWriter
	 */
	private void generateLoadFunction(SourcePrinter sourceWriter)
	{		
		try
		{
			sourceWriter.println("public void load(){");
			if (isAutoBindEnabled)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.load();");
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Error for register client datasource. DataSource: ["+dataSourceClass.getName()+"].", e);
		}
	}	
	
	/**
	 * 
	 * @param sourceWriter
	 */
	private void generateFetchFunction(SourcePrinter sourceWriter)
	{
		try
		{
			sourceWriter.println("public void fetch(int startRecord, int endRecord){");
			if (isAutoBindEnabled)
			{
				sourceWriter.println("updateControllerObjects();");
			}
			sourceWriter.println("super.fetch(startRecord, endRecord);");
			sourceWriter.println("}");
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Error for register client datasource. DataSource: ["+dataSourceClass.getName()+"].", e);
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateTypeSerializers(SerializableTypeOracle, SerializableTypeOracle)
	 */
	@Override
	protected void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser, SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException
	{
	}
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@SuppressWarnings("deprecation")
    protected SourcePrinter getSourcePrinter()
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
		composerFactory.addImplementedInterface(org.cruxframework.crux.core.client.screen.ScreenBindableObject.class.getCanonicalName());
		composerFactory.addImplementedInterface(ViewAware.class.getCanonicalName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
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

		org.cruxframework.crux.core.client.datasource.annotation.DataSourceBinding typeAnnot = 
			dataSourceClass.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.DataSourceBinding.class);
		if (typeAnnot == null)
		{
			throw new CruxGeneratorException("Error Generating DataSource ["+dataSourceClass.getName()+"]. No identifier selected. Use the @DataSourceRecordIdentifier annotation to inform the identifier");
		}
		return typeAnnot.identifier();
    }
	
}
