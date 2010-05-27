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
package br.com.sysmap.crux.widgets.client.event.moveitem;

import java.util.List;

import br.com.sysmap.crux.widgets.client.transferlist.TransferList.Item;
import br.com.sysmap.crux.widgets.client.transferlist.TransferList.ItemLocation;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event fired by TransferList widget before its items are moved across the lists
 * @author Gessé S. F. Dafé
 */
public class BeforeMoveItemsEvent extends GwtEvent<BeforeMoveItemsHandler>{

	private static Type<BeforeMoveItemsHandler> TYPE = new Type<BeforeMoveItemsHandler>();
	private HasBeforeMoveItemsHandlers source;
	private List<Item> items;
	private boolean canceled;

	/**
	 * Constructor 
	 */
	public BeforeMoveItemsEvent(HasBeforeMoveItemsHandlers source, List<Item> items)
	{
		this.source = source;
		this.items = items;
	}

	/**
	 * @return the source
	 */
	public HasBeforeMoveItemsHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<BeforeMoveItemsHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeMoveItemsHandler handler)
	{
		handler.onBeforeMoveItems(this);
	}

	@Override
	public Type<BeforeMoveItemsHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static BeforeMoveItemsEvent fire(HasBeforeMoveItemsHandlers source, List<Item> items)
	{
		BeforeMoveItemsEvent event = new BeforeMoveItemsEvent(source, items);
		source.fireEvent(event);
		return event;
	}

	/**
	 * @return the row
	 */
	public List<Item> getItems()
	{
		return items;
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

	/**
	 * Return true if selected items are being moved from left to right list
	 * @return
	 */
	public boolean isMovingToRight()
	{
		boolean existsItems = this.items != null && this.items.size() > 0;
		boolean itemsAreFromLeft = existsItems && ItemLocation.left.equals(this.items.get(0).getLocation());
		return itemsAreFromLeft;
	}
	
	/**
	 * Return true if selected items are being moved from right to left list
	 * @return
	 */
	public boolean isMovingToLeft()
	{
		boolean existsItems = this.items != null && this.items.size() > 0;
		boolean itemsAreFromRight = existsItems && ItemLocation.right.equals(this.items.get(0).getLocation());
		return itemsAreFromRight;
	}
}