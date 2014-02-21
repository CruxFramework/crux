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
import org.cruxframework.crux.core.client.db.DatabaseCallback;
import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;
import org.cruxframework.crux.core.client.db.WSQLAbstractDatabase;
import org.cruxframework.crux.core.client.db.WSQLAbstractObjectStore;
import org.cruxframework.crux.core.client.db.WSQLTransaction;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.database.AbstractDatabaseProxyCreator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * This class creates a client proxy for access a database
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class SQLDatabaseProxyCreator extends AbstractDatabaseProxyCreator
{
	public SQLDatabaseProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf);
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		generateUpdateDatabaseStructureMethod(srcWriter);
		generateGetObjectStoreMethod(srcWriter);
	}

	protected void generateUpdateDatabaseStructureMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected void updateDatabaseStructure(final "+SQLTransaction.class.getCanonicalName()+" tx, final DatabaseCallback callback){");

	    generateObjectStoresCreation(srcWriter, "callback");
	    
	    srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateObjectStoresCreation(SourcePrinter srcWriter, String callback)
    {
		srcWriter.println("deleteDBTables(tx, new DatabaseCallback(){");
		srcWriter.println("public void onSuccess(){");
	    
		ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    Set<String> addedObjectStores = new HashSet<String>();
	    
		String objectStoreVar = "objectStore";
		String indexNamesVar = "indexNames";
		srcWriter.println("WSQLAbstractObjectStore "+objectStoreVar+";");
		srcWriter.println("Array<String> "+indexNamesVar+";");
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (addedObjectStores.contains(objectStoreName))
			{
				throw new CruxGeneratorException("Duplicated objectstore declared on Datasource ["+databaseMetadata.name()+"]");
			}
			addedObjectStores.add(objectStoreName);
			
			srcWriter.println(objectStoreVar +" = getObjectStore("+EscapeUtils.quote(objectStoreName)+", null);");
			
			srcWriter.println(objectStoreVar+".createTable(tx, null);");
        }

		srcWriter.println("createFileStore(tx);");
			
		srcWriter.println("}");
		srcWriter.println("}, getDeleteErrorHandler(callback), false);");
    }
	
	protected void generateGetObjectStoreMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected <K, V> WSQLAbstractObjectStore<K, V> getObjectStore(String storeName, WSQLTransaction transaction){");
	    
	    boolean first = true;
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (!first)
			{
				srcWriter.print("else ");
			}
			first = false;
			Set<IndexData> indexes = getIndexes(objectStoreMetadata.indexes(), objectStoreTarget, objectStoreName);
			srcWriter.println("if (StringUtils.unsafeEquals(storeName, "+EscapeUtils.quote(objectStoreName)+")){");
			String[] keyPath = getKeyPath(objectStoreMetadata, objectStoreTarget);
			String objectStore = new SQLObjectStoreProxyCreator(context, logger, objectStoreTarget, objectStoreName, keyPath, isAutoIncrement(objectStoreTarget), indexes).create();
			srcWriter.println("return (WSQLAbstractObjectStore<K, V>) new "+objectStore+"(this, storeName, transaction);");
			srcWriter.println("}");
        }
	    
	    srcWriter.println("return null;");
	    srcWriter.println("}");
		srcWriter.println();
    }

	@Override
	protected String[] getImports()
	{
		return new String[]{
			DatabaseCallback.class.getCanonicalName(),
			WSQLAbstractObjectStore.class.getCanonicalName(),
			WSQLTransaction.class.getCanonicalName(),
			DatabaseErrorHandler.class.getCanonicalName(),
			Array.class.getCanonicalName(), 
			StringUtils.class.getCanonicalName(), 
			GWT.class.getCanonicalName()
		};
	}

	/**
	 * @return the simple name of the proxy object.
	 */
	@Override
	public String getProxySimpleName()
	{
		JClassType enclosingType = baseIntf.getEnclosingType();
		String enclosingTypeName = (enclosingType==null?"":enclosingType.getSimpleSourceName()+"_");
		return enclosingTypeName+baseIntf.getSimpleSourceName() + "_SQL_Impl";
	}

	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		JPackage pkg = baseIntf.getPackage();
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
		composerFactory.setSuperclass(WSQLAbstractDatabase.class.getCanonicalName());
		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
}