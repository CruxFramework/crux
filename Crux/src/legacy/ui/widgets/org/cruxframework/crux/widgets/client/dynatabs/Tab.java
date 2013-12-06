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
package org.cruxframework.crux.widgets.client.dynatabs;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * TODO - Gesse - Comment
 * 
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class Tab extends AbstractTab implements HasBeforeFocusAndBeforeBlurHandlers
{
	private FlapPanel flapPanel;
	private boolean closeable;
	private int insertionIndex;
	private String label;
	private boolean loaded = true;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param label
	 * @param url
	 * @param closeable
	 * @param reloadIfExists
	 */
	Tab(String id, String label, String url, boolean closeable, int insertionIndex, FlapPanel flapPanel)
	{
		super(id, url);
		this.label = label;
		this.insertionIndex = insertionIndex;
		this.flapPanel = flapPanel;
		this.closeable = closeable;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
		this.flapPanel.getFlapController().setTabTitle(label);
	}

	/**
	 * Changes the document shown in the tab
	 */
	void changeURL(String newURL){
		DynaTabs.getTabWindow(getId()).changeLocation(newURL);
		setUrl(newURL);
	}
	
	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}
	
	public HandlerRegistration addBeforeFocusHandler(BeforeFocusHandler handler)
	{
		return addHandler(handler, BeforeFocusEvent.getType());
	}

	public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
	{
		return flapPanel.addBeforeBlurHandler(handler);
	}

	/**
	 * @return the insertionIndex
	 */
	int getIndex()
	{
		return insertionIndex;
	}
	
	/**
	 * @return the flapPanel
	 */
	FlapPanel getFlapPanel()
	{
		return flapPanel;
	}
	
	/**
	 * @return the closeable
	 */
	boolean isCloseable()
	{
		return closeable;
	}

	/**
	 * @return the loaded
	 */
	boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * @param loaded the loaded to set
	 */
	void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}
}