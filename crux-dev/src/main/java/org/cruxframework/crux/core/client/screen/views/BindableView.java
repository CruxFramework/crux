/*
 * Copyright 2013 cruxframework.org.
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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.dto.DataObject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * BindableViews are Views that support data binding to given data objects.
 * You can define a BindableView using the dataObject attribute on view declaration.
  * <pre>
 * {@code <v:view dataObject="MyDataObject">}
 * {@code </gwt:textBox id="textBox1" dataBind="property1.innerProperty">}
 * {@code </v:view>}
 * </pre>
 * <p>
 * You must annotate your Data Object with {@link DataObject} annotation.
 * <pre>
 * {@code @DataObject("MyDataObject")}
 * public class MyDTO 
 * {
 *    ...//getters and setters
 * }
 * </pre>
 * <p>
 * And you can access your BindableView through {@link ViewAccessor}.
 * <pre>
 * public interface Views extends ViewAccessor 
 * {
 *    BindableView<MyDTO> myViewId();
 * }
 * </pre>
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class BindableView<T> extends View
{
	protected FastMap<PropertyBinder<T>> binders = new FastMap<BindableView.PropertyBinder<T>>();
	private T dataObject;
	
	/**
	 * 
	 * @param id
	 */
	public BindableView(String id)
    {
	    super(id);
    }

	/**
	 * Used by Crux to instantiate target data object
	 * @return
	 */
	protected abstract T createDataObject();
	
	/**
	 * Retrieve a DataObject instance, filled with data received from the widgets on this view.
	 * @return
	 */
	public T getData()
	{
		if (dataObject == null)
		{
			dataObject = createDataObject();
		}
		
		FastList<String> keys = binders.keys();
		int size = keys.size();
		for (int i=0; i< size; i++)
		{
			String id = keys.get(i);
			binders.get(id).copyFrom(getWidget(id), dataObject);
		}
		
		return dataObject;
	}
	
	/**
	 * Update bound widgets on this view with dataObject information
	 * @param dataObject
	 */
	public void setData(T dataObject)
	{
		FastList<String> keys = binders.keys();
		int size = keys.size();
		for (int i=0; i< size; i++)
		{
			String id = keys.get(i);
			binders.get(id).copyTo(dataObject, getWidget(id));
		}
		this.dataObject = dataObject;
	}
	
	/**
	 * Add a widget to this view and use the given binder to bound it with target dataObjet
	 * @param id
	 * @param widget
	 * @param binder
	 */
	public void addWidget(String id, IsWidget widget, PropertyBinder<T> binder)
	{
		addWidget(id, widget);
		binders.put(id, binder);
	}
	
	@Override
	public void removeWidget(String id, boolean removeFromDOM)
	{
	    super.removeWidget(id, removeFromDOM);
	    binders.remove(id);
	}
	
	/**
	 * Interface for data binding between dataObjects and widgets in this view.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface PropertyBinder<T>
	{
		/**
		 * Transfer data from dataObject to target widget
		 * @param dataObject
		 * @param w
		 */
		void copyTo(T dataObject, Widget w);
		
		/**
		 * Transfer data from given widget to target dataObject
		 * @param w
		 * @param dataObject
		 */
		void copyFrom(Widget w, T dataObject);
	}
}
