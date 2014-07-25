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

import java.util.Date;

import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;
import org.cruxframework.crux.core.rebind.database.AbstractKeyValueProxyCreator;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class IDBAbstractKeyValueProxyCreator extends AbstractKeyValueProxyCreator
{

	public IDBAbstractKeyValueProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
	}

	
	protected void generateGetKeyRangeFactoryMethod(SourcePrinter srcWriter, String parentName)
    {
		srcWriter.println("public KeyRangeFactory<"+getKeyTypeName()+"> getKeyRangeFactory(){");
		String keyRangeFatoryClassName = new IDBKeyRangeFactoryProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, parentName).create();
		srcWriter.println("return (KeyRangeFactory<"+getKeyTypeName()+">) new "+keyRangeFatoryClassName+"();");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String retrieveRequestVar)
    {		
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");

		srcWriter.println(retrieveRequestVar+".onSuccess(new IDBObjectRetrieveEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectRetrieveEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");

		if (!Environment.isProduction())
		{
			srcWriter.println("try{");
		}

		if (isEmptyType())
		{
			srcWriter.println(""+callbackVar+".onSuccess(event.getObject());");
		}
		else
		{
			srcWriter.println("if (event.getObject() != null){");
			srcWriter.println(""+callbackVar+".onSuccess("+serializerVariable+".decode(new JSONObject(event.getObject())));");
			srcWriter.println("}else{");
			srcWriter.println(""+callbackVar+".onSuccess(null);");
			srcWriter.println("}");
		}
		srcWriter.println(""+callbackVar+".setDb(null);");

		if (!Environment.isProduction())
		{
			srcWriter.println("}catch (Exception e){");
			srcWriter.println("reportError("+callbackVar+", "+dbVariable+".messages.objectStoreGetError(e.getMessage()), e);");
			srcWriter.println("}");
		}

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

	protected void generateWriteCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String writeRequestVar)
    {		
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");

		srcWriter.println(writeRequestVar+".onSuccess(new IDBObjectStoreEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectStoreEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");

		if (!Environment.isProduction())
		{
			srcWriter.println("try{");
		}

		String keyTypeName = getKeyTypeName();
		if (hasCompositeKey())
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
			srcWriter.println(callbackVar+".onSuccess(event.getObjectKey().cast());");
		}
		
		srcWriter.println(""+callbackVar+".setDb(null);");

		if (!Environment.isProduction())
		{
			srcWriter.println("}catch (Exception e){");
			srcWriter.println("reportError("+callbackVar+", "+dbVariable+".messages.objectStoreWriteError(e.getMessage()), e);");
			srcWriter.println("}");
		}
		
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println(writeRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreWriteError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreWriteError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("}");
    }
	
	protected void generateDeleteCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String deleteRequestVar)
    {	
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");

		srcWriter.println(deleteRequestVar+".onSuccess(new IDBObjectDeleteEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectDeleteEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");

		if (!Environment.isProduction())
		{
			srcWriter.println("try{");
		}
		
		srcWriter.println(callbackVar+".onSuccess();");
		srcWriter.println(""+callbackVar+".setDb(null);");

		if (!Environment.isProduction())
		{
			srcWriter.println("}catch (Exception e){");
			srcWriter.println("reportError("+callbackVar+", "+dbVariable+".messages.objectStoreDeleteError(e.getMessage()), e);");
			srcWriter.println("}");
		}
		
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println(deleteRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreDeleteError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreDeleteError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("}");
    }
	
	protected void generateCursorHandlers(SourcePrinter srcWriter, String callbackVar, String dbVariable, String cursorRequestVar, String cursorName)
	{
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");
		
		srcWriter.println(cursorRequestVar+".onSuccess(new IDBCursorEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBCursorEvent event){");
		String cursorClassName = new IDBCursorProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, cursorName).create();
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
	
	protected void generateGetNativeArrayKeyMethod(SourcePrinter srcWriter, String idbCursorVariable)
    {
		if (keyPath.length <= 1)
		{
			srcWriter.println("private native "+JsArrayMixed.class.getCanonicalName()+" createKeyArray(IDBCursor cursor)/*-{");
			srcWriter.println("return [cursor.key];");
			srcWriter.println("}-*/;");
			srcWriter.println();
		}

	    srcWriter.println("public JsArrayMixed getNativeArrayKey(){");
		if (keyPath.length > 1)
		{
			srcWriter.println("return "+idbCursorVariable+".getObjectKey();");
		}
		else 
		{
			srcWriter.println("return createKeyArray("+idbCursorVariable+");");
		}
		srcWriter.println("}");
    }
}
