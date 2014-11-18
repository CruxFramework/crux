/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.database.idb;

import java.io.PrintWriter;
import java.sql.Date;

import org.cruxframework.crux.core.client.db.IDXCursor;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursor;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBKeyCursorProxyCreator extends IDBAbstractKeyValueProxyCreator
{
	private JClassType cursorType;
	private String idbCursorVariable;
	private String[] objectStoreKeyPath;
	private String indexName;

	public IDBKeyCursorProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, 
								String objectStoreName, String[] keyPath, String[] objectStoreKeyPath, 
								String indexName)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.objectStoreKeyPath = objectStoreKeyPath;
		this.indexName = indexName;
		this.cursorType = context.getTypeOracle().findType(IDXCursor.class.getCanonicalName());
		this.idbCursorVariable = "idbCursor";
	}

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(IDBCursorWithValue idbCursor){");
		srcWriter.println("super(idbCursor);");
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetKeyMethod(srcWriter);
		generateGetValueMethod(srcWriter);
		generateUpdateMethod(srcWriter);
		generateContinueCursorMethod(srcWriter);
		generateGetNativeArrayKeyMethod(srcWriter, idbCursorVariable);
		if (hasCompositeKey())
		{
			generateFromNativeKeyMethod(srcWriter);
			generateGetNativeKeyMethod(srcWriter);
			generateFromNativeValueMethod(srcWriter, objectStoreKeyPath);
		}
	}
	
	protected void generateGetKeyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public "+keyTypeName+" getKey(){");
		if (hasCompositeKey())
		{
			srcWriter.println("return fromNativeKey("+idbCursorVariable+".getObjectKey());");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println("return "+idbCursorVariable+".getStringKey();");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println("return "+idbCursorVariable+".getIntKey();");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println("return "+idbCursorVariable+".getDoubleKey();");
		}
		else if ((keyTypeName.equals(Date.class.getCanonicalName())) || 
				 (keyTypeName.equals(java.util.Date.class.getCanonicalName())))
		{
			srcWriter.println("return "+idbCursorVariable+".getDateKey();");
		}
		else
		{
			srcWriter.println("return "+idbCursorVariable+".getObjectKey().cast();");
		}
		srcWriter.println("}");
    }

	protected void generateGetValueMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName(objectStoreKeyPath);
		srcWriter.println("public "+keyTypeName+" getValue(){");
		if (isEmptyType())
		{
			srcWriter.println("return "+idbCursorVariable+".getValue().cast();");
		}
		else if (objectStoreKeyPath.length > 1)
		{
			srcWriter.println("return fromNativeValue("+idbCursorVariable+".getObjectPrimaryKey());");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println("return "+idbCursorVariable+".getStringPrimaryKey();");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println("return "+idbCursorVariable+".getIntPrimaryKey();");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println("return "+idbCursorVariable+".getDoublePrimaryKey();");
		}
		else if (keyTypeName.equals(Date.class.getCanonicalName()))
		{
			srcWriter.println("return "+idbCursorVariable+".getDatePrimaryKey();");
		}
		else
		{
			srcWriter.println("return "+idbCursorVariable+".getObjectPrimaryKey().cast();");
		}
		srcWriter.println("}");
    }

	protected void generateUpdateMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void update("+getKeyTypeName(objectStoreKeyPath)+" value){");
		srcWriter.println("}");
    }
	
	protected void generateContinueCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void continueCursor("+getKeyTypeName()+" key){");
		if (hasCompositeKey())
		{
			srcWriter.println(idbCursorVariable+".continueCursor(getNativeKey(key));");
		}
		else
		{
			srcWriter.println(idbCursorVariable+".continueCursor(key);");
		}
		srcWriter.println("}");
    }
	
	@Override
	public String getProxyQualifiedName()
	{
		return cursorType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		return indexName.replaceAll("\\W", "_")+"_KeyCursor";
	}

	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = cursorType.getPackage().getName();
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
		composerFactory.setSuperclass("IDXCursor<"+getKeyTypeName()+","+getKeyTypeName(objectStoreKeyPath)+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				IDXCursor.class.getCanonicalName(), 
				IDBCursorWithValue.class.getCanonicalName(), 
				JSONObject.class.getCanonicalName(),
				JsArrayMixed.class.getCanonicalName(),
				IDBCursor.class.getCanonicalName()
		};
		return imports;
	}
}
