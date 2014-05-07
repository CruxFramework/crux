/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.rss.feed;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This class must be invoked before any other class in the Google AJAX Feed API
 * is used. It is responsible for downloading and initializing the API. Because
 * the AJAX Feed API is external to the GWT application, the API may not be
 * immediately available, so a callback is used to notify the GWT code that the
 * API is ready for use.
 */
public final class Loader {
  /**
   * A callback interface used to signal state changes in the load process.
   */
  public static interface LoaderCallback {
    public void onError(Throwable t);

    public void onLoad();
  }

  /**
   * The callback to invoke.
   */
  private static LoaderCallback callback;

  /**
   * A reference to the iframe containing the AJAX Feed API.
   */
  private static final Element SANDBOX;

  static {
     SANDBOX = initFrame();
  }
  
  private static Element initFrame()
  {
	  Element iframe = DOM.createIFrame();
	  iframe.setId("LoaderSandbox");
	  DOM.setStyleAttribute(iframe, "height", "0px");
	  DOM.setStyleAttribute(iframe, "width", "0px");
	  UIObject.setVisible(iframe, false);
	  
	  RootPanel.getBodyElement().appendChild(iframe);
	  
	  return iframe;
  }

  /**
   * Indicates whether or not the external JavaScript for the Google AJAX Feed
   * API has been installed into the JSVM.
   * 
   * @return <code>true</code> iff the API is ready for use.
   */
  public static native boolean apiReady() /*-{
   return Boolean($wnd.google_feed && $wnd.google_feed.feeds && $wnd.google_feed.feeds.Feed || false);
   }-*/;

  /**
   * Initialize the Google AJAX Feed API.
   * 
   * @param apiKey The developer's API key
   * @param apiCallback the callback to invoke when the Loader has finished
   */
  public static void init(final String apiKey, LoaderCallback apiCallback) {
    callback = apiCallback;
    registerSandboxCallback();

    setupDocument(getDocument(SANDBOX), apiKey);
  }

  /**
   * This is called from the sandbox iframe with the top-level google object.
   */
  static void ajaxFeedError(JavaScriptObject ex) {
    closeDocument(getDocument(SANDBOX));
    callback.onError(new RuntimeException("Unable to initialize ajax feed api "
        + ex.toString()));
  }

  /**
   * This is called from the sandbox iframe with the top-level google object.
   */
  static native void ajaxFeedLoad(JavaScriptObject google) /*-{
   $wnd.google_feed = google;
   @org.cruxframework.crux.widgets.client.rss.feed.Loader::startTimer()();
   }-*/;

  static void startTimer() {
    Timer t = new Timer() {
      public void run() {
        if (apiReady()) {
          cancel();
          closeDocument(getDocument(SANDBOX));
          callback.onLoad();
        }
      }
    };
    t.scheduleRepeating(10);
  }

  /**
   * Allow a Java exception to be thrown by a JSNI function.
   */
  static void throwRuntimeException(String msg) {
    throw new RuntimeException(msg);
  }

  /**
   * Because we're using document.write() to construct the sandbox iframe, we
   * must call document.close() or some browsers will contine to display the
   * spinner. It's also possible that a browser that does not use progressive
   * rendering would not evaluate the contents of the document until it's
   * closed.
   */
  private static native void closeDocument(Element doc) /*-{
   doc.close();
   }-*/;

  /**
   * Extract the document Element from a Frame.
   */
  private static native Element getDocument(Element elt) /*-{
   
   // FF || IE
   var doc = elt.contentDocument || elt.contentWindow;
   // Opera sometimes returns the window
   if (doc.document) {
   doc = doc.document;
   }
   
   if (!doc) {
   @org.cruxframework.crux.widgets.client.rss.feed.Loader::throwRuntimeException(Ljava/lang/String;)("Unable to obtain sandbox");
   }
   
   return doc;
   }-*/;

  private static native void openDocument(Element doc) /*-{
   doc.open();
   }-*/;

  /**
   * Registers a function that will be called from within the sandbox iframe to
   * indicate that the API download process has started.
   * 
   * @see #ajaxFeedLoad(JavaScriptObject)
   */
  private static native void registerSandboxCallback() /*-{
   $wnd.AjaxFeedError = @org.cruxframework.crux.widgets.client.rss.feed.Loader::ajaxFeedError(Lcom/google/gwt/core/client/JavaScriptObject;);
   $wnd.AjaxFeedLoad = @org.cruxframework.crux.widgets.client.rss.feed.Loader::ajaxFeedLoad(Lcom/google/gwt/core/client/JavaScriptObject;);
   }-*/;

  private static void setupDocument(Element doc, String apiKey) {
    String docContents = Resources.bootstrap(Window.Location.getProtocol().equals("https"));
    docContents = docContents.replaceAll("KEY", (apiKey != null) ? "?key=" + apiKey : "");

    openDocument(doc);

    // IE can't handle having the document written as a single string
    String[] lines = docContents.split("\n");
    for (int i = 0 ; i < lines.length; i++) {
      write(doc, lines[i]);
    }
    
    // Don't close the document because the loader API will also perform
    // document.write calls, which would re-open and thus destoy the document
  }

  private static native void write(Element doc, String data) /*-{
   doc.write(data);
   }-*/;

  private Loader() {
  }
}
