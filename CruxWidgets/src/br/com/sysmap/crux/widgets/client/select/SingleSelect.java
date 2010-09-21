package br.com.sysmap.crux.widgets.client.select;

import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A simple single-select listBox that implements HasValue interface.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class SingleSelect extends ListBox implements HasValue<String>
{
	/**
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	public String getValue()
	{
		if(getSelectedIndex() >= 0)
		{
			return getValue(getSelectedIndex());
		}
		
		return null;
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	public void setValue(String value)
	{
		int count = getItemCount();
		
		for (int i = 0; i < count; i++)
		{
			String itemValue = getValue(i);
			
			if((StringUtils.isEmpty(itemValue) && StringUtils.isEmpty(value)) || itemValue.equals(value))
			{
				setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	public void setValue(String value, boolean fireEvents)
	{
		String oldValue = getValue();
		setValue(value);
		if(fireEvents)
		{
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}		
	}

	/**
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}	
}