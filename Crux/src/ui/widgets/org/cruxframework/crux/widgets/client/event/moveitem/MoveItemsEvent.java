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
package org.cruxframework.crux.widgets.client.event.moveitem;

import java.util.List;

import org.cruxframework.crux.widgets.client.transferlist.TransferList.Item;


import com.google.gwt.event.shared.GwtEvent;


/**
 * Event fired by TransferList widget when its items are moved across the lists
 * @author Thiago da Rosa de Bustamante
 */
public class MoveItemsEvent extends GwtEvent<MoveItemsHandler>{

	private static Type<MoveItemsHandler> TYPE = new Type<MoveItemsHandler>();
	private HasMoveItemsHandlers source;
	private List<Item> items;
	private boolean leftToRight;

	/**
	 * Constructor 
	 */
	private MoveItemsEvent(HasMoveItemsHandlers source, List<Item> items, boolean leftToRight)
	{
		this.source = source;
		this.items = items;
		this.leftToRight = leftToRight;
	}

	/**
	 * @return the source
	 */
	public HasMoveItemsHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<MoveItemsHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(MoveItemsHandler handler)
	{
		handler.onMoveItems(this);
	}

	@Override
	public Type<MoveItemsHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static MoveItemsEvent fire(HasMoveItemsHandlers source, List<Item> items, boolean leftToRight)
	{
		MoveItemsEvent event = new MoveItemsEvent(source, items, leftToRight);
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
	 * Return true if selected items are being moved from left to right list
	 * @return
	 */
	public boolean isMovingToRight()
	{
		return leftToRight;
	}
	
	/**
	 * Return true if selected items are being moved from right to left list
	 * @return
	 */
	public boolean isMovingToLeft()
	{
		return !leftToRight;
	}
}