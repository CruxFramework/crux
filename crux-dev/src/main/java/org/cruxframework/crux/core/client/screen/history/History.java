/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen.history;

import org.cruxframework.crux.core.client.collection.FastList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Implementation of the HTML5 History API
 * @author Thiago da Rosa de Bustamante
 */
public class History 
{
	private static boolean handlerInitialized = false;
	private static FastList<HistoryHandler> historyHandlers;

	/**
	 * Programmatic equivalent to the user pressing the browser's 'back' button.
	 */
	public static native void back() /*-{
       $wnd.history.back(); 
    }-*/;

	/**
	 * Programmatic equivalent to the user pressing the browser's 'forward'
	 * button.
	 */
	public static native void forward() /*-{
       $wnd.history.forward();
    }-*/;

	/**
	 * Goes back or forward the specified number of steps in the joint session history.
	 * A zero delta will reload the current page.
	 * If the delta is out of range, does nothing.
	 * @param delta number of steps
	 */
	public static native void go(int delta) /*-{
       $wnd.history.go(delta); 
    }-*/;

	/**
	 * Returns the number of entries in the joint session history.
	 * @return history length
	 */
	public static native int length()/*-{
       return ($wnd.history.length?$wnd.history.length:0); 
    }-*/;

	/**
	 * Adds a state object entry to the history.
	 * @param data Some structured data, such as settings or content, assigned to the history item.
	 * @param title The name of the item in the history drop-down shown by the browser’s back and forward buttons.
	 * @param url The URL to this state that should be displayed in the address bar.
	 */
	public static native void pushState(JavaScriptObject data, String title, String url)/*-{
       $wnd.history.pushState({'data': data, 'url': url?url:'', 'title': title?title:''}, title, url); 
    }-*/;

	/**
	 * Adds a state object entry to the history.
	 * @param data Some structured data, such as settings or content, assigned to the history item.
	 * @param title The name of the item in the history drop-down shown by the browser’s back and forward buttons.
	 */
	public static native void pushState(JavaScriptObject data, String title)/*-{
       $wnd.history.pushState({'data': data, 'url': '', 'title': title?title:''}, title); 
    }-*/;

	/**
	 * Updates the state object, title of the current entry in the history.
	 * @param data Some structured data, such as settings or content, assigned to the history item.
	 * @param title The name of the item in the history drop-down shown by the browser’s back and forward buttons.
	 */
	public static native void replaceState(JavaScriptObject data, String title)/*-{
       $wnd.history.replaceState({'data': data, 'url': '', 'title': title?title:''}, title); 
    }-*/;

	/**
	 * Updates the state object, title and the URL of the current entry in the history.
	 * @param data Some structured data, such as settings or content, assigned to the history item.
	 * @param title The name of the item in the history drop-down shown by the browser’s back and forward buttons.
	 * @param url The URL to this state that should be displayed in the address bar.
	 */
	public static native void replaceState(JavaScriptObject data, String title, String url)/*-{
       $wnd.history.replaceState({'data': data, 'url': url?url:'', 'title': title?title:''}, title, url); 
    }-*/;

	/**
	 * Some structured data, such as settings or content, assigned to the history item
	 * @return item state data
	 */
	public static native <T extends JavaScriptObject> T state()/*-{
		return ($wnd.history.state?$wnd.history.state.data:null);
    }-*/;

	/**
	 * The URL to this state that should be displayed in the address bar
	 * @return item state url
	 */
	public static native String url()/*-{
		return ($wnd.history.state?$wnd.history.state.url:null);
    }-*/;

	/**
	 * The name of the item in the history drop-down shown by the browser’s back and forward buttons.
	 * @return item state url
	 */
	public static native String title()/*-{
		return ($wnd.history.state?$wnd.history.state.title:null);
    }-*/;

	public static HandlerRegistration addHistoryHandler(final HistoryHandler historyHandler)
	{
		if (!handlerInitialized)
		{
			initWindowHistoryHandler();
			historyHandlers = new FastList<HistoryHandler>();
			handlerInitialized = true;
		}
		historyHandlers.add(historyHandler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = historyHandlers.indexOf(historyHandler);
				if (index >= 0)
				{
					historyHandlers.remove(index);
				}
			}
		};
	}

	private static void onStateChanged(JavaScriptObject data, String url, String title)
	{
		HistoryEvent event = new HistoryEvent(data, url, title);
		for (int i=0; i<historyHandlers.size(); i++)
		{
			historyHandlers.get(i).onStateChange(event);
		}
	}
	
	private static native void initWindowHistoryHandler() /*-{
	   var oldOnPopstate =  $wnd.onpopstate;
	   $wnd.onpopstate = function(evt) {
	      try 
	      {
	      	 if (evt && evt.state)
	      	 {
	         	@org.cruxframework.crux.core.client.screen.history.History::onStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;)(evt.state.data, evt.state.url, evt.state.title);
	         }
	         else
	         {
	         	@org.cruxframework.crux.core.client.screen.history.History::onStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;)(null, $wnd.location.pathname, $doc.title);
	         }
	      } 
	      finally 
	      {
	         oldOnPopstate && oldOnPopstate(evt);
	      }
	   };
	}-*/;
}
