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
package br.com.sysmap.crux.widgets.client.textarea;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.paste.HasPasteHandlers;
import br.com.sysmap.crux.widgets.client.event.paste.PasteEvent;
import br.com.sysmap.crux.widgets.client.event.paste.PasteEventSourceRegisterFactory;
import br.com.sysmap.crux.widgets.client.event.paste.PasteHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;


/**
 * A simple TextArea that obeys the <code>maxLength</code> attribute.
 * @author Gessé S. F. Dafé
 */
public class TextArea extends com.google.gwt.user.client.ui.TextArea implements HasPasteHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-TextArea" ;
	
	private List<HandlerRegistration> registrations;
	
	private MaxLengthHandler maxLengthHandler = new MaxLengthHandler(this);	
	
	private int maxLength = -1;
	
	/**
	 * Default constructor. 
	 * <p>Attaches event handlers to the new widget, for handling maxLength attribute.</p>
	 */
	public TextArea()
	{
		PasteEventSourceRegisterFactory.getRegister().registerPasteEventSource(this, this.getElement());
		attachHandlers();
	}
	
	/**
	 * @return the maxLength
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * @param newMaxLength the maxLength to set
	 */
	public void setMaxLength(int newMaxLength)
	{
		if(newMaxLength < 0)
		{
			throw new IllegalArgumentException(WidgetMsgFactory.getMessages().textAreaInvalidMaxLengthParameter());
		}
		
		if(this.maxLength == -1)
		{
			attachHandlers();
		}
		
		this.maxLength = newMaxLength;
		
		this.maxLengthHandler.maybeTruncateText();
	}
	
	/**
	 * Clears the maxLength attribute
	 */
	public void clearMaxLength()
	{
		this.maxLength = -1;
		removeHandlers();
	}
	
	/**
	 * Attaches event handlers to the new widget, for handling maxLength attribute.
	 */
	private void attachHandlers()
	{
		this.registrations = new ArrayList<HandlerRegistration>();		
		this.registrations.add(addKeyDownHandler(this.maxLengthHandler));
		this.registrations.add(addChangeHandler(this.maxLengthHandler));
		this.registrations.add(addPasteHandler(this.maxLengthHandler));		
	}	

	/**
	 * Removes all event handlers used for handling maxLength attribute.
	 */
	private void removeHandlers()
	{
		for(HandlerRegistration registration : this.registrations)
		{
			registration.removeHandler();
		}		
		registrations.clear();
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.paste.HasPasteHandlers#addPasteHandler(br.com.sysmap.crux.widgets.client.event.paste.PasteHandler)
	 */
	public HandlerRegistration addPasteHandler(PasteHandler handler)
	{
		return addHandler(handler, PasteEvent.getType());
	}
	
	@Override
	public void setValue(String value, boolean fireEvents)
	{
		super.setValue(value, fireEvents);
		this.maxLengthHandler.maybeTruncateText();
	}
	
	@Override
	public void setText(String text)
	{
		super.setText(text);
		this.maxLengthHandler.maybeTruncateText();
	}
	
	/**
	 * Truncates the text of the widget when it exceeds its maxLength.
	 * @author Gessé S. F. Dafé
	 */
	private static class MaxLengthHandler implements KeyDownHandler, ChangeHandler, PasteHandler
	{
		private final TextArea textArea;
		
		public MaxLengthHandler(TextArea textArea)
		{
			this.textArea = textArea;
		}

		/**
		 * @see com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt.event.dom.client.KeyDownEvent)
		 */
		public void onKeyDown(KeyDownEvent event)
		{
			maybeTruncateText();
		}

		/**
		 * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
		 */
		public void onChange(ChangeEvent event)
		{
			maybeTruncateText();
		}
		
		/**
		 * @see br.com.sysmap.crux.widgets.client.event.paste.PasteHandler#onPaste(br.com.sysmap.crux.widgets.client.event.paste.PasteEvent)
		 */
		public void onPaste(PasteEvent event)
		{
			maybeTruncateText();
		}

		/**
		 * If the current textArea's text length is greater than the maxLength, truncates it.
		 * Its is done asynchronously, because of onKeyDown event's nature.
		 */
		private void maybeTruncateText()
		{
			truncTimer.schedule(1);			
		}
		
		Timer truncTimer = new Timer()
		{			
			@Override
			public void run()
			{
				TextArea target = textArea;
				int length = target.getText().length();
				if(target.maxLength != -1 && length > target.maxLength)
				{
					int cursorPos = target.getCursorPos();
					String newValue = target.getText().substring(0, target.maxLength);
					target.setText(newValue);
					
					if(cursorPos <= target.maxLength)
					{
						target.setCursorPos(cursorPos);
					}
				}
			}
		};
	}	
}