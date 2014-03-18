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
import java.util.Date;

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.IDXAbstractDatabase;
import org.cruxframework.crux.core.client.db.IDXCursor;
import org.cruxframework.crux.core.client.db.IDXIndex;
import org.cruxframework.crux.core.client.db.IDXKeyRange;
import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;
import org.cruxframework.crux.core.client.db.indexeddb.IDBIndex;
import org.cruxframework.crux.core.client.db.indexeddb.IDBIndex.IDBIndexCursorRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectStoreRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBIndexProxyCreator extends IDBAbstractKeyValueProxyCreator
{
	private JClassType indexType;
	private String idbIndexVariable;
 	private String indexName;
	private String dbVariable;
	private String[] objectStoreKeyPath;

	public IDBIndexProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, 
			String[] keyPath, String indexName, String[] objectStoreKeyPath)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.objectStoreKeyPath = objectStoreKeyPath;
		this.indexType = context.getTypeOracle().findType(IDXIndex.class.getCanonicalName());
		this.idbIndexVariable = "idbIndex";
		this.dbVariable = "db";
		this.indexName = indexName;
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(IDXAbstractDatabase db, IDBIndex idbIndex){");
		srcWriter.println("super(db, idbIndex);");
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetMethod(srcWriter);
		generateGetRangetMethod(srcWriter);
		generateGetKeyMethod(srcWriter);
		generateGetKeyRangetMethod(srcWriter);
		generateOpenCursorMethod(srcWriter);
		generateOpenCursorKeyMethod(srcWriter);
		generateOpenCursorKeyDirectionMethod(srcWriter);
		generateOpenKeyCursorMethod(srcWriter);
		generateOpenKeyCursorKeyMethod(srcWriter);
		generateOpenKeyCursorKeyDirectionMethod(srcWriter);
		generateGetKeyRangeFactoryMethod(srcWriter, indexName);
		if (hasCompositeKey())
		{
			generateGetNativeKeyMethod(srcWriter);
			generateFromNativeKeyMethod(srcWriter, objectStoreKeyPath);
		}
	}
	
	protected void generateGetMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		String targetObjectClassName = getTargetObjectClassName();
		srcWriter.println("public void get("+keyTypeName+" key, final DatabaseRetrieveCallback<"+targetObjectClassName+"> callback){");
		if (hasCompositeKey())
		{
			srcWriter.println("IDBObjectRetrieveRequest retrieveRequest = "+idbIndexVariable+".get(getNativeKey(key));");
		}
		else
		{
			srcWriter.println("IDBObjectRetrieveRequest retrieveRequest = "+idbIndexVariable+".get(key);");
		}
		generateGetCallbacks(srcWriter, "callback", dbVariable, "retrieveRequest");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetRangetMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		String targetObjectClassName = getTargetObjectClassName();
		srcWriter.println("public void get(KeyRange<"+keyTypeName+"> keyRange, final DatabaseRetrieveCallback<"+targetObjectClassName+"> callback){");
		srcWriter.println("IDBObjectRetrieveRequest retrieveRequest = "+idbIndexVariable+".get("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange));");
		generateGetCallbacks(srcWriter, "callback", dbVariable, "retrieveRequest");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetKeyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public void getKey("+keyTypeName+" key, final DatabaseRetrieveCallback<"+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		if (hasCompositeKey())
		{
			srcWriter.println("IDBObjectStoreRequest retrieveRequest = "+idbIndexVariable+".getKey(getNativeKey(key));");
		}
		else
		{
			srcWriter.println("IDBObjectStoreRequest retrieveRequest = "+idbIndexVariable+".getKey(key);");
		}
		generateGetKeyCallbacks(srcWriter, "callback", dbVariable, "retrieveRequest");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetKeyRangetMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public void getKey(KeyRange<"+keyTypeName+"> keyRange, final DatabaseRetrieveCallback<"+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		srcWriter.println("IDBObjectStoreRequest retrieveRequest = "+idbIndexVariable+".getKey("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange));");
		generateGetKeyCallbacks(srcWriter, "callback", dbVariable, "retrieveRequest");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openCursor();");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", indexName);
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenCursorKeyMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openCursor("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange));");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", indexName);
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateOpenCursorKeyDirectionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openCursor("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange), "+IDXCursor.class.getCanonicalName()+".getNativeCursorDirection(direction));");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", indexName);
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenKeyCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openKeyCursor(final DatabaseCursorCallback<"+getKeyTypeName()+", "+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openKeyCursor();");
		generateKeyCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenKeyCursorKeyMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openKeyCursor(KeyRange<"+getKeyTypeName()+"> keyRange, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openKeyCursor("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange));");
		generateKeyCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateOpenKeyCursorKeyDirectionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openKeyCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getKeyTypeName(objectStoreKeyPath)+"> callback){");
		srcWriter.println("IDBIndexCursorRequest cursorRequest = " + idbIndexVariable+".openKeyCursor("+IDXKeyRange.class.getCanonicalName()+".getNativeKeyRange(keyRange), "+IDXCursor.class.getCanonicalName()+".getNativeCursorDirection(direction));");
		generateKeyCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateKeyCursorHandlers(SourcePrinter srcWriter, String callbackVar, String dbVariable, String cursorRequestVar)
	{
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");
		
		srcWriter.println(cursorRequestVar+".onSuccess(new IDBCursorEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBCursorEvent event){");
		String cursorClassName = new IDBKeyCursorProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, objectStoreKeyPath, indexName).create();
		srcWriter.println(IDBCursorWithValue.class.getCanonicalName()+" cursor = event.getCursor();");
		srcWriter.println("if ("+callbackVar+" != null){");
		
		if (!Environment.isProduction())
		{
			srcWriter.println("try{");
		}
		
		srcWriter.println("if(cursor != null){");
		srcWriter.println(""+callbackVar+".onSuccess(new "+cursorClassName+"(cursor));");
		srcWriter.println("}else{");
		srcWriter.println(""+callbackVar+".onSuccess(null);");
		srcWriter.println("}");
		srcWriter.println(""+callbackVar+".setDb(null);");
		
		if (!Environment.isProduction())
		{
			srcWriter.println("}catch (Exception e){");
			srcWriter.println("reportError("+callbackVar+", "+dbVariable+".messages.objectStoreCursorError(e.getMessage()), e);");
			srcWriter.println("}");
		}
		
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});"); 
		//TODO ver possibiliade de aceitar date como chave
		
		srcWriter.println(cursorRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreCursorError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreCursorError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");
		
		srcWriter.println("}");
	}
	
	protected void generateGetKeyCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String retrieveRequestVar)
    {
	    srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");
		
		srcWriter.println(retrieveRequestVar+".onSuccess(new IDBObjectStoreEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectStoreEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		String keyTypeName = getKeyTypeName(objectStoreKeyPath);
		if ((objectStoreKeyPath.length > 1) && (!isEmptyType()))
		{
			srcWriter.println(callbackVar+".onSuccess(fromNativeKey(event.getObjectKey()));");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getStringKey());");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getIntKey());");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getDoubleKey());");
		}
		else if (keyTypeName.equals(Date.class.getCanonicalName()))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getDateKey());");
		}
		else
		{
			srcWriter.println(callbackVar+".onSuccess(event.getObjectKey.cast());");
		}
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println(retrieveRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreGetError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreGetError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("}");
    }

	@Override
	public String getProxyQualifiedName()
	{
		return indexType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		String typeName = indexName.replaceAll("\\W", "_");
		return objectStoreName + "_" + typeName+"_Index";
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
		composerFactory.setSuperclass("IDXIndex<"+getKeyTypeName(objectStoreKeyPath)+","+getKeyTypeName()+","+getTargetObjectClassName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				IDXIndex.class.getCanonicalName(), 
				IDBIndex.class.getCanonicalName(),
				IDBObjectRetrieveRequest.class.getCanonicalName(),
				IDBObjectRetrieveEvent.class.getCanonicalName(),
				IDBObjectStoreEvent.class.getCanonicalName(),
				IDBObjectStoreRequest.class.getCanonicalName(),
				IDBErrorEvent.class.getCanonicalName(),
				IDBIndexCursorRequest.class.getCanonicalName(),
				IDBCursorEvent.class.getCanonicalName(),
				DatabaseRetrieveCallback.class.getCanonicalName(),
				DatabaseCursorCallback.class.getCanonicalName(),
				IDXAbstractDatabase.class.getCanonicalName(),
				KeyRange.class.getCanonicalName(), 
				KeyRangeFactory.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(), 
				CursorDirection.class.getCanonicalName()
		};
		return imports;
	}
	
}
