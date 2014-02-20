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

public class FeedApi extends JavaScriptObject 
{
	protected FeedApi() {}
	
	public static native FeedApi create(String feedUrl)/*-{
	   var feed = new $wnd.google_feed.feeds.Feed(feedUrl);
	   feed.setResultFormat($wnd.google_feed.feeds.Feed.JSON_FORMAT);
	   return feed;
    }-*/;
  
    public final native void load(FeedCallback callback)/*-{
  	   this.load(function(result){
  	      if (result.error){
  	         callback.@org.cruxframework.crux.widgets.client.rss.feed.FeedCallback::onError(Lorg/cruxframework/crux/widgets/client/rss/feed/Error;)(result.error);
  	      }else{
  	         callback.@org.cruxframework.crux.widgets.client.rss.feed.FeedCallback::onLoad(Lorg/cruxframework/crux/widgets/client/rss/feed/Feed;)(result.feed);
  	      }
  	   });
    }-*/;
  
    public final native void setNumEntries(int entries)/*-{
       this.setNumEntries(entries);
    }-*/;
  
    public final native void includeHistoricalEntries()/*-{
	   this.includeHistoricalEntries();
    }-*/; 
    
    public final native void findFeeds(String query, SearchFeedCallback callback)/*-{
  	   $wnd.google_feed.feeds.findFeeds(query, function(result){
  	      if (result.error){
  	         callback.@org.cruxframework.crux.widgets.client.rss.feed.SearchFeedCallback::onError(Lorg/cruxframework/crux/widgets/client/rss/feed/Error;)(result.error);
  	      }else{
  	         callback.@org.cruxframework.crux.widgets.client.rss.feed.SearchFeedCallback::onSearchComplete(Lcom/google/gwt/core/client/JsArray;)(result.entries);
  	      }
  	   });
    }-*/;
}
