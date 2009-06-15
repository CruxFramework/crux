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
package br.com.sysmap.crux.advanced.client.filter;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@SuppressWarnings("unchecked")
public class Filter extends SuggestBox implements HasFocusHandlers, HasBlurHandlers
{
	private static final String DEFAULT_STYLE_NAME = "crux-Filter";

	/**
	 * @param filterable
	 */
	public Filter()
	{
		super (new FilterSuggestOracle());
		addSelectionHandler(createSelectionHandler());
		setStyleName(DEFAULT_STYLE_NAME);
		addFocusHandler(createFocusHandler());
		addBlurHandler(createBlurHandler());
	}

	/**
	 * @return
	 */
	private FocusHandler createFocusHandler()
	{
		return new FocusHandler()
		{
			public void onFocus(FocusEvent event)
			{
				removeStyleDependentName("focused");
				addStyleDependentName("focused");
			}			
		};
	}

	/**
	 * @return
	 */
	private BlurHandler createBlurHandler()
	{
		final Filter filter = this;
		
		return new BlurHandler()
		{
			public void onBlur(BlurEvent event)
			{
				if(filter.getValue().trim().length() == 0)
				{
					removeStyleDependentName("focused");
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionHandler<Suggestion> createSelectionHandler()
	{
		return new SelectionHandler<Suggestion>()
		{
			public void onSelection(SelectionEvent<Suggestion> event)
			{
				Suggestion suggestion = event.getSelectedItem();
				FilterSuggestion filterSuggestion = (FilterSuggestion) suggestion;
				getFilterSuggestOracle().getFilterable().onSelectItem(filterSuggestion.getValue());
			}			
		};		
	}

	/**
	 * @param filterable
	 * @return
	 */
	public void setFilterable(Filterable<?> filterable)
	{
		FilterSuggestOracle oracle = getFilterSuggestOracle();
		if(oracle != null && filterable != null)
		{
			oracle.setFilterable(filterable);
		}		
	}
	
	/**
	 * @return
	 */
	protected FilterSuggestOracle getFilterSuggestOracle()
	{
		return (FilterSuggestOracle) getSuggestOracle();
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasFocusHandlers#addFocusHandler(com.google.gwt.event.dom.client.FocusHandler)
	 */
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}
	
	/**
	 * @see com.google.gwt.event.dom.client.HasBlurHandlers#addBlurHandler(com.google.gwt.event.dom.client.BlurHandler)
	 */
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}
}