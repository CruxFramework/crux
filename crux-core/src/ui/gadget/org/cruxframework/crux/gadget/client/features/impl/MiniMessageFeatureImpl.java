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
package org.cruxframework.crux.gadget.client.features.impl;

import org.cruxframework.crux.gadget.client.features.MiniMessageFactory;
import org.cruxframework.crux.gadget.client.features.MiniMessageFeature;

import com.google.gwt.user.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MiniMessageFeatureImpl implements MiniMessageFeature
{
	private MiniMessageFeatureImpl() {}

	public native MiniMessageFactory getMessageFactory()/*-{
	    return new $wnd.gadgets.MiniMessage();
    }-*/;

	public native MiniMessageFactory getMessageFactory(String moduleId)/*-{
	    return new $wnd.gadgets.MiniMessage(moduleId);
    }-*/;

	public native MiniMessageFactory getMessageFactory(String moduleId, Element htmlContainer)/*-{
	    return new $wnd.gadgets.MiniMessage(moduleId, htmlContainer);
    }-*/;
}
