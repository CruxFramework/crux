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
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class FileReader extends JavaScriptObject
{
	public static enum State
	{
		EMPTY, LOADING, DONE
	}
	
	public static enum Error
	{
		NotFoundError, SecurityError, NotReadableError, EncodingError
	}

	public static interface ReaderStringCallback 
	{
		void onComplete(String result);
	}
	
	public static interface ReaderArrayCallback 
	{
		void onComplete(ArrayBuffer result);
	}

	public static interface ErrorHandler 
	{
		void onError(Error error);
	}

	protected FileReader(){}

	public native final void readAsArrayBuffer(Blob blob, ReaderArrayCallback callback)/*-{
		this.readAsArrayBuffer(blob);
		this.onload = function(e) {
			callback.@org.cruxframework.crux.core.client.file.FileReader.ReaderArrayCallback::onComplete(Lcom/google/gwt/typedarrays/shared/ArrayBuffer;)(e.target.result);
		};
	}-*/;

	public native final void readAsText(Blob blob, String encoding, ReaderStringCallback callback)/*-{
		this.readAsText(blob, encoding);
		this.onload = function(e) {
			callback.@org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback::onComplete(Ljava/lang/String;)(e.target.result);
		};
	}-*/;

	public native final void readAsDataURL(Blob blob, ReaderStringCallback callback)/*-{
		this.readAsDataURL(blob);
		this.onload = function(e) {
			callback.@org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback::onComplete(Ljava/lang/String;)(e.target.result);
		};
	}-*/;
	
	public final native void readAsBinaryString(Blob blob, ReaderStringCallback callback)/*-{
		this.readAsBinaryString(blob);
		this.onload = function(e) {
			callback.@org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback::onComplete(Ljava/lang/String;)(e.target.result);
		};
	}-*/;

	public native final void addErrorHandler(ErrorHandler errorHandler)/*-{
		this.onerror = function(e) {
			@org.cruxframework.crux.core.client.file.FileReader::fireError(Lorg/cruxframework/crux/core/client/file/FileReader$ErrorHandler;Ljava/lang/String;)(errorHandler, e.target.error.name);
		};
	}-*/;

	public native final void abort()/*-{
		this.abort();
	}-*/;
	
	public final State getReadyState()
	{
		switch (readyState())
        {
        	case 1:return State.LOADING;
        	case 2:return State.DONE;
        	default:return State.EMPTY;
        }
	}

	private native int readyState()/*-{
		return this.readyState;
	}-*/;
	
	static void fireError(ErrorHandler callback, String errorName)
	{
		callback.onError(Error.valueOf(errorName));
	}

	public static FileReader createIfSupported()
	{
		if (isSupported())
		{
			return create();
		}
		return null;
	}
	
	public static native boolean isSupported()/*-{
		if ($wnd.FileReader)
		{
			return true;
		}
		return false;	
	}-*/;
	
	private static native FileReader create()/*-{
		return new $wnd.FileReader();
	}-*/;
}

