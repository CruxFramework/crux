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
package org.cruxframework.crux.widgets.client.filter;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * TODO - Gesse - Comment this
 * 
 * @author Gesse S. F. Dafe
 */
public class FilterSuggestion implements Suggestion
{
	private String label;
	private Object value;
	private String replacementString;

	/**
	 * @param value
	 * @param label
	 */
	public FilterSuggestion(Object value, String label, String replacementString)
	{
		this.value = value;
		this.label = label;
		this.replacementString = replacementString;
	}

	/**
	 * @see com.google.gwt.user.client.ui.SuggestOracle.Suggestion#getDisplayString()
	 */
	public String getDisplayString()
	{
		return label;
	}

	/**
	 * @see com.google.gwt.user.client.ui.SuggestOracle.Suggestion#getReplacementString()
	 */
	public String getReplacementString()
	{
		return replacementString;
	}

	/**
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}
}