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


import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OrientationChangeEvent extends GwtEvent<OrientationChangeHandler>
{
	private static Type<OrientationChangeHandler> TYPE;

	private String orientation;

	/**
	 * Constructor
	 */
	protected OrientationChangeEvent(String orientation)
	{
		this.orientation = orientation;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final Type<OrientationChangeHandler> getAssociatedType()
	{
		return (Type) TYPE;
	}

	public String getOrientation()
	{
		return orientation;
	}

	@Override
	protected void dispatch(OrientationChangeHandler handler)
	{
		handler.onOrientationChange(this);
	}

	public static void fire(View source, String orientation)
	{
		if (TYPE != null)
		{
			OrientationChangeEvent event = new OrientationChangeEvent(orientation);
			source.fireOrientationChangeEvent(event);
		}
	}

	public static Type<OrientationChangeHandler> getType()
	{
		return TYPE != null ? TYPE : (TYPE = new Type<OrientationChangeHandler>());
	}
}
