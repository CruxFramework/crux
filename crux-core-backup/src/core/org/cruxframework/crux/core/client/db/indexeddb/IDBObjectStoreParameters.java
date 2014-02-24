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
package org.cruxframework.crux.core.client.db.indexeddb;

import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBObjectStoreParameters extends JavaScriptObject 
{
    protected IDBObjectStoreParameters() {}
    
    public final native void setAutoIncrement(boolean value) /*-{
            this.autoIncrement = value;
    }-*/;
    
    public final native void setKeyPath(String keyPath) /*-{
            this.keyPath = keyPath;
    }-*/;
    
    public final void setKeyPath(String[] keyPath)
    {
    	setKeyPath(JsUtils.toJsArray(keyPath));
    }
    
    private native void setKeyPath(JsArrayString keyPath) /*-{
	    this.keyPath = keyPath;
	}-*/;

    public static IDBObjectStoreParameters create() 
    {
    	IDBObjectStoreParameters res = JavaScriptObject.createObject().cast();
    	res.setAutoIncrement(false);
    	return res;
    }

    public static IDBObjectStoreParameters create(String keyPath,boolean autoIncrement) 
    {
    	IDBObjectStoreParameters res = JavaScriptObject.createObject().cast();
    	res.setKeyPath(keyPath);
    	res.setAutoIncrement(autoIncrement);
    	return res;
    }

    public static IDBObjectStoreParameters create(String[] keyPath,boolean autoIncrement) 
    {
    	IDBObjectStoreParameters res = JavaScriptObject.createObject().cast();
    	res.setKeyPath(keyPath);
    	res.setAutoIncrement(autoIncrement);
    	return res;
    }
}