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
package org.cruxframework.crux.core.client.event.paste;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 */
//TODO Remove this as long as https://github.com/gwtproject/gwt/issues/4549 will be closed
//@Deprecated
@Legacy
public class PasteEventSourceRegister
{
	public static native void registerPasteEventSource(HasPasteHandlers source, Element element)/*-{
		element.addEventListener('input', function(e) {
			@org.cruxframework.crux.core.client.event.paste.PasteEvent::fire(Lorg/cruxframework/crux/core/client/event/paste/HasPasteHandlers;)(source);
		}, false);
	}-*/;
}