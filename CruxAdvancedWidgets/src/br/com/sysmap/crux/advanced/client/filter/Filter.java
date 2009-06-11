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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@SuppressWarnings("unchecked")
public class Filter extends SuggestBox
{
	private Filterable filterable;

	/**
	 * @param filterable
	 */
	public Filter(Filterable filterable)
	{
		super (getOracle(filterable));
		this.filterable = filterable;
		addSelectionHandler(createSelectionHandler());
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
				filterable.onSelectItem(filterSuggestion.getValue());
			}			
		};		
	}

	/**
	 * @param filterable
	 * @return
	 */
	private static SuggestOracle getOracle(Filterable filterable)
	{
		return new FilterSuggestOracle(filterable);
	}
}