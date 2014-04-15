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

import com.google.gwt.dom.client.PartialSupport;

/**
 * @author Samuel Almeida Cardoso
 *
 */
@PartialSupport
public class DownloadWindow
{
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
		
		public native final void openSaveAsWindow(Blob blob, String name)
		/*-{
			if(!navigator.saveBlob)
			{
				navigator.saveBlob = navigator.saveBlob || navigator.msSaveBlob || navigator.mozSaveBlob || navigator.webkitSaveBlob;
			}
		 	navigator.saveBlob(blob, name);
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
		
		public native final void openSaveAsWindow(Blob blob, String name)
		/*-{
			if(!$wnd.saveAs)
			{
				$wnd.saveAs = $wnd.saveAs || $wnd.webkitSaveAs || $wnd.mozSaveAs || $wnd.msSaveAs;
			}
			$wnd.saveAs(blob, name);
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
			if(instance == null)
			{
				instance = SaveWindow.isSupported() ? new SaveWindow() : new NavigatorWindow();
			}
			return instance;
		}
		return null;
	}
	
	public void openSaveAsWindow(Blob blob, String name)
	{
		instance.openSaveAsWindow(blob,name);
	}
}

