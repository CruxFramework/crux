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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.JSEngine;
import br.com.sysmap.crux.core.client.component.ScreenFactory;
import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBoxBase.TextAlignConstant;


/**
 * Base class for text box based components
 * @author Thiago Bustamante
 *
 */
public abstract class TextBoxBase extends FocusComponent
{
	protected com.google.gwt.user.client.ui.TextBoxBase textBoxBaseWidget;
	protected Formatter clientFormatter = null;
	
	protected TextBoxBase(String id, com.google.gwt.user.client.ui.TextBoxBase widget) 
	{
		super(id, widget);
		this.textBoxBaseWidget = widget;
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		String formatter = element.getAttribute("_formatter");
		if (formatter != null && formatter.length() > 0)
		{
			clientFormatter = ScreenFactory.getInstance().getClientFormatter(formatter);
			if (clientFormatter == null)
			{
				Window.alert(JSEngine.messages.componentFormatterNotFound(formatter));
			}
		}
		String value = element.getAttribute("_value");
		if (value != null && value.length() > 0)
		{
			setValue(value);
		}
		String readOnly = element.getAttribute("_readOnly");
		if (readOnly != null && readOnly.length() > 0)
		{
			setReadOnly(Boolean.parseBoolean(readOnly));
		}
		String textAlignment = element.getAttribute("_textAlignment");
		if (textAlignment != null)
		{
			if ("center".equalsIgnoreCase(textAlignment))
			{
				setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_CENTER);
			}
			else if ("justify".equalsIgnoreCase(textAlignment))
			{
				setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_JUSTIFY);
			} 
			else if ("left".equalsIgnoreCase(textAlignment))
			{
				setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_LEFT);
			} 
			else if ("right".equalsIgnoreCase(textAlignment))
			{
				setTextAlignment(com.google.gwt.user.client.ui.TextBoxBase.ALIGN_RIGHT);
			} 
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		if (clientFormatter != null)
		{
			textBoxBaseWidget.addBlurHandler(new BlurHandler()
			{
				@Override
				public void onBlur(BlurEvent event) 
				{
					try 
					{
						setValue(getValue());
					} 
					catch (InvalidFormatException e) 
					{
						Window.alert(e.getLocalizedMessage());
						setValue(null);
					}
				}
			});
		}
		
		ChangeEvtBind.bindValueEvent(element, textBoxBaseWidget, getId());
		super.attachEvents(element);
	}
	
	/**
	 * Return the component value applying any format changes performed by it's formatter
	 * @return
	 */
	public Object getFormattedValue() throws InvalidFormatException 
	{
		String text = textBoxBaseWidget.getValue();
		if (clientFormatter != null)
		{
			return clientFormatter.unformat(text);
		}
		return text;

	}	
	
	public void setValue(String value)
	{
		textBoxBaseWidget.setValue(value);
	}

	public void setValue(String value, boolean fireEvents)
	{
		textBoxBaseWidget.setValue(value, fireEvents);
	}
	
	/**
	 * Return the component value
	 * @return
	 */
	public String getValue() throws InvalidFormatException 
	{
		return textBoxBaseWidget.getValue();
	}
	
	/**
	 * Set the component value applying any format changes performed by it's formatter
	 * @return
	 */
	public void setValueFormatted(Object value)
	{
		setValueFormatted(value, false);
	}

	/**
	 * Set the component value applying any format changes performed by it's formatter
	 * @return
	 */
	public void setValueFormatted(Object value, boolean fireEvents) 
	{
		if (clientFormatter != null)
		{
			String text = clientFormatter.mask(clientFormatter.format(value));
			textBoxBaseWidget.setValue(text, fireEvents);
		}
		else
		{
			textBoxBaseWidget.setValue(value!=null?value.toString():"", fireEvents);
		}
	}

	/**
	 * If a keyboard event is currently being handled on this text box, calling
	 * this method will suppress it. This allows listeners to easily filter
	 * keyboard input.
	 */
	public void cancelKey() 
	{
		textBoxBaseWidget.cancelKey();
	}
	/**
	 * Gets the current position of the cursor (this also serves as the beginning
	 * of the text selection).
	 * 
	 * @return the cursor's position
	 */
	public int getCursorPos() 
	{
		return textBoxBaseWidget.getCursorPos();
	}

	public String getName() 
	{
		return textBoxBaseWidget.getName();
	}

	/**
	 * Gets the text currently selected within this text box.
	 * 
	 * @return the selected text, or an empty string if none is selected
	 */
	public String getSelectedText() 
	{
		return textBoxBaseWidget.getSelectedText();
	}

	/**
	 * Gets the length of the current text selection.
	 * 
	 * @return the text selection length
	 */
	public int getSelectionLength() 
	{
		return textBoxBaseWidget.getSelectionLength();
	}

	public String getText() 
	{
		return textBoxBaseWidget.getText();
	}

	/**
	 * Determines whether or not the widget is read-only.
	 * 
	 * @return <code>true</code> if the widget is currently read-only,
	 *         <code>false</code> if the widget is currently editable
	 */
	public boolean isReadOnly() 
	{
		return textBoxBaseWidget.isReadOnly();
	}

	/**
	 * Selects all of the text in the box.
	 * 
	 * This will only work when the widget is attached to the document and not
	 * hidden.
	 */
	public void selectAll() 
	{	
		textBoxBaseWidget.selectAll();
	}

	/**
	 * Sets the cursor position.
	 * 
	 * This will only work when the widget is attached to the document and not
	 * hidden.
	 * 
	 * @param pos the new cursor position
	 */
	public void setCursorPos(int pos) 
	{	
		textBoxBaseWidget.setCursorPos(pos);
	}

	public void setName(String name) 
	{
		textBoxBaseWidget.setName(name);
	}

	/**
	 * Turns read-only mode on or off.
	 * 
	 * @param readOnly if <code>true</code>, the widget becomes read-only; if
	 *          <code>false</code> the widget becomes editable
	 */
	public void setReadOnly(boolean readOnly) 
	{
		textBoxBaseWidget.setReadOnly(readOnly);
	}

	/**
	 * Sets the range of text to be selected.
	 * 
	 * This will only work when the widget is attached to the document and not
	 * hidden.
	 * 
	 * @param pos the position of the first character to be selected
	 * @param length the number of characters to be selected
	 */
	public void setSelectionRange(int pos, int length) 
	{
		textBoxBaseWidget.setSelectionRange(pos, length);
	}

	public void setText(String text) 
	{
		textBoxBaseWidget.setText(text);
	}


	/**
	 * Sets the alignment of the text in the text box.
	 * 
	 * @param align the text alignment (as specified by {@link #ALIGN_CENTER},
	 *          {@link #ALIGN_JUSTIFY}, {@link #ALIGN_LEFT}, and
	 *          {@link #ALIGN_RIGHT})
	 */
	public void setTextAlignment(TextAlignConstant align) 
	{
		textBoxBaseWidget.setTextAlignment(align);
	}
}
