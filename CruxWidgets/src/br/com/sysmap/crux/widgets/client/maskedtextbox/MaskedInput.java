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

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.widgets.client.event.paste.PasteEvent;
import br.com.sysmap.crux.widgets.client.event.paste.PasteHandler;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Masks an TextBox
 * 
 * @author Thiago da Rosa de Bustamante
 */
class MaskedInput implements KeyDownHandler, KeyPressHandler, FocusHandler, BlurHandler, PasteHandler
{
	private static FastMap<String> definitions = new FastMap<String>();
	static
	{
		definitions.put("9", "[0-9]");
		definitions.put("a", "[A-Za-z]");
		definitions.put("*", "[A-Za-z0-9]");
	}
	
	private TextBox textBox;
	private int firstNonMaskPos = -1;
	private boolean ignore = false;
	private FastList<String> tests = new FastList<String>();
	private char placeHolder;
	private char[] buffer;
	private String focusText;
	private int partialPosition = -1;
	private int length;

	private HandlerRegistration keyDownHandlerRegistration;
	private HandlerRegistration keyPressHandlerRegistration;
	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;
	private HandlerRegistration pasteHandlerRegistration;
	private MaskedTextBox maskedTextBox;
	
	/**
	 * Constructor
	 * @param textBox
	 * @param mask
	 * @param placeHolder
	 */
	public MaskedInput(MaskedTextBox maskedTextBox, String mask, char placeHolder)
	{
		this.maskedTextBox = maskedTextBox;
		this.placeHolder = placeHolder;
		this.partialPosition = mask.length();;
		this.buffer = new char[mask.length()];
		this.length = mask.length();

		for (int i=0; i< mask.length(); i++)
		{
			char c = mask.charAt(i);
			if (c == '?')
			{
				this.length--;
				this.partialPosition = i;
			}
			else
			{					
				String key = c+"";
				this.tests.add(definitions.containsKey(key)?definitions.get(key):null);
				if (this.tests.get(this.tests.size()-1) != null && this.firstNonMaskPos == -1)
				{
					this.firstNonMaskPos = this.tests.size()-1;
				}
				this.buffer[i] = (definitions.containsKey(key)?placeHolder:c);
			}
		}

		this.textBox = maskedTextBox.textBox;
		keyDownHandlerRegistration = this.textBox.addKeyDownHandler(this);
		keyPressHandlerRegistration = this.textBox.addKeyPressHandler(this);
		focusHandlerRegistration = this.textBox.addFocusHandler(this);
		blurHandlerRegistration = this.textBox.addBlurHandler(this);
		pasteHandlerRegistration = this.maskedTextBox.addPasteHandler(this);
		
		
		this.checkVal(false);
	}
	
	TextBox getTextBox()
	{
		return textBox;
	}

