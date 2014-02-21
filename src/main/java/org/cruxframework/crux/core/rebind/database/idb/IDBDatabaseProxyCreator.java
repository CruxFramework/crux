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
import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;
import org.cruxframework.crux.core.client.db.IDXAbstractDatabase;
import org.cruxframework.crux.core.client.db.IDXFileStore;
import org.cruxframework.crux.core.client.db.ObjectStore;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.IndexDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;
import org.cruxframework.crux.core.client.db.indexeddb.IDBIndexParameters;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStoreParameters;
import org.cruxframework.crux.core.client.db.indexeddb.IDBOpenDBRequest;
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
public class IDBDatabaseProxyCreator extends AbstractDatabaseProxyCreator
{
	public IDBDatabaseProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
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
	    srcWriter.println("protected void updateDatabaseStructure(IDBOpenDBRequest openDBRequest){");

	    generateObjectStoresCreation(srcWriter, "openDBRequest");
	    
	    srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateGetObjectStoreMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected <K, V> ObjectStore<K, V> getObjectStore(String storeName, IDBObjectStore idbObjectStore){");
	    
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
			String objectStore = new IDBObjectStoreProxyCreator(context, logger, objectStoreTarget, objectStoreName, keyPath, indexes).create();
			srcWriter.println("return (ObjectStore<K, V>) new "+objectStore+"(this, idbObjectStore);");
			srcWriter.println("}");
        }
	    
