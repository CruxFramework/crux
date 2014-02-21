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
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class IDBKeyRange extends JavaScriptObject
{
	protected IDBKeyRange() {}

	public final native JsArrayMixed getLowerObject() /*-{
		return this.lower;
	}-*/;

	public final native String getLowerString() /*-{
		return this.lower;
	}-*/;

	public final native int getLowerInt() /*-{
		return this.lower;
	}-*/;

	public final Date getLowerDate()
	{
		return new Date((long)getLowerDouble());
	}

	public final  native double getLowerDouble() /*-{
		return this.lower;
	}-*/;

	public final native JsArrayMixed getUpperObject() /*-{
		return this.upper;
	}-*/;

	public final native String getUpperString() /*-{
		return this.upper;
	}-*/;

	public final native int getUpperInt() /*-{
		return this.upper;
	}-*/;

	public final Date getUpperDate()
	{
		return new Date((long)getUpperDouble());
	}

	public final  native double getUpperDouble() /*-{
		return this.upper;
	}-*/;

	public final native boolean isLowerOpen() /*-{
		return this.lowerOpen;
	}-*/;

	public final native boolean isUpperOpen() /*-{
		return this.upperOpen;
	}-*/;

	public final native static IDBKeyRange only(JsArrayMixed key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final static IDBKeyRange only(Date key)
	{
		return only(key.getTime());
	}

	public final  native static IDBKeyRange only(double key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange only(int key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange only(String key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange lowerBound(JsArrayMixed key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final static IDBKeyRange lowerBound(Date key)
	{
		return lowerBound(key.getTime());
	}

	public final  native static IDBKeyRange lowerBound(double key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;

	public final native static IDBKeyRange lowerBound(int key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final native static IDBKeyRange lowerBound(String key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final native static IDBKeyRange lowerBound(JsArrayMixed key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;
	
	public final static IDBKeyRange lowerBound(Date key, boolean open)
	{
		return lowerBound(key.getTime(), open);
	}

	public final  native static IDBKeyRange lowerBound(double key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key, open);
	}-*/;

	public final native static IDBKeyRange lowerBound(int key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;
	
	public final native static IDBKeyRange lowerBound(String key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;

	public final native static IDBKeyRange upperBound(JsArrayMixed key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final static IDBKeyRange upperBound(Date key, boolean open)
	{
		return upperBound(key.getTime(), open);
	}

	public final  native static IDBKeyRange upperBound(double key, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(key, open);
	}-*/;

	public final static IDBKeyRange upperBound(Date key)
	{
		return upperBound(key.getTime());
	}

	public final  native static IDBKeyRange upperBound(double key) /*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;

	public final native static IDBKeyRange upperBound(int key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final native static IDBKeyRange upperBound(String key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final native static IDBKeyRange upperBound(JsArrayMixed key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;
	
	public final native static IDBKeyRange upperBound(int key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;
	
	public final native static IDBKeyRange upperBound(String key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;

	public final native static IDBKeyRange bound(JsArrayMixed startKey, JsArrayMixed endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(int startKey, int endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final static IDBKeyRange bound(Date startKey, Date endKey, boolean startOpen, boolean endOpen)
	{
		return bound(startKey.getTime(), startKey.getTime(), startOpen, endOpen);
	}

	public final  native static IDBKeyRange bound(double startKey, double endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(String startKey, String endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(JsArrayMixed startKey, JsArrayMixed endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
	
	public final static IDBKeyRange bound(Date startKey, Date endKey)
	{
		return bound(startKey.getTime(), startKey.getTime());
	}

	public final native static IDBKeyRange bound(double startKey, double endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;

	public final native static IDBKeyRange bound(int startKey, int endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
	
	public final native static IDBKeyRange bound(String startKey, String endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
}