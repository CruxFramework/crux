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

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.PartialSupport;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class IDBFactory 
{
	private static boolean initialized = false;
	private static JavaScriptObject indexedDBContext;

	public static void init(JavaScriptObject context)
	{
		indexedDBContext = context;
		initialized = true;
	}
	
    protected IDBFactory() {}

    public final static IDBFactory get() 
    {
    	if (!initialized)
    	{
    		init(getDefaultContext());
    	}
    	return create();
    }
    
    private static IDBFactory create()
    {
    	return new IDBFactory();
    };

    public final IDBOpenDBRequest open(String name) 
    {
		return this.open(indexedDBContext, name);
    }

    public final IDBOpenDBRequest open(String name, int version)
    {
    	return this.open(indexedDBContext, name, version);
    }

    public final IDBDeleteDBRequest deleteDatabase(String name)
    {
    	return this.deleteDatabase(indexedDBContext, name);
    }
    
    public final int cmp(JavaScriptObject o1, JavaScriptObject o2)
    {
    	return this.cmp(indexedDBContext, o1, o2);
    }

    public final int cmp(String o1, String o2)
    {
    	return this.cmp(indexedDBContext, o1, o2);
    }

    public final int cmp(int o1, int o2)
    {
    	return this.cmp(indexedDBContext, o1, o2);
    }
    
    public final int cmp(double o1, double o2)
    {
    	return this.cmp(indexedDBContext, o1, o2);
    }

    public final int cmp(Date o1, Date o2)
    {
		return this.cmp(o1.getTime(),o2.getTime());
	};
	
	private native IDBOpenDBRequest open(JavaScriptObject indexedDBContext, String name) /*-{
		return indexedDBContext.open(name);
    }-*/;
    
    private native IDBOpenDBRequest open(JavaScriptObject indexedDBContext, String name, int version) /*-{
		return indexedDBContext.open(name, version);
	}-*/;
    
    private native IDBDeleteDBRequest deleteDatabase(JavaScriptObject indexedDBContext, String name) /*-{
		return indexedDBContext.deleteDatabase(name);
    }-*/;
    
    private native int cmp(JavaScriptObject indexedDBContext, JavaScriptObject o1, JavaScriptObject o2) /*-{
		return indexedDBContext.cmp(o1,o2);
    }-*/;
    
    private native int cmp(JavaScriptObject indexedDBContext, String o1, String o2) /*-{
		return indexedDBContext.cmp(o1,o2);
	}-*/;
    
    private native int cmp(JavaScriptObject indexedDBContext, int o1, int o2) /*-{
		return indexedDBContext.cmp(o1,o2);
	}-*/;
    
    private native int cmp(JavaScriptObject indexedDBContext, double o1, double o2) /*-{
		return indexedDBContext.cmp(o1,o2);
	}-*/;

    private static native JavaScriptObject getDefaultContext()/*-{
	    $wnd.IDBKeyRange = $wnd.IDBKeyRange || $wnd.webkitIDBKeyRange;
	    $wnd.indexedDB = $wnd.indexedDB || $wnd.mozIndexedDB || $wnd.webkitIndexedDB;
		
		return $wnd.indexedDB;
    }-*/;
    
	public static native boolean isSupported()/*-{
	    var IDBKeyRange = $wnd.IDBKeyRange || $wnd.webkitIDBKeyRange;
	    var indexedDB = $wnd.indexedDB || $wnd.mozIndexedDB || $wnd.webkitIndexedDB;
		if (IDBKeyRange && indexedDB) {
			return true;
		}
		return false;
	}-*/;
}

