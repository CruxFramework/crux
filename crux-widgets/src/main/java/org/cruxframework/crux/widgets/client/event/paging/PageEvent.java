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
package org.cruxframework.crux.widgets.client.event.paging;

import com.google.gwt.event.shared.GwtEvent;


/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class PageEvent extends GwtEvent<PageHandler>{

	private static Type<PageHandler> TYPE = new Type<PageHandler>();
	private HasPageHandlers source;
	private int page;
	private boolean canceled;

	/**
	 * 
	 */
	public PageEvent(HasPageHandlers source, int requestedPage)
	{
		this.source = source;
		this.page = requestedPage;
	}

	/**
	 * @return the source
	 */
	public HasPageHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<PageHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(PageHandler handler)
	{
		handler.onPage(this);
	}

	@Override
	public Type<PageHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static PageEvent fire(HasPageHandlers source, int requestedPage)
	{
		PageEvent event = new PageEvent(source, requestedPage);
		source.fireEvent(event);
		return event;
	}

	/**
	 * @return the row
	 */
	public int getRequestedPage()
	{
		return page;
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * @param canceled the canceled to set
	 */
	public void cancel()
	{
		this.canceled = true;
	}
}