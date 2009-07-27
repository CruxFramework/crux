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
package br.com.sysmap.crux.advanced.client.maskedtextbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Masks an TextBox
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class MaskedInput implements KeyDownHandler, KeyPressHandler, FocusHandler, BlurHandler
{
	private static Map<Character, String> definitions = new HashMap<Character, String>();
	static
	{
		definitions.put('9', "[0-9]");
		definitions.put('a', "[A-Za-z]");
		definitions.put('*', "[A-Za-z0-9]");
	}
	
	private TextBox textBox;
	private int firstNonMaskPos = -1;
	private boolean ignore = false;
	private List<String> tests = new ArrayList<String>();
	private char placeHolder;
	private char[] buffer;
	private String focusText;
	private int partialPosition = -1;
	private int length;

	private HandlerRegistration keyDownHandlerRegistration;
	private HandlerRegistration keyPressHandlerRegistration;
	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;
	private PasteEventHandler pasteEventHandler = GWT.create(PasteEventHandlerImpl.class);
	
	/**
	 * Constructor
	 * @param textBox
	 * @param mask
	 * @param placeHolder
	 */
	public MaskedInput(TextBox textBox, String mask, char placeHolder)
	{
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
				this.tests.add(definitions.containsKey(c)?definitions.get(c):null);
				if (this.tests.get(this.tests.size()-1) != null && this.firstNonMaskPos == -1)
				{
					this.firstNonMaskPos = this.tests.size()-1;
				}
				this.buffer[i] = (definitions.containsKey(c)?placeHolder:c);
			}
		}

		this.textBox = textBox;
		keyDownHandlerRegistration = this.textBox.addKeyDownHandler(this);
		keyPressHandlerRegistration = this.textBox.addKeyPressHandler(this);
		focusHandlerRegistration = this.textBox.addFocusHandler(this);
		blurHandlerRegistration = this.textBox.addBlurHandler(this);
		pasteEventHandler.addNativeHandlerForPaste(this, this.textBox.getElement());
		
		this.checkVal(false);
	}

	@Override
	protected void finalize() throws Throwable
	{
		Window.alert("finalize é chamado");
		super.finalize();
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
		pasteEventHandler.removeNativeHandlerForPaste(this, this.textBox.getElement());
		this.textBox = null;
	}
	
	/**
	 * keyDown event handler
	 */
	public void onKeyDown(KeyDownEvent event)
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
		focusText = textBox.getValue();
		int pos = checkVal(false);
		writeBuffer();
		caret(pos, -1);
	}

	/**
	 * Blur event handler
	 */
	public void onBlur(BlurEvent event)
	{
		checkVal(false);
		if (textBox.getValue() != focusText)
		{
			ValueChangeEvent.fire(textBox, textBox.getValue());
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
		textBox.setValue(new String(buffer));
		return textBox.getValue();
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
		String test = textBox.getValue();
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
			textBox.setValue("");
			clearBuffer(0, length);
		}
		else if (allow || lastMatch+1 >= partialPosition)
		{
			writeBuffer();
			if (!allow)
			{
				String value = textBox.getValue().substring(0, lastMatch+1);
				textBox.setValue(value);
			}
		}
		return ((partialPosition != 0) ? i : firstNonMaskPos);
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static interface PasteEventHandler 
	{
		void addNativeHandlerForPaste(MaskedInput handler, Element element);
		void removeNativeHandlerForPaste(MaskedInput handler, Element element);
	};
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static class PasteEventHandlerImpl implements PasteEventHandler
	{
		public native void addNativeHandlerForPaste(MaskedInput handler, Element element)/*-{
			element.onpaste = function(){
				setTimeout(function(){handler.@br.com.sysmap.crux.advanced.client.maskedtextbox.MaskedInput::checkVal(Z)(true);},10);
			};
		}-*/;

		public native void removeNativeHandlerForPaste(MaskedInput handler, Element element)/*-{
			element.onpaste = null;
		}-*/;
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static class PasteEventHandlerOperaImpl implements PasteEventHandler
	{
		public native void addNativeHandlerForPaste(MaskedInput handler, Element element)/*-{
			element.oninput = function(){
				setTimeout(function(){handler.@br.com.sysmap.crux.advanced.client.maskedtextbox.MaskedInput::checkVal(Z)(true);},10);
			};
		}-*/;

		public native void removeNativeHandlerForPaste(MaskedInput handler, Element element)/*-{
			element.onpaste = null;
		}-*/;
	}
}
