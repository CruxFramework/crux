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
package org.cruxframework.crux.core.client.screen.binding;

import org.cruxframework.crux.core.client.html5.api.MutationObserver;
import org.cruxframework.crux.core.client.html5.api.MutationObserver.MutationRecord;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface for data binding between dataObjects and widgets in this view.
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class PropertyBinder<T, W extends IsWidget>
{
	protected DataObjectBinder<T> dataObjectBinder;
	protected W widget;
	private boolean boundToAttribute;

	@SuppressWarnings("unchecked")
    protected void bind(IsWidget w)
	{
		this.widget = (W) w;
		listenDOMChanges();
	}
	
	protected void setDataObjectBinder(DataObjectBinder<T> dataObjectBinder)
	{
		this.dataObjectBinder = dataObjectBinder;
	}
	
	protected boolean isBound()
	{
		return widget != null;
	}

	protected void observeDOMChanges()
	{
		widget.asWidget().addAttachHandler(new AttachEvent.Handler()
		{
			private MutationObserver mutationObserver;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					mutationObserver = MutationObserver.createIfSupported(new MutationObserver.Callback()
					{
						@Override
						public void onChanged(JsArray<MutationRecord> mutations, MutationObserver observer)
						{
							notifyChanges();
						}
					});
				}
				else if (mutationObserver != null)
				{
					mutationObserver.disconnect();
					mutationObserver = null;
				}
			}
		});
	}

	protected void observeChangeEvents()
	{
		observeChangeEvents(widget.asWidget().getElement(), this);
	}

	protected native void observeChangeEvents(Element el, PropertyBinder<T, W> binder)/*-{
		el.addEventListener('change', function(e){
			binder.@org.cruxframework.crux.core.client.screen.binding.PropertyBinder::notifyChanges()();
		});
	}-*/;
	
	protected void notifyChanges()
	{
		dataObjectBinder.updateObjectAndNotifyChanges(this);
	}
	
	/**
	 * Implements DOM listening to enable Crux to update bound dataObject when 
	 * DOM changes.
	 */
	protected void listenDOMChanges()
	{
		if (isBoundToAttribute())
		{
			observeDOMChanges();
		}
		else
		{
			observeChangeEvents();
		}
	}
	
	protected void setBoundToAttribute(boolean boundToAttribute)
	{
		this.boundToAttribute = boundToAttribute;
	}
	
	/**
	 * Inform if this property binding references an HTML attribute or property.
	 * It is important for considerable performance improvements, as it allow us to avoid
	 * dirty checking to implement data binding. 
	 * @return true if a binding reference an HTML attribute.
	 */
	protected boolean isBoundToAttribute()
	{
		return boundToAttribute;
	}
	
	/**
	 * Transfer data from dataObject to target widget
	 * @param dataObject
	 */
	public abstract void copyTo(T dataObject);
	
	/**
	 * Transfer data from given widget to target dataObject
	 * @param dataObject
	 */
	public abstract void copyFrom(T dataObject);
}
