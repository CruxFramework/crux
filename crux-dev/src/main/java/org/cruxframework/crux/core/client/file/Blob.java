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
package org.cruxframework.crux.core.client.file;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class Blob extends JavaScriptObject
{
	protected Blob(){}
	
	public final native double getSize()/*-{
		return this.size;
	}-*/;

	public final native String getType()/*-{
		return this.type;
	}-*/;
	
	public final native void close()/*-{
		this.close();
	}-*/;
	
	public final native Blob slice()/*-{
		return this.slice();
	}-*/;

	public final native Blob slice(int start)/*-{
		return this.slice(start);
	}-*/;

	public final native Blob slice(int start, int end)/*-{
		return this.slice(start, end);
	}-*/;

	public final native Blob slice(int start, int end, String contentType)/*-{
		return this.slice(start, end, contentType);
	}-*/;

	public static native boolean isSupported()/*-{
		if ($wnd.Blob)
		{
			return true;
		}
		return false;	
	}-*/;

	public static Blob createIfSupported(JsArrayMixed body, String type)
	{
		if (isSupported())
		{
			return create(body, type);
		}
		return null;
	}
	
	public static Blob createIfSupported(ArrayBuffer body, String type)
	{
		if (isSupported())
		{
			return create(body, type);
		}
		return null;
	}
	
	public static Blob createIfSupported(ArrayBufferView body, String type)
	{
		if (isSupported())
		{
			return create(body, type);
		}
		return null;
	}
	
	public static Blob createIfSupported(Blob body, String type)
	{
		if (isSupported())
		{
			return create(body, type);
		}
		return null;
	}
	
	public static Blob createIfSupported(String body, String type)
	{
		if (isSupported())
		{
			return create(body, type);
		}
		return null;
	}
	
	protected static native Blob create(JsArrayMixed b, String t)/*-{
		return new $wnd.Blob(b, {type: t});
	}-*/;

	protected static native Blob create(ArrayBuffer b, String t)/*-{
		return new $wnd.Blob([b], {type: t});
	}-*/;

	protected static native Blob create(ArrayBufferView b, String t)/*-{
		return new $wnd.Blob([b], {type: t});
	}-*/;

	protected static native Blob create(Blob b, String t)/*-{
		return new $wnd.Blob([b], {type: t});
	}-*/;

	protected static native Blob create(String b, String t)/*-{
		return new $wnd.Blob([b], {type: t});
	}-*/;
}
