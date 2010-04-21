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
package br.com.sysmap.crux.widgets.client.dynatabs;

import br.com.sysmap.crux.widgets.client.event.focusblur.BeforeBlurHandler;
import br.com.sysmap.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import br.com.sysmap.crux.widgets.client.event.focusblur.BeforeFocusHandler;
import br.com.sysmap.crux.widgets.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * TODO - Gessé - Comment
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class Tab extends AbstractTab implements HasBeforeFocusAndBeforeBlurHandlers
{
	private FlapPanel flapPanel;
	private boolean closeable;
	private int insertionIndex;

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
		super(id, label, url);
		this.insertionIndex = insertionIndex;
		this.flapPanel = flapPanel;
		this.closeable = closeable;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		super.setLabel(label);
		this.flapPanel.getFlapController().setTabTitle(label);
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
}