	/**
	 * Unmask the current textBox
	 */
	public void removeMask()
	{
		keyDownHandlerRegistration.removeHandler();
		keyPressHandlerRegistration.removeHandler();
		focusHandlerRegistration.removeHandler();
		blurHandlerRegistration.removeHandler();
		pasteHandlerRegistration.removeHandler();
		this.textBox = null;
		this.maskedTextBox = null;
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
			int[] pos = caret(-1,-1);
			int code = event.getNativeKeyCode();
			ignore = (code < 16 || (code > 16 && code < 32) || (code > 32 && code < 41));

			if ((pos[0] - pos[1]) != 0 && (!ignore || code == KeyCodes.KEY_BACKSPACE || code == KeyCodes.KEY_DELETE)) 
			{
				clearBuffer(pos[0], pos[1]);
			}

			if (code == KeyCodes.KEY_BACKSPACE || code == KeyCodes.KEY_DELETE ) // || (iPhone && k==127) 
			{
				if (pos[0] < this.length  || code != KeyCodes.KEY_DELETE)
				{
					shiftL(pos[0] + (code == KeyCodes.KEY_DELETE ? 0 : -1));
				}
				event.preventDefault();
			}
			else if (code == KeyCodes.KEY_ESCAPE)
			{
				clearBuffer(0, length);
				writeBuffer();
				caret(firstNonMaskPos, -1);
				event.preventDefault();
			}
		}
	}

	public void onPaste(PasteEvent event)
	{
		checkVal(true);
	}

	/**
	 * keyPress event handler
	 */
	public void onKeyPress(KeyPressEvent event)
	{
		if (ignore)
		{
			ignore = false;
			if (event.getCharCode() == '\b')
			{
				event.preventDefault();
			}
		}

		if (event.isControlKeyDown() || event.isAltKeyDown())
		{
			return;
		}

		char code = event.getCharCode();
		if ((code >= 41 && code <= 122) || code == 32 || code > 186) //typeable characters
		{
			int[] pos = caret(-1, -1);
			int p = seekNext(pos[0] - 1);
			if (p < length)
			{
				String c = ""+event.getCharCode();
				if (c.matches(tests.get(p)))
				{
					shiftR(p);
					buffer[p] = c.charAt(0);
					writeBuffer();
					int next = seekNext(p);
					caret(next, -1);
				}
			}

		}
		event.preventDefault();
	}

	/**
	 * Focus event handler
	 */
	public void onFocus(FocusEvent event)
	{
		int pos = checkVal(false);
		writeBuffer();
		caret(pos, -1);
		focusText = textBox.getText();
	}

	/**
	 * Blur event handler
	 */
	public void onBlur(BlurEvent event)
	{
		checkVal(false);
		if (textBox.getText() != focusText)
		{
			ValueChangeEvent.fire(textBox, textBox.getText());
		}
	}

	/**
	 * Controls caret
	 * @param begin
	 * @param end
	 * @return
	 */
	private int[] caret (int begin, int end)
	{
		if (begin > -1)
		{
			if (end < 0)
			{
				end = begin;
			}
			textBox.setFocus(true);
			textBox.setSelectionRange(begin, end - begin);
		}
		else
		{
			begin = textBox.getCursorPos();
			end = begin + textBox.getSelectionLength();
		}
		return new int[]{begin, end};
	}

	/**
	 * 
	 * @param start
	 * @param end
	 */
	private void clearBuffer(int start, int end)
	{
		for(int i=start; i<end && i< length; i++)
		{
			if (tests.get(i) != null && tests.get(i).length() > 0)
			{
				buffer[i] = placeHolder;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private String writeBuffer()
	{
		textBox.setText(new String(buffer));
		return textBox.getText();
	}

	/**
	 * 
	 * @param pos
	 * @return
	 */
	private int seekNext(int pos)
	{
		if (pos < -1)
		{
			pos = -1;
		}
		while (++pos < length)
		{
			if (pos < tests.size() && tests.get(pos) != null && tests.get(pos).length() > 0)
			{
				return pos;
			}
		}
		return length;
	}

	/**
	 * 
	 * @param pos
	 */
	private void shiftL(int pos)
	{
		if (pos >= tests.size()) 
		{
			pos = tests.size()-1;
		}
		while (pos >= 0 && (tests.get(pos) == null || tests.get(pos).length() == 0))
		{
			pos--;
		}
		if (pos < 0) 
		{
			pos = 0;
		}
		for (int i=pos; i<length; i++)
		{
			if (tests.get(i) != null && tests.get(i).length() > 0)
			{
				buffer[i] = placeHolder;
				int j = seekNext(i);
				if (j < length && (""+buffer[j]).matches(tests.get(i)))
				{
					buffer[i] = buffer[j];
				}
				else
				{
					break;
				}
			}
		}
		writeBuffer();
		caret(Math.max(firstNonMaskPos, pos), -1);
	}

	/**
	 * 
	 * @param pos
	 */
	private void shiftR(int pos)
	{
		char c = placeHolder;
		for (int i=pos; i < length; i++)
		{
			if (tests.get(i) != null && tests.get(i).length() > 0)
			{
				int j = seekNext(i);
				char t = buffer[i];
				buffer[i] = c;
				if (j < length && (""+t).matches(tests.get(j)))
				{
					c = t;
				}
				else
				{
					break;
				}
			}
		}
	}		

	/**
	 * 
	 * @param allow
	 * @return
	 */
	private int checkVal(boolean allow)
	{
		String test = textBox.getText();
		int lastMatch = -1;
		int pos=0;
		int i=0;

		for (i=0; i<length; i++)
		{
			if (tests.get(i) != null && tests.get(i).length() > 0)
			{
				buffer[i] = placeHolder;
				while (pos++ < test.length())
				{
					char c = test.charAt(pos-1);
					if ((""+c).matches(tests.get(i)))
					{
						buffer[i] = c;
						lastMatch = i;
						break;
					}
				}
				if (pos > test.length())
				{
					break;
				}
			}
		}
		if (!allow && lastMatch+1 < partialPosition)
		{
			textBox.setText("");
			clearBuffer(0, length);
		}
		else if (allow || lastMatch+1 >= partialPosition)
		{
			writeBuffer();
			if (!allow)
			{
				String value = textBox.getText().substring(0, lastMatch+1);
				textBox.setText(value);
			}
		}
		return ((partialPosition != 0) ? i : firstNonMaskPos);
	}
}
