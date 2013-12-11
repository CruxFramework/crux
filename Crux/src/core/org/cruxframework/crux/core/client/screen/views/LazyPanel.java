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
package org.cruxframework.crux.core.client.screen.views;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class LazyPanel extends com.google.gwt.user.client.ui.LazyPanel
{
	private boolean initialized = false;
	private final String lazyId;
	private final View view;

	public LazyPanel(View view, String lazyId)
    {
		this.view = view;
		this.lazyId = lazyId;
    }
	
	/**
	 * @see com.google.gwt.user.client.ui.LazyPanel#ensureWidget()
	 */
	@Override
	public void ensureWidget()
	{
		if (!initialized)
		{
			view.cleanLazyDependentWidgets(lazyId);
			initialized = true;
		}
		super.ensureWidget();
	}

}
