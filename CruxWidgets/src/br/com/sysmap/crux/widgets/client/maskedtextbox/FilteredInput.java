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
package br.com.sysmap.crux.widgets.client.maskedtextbox;

import br.com.sysmap.crux.widgets.client.event.paste.PasteEvent;
import br.com.sysmap.crux.widgets.client.event.paste.PasteHandler;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Filters a TextBox
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class FilteredInput implements KeyDownHandler, KeyPressHandler, PasteHandler
{
	private String filterExpression;
	private HandlerRegistration keyDownHandlerRegistration;

	private HandlerRegistration keyPressHandlerRegistration;
	private MaskedTextBox maskedTextBox;
	private HandlerRegistration pasteHandlerRegistration;
	private TextBox textBox;
	private boolean evaluateKey;
	
	/**
	 * Constructor
	 * @param textBox
	 * @param mask
	 */
	public FilteredInput(MaskedTextBox maskedTextBox, String filterExpression)
	{
		this.maskedTextBox = maskedTextBox;
		this.filterExpression = filterExpression;
		this.textBox = maskedTextBox.textBox;

		keyDownHandlerRegistration = this.textBox.addKeyDownHandler(this);
		keyPressHandlerRegistration = this.textBox.addKeyPressHandler(this);
		pasteHandlerRegistration = this.maskedTextBox.addPasteHandler(this);
		
		this.checkVal();
	}
	
	/**
	 * keyDown event handler
	 */
	public void onKeyDown(KeyDownEvent event)
	{
		if (textBox.isReadOnly())
		{
			event.preventDefault();
		}
		else
		{
			int code = event.getNativeKeyCode();
			this.evaluateKey = ((code != KeyCodes.KEY_ALT) && (code != KeyCodes.KEY_BACKSPACE) && (code != KeyCodes.KEY_DELETE) && (code != KeyCodes.KEY_END) && 
					(code != KeyCodes.KEY_ENTER) && (code != KeyCodes.KEY_ESCAPE) && (code != KeyCodes.KEY_HOME) && (code != KeyCodes.KEY_LEFT) && 
					(code != KeyCodes.KEY_RIGHT));
		}
	}

	/**
	 * keyPress event handler
	 */
	public void onKeyPress(KeyPressEvent event)
	{
		if (event.isControlKeyDown() || event.isAltKeyDown())
		{
			return;
		}

		if (evaluateKey) 
		{
			String c = ""+event.getCharCode();
			if (!c.matches(filterExpression))
			{
				event.preventDefault();
			}
		}
	}
	
	public void onPaste(PasteEvent event)
	{
		checkVal();
	}

	/**
	 * Unmask the current textBox
	 */
	public void removeFilter()
	{
		keyDownHandlerRegistration.removeHandler();
		keyPressHandlerRegistration.removeHandler();
		pasteHandlerRegistration.removeHandler();
		this.textBox = null;
		this.maskedTextBox = null;
	}

	TextBox getTextBox()
	{
		return textBox;
	}
	
	private void checkVal()
	{
		String value = textBox.getValue();
		StringBuilder result = new StringBuilder();
		for (int i=0; i< value.length(); i++)
		{
			String c = ""+value.charAt(i);
			if (c.matches(filterExpression))
			{
				result.append(c);
			}
		}
		textBox.setValue(result.toString());
	}
}
