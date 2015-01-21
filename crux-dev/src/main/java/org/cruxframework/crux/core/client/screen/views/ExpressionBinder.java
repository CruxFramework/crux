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
package org.cruxframework.crux.core.client.screen.views;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
/**
 * Interface for expression binding between dataObjects and widgets in this view.
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ExpressionBinder<W extends IsWidget>
{
	// to avoid the same expression be evaluated more than once on the same execution context
	private long timestamp = 0;
	protected W widget;

	@SuppressWarnings("unchecked")
    protected void bind(IsWidget w)
	{
		this.widget = (W) w;
	}
	
	protected boolean isBound()
	{
		return widget != null;
	}
	
	void execute(BindingContext context)
	{
		long executionTimestamp = context.getExecutionTimestamp();
		if (timestamp < executionTimestamp)
		{
			timestamp = executionTimestamp;
			updateExpression(context);
		}
	}
	
	/**
	 * Update the widget expression binding 
	 * @param w widget
	 */
	abstract void updateExpression(BindingContext context);
	
	public static interface BindingContext
	{
		<T> T getDataObject(String dataObject);
		long getExecutionTimestamp();
	}
}
