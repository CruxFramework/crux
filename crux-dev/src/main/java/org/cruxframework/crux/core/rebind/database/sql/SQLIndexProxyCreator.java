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
import java.util.Set;

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.WSQLIndex;
import org.cruxframework.crux.core.client.db.WSQLTransaction;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLIndexProxyCreator extends SQLAbstractKeyValueProxyCreator
{
	private JClassType indexType;
 	private String indexName;
	private String dbVariable;
	private String[] objectStoreKeyPath;
	private final Set<String> objectStoreIndexColumns;
	private final boolean unique;
	private final boolean autoIncrement;

	public SQLIndexProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, 
			boolean autoIncrement, String[] keyPath, String indexName, String[] objectStoreKeyPath, Set<String> objectStoreIndexColumns, boolean unique)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.autoIncrement = autoIncrement;
		this.objectStoreKeyPath = objectStoreKeyPath;
		this.objectStoreIndexColumns = objectStoreIndexColumns;
		this.unique = unique;
		this.indexType = context.getTypeOracle().findType(WSQLIndex.class.getCanonicalName());
		this.dbVariable = "db";
		this.indexName = indexName;
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(WSQLAbstractDatabase db, WSQLTransaction transaction){");
		srcWriter.println("super(db, transaction);");
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateOpenCursorMethod(srcWriter);
		generateOpenKeyCursorMethod(srcWriter);
		generateGetNameMethod(srcWriter);
		generateIsUniqueMethod(srcWriter);
		generateIsMultiEntryMethod(srcWriter);
		generateGetKeyRangeFactoryMethod(srcWriter, getIndexClassName());
		if (hasCompositeKey())
		{
			generateGetNativeKeyMethod(srcWriter);
			generateFromNativeKeyMethod(srcWriter, objectStoreKeyPath);
		}
	}

	protected void generateGetNameMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public String getName(){");
		srcWriter.println("return "+EscapeUtils.quote(indexName)+";");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateIsUniqueMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public boolean isUnique(){");
		srcWriter.println("return "+unique+";");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateIsMultiEntryMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public boolean isMultiEntry(){");
		srcWriter.println("return "+(keyPath.length > 1)+";");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateOpenCursorMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		String cursorClassName = new SQLCursorProxyCreator(context, logger, targetObjectType, objectStoreName, autoIncrement, objectStoreIndexColumns, keyPath, objectStoreKeyPath, getIndexClassName()).create();
		srcWriter.println("new "+cursorClassName+"("+dbVariable+", (WSQLKeyRange<"+getKeyTypeName()+">)keyRange, direction, transaction).start(callback);");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateOpenKeyCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openKeyCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		String cursorClassName = new SQLKeyCursorProxyCreator(context, logger, targetObjectType, objectStoreName, autoIncrement, keyPath, objectStoreKeyPath, getIndexClassName()).create();
		srcWriter.println("new "+cursorClassName+"("+dbVariable+", (WSQLKeyRange<"+getKeyTypeName()+">)keyRange, direction, transaction).start(callback);");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	@Override
	public String getProxyQualifiedName()
	{
		return indexType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		return getIndexClassName()+"SQL__Index";
	}

	protected String getIndexClassName()
	{
		String typeName = indexName.replaceAll("\\W", "_");
		return objectStoreName + "_" + typeName;
	}
	
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = indexType.getPackage().getName();
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
		composerFactory.setSuperclass("WSQLIndex<"+getKeyTypeName(objectStoreKeyPath)+","+getKeyTypeName()+","+getTargetObjectClassName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				WSQLIndex.class.getCanonicalName(),
				WSQLTransaction.class.getCanonicalName(),
				DatabaseRetrieveCallback.class.getCanonicalName(),
				DatabaseCursorCallback.class.getCanonicalName(),
				KeyRange.class.getCanonicalName(), 
				KeyRangeFactory.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(), 
				CursorDirection.class.getCanonicalName()
		};
		return imports;
	}
}