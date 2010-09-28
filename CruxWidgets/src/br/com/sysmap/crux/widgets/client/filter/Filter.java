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
package br.com.sysmap.crux.widgets.client.filter;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
@SuppressWarnings("unchecked")
public class Filter extends SuggestBox
{
	private static final String DEFAULT_STYLE_NAME = "crux-Filter";
	private String initialText = null;

	/**
	 * Default constructor
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
				if(initialText != null && initialText.equals(getText()))
				{
					getTextBox().setText("");
					removeStyleDependentName("focused");
					addStyleDependentName("focused");
				}
			}			
		};
	}

	/**
	 * @return
	 */
	private BlurHandler createBlurHandler()
	{
		return new BlurHandler()
		{
			public void onBlur(BlurEvent event)
			{
				if(initialText != null && (getText() == null || getText().length() == 0))
				{
					getTextBox().setText(initialText);
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
	private HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}
	
	/**
	 * @see com.google.gwt.event.dom.client.HasBlurHandlers#addBlurHandler(com.google.gwt.event.dom.client.BlurHandler)
	 */
	private HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}
	
	@Override
	public void setText(String text)
	{
		if(this.initialText == null)
		{
			this.initialText = text;
		}
		
		super.setText(text);
	}
}