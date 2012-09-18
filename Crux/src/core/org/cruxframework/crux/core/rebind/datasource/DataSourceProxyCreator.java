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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.datasource.ColumnDefinition;
import org.cruxframework.crux.core.client.datasource.ColumnDefinitions;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord;
import org.cruxframework.crux.core.client.datasource.LocalDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractInvocableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
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
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataSourceProxyCreator extends AbstractInvocableProxyCreator
{
	private static final String DATASOURCE_PROXY_SUFFIX = "_DataSourceProxy";

	private final JClassType dataSourceClass;
	private final JClassType dtoType;
	private final JClassType recordType;

	private final boolean isAutoBindEnabled;
	private String identifier;

	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public DataSourceProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType dataSourceClass)
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
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected void generateProxyContructor(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public " + getProxySimpleName() + "() {");
		srcWriter.indent();
		generateAutoCreateFields(srcWriter, "this", isAutoBindEnabled);
		IocContainerRebind.injectProxyFields(srcWriter, dataSourceClass);
		createColumnDefinitions(srcWriter);
		srcWriter.outdent();
		srcWriter.println("}");
	}	

	protected void createColumnDefinitions(SourceWriter out)
	{
		org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions columnDefinitionsAnot = 
			dataSourceClass.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions.class);

		if (columnDefinitionsAnot != null)
		{
			String colDefs = "colDefs";
			String dtoClassName = dtoType.getParameterizedQualifiedSourceName();
			String columnDefinitionsClassName = org.cruxframework.crux.core.client.datasource.ColumnDefinitions.class.getCanonicalName()+"<"+dtoClassName+">";

			out.println(columnDefinitionsClassName+" "+colDefs+" = new "+columnDefinitionsClassName+"();");
			out.println("setColumnDefinitions("+colDefs+");");

			autoCreateDataSourceColumnDefinitions(out, colDefs, columnDefinitionsAnot);
		}
	}

	protected void autoCreateDataSourceColumnDefinitions(SourceWriter out, String colDefs,
			org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions columnDefinitions)
    {
		String dtoClassName = dtoType.getParameterizedQualifiedSourceName();

    	for (org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinition columnDefinition : columnDefinitions.value())
        {
    		StringBuilder getValueExpression = new StringBuilder();
    		String colKey = columnDefinition.value();

    		JType propType;
    		try
    		{
    			propType = JClassUtils.buildGetValueExpression(getValueExpression, dtoType, colKey, "recordObject", true);
    		}
    		catch (Exception e)
    		{
    			throw new CruxGeneratorException("Datasource ["+dataSourceClass.getQualifiedSourceName()+"] has an invalid ColumnDefinition ["+colKey+"].");
    		}

    		JClassType comparableType = context.getTypeOracle().findType(Comparable.class.getCanonicalName());

    		boolean isSortable = (propType.isPrimitive() != null) || (comparableType.isAssignableFrom((JClassType) propType));
    		String propTypeName = JClassUtils.getGenericDeclForType(propType);
    		out.println(colDefs+".addColumn(new "+org.cruxframework.crux.core.client.datasource.ColumnDefinition.class.getCanonicalName()+
    				"<"+propTypeName+","+dtoClassName+">("+EscapeUtils.quote(colKey)+","+isSortable+"){");
    		out.println("public "+propTypeName+" getValue("+dtoClassName+" recordObject){");
    		out.println("return "+getValueExpression.toString());
    		out.println("}");
    		out.println("});");
		}
    }

	@Override
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
	{
	    super.generateSubTypes(srcWriter);
	    new IocContainerRebind(logger, context).create();
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
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
			throw new CruxGeneratorException("Error for register client datasource. DataSource: ["+dataSourceClass.getName()+"].", e);
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
			sourceWriter.println("if (data == null){");
			sourceWriter.println("update(new "+recordTypeDeclaration+"[0]);");
			sourceWriter.println("} else {");
			sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[data.length];");
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
			sourceWriter.println("}");

			sourceWriter.println("public void updateData(java.util.List<"+dtoType.getParameterizedQualifiedSourceName()+"> data){");
			sourceWriter.indent();
			sourceWriter.println("if (data == null){");
			sourceWriter.println("update(new "+recordTypeDeclaration+"[0]);");
			sourceWriter.println("} else {");
			sourceWriter.println(recordTypeDeclaration+"[] ret = new "+recordTypeDeclaration+"[data.size()];");
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
			sourceWriter.println("}");
		
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Error for register client datasource. DataSource: ["+dataSourceClass.getName()+"].", e);
		}
	}
	
	/**
	 * 
	 * @param parentVariable
	 * @return
	 */
	private String getIdentifierDeclaration(String parentVariable) 
	{
		String[] identifier = RegexpPatterns.REGEXP_COMMA.split(this.identifier);
		StringBuilder result = new StringBuilder("\"\""); 

		for (int i = 0; i < identifier.length; i++)
		{
			String[] fields = RegexpPatterns.REGEXP_DOT.split(identifier[i]);
			if (fields != null)
			{
				StringBuilder fieldExpression = new StringBuilder();
				boolean first = true;
				JType fieldType = dtoType;
				for (String fieldName : fields)
                {
					JField field = ((JClassType)fieldType).findField(fieldName.trim());
					if (field == null)
					{
						throw new CruxGeneratorException("Error Generating DataSource ["+dataSourceClass.getName()+"]. Can not retrieve identifier field ["+identifier[i]+"].");
					}
					if (first)
					{
						fieldExpression.append(parentVariable);
					}
					first = false;
					fieldExpression.append(JClassUtils.getFieldValueGet((JClassType)fieldType, field, "", false));
					fieldType = field.getType();
                }
				result.append("+"+fieldExpression.toString());
			}
		}
		return result.toString();
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
			throw new CruxGeneratorException("Error for register client datasource. DataSource: ["+dataSourceClass.getName()+"].", e);
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
	 * @return
	 */
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(),
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		ColumnDefinition.class.getCanonicalName(), 
    		ColumnDefinitions.class.getCanonicalName(),
    		DataSourceRecord.class.getCanonicalName()
		};
	    return imports;
    }
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	public String getProxyQualifiedName()
	{
		return dataSourceClass.getPackage().getName() + "." + getProxySimpleName();
	}

	/**
	 * @return the simple name of the proxy object.
	 */
	public String getProxySimpleName()
	{
		return dataSourceClass.getSimpleSourceName() + DATASOURCE_PROXY_SUFFIX;
	}
	
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@SuppressWarnings("deprecation")
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
		composerFactory.addImplementedInterface(org.cruxframework.crux.core.client.screen.ScreenBindableObject.class.getCanonicalName());

		return composerFactory.createSourceWriter(context, printWriter);
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
		JType returnType = JClassUtils.getReturnTypeFromMethodClass(dataSourceClass, methodName, new JType[]{});
		JClassType returnClassType = returnType.isClassOrInterface();
		
		if (returnClassType == null)
		{
			throw new CruxGeneratorException("Error Generating DataSource ["+dataSourceClass.getName()+"]. Invalid Bound object. Primitive is not allowed");
		}
		return returnClassType;
    }
	
	/**
	 * @return
	 */
	private JClassType getRecordTypeFromClass()
	{
		return getTypeFromMethodClass("getRecord");
	}
	
	
	/**
	 * Generates the copyValueToWidget method
	 * @param srcWriter
	 */
	protected void generateCopyValueToWidgetMethod(SourceWriter srcWriter)
	{
		srcWriter.println("public void copyValueToWidget(HasValue<?> valueContainer, String columnKey, DataSourceRecord<?> dataSourceRecord) {");

		String elseStm = "";
		JField[] fields = JClassUtils.getDeclaredFields(dtoType);
		for (int i = 0; i < fields.length; i++) 
		{
			JField field = fields[i];
			String name = field.getName();
			JType type = field.getType();
			String typeName = type.getQualifiedSourceName();
			
			if (type.isPrimitive() != null)
			{
				JPrimitiveType jPrimitiveType = type.isPrimitive();
				typeName = jPrimitiveType.getQualifiedBoxedSourceName();
			}
			
			srcWriter.println();
			
			srcWriter.indent();
			if(JClassUtils.getGetterMethod(name, dtoType) != null)
			{
				srcWriter.println(elseStm + "if(" + EscapeUtils.quote(name) + ".equals(columnKey)){");
				srcWriter.indent();
				srcWriter.println("((HasValue<" + typeName + ">)valueContainer).setValue((" + typeName + ") getValue(columnKey, dataSourceRecord));");
				srcWriter.outdent();
				srcWriter.print("}");
				srcWriter.println();
				elseStm = "else ";
			}
			
			srcWriter.outdent();
		}
		
		srcWriter.indent();
		srcWriter.println();
		srcWriter.println("bindToWidget(valueContainer, columnKey, dataSourceRecord);");
		srcWriter.outdent();
		
		srcWriter.println("}");
		
	}
	
	/**
	 * Generates the copyValueToWidget method
	 * @param srcWriter
	 */
	protected void generateBindToWidgetMethod(SourceWriter srcWriter)
	{
		srcWriter.println("private void bindToWidget(Object widget, final String columnKey, final DataSourceRecord<?> dataSourceRecord) {");
		JField[] fields = JClassUtils.getDeclaredFields(dtoType);
		srcWriter.indent();
		
		String elseStm = "";
		for (int i = 0; i < fields.length; i++) 
		{
			JField field = fields[i];
			String name = field.getName();
			JType type = field.getType();
			String typeName = type.getQualifiedSourceName();
			
			if (type.isPrimitive() != null)
			{
				JPrimitiveType jPrimitiveType = type.isPrimitive();
				typeName = jPrimitiveType.getQualifiedBoxedSourceName();
			}
			
			srcWriter.println();
			
			srcWriter.println(elseStm + "if(" + EscapeUtils.quote(name) + ".equals(columnKey)){");
			srcWriter.indent();
			
			srcWriter.println("((" + HasValueChangeHandlers.class.getCanonicalName() + ") widget).addValueChangeHandler(");
			srcWriter.indent();
			srcWriter.println("new " + ValueChangeHandler.class.getCanonicalName()  + "<" + typeName + ">(){");
			srcWriter.indent();
			srcWriter.println("public void onValueChange(" + ValueChangeEvent.class.getCanonicalName() + "<" + typeName + "> event){");
			srcWriter.indent();
			srcWriter.println(getProxySimpleName() + ".this.setValue(event.getValue(), columnKey, dataSourceRecord);");
			srcWriter.outdent();
			srcWriter.println("}");
			srcWriter.outdent();
			srcWriter.println("});");
			srcWriter.outdent();
			srcWriter.println("}");
			srcWriter.outdent();
			elseStm = "else ";
		}
		
		srcWriter.outdent();
		srcWriter.println("}");
	}	
	
	/**
	 * Generates the setValue method
	 * @param srcWriter
	 */
	protected void generateSetValueMethod(SourceWriter srcWriter)
	{
		srcWriter.println("public void setValue(Object value, String columnKey, DataSourceRecord<?> dataSourceRecord) {");

		JField[] fields = JClassUtils.getDeclaredFields(dtoType);
		String dtoTypeName = dtoType.getParameterizedQualifiedSourceName();
		
		for (int i = 0; i < fields.length; i++) 
		{
			JField field = fields[i];
			String name = field.getName();
			JType fieldType = field.getType();
			String fieldTypeName = fieldType.getParameterizedQualifiedSourceName();
			String setterName = null;
			String getterName = null;

			if (fieldType.isPrimitive() != null)
			{
				JPrimitiveType jPrimitiveType = fieldType.isPrimitive();
				fieldTypeName = jPrimitiveType.getQualifiedBoxedSourceName();
			}
			
			try 
			{
				setterName = dtoType.getMethod(ClassUtils.getSetterMethod(name), new JType[]{fieldType}).getName();
				getterName = dtoType.getMethod(ClassUtils.getGetterMethod(name), new JType[]{}).getName();
			} 
			catch (NotFoundException e) 
			{
				// do nothing
			}
			
			boolean isPublic = field.isPublic() && !field.isStatic();
			boolean hasGetterAndSetter = setterName != null && getterName != null;
			boolean isAccessible = isPublic || hasGetterAndSetter;
			
			if(isAccessible)
			{
				srcWriter.println();
				srcWriter.indent();
				srcWriter.println("if(" + EscapeUtils.quote(name) + ".equals(columnKey)){");
				srcWriter.indent();

				if(isPublic)
				{
					srcWriter.println(fieldTypeName + " field = ((" + dtoTypeName + ") dataSourceRecord.getRecordObject())." + name + ";");
				}
				else
				{
					srcWriter.println(fieldTypeName + " field = ((" + dtoTypeName + ") dataSourceRecord.getRecordObject())." + getterName + "();");
				}
				
				srcWriter.println("boolean changed = (value == null && field != null) || (value != null && field == null) || !field.equals(value);");
				srcWriter.println("if(changed){");
				srcWriter.indent();
				
				if(isPublic)
				{
					srcWriter.println("((" + dtoTypeName + ") dataSourceRecord.getRecordObject())." + name + " = (" + fieldTypeName + ") value;");
				}
				else
				{
					srcWriter.println("((" + dtoTypeName + ") dataSourceRecord.getRecordObject())." + setterName + "((" + fieldTypeName + ") value);");
				}
				
				srcWriter.println("dataSourceRecord.setDirty();");
				srcWriter.println("return;");
				srcWriter.outdent();
				srcWriter.print("}");
				srcWriter.outdent();
				srcWriter.print("}");
				srcWriter.outdent();
			}
		}
		
		srcWriter.println("}");
	}	
}
