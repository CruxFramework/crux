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
package org.cruxframework.crux.core.rebind.database.sql;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.WSQLAbstractDatabase;
import org.cruxframework.crux.core.client.db.WSQLCursor;
import org.cruxframework.crux.core.client.db.WSQLKeyRange;
import org.cruxframework.crux.core.client.db.WSQLTransaction;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLCursorProxyCreator extends SQLAbstractKeyValueProxyCreator
{
	protected JClassType cursorType;
	protected final String cursorName;
	protected final String[] objectStoreKeyPath;
	protected final Set<String> objectStoreIndexColumns;
	protected final boolean autoIncrement;

	public SQLCursorProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName,
								boolean autoIncrement, Set<String> objectStoreIndexColumns, String[] keyPath, String[] objectStoreKeyPath, 
								String cursorName)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.autoIncrement = autoIncrement;
		this.objectStoreIndexColumns = objectStoreIndexColumns;
		this.objectStoreKeyPath = objectStoreKeyPath;
		this.cursorName = cursorName;
		this.cursorType = context.getTypeOracle().findType(WSQLCursor.class.getCanonicalName());
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(WSQLAbstractDatabase db, WSQLKeyRange<"+getKeyTypeName()+"> range, CursorDirection direction, WSQLTransaction transaction){");
		srcWriter.println("super(db, range, "+EscapeUtils.quote(objectStoreName)+", "+autoIncrement+", direction, transaction);");
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetNativeKeyMethod(srcWriter);
		generateGetKeyMethod(srcWriter);
		generateGetPrimaryKeyMethod(srcWriter);
		if (hasCompositeKey())
		{
			generateFromNativeValueMethod(srcWriter, keyPath);
		}
		generateGetIndexedColumnNamesMethod(srcWriter, objectStoreIndexColumns);
		generateGetKeyPathMethod(srcWriter);
		generateAddKeyRangeToQueryMethod(srcWriter);
		generateAddKeyToQueryMethod(srcWriter);
		generateAddPrimaryKeyToQueryMethod(srcWriter);
		generateDecodeObjectMethod(srcWriter);
		generateEncodeObjectMethod(srcWriter);
		generateSetObjectKeyMethod(srcWriter);
	}
	
	protected void generateSetObjectKeyMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected void setObjectKey("+getTargetObjectClassName()+" object, "+getKeyTypeName(objectStoreKeyPath)+" key){");

		if (objectStoreKeyPath.length > 1)
		{
			for (int i = 0; i < objectStoreKeyPath.length; i++)
			{
				String k = objectStoreKeyPath[i];
				JType jType = JClassUtils.getTypeForProperty(k, targetObjectType);
				String setterMethod = JClassUtils.getSetterMethod(k, targetObjectType, jType);
				srcWriter.println("object."+setterMethod+"((key==null?null:("+jType.getParameterizedQualifiedSourceName()+")key["+i+"]));");
			}
		}
		else
		{
			String k = objectStoreKeyPath[0];
			JType jType = JClassUtils.getTypeForProperty(k, targetObjectType);
			String setterMethod = JClassUtils.getSetterMethod(k, targetObjectType, jType);
			srcWriter.println("object."+setterMethod+"(key);");
		}
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateAddPrimaryKeyToQueryMethod(SourcePrinter srcWriter)
	{
	    generateGetNativeKeyMethod(srcWriter, objectStoreKeyPath, "getNativePrimaryKey");
		srcWriter.println("protected void addPrimaryKeyToQuery(final "+getKeyTypeName(objectStoreKeyPath)+" key, "+
				StringBuilder.class.getCanonicalName()+" sql, "+JsArrayMixed.class.getCanonicalName()+" args){");
		
		boolean first = true;
		for(String path: objectStoreKeyPath)
		{
			if (!first)
			{
				srcWriter.print("sql.append(\" AND \");");
			}
			srcWriter.println("sql.append(\""+path+" = ?\");");
			first = false;
		}
		srcWriter.println("getNativePrimaryKey(key, args);");
		
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateGetKeyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public "+keyTypeName+" getKey(){");
		srcWriter.println("if (offset >= length) return null;");
		srcWriter.println(JavaScriptObject.class.getCanonicalName()+" object = resultSet.getRows().itemObject(offset);");
		if (hasCompositeKey())
		{
			srcWriter.println(JsArrayMixed.class.getCanonicalName() + " k = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast(); ");
			for(String key: keyPath)
			{
				srcWriter.println(JsUtils.class.getCanonicalName()+".readPropertyValue(object, "+EscapeUtils.quote(key)+", k, true);");
			}
			srcWriter.println("return fromNativeValue(k);");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println("return "+JsUtils.class.getCanonicalName()+".readStringPropertyValue(object, "+EscapeUtils.quote(keyPath[0])+");");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println("return ("+JsUtils.class.getCanonicalName()+".hasPropertyValue(object, "+EscapeUtils.quote(keyPath[0])+")?"+JsUtils.class.getCanonicalName()+".readIntPropertyValue(object, "+EscapeUtils.quote(keyPath[0])+"):null);");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println("return "+JsUtils.class.getCanonicalName()+".readDoublePropertyValue(object, "+EscapeUtils.quote(keyPath[0])+");");
		}
		else if (keyTypeName.equals(Date.class.getCanonicalName()))
		{
			srcWriter.println("return new "+Date.class.getCanonicalName()+"((long)"+JsUtils.class.getCanonicalName()+".readDoublePropertyValue(object, "+EscapeUtils.quote(keyPath[0])+"));");
		}
		else
		{
			srcWriter.println("return object.cast();");
		}
		srcWriter.println("}");
    }

	protected void generateGetPrimaryKeyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName(objectStoreKeyPath);
		srcWriter.println("public "+keyTypeName+" getPrimaryKey(){");
		srcWriter.println("if (offset >= length) return null;");
		srcWriter.println(JavaScriptObject.class.getCanonicalName()+" object = resultSet.getRows().itemObject(offset);");
		if (objectStoreKeyPath.length > 1 && !isEmptyType())
		{
			srcWriter.println(JsArrayMixed.class.getCanonicalName() + " k = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast(); ");
			for(String key: objectStoreKeyPath)
			{
				srcWriter.println(JsUtils.class.getCanonicalName()+".readPropertyValue(object, "+EscapeUtils.quote(key)+", k, true);");
			}
			srcWriter.println("return fromNativeKey(k);");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println("return "+JsUtils.class.getCanonicalName()+".readStringPropertyValue(object, "+EscapeUtils.quote(objectStoreKeyPath[0])+");");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println("return ("+JsUtils.class.getCanonicalName()+".hasPropertyValue(object, "+EscapeUtils.quote(objectStoreKeyPath[0])+")?"+JsUtils.class.getCanonicalName()+".readIntPropertyValue(object, "+EscapeUtils.quote(objectStoreKeyPath[0])+"):null);");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println("return "+JsUtils.class.getCanonicalName()+".readDoublePropertyValue(object, "+EscapeUtils.quote(objectStoreKeyPath[0])+");");
		}
		else if (keyTypeName.equals(Date.class.getCanonicalName()))
		{
			srcWriter.println("return new "+Date.class.getCanonicalName()+"((long)"+JsUtils.class.getCanonicalName()+".readDoublePropertyValue(object, "+EscapeUtils.quote(objectStoreKeyPath[0])+"));");
		}
		else
		{
			srcWriter.println("return object.cast();");
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
		String typeName = cursorName.replaceAll("\\W", "_");
		return typeName+"_SQL_Cursor";
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
		composerFactory.setSuperclass("WSQLCursor<"+getKeyTypeName()+","+getKeyTypeName(objectStoreKeyPath)+","+getTargetObjectClassName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				Array.class.getCanonicalName(),
				WSQLAbstractDatabase.class.getCanonicalName(),
				WSQLKeyRange.class.getCanonicalName(),
				WSQLTransaction.class.getCanonicalName(),
				WSQLCursor.class.getCanonicalName(),
				CursorDirection.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(),
				JsArrayMixed.class.getCanonicalName()
		};
		return imports;
	}
}