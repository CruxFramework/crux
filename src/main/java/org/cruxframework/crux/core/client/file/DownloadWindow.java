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

/**
 * @author Samuel Almeida Cardoso
 *
 */
@PartialSupport
public class DownloadWindow extends JavaScriptObject
{
	protected DownloadWindow(){}
	
	private static DownloadWindow instance;
	
	private static class NavigatorWindow extends DownloadWindow 
	{
		public static native boolean isSupported()/*-{
			if ($wnd.navigator.saveBlob || $wnd.navigator.msSaveBlob || $wnd.navigator.mozSaveBlob || $wnd.navigator.webkitSaveBlob)
			{
				return true;
			}
			return false;
		}-*/;
		
		protected static native NavigatorWindow getInstance()/*-{
			return $wnd.navigator.saveBlob || $wnd.navigator.msSaveBlob || $wnd.navigator.mozSaveBlob || $wnd.navigator.webkitSaveBlob;
		}-*/;
		
		public native final void openSaveAsWindow(Blob blob, String name)
		/*-{
			var saveBlobVar = navigator.saveBlob || navigator.msSaveBlob || navigator.mozSaveBlob || navigator.webkitSaveBlob;
			if(saveBlobVar)
			{
			 	saveBlobVar.saveBlob(blob, name);
			 	return;
			}
		}-*/;
	}
	
	private static class SaveWindow extends DownloadWindow 
	{
		public static native boolean isSupported()/*-{
			if ($wnd.saveAs || $wnd.webkitSaveAs || $wnd.mozSaveAs || $wnd.msSaveAs)
			{
				return true;
			}
			return false;
		}-*/;
		
		protected static native NavigatorWindow getInstance()/*-{
			return $wnd.saveAs || $wnd.webkitSaveAs || $wnd.mozSaveAs || $wnd.msSaveAs;
		}-*/;
		
		public native final void openSaveAsWindow(Blob blob, String name)
		/*-{
			var saveWinVar = $wnd.saveAs || $wnd.webkitSaveAs || $wnd.mozSaveAs || $wnd.msSaveAs;
			if(saveWinVar)
			{
				saveWinVar.saveAs(blob, name);
				return;
			}
		}-*/;
	}
	
	public static boolean isSupported()
	{
		return NavigatorWindow.isSupported() || SaveWindow.isSupported();
	}
	
	public static DownloadWindow createIfSupported()
	{
		if (isSupported())
		{
			if(instance != null)
			{
				instance = new DownloadWindow();
			}
			return instance;
		}
		return null;
	}
	
	public void openSaveAsWindow(Blob blob, String name)
	{
		if(NavigatorWindow.isSupported())
		{
			NavigatorWindow.getInstance().openSaveAsWindow(blob, name);
			return;
		}
		
		if(SaveWindow.isSupported())
		{
			SaveWindow.getInstance().openSaveAsWindow(blob, name);
			return;
		}
	}
}

