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

public class Resources
{
	public static String bootstrap(boolean useHTTPS)
	{
		return "<html>\n<head>\n"+
			   "<script type=\"text/javascript\" src=\"" + (useHTTPS ? "https" : "http") + "://www.google.com/jsapiKEY\"></script>\n"+
			   "<script type=\"text/javascript\">\n"+
			   "try {"+
			   "google.load('feeds', '1', {'nocss' : true});"+
			   "window.parent.AjaxFeedLoad(google);"+
			   "} catch (ex) {"+
			   "window.parent.AjaxFeedError(ex);"+
			   "}\n"+
			   "</script>\n"+
			   "</head>\n<body>\n</body>\n</html>\n";
	}
}
