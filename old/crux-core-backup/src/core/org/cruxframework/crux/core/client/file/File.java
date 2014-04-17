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

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class File extends Blob
{
	protected File(){}
	
	public final native String getName()/*-{
		return this.name;
	}-*/;
	
	public final native JsDate getLastModifiedDate()/*-{
		return this.lastModifiedDate;
	}-*/;
	
	public static native boolean isSupported()/*-{
		if ($wnd.File)
		{
			return true;
		}
		return false;
	}-*/;
	
	public static File createIfSupported(JsArrayMixed body, String type, String fileName)
	{
		if (isSupported())
		{
			return create(Blob.createIfSupported(body, type), fileName);
		}
		return null;
	}
	
	public static File createIfSupported(Blob blob, String fileName)
	{
		if (isSupported())
		{
			return create(blob, fileName);
		}
		return null;
	}
	
	public static File createIfSupported(ArrayBuffer body, String type, String fileName)
	{
		if (isSupported())
		{
			return create(Blob.createIfSupported(body, type), fileName);
		}
		return null;
	}
	
	public static File createIfSupported(ArrayBufferView body, String type, String fileName)
	{
		if (isSupported())
		{
			return create(Blob.createIfSupported(body, type), fileName);
		}
		return null;
	}
	
	public static File createIfSupported(String body, String type, String fileName)
	{
		if (isSupported())
		{
			return create(Blob.createIfSupported(body, type), fileName);
		}
		return null;
	}

	protected static native File create(Blob blob, String fileName)/*-{
		return new $wnd.File(blob, fileName);
	}-*/;
}
