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
package org.cruxframework.crux.widgets.client.maskedtextbox;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.widgets.client.event.paste.PasteEvent;
import org.cruxframework.crux.widgets.client.event.paste.PasteHandler;

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
	private final FastList<String> tests = new FastList<String>();
	private final char placeHolder;
	private final char[] buffer;
	private String focusText;
	private int partialPosition = -1;
	private int length;

	private HandlerRegistration keyDownHandlerRegistration;
	private HandlerRegistration keyPressHandlerRegistration;
	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;
	private HandlerRegistration pasteHandlerRegistration;
	private MaskedTextBox maskedTextBox;
	private boolean clearIfNotValid = true;

	/**
	 * Constructor
	 * @param textBox
	 * @param mask
	 * @param placeHolder
	 */
	public MaskedInput(MaskedTextBox maskedTextBox, String mask, char placeHolder,
		boolean clearIfNotValid)
	{
		this.maskedTextBox = maskedTextBox;
		this.placeHolder = placeHolder;
		char[] internalBuffer = new char[mask.length()];
		this.length = mask.length();
		this.clearIfNotValid = clearIfNotValid;
		
		boolean escapeModeEnabled = false;
		int offset = 0;

		for (int i = 0; i < mask.length(); i++)
		{
			char c = mask.charAt(i);
			
			if (c == '?')
			{
				if (escapeModeEnabled)
				{
					internalBuffer[i - offset] = c;
					this.tests.add(null);
				} else
				{
					// partial validation
					this.length--;
					this.partialPosition = i - offset;
					internalBuffer[i - offset] = '?';
				}
			} else if (c == '"')
			{
				// turn on or turn off escape mode 
				escapeModeEnabled = !escapeModeEnabled;
				this.length--;
				offset++;
			} 
				else
			{
				if (escapeModeEnabled)
				{
					// if escape mode is enabled
					internalBuffer[i - offset] = c;
					this.tests.add(null);
				} else
				{
					String key = "" + c;
					
					// if escape mode is disabled
					if (!definitions.containsKey(key) && Character.isLetterOrDigit(c))
					{
						throw new IllegalArgumentException("Character '" + c + 
							"' is not valid for the mask format. Use a, 9, *, ? or escape with quotes");
					}
					
					this.tests.add(definitions.get(key));
					
					if (this.tests.get(this.tests.size() - 1) != null && this.firstNonMaskPos == -1)
					{
						this.firstNonMaskPos = this.tests.size() - 1;
					}
					
					internalBuffer[i - offset] = (definitions.containsKey(key) ? placeHolder : c);
				}
			}
		}
		
		// if there aren't '?', all string must be evaluated
		if (this.partialPosition == -1)
		{
			this.partialPosition = this.length;
		}
		
		this.buffer = new char[length];
		
		// copying chars from internal buffer
		int numQuestionMark = 0;
		for (int j = 0; j < (internalBuffer.length - offset); j++)
		{
			char aux = internalBuffer[j];
			// ignore '?'
			if (aux == '?')
			{
				numQuestionMark++;
				continue;
			}
			this.buffer[j - numQuestionMark] = aux;
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
		if (keyDownHandlerRegistration != null)
		{
			keyDownHandlerRegistration.removeHandler();
			keyDownHandlerRegistration = null;
		}
		if (keyPressHandlerRegistration != null)
		{
			keyPressHandlerRegistration.removeHandler();
			keyPressHandlerRegistration = null;
		}
		if (focusHandlerRegistration != null)
		{
			focusHandlerRegistration.removeHandler();
			focusHandlerRegistration = null;
		}
		if (blurHandlerRegistration != null)
		{
			blurHandlerRegistration.removeHandler();
			blurHandlerRegistration = null;
		}
		if (pasteHandlerRegistration != null)
		{
			pasteHandlerRegistration.removeHandler();
			pasteHandlerRegistration = null;
		}
		this.textBox = null;
		this.maskedTextBox = null;
	}

	/**
	 * keyDown event handler
	 */
	@Override
	public void onKeyDown(KeyDownEvent event)
	{
		if (textBox.isReadOnly())
		{
			event.preventDefault();
		} else
		{
			int[] pos = caret(-1, -1);
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
			} else if (code == KeyCodes.KEY_ESCAPE)
			{
				clearBuffer(0, length);
				writeBuffer();
				caret(firstNonMaskPos, -1);
				event.preventDefault();
			} else
			{
				int start = pos[0];
				if(start > 0)
				{
					PreviousCharMatchResult lastMatch = getLastMatch(start - 1);
					if(!lastMatch.previousCharMatches)
					{
						caret(lastMatch.previousMatchPosition + 1, lastMatch.previousMatchPosition + 1);
					}
				}
			}
		}
	}

	private PreviousCharMatchResult getLastMatch(int beforePos) 
	{
		PreviousCharMatchResult result = new PreviousCharMatchResult();
		
		boolean previousReached = false;
		
		for(int i = beforePos; i >= 0; i--)
		{
			String acceptable = tests.get(i);
			
			if(acceptable != null)
			{
				if(("" + buffer[i]).matches(acceptable))
				{
					if(!previousReached)
					{
						result.previousCharMatches = true;
					}
					
					result.previousMatchPosition = i;
					break;
				}

				previousReached = true;
			}
		}
		
		return result;
	}
	
	private static class PreviousCharMatchResult
	{
		boolean previousCharMatches = false;
		int previousMatchPosition = -1;
	}
	
	@Override
	public void onPaste(PasteEvent event)
	{
		checkVal(true);
	}

	/**
	 * keyPress event handler
	 */
	@Override
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

		} else
		{
			// WORKAROUND TO FIX KEYPRESS ON FIREFOX
			if (event.getNativeEvent() != null && KeyCodes.KEY_TAB == event.getNativeEvent().getKeyCode())
			{
				return;
			}
		}
		event.preventDefault();
	}

	/**
	 * Focus event handler
	 */
	@Override
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
	@Override
	public void onBlur(BlurEvent event)
	{
		checkVal(false);
		if (!textBox.getText().equals(focusText))
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
				if (j < length && ("" + buffer[j]).matches(tests.get(i)))
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
		for (int i = pos; i < length; i++)
		{
			if (tests.get(i) != null && tests.get(i).length() > 0)
			{
				int j = seekNext(i);
				char t = buffer[i];
				buffer[i] = c;
				if (j < length && (""  +t).matches(tests.get(j)))
				{
					c = t;
				} else
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
			if(clearIfNotValid)
			{
				textBox.setText("");
				clearBuffer(0, length);
			}
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
