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
package org.cruxframework.crux.core.client.xhr;

import org.cruxframework.crux.core.client.file.Blob;

import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class XMLHttpRequest2 extends XMLHttpRequest
{
	protected XMLHttpRequest2(){}

	public static XMLHttpRequest2 create()
	{
		return XMLHttpRequest.create().cast();
	}

	public final native void setOnProgressHandler(ProgressHandler handler) /*-{
	    this.upload.onprogress = function(e) {
	    	if (e.lengthComputable){
	      		handler.@org.cruxframework.crux.core.client.xhr.XMLHttpRequest2.ProgressHandler::onProgress(DD)(e.loaded, e.total);
	      	}
	    };
	}-*/;
	
	public final native void send(String paramName, Blob file) /*-{
	    var fd = new FormData();
	    fd.append(paramName, file);
	    this.send(fd);
	}-*/;
	
	public static interface ProgressHandler
	{
		void onProgress(double loaded, double total);
	}
	
	public static native boolean isSupported()/*-{
		try {
		    var xhr = new $wnd.XMLHttpRequest();

		    if ('upload' in xhr) {
		    	return true;
		    } 
		} catch (e) {}		
		return false;
	}-*/;
}
