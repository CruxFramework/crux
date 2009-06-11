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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.advanced.client.filter.Filterable.FilterResult;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@SuppressWarnings("unchecked")
class FilterSuggestOracle extends SuggestOracle
{
	private Filterable filterable;

	/**
	 * 
	 */
	public FilterSuggestOracle()
	{
	}
	
	/**
	 * @param filterable
	 */
	public FilterSuggestOracle(Filterable<?> filterable)
	{
		this.filterable = filterable;
	}	
	
	
	@Override
	public void requestSuggestions(Request request, Callback callback)
	{
		List<FilterSuggestion> suggestions = new ArrayList<FilterSuggestion>();
		
		String query = request.getQuery();
		List<FilterResult> results = filterable.filter(query);
		if(results != null)
		{
			for (FilterResult<?> result : results)
			{
				FilterSuggestion suggestion = new FilterSuggestion(result.getValue(), result.getLabel());
				suggestions.add(suggestion);
			}
		}	
		
		callback.onSuggestionsReady(request, new Response(suggestions));
	}

	/**
	 * @param filterable the filterable to set
	 */
	public void setFilterable(Filterable filterable)
	{
		this.filterable = filterable;
	}

	/**
	 * @return the filterable
	 */
	public Filterable getFilterable()
	{
		return filterable;
	}
}