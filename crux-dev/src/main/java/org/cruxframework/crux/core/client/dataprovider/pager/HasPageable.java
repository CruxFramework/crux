/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.client.dataprovider.pager;

import com.google.gwt.user.client.ui.Panel;

/**
 * A HasPageable is a component to navigate on pages of a {@link Pageable} widget.
 * @author Thiago da Rosa de Bustamante
 */
public interface HasPageable<T> extends Pager<T>
{
	/**
	 * Inform if the pager supports that multiple pages are rendered into
	 * the pageable widget. If this method returns false, the pageable widget
	 * will first clear its content panel before render any new page data. If 
	 * it return true, this panel will not be cleared, allowing infinit scrolling.
	 * @return true if infinite scroll is supported
	 */
	boolean supportsInfiniteScroll();
	
	/**
	 * Called to update the pageable content panel assigning to it the given pagePanel.
	 * @param pagePanel page panel.
	 * @param forward true if the next page index is bigger than the previous page index.
	 */
	void updatePagePanel(Panel pagePanel, boolean forward);
	
	/**
	 * Initialize the panel that will contain the pages
	 * @param contentPanel the main panel, that contains the other pages.
	 */
	void initializeContentPanel(Panel contentPanel);
}
