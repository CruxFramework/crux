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
import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.db.Cursor;
import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.DatabaseDeleteCallback;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.DatabaseWriteCallback;
import org.cruxframework.crux.core.client.db.Index;
import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.WSQLAbstractDatabase;
import org.cruxframework.crux.core.client.db.WSQLAbstractObjectStore;
import org.cruxframework.crux.core.client.db.WSQLTransaction;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.database.AbstractDatabaseProxyCreator.IndexData;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLObjectStoreProxyCreator extends SQLAbstractKeyValueProxyCreator
{
	private JClassType abstractObjectStoreType;
	private String dbVariable;
	private final Set<IndexData> indexes;
	private final boolean autoIncrement;

	public SQLObjectStoreProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, 
			String[] keyPath, boolean autoIncrement, Set<IndexData> indexes)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.autoIncrement = autoIncrement;
		validatePrimaryKeyPath(targetObjectType, objectStoreName, keyPath);
		
		this.indexes = indexes;
		this.abstractObjectStoreType = context.getTypeOracle().findType(WSQLAbstractObjectStore.class.getCanonicalName());
		this.dbVariable = "db";
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(WSQLAbstractDatabase db, String name, WSQLTransaction transaction){");
		srcWriter.println("super(db, name, transaction);");

		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetNativeKeyMethod(srcWriter);
		generateGetIndexedColumnNamesMethod(srcWriter, getIndexColumns());
		generateGetKeyPathMethod(srcWriter);
		generateGetObjectStoreNameMethod(srcWriter);
		generateAddKeyRangeToQueryMethod(srcWriter);
		generateAddKeyToQueryMethod(srcWriter);
		generateDeriveKeyMethod(srcWriter);
		generateSetObjectKeyMethod(srcWriter);
		generateDecodeObjectMethod(srcWriter);
		generateEncodeObjectMethod(srcWriter);
		generateGetCreateTableSQLMethod(srcWriter);
		generateGetIndexNamesMethod(srcWriter);
		generateIsAutoIncrementMethod(srcWriter);
		generateOpenCursorMethod(srcWriter);
		generateGetIndexMethod(srcWriter);
		generateGetKeyRangeFactoryMethod(srcWriter, objectStoreName);
	}

	protected Set<String> getIndexColumns()
	{
		Set<String> indexColumns = new HashSet<String>();
		for (IndexData indexData:indexes)
		{
			for (String col : indexData.keyPath)
			{
				indexColumns.add(col);
			}
		}
		return indexColumns;
	}

	protected void generateGetObjectStoreNameMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public String getObjectStoreName(){");
		srcWriter.println("return "+EscapeUtils.quote(objectStoreName)+";");
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateDeriveKeyMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected "+getKeyTypeName()+" getKey("+ getTargetObjectClassName()+" object){");
		
		srcWriter.print("boolean hasKey = ");
		boolean first = true;
		for (String k : keyPath)
        {
			if (!first)
			{
				srcWriter.print(" || ");
			}
			String getterMethod = JClassUtils.getGetterMethod(k, targetObjectType);
			srcWriter.print("object."+getterMethod+"() != null");
			first = false;
        }
		srcWriter.println(";");
		
		srcWriter.println("if (hasKey){");
		srcWriter.print(getKeyTypeName()+" key");
		if (hasCompositeKey())
		{
			srcWriter.println(" = new Object["+keyPath.length+"];");
			int i = 0;
			for (String k : keyPath)
	        {
				String getterMethod = JClassUtils.getGetterMethod(k, targetObjectType);
				srcWriter.print("key ["+i+"] = object."+getterMethod+"();");
				i++;
	        }
		}
		else
		{
			srcWriter.println(" = object."+JClassUtils.getGetterMethod(keyPath[0], targetObjectType)+"();");
		}
		srcWriter.println("return key;");
		
		if (autoIncrement)
		{
			if (!getKeyTypeName().equals("Integer"))
			{
				throw new CruxGeneratorException("Auto increment keys can only be used on integer keys");
			}
			srcWriter.println("} else {");
			srcWriter.println("return null;");
		}
		else
		{
			srcWriter.println("} else {");
			srcWriter.println("reportError(db.messages.objectStoreDeriveKeyError(name));");
			srcWriter.println("return null;");
		}
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateSetObjectKeyMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected void setObjectKey("+getTargetObjectClassName()+" object, "+getKeyTypeName()+" key){");

		if (hasCompositeKey())
		{
			for (int i = 0; i < keyPath.length; i++)
			{
				String k = keyPath[i];
				JType jType = JClassUtils.getTypeForProperty(k, targetObjectType);
				String setterMethod = JClassUtils.getSetterMethod(k, targetObjectType, jType);
				srcWriter.println("object."+setterMethod+"((key==null?null:("+jType.getParameterizedQualifiedSourceName()+")key["+i+"]));");
			}
		}
		else
		{
			String k = keyPath[0];
			JType jType = JClassUtils.getTypeForProperty(k, targetObjectType);
			String setterMethod = JClassUtils.getSetterMethod(k, targetObjectType, jType);
			srcWriter.println("object."+setterMethod+"(key);");
		}
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateGetCreateTableSQLMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected String getCreateTableSQL(){");
		srcWriter.print("String sql = \"CREATE TABLE  \\\"\"+name+\"\\\" (\\\"value\\\" BLOB");//TODO alterar nome da coluna pra evitar conflito com indices
		Set<String> addedColumns = new HashSet<String>();
		
		for (String col : keyPath)
		{
			if (!addedColumns.contains(col))
			{
				addedColumns.add(col);
				JType propertyType = getPropertyType(col);
				srcWriter.print(",\\\""+col+"\\\" "+getSQLTypeForProperty(propertyType)+" PRIMARY KEY");
				if (autoIncrement)
				{
					if (!getKeyTypeName().equals("Integer"))
					{
						throw new CruxGeneratorException("Auto increment keys can only be used on integer keys");
					}
					srcWriter.print(" AUTOINCREMENT");
				}
			}
			else
			{
				throw new CruxGeneratorException("Invalid KeyPath for object store ["+objectStoreName+"]. Duplicated column on keyPath ["+col+"]");
			}
		}

		StringBuilder uniqueConstraints = new StringBuilder();
		for (IndexData indexData: indexes)
		{
			if (indexData.unique)
			{
				uniqueConstraints.append(", UNIQUE(");
			}
			boolean firstCostraint = true;
			for (String col : indexData.keyPath)
			{
				if (!addedColumns.contains(col))
				{
					addedColumns.add(col);
					JType propertyType = getPropertyType(col);
					srcWriter.print(",\\\""+col+"\\\" "+getSQLTypeForProperty(propertyType));
				}
				if (indexData.unique)
				{
					if (!firstCostraint)
					{
						uniqueConstraints.append(",");
					}
					uniqueConstraints.append("\\\""+col+"\\\"");
					firstCostraint = false;
				}
			}
			if (indexData.unique)
			{
				uniqueConstraints.append(") ON CONFLICT REPLACE");
			}
		}
		
		srcWriter.print(uniqueConstraints.toString());
		srcWriter.println(")\";");
		srcWriter.println("return sql;");
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateGetIndexNamesMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public String[] getIndexNames(){");
		srcWriter.print("return new String[]{");
		
		boolean first = true;
		for (IndexData index : indexes)
        {
	        if (!first)
	        {
	        	srcWriter.print(",");
	        }
	        first = false;
	        srcWriter.print(EscapeUtils.quote(index.indexName));
        }
		
		srcWriter.println("};");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateIsAutoIncrementMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public boolean isAutoIncrement(){");
		srcWriter.println("return "+autoIncrement+";");
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateOpenCursorMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		String cursorClassName = new SQLCursorProxyCreator(context, logger, targetObjectType, objectStoreName, autoIncrement, getIndexColumns(), keyPath, keyPath, "ObjectStore_"+getTargetObjectClassName()).create();
		srcWriter.println("new "+cursorClassName+"("+dbVariable+", (WSQLKeyRange<"+getKeyTypeName()+">)keyRange, direction, transaction).start(callback);");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	private String getSQLTypeForProperty(JType jType)
	{
	    if (jType.equals(stringType))
	    {
	    	return "BLOB";
	    }
	    else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
	    {
	    	return "INTEGER";
	    }
	    else if (jType.equals(doubleType) || (jType.equals(JPrimitiveType.DOUBLE)))
	    {
	    	return "REAL";
	    }
	    else if (jType.equals(dateType))
	    {
	    	return "INTEGER";
	    }
		
		return "BLOB";
	}

	protected void generateGetIndexMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public <I> Index<"+getKeyTypeName()+", I, "+getTargetObjectClassName()+"> getIndex(String name){");
		for(IndexData index: indexes)
		{
			srcWriter.println("if (StringUtils.unsafeEquals(name, "+EscapeUtils.quote(index.indexName)+")){");
			String indexClassName = new SQLIndexProxyCreator(context, logger, targetObjectType, objectStoreName, autoIncrement, index.keyPath, index.indexName, keyPath, getIndexColumns(), index.unique).create();
			srcWriter.println("return (Index<"+getKeyTypeName()+", I, "+getTargetObjectClassName()+">) new " + indexClassName + "("+dbVariable+", transaction);");
			srcWriter.println("}");
		}

		srcWriter.println("return null;");
		srcWriter.println("}");
		srcWriter.println();
	}

	@Override
	public String getProxyQualifiedName()
	{
		return abstractObjectStoreType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		String typeName = objectStoreName.replaceAll("\\W", "_");
		return typeName+"_SQL_ObjectStore";
	}

	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = abstractObjectStoreType.getPackage().getName();
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
		composerFactory.setSuperclass("WSQLAbstractObjectStore<"+getKeyTypeName()+","+getTargetObjectClassName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}

	/**
	 * @return
	 */
	protected String[] getImports()
	{
		String[] imports = new String[] {
				Array.class.getCanonicalName(),
				WSQLAbstractDatabase.class.getCanonicalName(), 
				WSQLAbstractObjectStore.class.getCanonicalName(),
				WSQLTransaction.class.getCanonicalName(),
				DatabaseRetrieveCallback.class.getCanonicalName(),
				DatabaseWriteCallback.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(), 
				DatabaseCursorCallback.class.getCanonicalName(), 
				KeyRangeFactory.class.getCanonicalName(),
				Cursor.class.getCanonicalName(), 
				CursorDirection.class.getCanonicalName(), 
				StringUtils.class.getCanonicalName(),
				DatabaseDeleteCallback.class.getCanonicalName(), 
				JsArrayMixed.class.getCanonicalName(), 
				Index.class.getCanonicalName(), 
				KeyRange.class.getCanonicalName()

		};
		return imports;
	}
}
