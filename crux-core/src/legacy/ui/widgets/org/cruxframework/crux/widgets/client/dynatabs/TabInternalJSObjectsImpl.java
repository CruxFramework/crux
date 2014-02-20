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
package org.cruxframework.crux.widgets.client.dynatabs;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.JSWindow;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class TabInternalJSObjectsImpl extends TabInternalJSObjects
{
	@Override
	public native Document getTabDocument(IFrameElement elem)/*-{
		return elem.contentDocument;
	}-*/;

	@Override
	protected native JSWindow getTabWindow(IFrameElement elem)/*-{
		return elem.contentDocument.defaultView;
	}-*/;
}