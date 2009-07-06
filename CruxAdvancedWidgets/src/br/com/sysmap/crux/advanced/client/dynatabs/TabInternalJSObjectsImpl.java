/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.advanced.client.dynatabs;

import br.com.sysmap.crux.advanced.client.js.JSWindow;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
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