	    srcWriter.println("return null;");
	    srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateObjectStoresCreation(SourcePrinter srcWriter, String requestVar)
    {
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
	    Set<String> addedObjectStores = new HashSet<String>();
		String objectStoreVar = "objectStore";
		String indexNamesVar = "indexNames";
		srcWriter.println("IDBObjectStore "+objectStoreVar+";");
		srcWriter.println(FastList.class.getCanonicalName() +"<String> storeNames = db.listObjectStoreNames();");
		srcWriter.println("for(int i=0; i< storeNames.size(); i++){");
		srcWriter.println("String storeName = storeNames.get(i);");
		srcWriter.println("try{");
		srcWriter.println("db.deleteObjectStore(storeName);");
		srcWriter.println("}catch (Exception e){/* Chrome BUG. When an object store is created, but have no data, chrome raises a NotFoundException when removing these store. So ignore any delete failed attempt.*/}");
		srcWriter.println("}");
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (addedObjectStores.contains(objectStoreName))
			{
				throw new CruxGeneratorException("Duplicated objectstore declared on Datasource ["+databaseMetadata.name()+"]");
			}
			addedObjectStores.add(objectStoreName);
			
			if (objectStoreMetadata.keyPath().length == 1)
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.keyPath()[0], objectStoreMetadata.autoIncrement(), objectStoreName, objectStoreVar);
			}
			else if (objectStoreMetadata.keyPath().length > 1)
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.keyPath(), objectStoreMetadata.autoIncrement(), objectStoreName, objectStoreVar);
			}
			else if (objectStoreTarget != null && !objectStoreTarget.isAssignableTo(emptyType))
			{
				String[] keyPath = getKeyPath(objectStoreTarget);
				if (keyPath == null || keyPath.length == 0)
				{
					throw new CruxGeneratorException("can not create an objectStore without a key definition. ObjectStore["+objectStoreName+"].");
				}
				else if (keyPath.length == 1)
				{
					generateObjectStoreCreation(srcWriter, keyPath[0], isAutoIncrement(objectStoreTarget), objectStoreName, objectStoreVar);
				}
				else
				{
					generateObjectStoreCreation(srcWriter, keyPath, isAutoIncrement(objectStoreTarget), objectStoreName, objectStoreVar);
				}
			}
			else
			{
				throw new CruxGeneratorException("can not create an objectStore without a key definition. ObjectStore["+objectStoreName+"].");
			}
			generateIndexesCreation(srcWriter, objectStoreMetadata.indexes(), objectStoreTarget, objectStoreVar, objectStoreName, indexNamesVar);
        }
		generateFileStoreCreation(srcWriter);		
    }
	//TODO remover indices antigos...que foram removidos do metadado
	protected void generateIndexesCreation(SourcePrinter srcWriter, IndexDef[] indexMetadata, 
										   JClassType objectStoreTarget, String objectStoreVar, 
										   String objectStoreName, String indexNamesVar)
    {
		Set<IndexData> indexesCreated = getIndexes(indexMetadata, objectStoreTarget, objectStoreName);
		for (IndexData index : indexesCreated)
        {
			if (index.keyPath.length == 1)
			{//TODO ensureValidIdentifier... remove ',' etc
				generateIndexCreation(srcWriter, index.keyPath[0], index.unique, false, index.indexName, objectStoreVar, indexNamesVar);
			}
			else
			{
				generateIndexCreation(srcWriter, index.keyPath, index.unique, index.multiEntry, index.indexName, objectStoreVar, indexNamesVar);
			}
        }
    }

	protected void generateIndexCreation(SourcePrinter srcWriter, String keyPath, boolean unique, boolean multiEntry, 
										 String name, String objectStoreVar, String indexNamesVar)
    {
	    srcWriter.println(objectStoreVar+".createIndex("+EscapeUtils.quote(name)+", "+ EscapeUtils.quote(keyPath) + ", " +
	    		IDBIndexParameters.class.getCanonicalName()+".create("+unique+", "+multiEntry+"));");
    }

	protected void generateIndexCreation(SourcePrinter srcWriter, String[] keyPaths, boolean unique, boolean multiEntry, 
										String name, String objectStoreVar, String indexNamesVar)
	{
	    srcWriter.println(objectStoreVar+".createIndex("+EscapeUtils.quote(name)+", new String[]{");
	    boolean first = true;
	    for (String keyPath : keyPaths)
	    {
	    	if (!first)
	    	{
	    		srcWriter.print(", ");
	    	}
	    	first = false;
	    	srcWriter.print(EscapeUtils.quote(keyPath));
	    }		
	    srcWriter.println("}, "  + IDBIndexParameters.class.getCanonicalName()+".create("+unique+", "+multiEntry+"));");
	}

	protected void generateFileStoreCreation(SourcePrinter srcWriter)
    {
		srcWriter.println("db.createObjectStore("+EscapeUtils.quote(IDXFileStore.OBJECT_STORE_NAME)+");");
    }

	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String keyPath, boolean autoIncrement, String objectStoreName, String objectStoreVar)
	{
		srcWriter.println(objectStoreVar+" = db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
				IDBObjectStoreParameters.class.getCanonicalName()+".create("+EscapeUtils.quote(keyPath)+", "+autoIncrement+"));");
	}
	
	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String[] keyPaths, boolean autoIncrement, String objectStoreName, String objectStoreVar)
    {
	    srcWriter.println(objectStoreVar+" = db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
	    		IDBObjectStoreParameters.class.getCanonicalName()+".create(new String[]{");
	    boolean first = true;
	    for (String keyPath : keyPaths)
	    {
	    	if (!first)
	    	{
	    		srcWriter.print(", ");
	    	}
	    	first = false;
	    	srcWriter.print(EscapeUtils.quote(keyPath));
	    }		
	    srcWriter.println("}, "+autoIncrement+"));");
    }

	@Override
	protected String[] getImports()
	{
		return new String[]{
			IDBObjectStore.class.getCanonicalName(),
			IDBOpenDBRequest.class.getCanonicalName(),
			DatabaseErrorHandler.class.getCanonicalName(),
			ObjectStore.class.getCanonicalName(), 
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
		return enclosingTypeName+baseIntf.getSimpleSourceName() + "_IDB_Impl";
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
		composerFactory.setSuperclass(IDXAbstractDatabase.class.getCanonicalName());
		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
}