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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Factory for SuggestBox widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class SuggestBoxFactory extends CompositeFactory<SuggestBox>
{
	@Override
	protected void processAttributes(SuggestBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.length() == 1)
		{
			widget.setAccessKey(accessKey.charAt(0));
		}
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.trim().length() > 0)
		{
			widget.setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		
		String autoSelectEnabled = element.getAttribute("_autoSelectEnabled");
		if (autoSelectEnabled != null && autoSelectEnabled.trim().length() > 0)
		{
			widget.setAutoSelectEnabled(Boolean.parseBoolean(autoSelectEnabled));
		}
		
		String focus = element.getAttribute("_focus");
		if (focus != null && focus.trim().length() > 0)
		{
			widget.setFocus(Boolean.parseBoolean(focus));
		}
		
		String limit = element.getAttribute("_limit");
		if (limit != null && limit.trim().length() > 0)
		{
			widget.setLimit(Integer.parseInt(limit));
		}
		
		String popupStyleName = element.getAttribute("_popupStyleName");
		if (popupStyleName != null && popupStyleName.trim().length() > 0)
		{
			widget.setPopupStyleName(popupStyleName);
		}
		
		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.length() > 0)
		{
			widget.setTabIndex(Integer.parseInt(tabIndex));
		}
		
		String text = element.getAttribute("_text");
		if (text != null && text.length() > 0)
		{
			widget.setText(text);
		}
		
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			widget.setValue(value);
		}
	}
	
	@Override
	protected SuggestBox instantiateWidget(Element element, String widgetId) 
	{
		Event eventLoadOracle = EvtBind.getWidgetEvent(element, EventFactory.EVENT_LOAD_ORACLE);
		
		if (eventLoadOracle != null)
		{
			LoadOracleEvent<SuggestBox> loadOracleEvent = new LoadOracleEvent<SuggestBox>(widgetId);
			SuggestOracle oracle = (SuggestOracle) EventFactory.callEvent(eventLoadOracle, loadOracleEvent);
			SuggestBox ret = new SuggestBox(oracle);
			loadOracleEvent.setSource(ret);
			return ret;
		}
		
		return new SuggestBox();
	}
	
	@Override
	protected void processEvents(SuggestBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		ChangeEvtBind.bindValueEvent(element, widget);
		SelectionEvtBind.bindEvent(element, widget);
		KeyEvtBind.bindEvents(element, widget);		
	}
}
