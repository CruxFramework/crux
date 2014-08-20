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
package org.cruxframework.crux.core.rebind.database;

import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.db.Database;
import org.cruxframework.crux.core.client.db.DatabaseCallback;
import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.Transaction;
import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.database.idb.IDBDatabaseProxyCreator;
import org.cruxframework.crux.core.rebind.database.sql.SQLDatabaseProxyCreator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * This class creates a client proxy for access a database
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class DatabaseProxyCreator extends AbstractInterfaceWrapperProxyCreator
{

	private String idxDatabaseImplClass;
	private String sqlDatabaseImplClass;

	public DatabaseProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, false);
		idxDatabaseImplClass = new IDBDatabaseProxyCreator(logger, context, baseIntf).create();
		sqlDatabaseImplClass = new SQLDatabaseProxyCreator(logger, context, baseIntf).create();
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.println("if ("+Crux.class.getCanonicalName()+".getConfig().preferWebSQLForNativeDB() && isWebSQLDBSupported()){");
		srcWriter.println("useWebSQL();");
		srcWriter.println("}");
		srcWriter.println("else if (isIndexedDBSupported()){");
		srcWriter.println("useIndexedDB();");
		srcWriter.println("}");
		srcWriter.println("else if (isWebSQLDBSupported()){");
		srcWriter.println("useWebSQL();");
		srcWriter.println("}");
		srcWriter.println("}");
	}
	
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("protected static Logger logger = Logger.getLogger("+getProxySimpleName()+".class.getName());");
		srcWriter.println("private Database impl;");
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		generateIsOpenMethod(srcWriter);
		generateGetNameMethod(srcWriter);
		generateGetVersionMethod(srcWriter);
		generateSetNameMethod(srcWriter);
		generateSetVersionMethod(srcWriter);
		generateOpenMethod(srcWriter);
		generateCloseMethod(srcWriter);
		generateDeleteMethod(srcWriter);
		generateGetTransactionMethod(srcWriter);
		generateGetTransactionCallbackMethod(srcWriter);
		generateAddMethod(srcWriter);
		generatePutMethod(srcWriter);
		generateGetMethod(srcWriter);
		generateDeleteStoreMethod(srcWriter);
		generateDeleteStoreRangeMethod(srcWriter);
		generateDefaultErrorHandlerMethod(srcWriter);
		generateIsSupportedMethod(srcWriter);
		generateIsIndexedDBSupported(srcWriter);
		generateIsWebSQLDBSupported(srcWriter);
		generateUseWebSQL(srcWriter);
		generateUseIndexedDB(srcWriter);
	}
	
	private void generateUseWebSQL(SourcePrinter srcWriter)
    {
		srcWriter.println("public void useWebSQL(){");
		srcWriter.println("if (isWebSQLDBSupported()){");
		srcWriter.println("if (LogConfiguration.loggingIsEnabled()){");
		srcWriter.println("logger.log(Level.INFO, \"Using WEB SQL as native database implementation.\");");
		srcWriter.println("}");
		srcWriter.println("this.impl = new "+sqlDatabaseImplClass+"();");
		srcWriter.println("}");
		srcWriter.println("else {");
		srcWriter.println("this.impl = null;");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateUseIndexedDB(SourcePrinter srcWriter)
    {
		srcWriter.println("public void useIndexedDB(){");
		srcWriter.println("if (isIndexedDBSupported()){");
		srcWriter.println("if (LogConfiguration.loggingIsEnabled()){");
		srcWriter.println("logger.log(Level.INFO, \"Using Indexed DB as native database implementation.\");");
		srcWriter.println("}");
		srcWriter.println("this.impl = new "+idxDatabaseImplClass+"();");
		srcWriter.println("}");
		srcWriter.println("else {");
		srcWriter.println("this.impl = null;");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateIsSupportedMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public boolean isSupported(){");
		srcWriter.println("return impl != null;");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateDefaultErrorHandlerMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("	public void setDefaultErrorHandler("+DatabaseErrorHandler.class.getCanonicalName()+" errorHandler){");
		srcWriter.println("impl.setDefaultErrorHandler(errorHandler);");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	private void generateDeleteStoreRangeMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("	public <K> void delete("+KeyRange.class.getCanonicalName()+"<K> keyRange, String objectStore, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.delete(keyRange, objectStore, callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateDeleteStoreMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("	public <K> void delete(K key, String objectStore, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.delete(key, objectStore, callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateGetMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("	public <K, V> void get(K key, String objectStore, "+DatabaseRetrieveCallback.class.getCanonicalName()+"<V> callback){");
		srcWriter.println("impl.get(key, objectStore, callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generatePutMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("	public <V> void put(V[] objects, String objectStoreName, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.put(objects, objectStoreName, callback);");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("	public <V> void put(List<V> objects, String objectStoreName, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.put(objects, objectStoreName, callback);");
		srcWriter.println("}");
		srcWriter.println();
}

	private void generateAddMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public <V> void add(V[] objects, String objectStoreName, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.add(objects, objectStoreName, callback);");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("public <V> void add(List<V> objects, String objectStoreName, "+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.add(objects, objectStoreName, callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateGetTransactionCallbackMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public "+Transaction.class.getCanonicalName()+" getTransaction(String[] storeNames, "+
							Transaction.class.getCanonicalName()+".Mode mode, "+
							TransactionCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("return impl.getTransaction(storeNames, mode, callback);");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	private void generateGetTransactionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public "+Transaction.class.getCanonicalName()+" getTransaction(String[] storeNames, "+Transaction.class.getCanonicalName()+".Mode mode){");
		srcWriter.println("return impl.getTransaction(storeNames, mode);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateDeleteMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void delete("+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.delete(callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateCloseMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void close(){");
		srcWriter.println("impl.close();");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateOpenMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void open("+DatabaseCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("impl.open(callback);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateSetVersionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void setVersion(int version){");
		srcWriter.println("impl.setVersion(version);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateSetNameMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void setName(String name){");
		srcWriter.println("impl.setName(name);");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateGetVersionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public int getVersion(){");
		srcWriter.println("return impl.getVersion();");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateGetNameMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public String getName(){");
		srcWriter.println("return impl.getName();");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateIsOpenMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public boolean isOpen(){");
		srcWriter.println("return impl.isOpen();");
		srcWriter.println("}");
		srcWriter.println();
    }

	private void generateIsIndexedDBSupported(SourcePrinter srcWriter)
    {
		srcWriter.println("private native boolean isIndexedDBSupported()/*-{");
		srcWriter.println("var IDBKeyRange = $wnd.IDBKeyRange || $wnd.webkitIDBKeyRange;");
		srcWriter.println("var indexedDB = $wnd.indexedDB || $wnd.mozIndexedDB || $wnd.webkitIndexedDB;");
		srcWriter.println("if (IDBKeyRange && indexedDB) {");
		srcWriter.println("return true;");
		srcWriter.println("}");
		srcWriter.println("return false;");
		srcWriter.println("}-*/;");
		srcWriter.println();
    }

	private void generateIsWebSQLDBSupported(SourcePrinter srcWriter)
    {
		srcWriter.println("private native boolean isWebSQLDBSupported()/*-{");
		srcWriter.println("var sqlsupport = !!$wnd.openDatabase;");
		srcWriter.println("return sqlsupport;");
		srcWriter.println("}-*/;");
		srcWriter.println();
    }

	@Override
	protected String[] getImports()
	{
		return new String[]{
			Logger.class.getCanonicalName(),
			List.class.getCanonicalName(),
			LogConfiguration.class.getCanonicalName(),
			Level.class.getCanonicalName(),
			StringUtils.class.getCanonicalName(),
			Database.class.getCanonicalName(), 
			GWT.class.getCanonicalName()
		};
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
		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
}
