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
package br.com.sysmap.crux.widgets.client.scrollbanner;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.widgets.client.util.TextSelectionUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A banner for showing multiple text messages, one each time. The messages are displayed in a periodic and sequential way.  
 * @author Gesse S. F. Dafe
 */
public class ScrollBanner extends Composite
{
	public static final String DEFAULT_STYLE_NAME = "crux-ScrollBanner" ;
	
	private Label messagesLabel;
	private Label messageCountLabel;
	private int messageScrollingPeriod;
	
	private int currentMessageIndex = -1;
	private List<String> messages = new ArrayList<String>();

	private Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			showNextMessage();				
		}			
	};

	/**
	 * Parameterized constructor
	 * @param messageScrollingPeriod the period, in milliseconds, for swaping messages
	 */
	public ScrollBanner(int messageScrollingPeriod)
	{
		this();
		this.messageScrollingPeriod = messageScrollingPeriod;
		timer.scheduleRepeating(this.messageScrollingPeriod);
	}
	
	/**
	 * Default constructor
	 */
	public ScrollBanner()
	{
		HorizontalPanel basePanel = new HorizontalPanel();
		basePanel.setStyleName(DEFAULT_STYLE_NAME);
		basePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		messagesLabel = new Label(" ");
		messagesLabel.setStyleName("message");
		messagesLabel.getElement().getStyle().setProperty("overflow", "hidden");
				
		HorizontalPanel verticalCenteringPanel = createMessageVerticalCenteringPanel();
		SimplePanel overflowHiddenPanel = createMessageOverflowHiddenPanel(verticalCenteringPanel);		
		
		basePanel.add(overflowHiddenPanel);
		basePanel.setCellHorizontalAlignment(overflowHiddenPanel, HasHorizontalAlignment.ALIGN_CENTER);
		basePanel.setCellVerticalAlignment(overflowHiddenPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		VerticalPanel controllersPanel = createScrollingControlPanel();
		basePanel.add(controllersPanel);
		basePanel.setCellWidth(controllersPanel, "30");
		basePanel.setCellHorizontalAlignment(controllersPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		basePanel.getElement().getStyle().setProperty("tableLayout", "fixed");
	
		initWidget(basePanel);
	}

	/**
	 * Creates a panel for hiding long texts that don't fit the banner size 
	 * @return
	 */
	private SimplePanel createMessageOverflowHiddenPanel
	(
		HorizontalPanel verticalCenteringPanel) {
		SimplePanel overflowHiddenPanel = new SimplePanel();
		overflowHiddenPanel.getElement().getStyle().setProperty("overflow", "hidden");
		overflowHiddenPanel.setWidth("100%");
		overflowHiddenPanel.setHeight("100%");
		overflowHiddenPanel.setWidget(verticalCenteringPanel);
		return overflowHiddenPanel;
	}

	/**
	 * Creates a panel for vertical centering short texts.
	 * @return
	 */
	private HorizontalPanel createMessageVerticalCenteringPanel() 
	{
		HorizontalPanel verticalCenteringPanel = new HorizontalPanel();
		verticalCenteringPanel.setHeight("100%");
		verticalCenteringPanel.setWidth("100%");
		verticalCenteringPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalCenteringPanel.add(messagesLabel);
		return verticalCenteringPanel;
	}

	/**
	 * Creates a vertical panel containing the scrolling buttons and the message counter
	 * @return
	 */
	private VerticalPanel createScrollingControlPanel() 
	{
		VerticalPanel controllersPanel = new VerticalPanel();
		controllersPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		controllersPanel.setHeight("100%");
		
		FocusPanel previousMsgButton = createScrollButton("previousMessageButton", createPreviousMessageClickHandler());
		controllersPanel.add(previousMsgButton);
		controllersPanel.setCellVerticalAlignment(previousMsgButton, HasVerticalAlignment.ALIGN_TOP);
		
		this.messageCountLabel = new Label(" ");
		this.messageCountLabel.setStyleName("messageCount");
		controllersPanel.add(this.messageCountLabel);
		controllersPanel.setCellVerticalAlignment(this.messageCountLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FocusPanel nextMsgButton = createScrollButton("nextMessageButton", createNextMessageClickHandler());		
		controllersPanel.add(nextMsgButton);
		controllersPanel.setCellVerticalAlignment(nextMsgButton, HasVerticalAlignment.ALIGN_BOTTOM);
		return controllersPanel;
	}

	/**
	 * Creates a scrolling button
	 * @param styleName
	 * @param handler
	 * @return
	 */
	private FocusPanel createScrollButton(String styleName, ClickHandler handler) 
	{
		Label btnFace = new Label(" ");
		TextSelectionUtils.makeUnselectable(btnFace.getElement());
		FocusPanel button = new FocusPanel(btnFace);
		button.setStyleName(styleName);
		button.addClickHandler(handler);
		return button;
	}
	
	/**
	 * Adds a new message to the banner. If no message exists, the new added message is shown. 
	 * Otherwise, it is stored in the queue, and shown timely.  
	 * @param message
	 */
	public void addMessage(String message)
	{
		this.messages.add(message);
		
		if(this.messages.size() == 1)
		{
			showNextMessage();
		}
	}
	
	/**
	 * Shows the next message in the queue.
	 */
	protected void showNextMessage()
	{
		if(this.messages.size() > 0)
		{
			currentMessageIndex = (currentMessageIndex + 1) % this.messages.size();
			showMessage(currentMessageIndex);
		}
	}
	
	/**
	 * Shows the previous message in the queue.
	 */
	protected void showPreviousMessage()
	{
		if(this.messages.size() > 0)
		{
			this.currentMessageIndex = this.currentMessageIndex - 1;
			
			if(this.currentMessageIndex < 0)
			{
				this.currentMessageIndex = this.messages.size() - 1;
			}
			
			showMessage(this.currentMessageIndex);
		}
	}

	/**
	 * Shows the Nth message in the queue.
	 * @param index
	 */
	private void showMessage(int index)
	{
		this.messagesLabel.setText(this.messages.get(index));
		this.messagesLabel.setTitle(this.messages.get(index));
		this.messageCountLabel.setText("" + (index + 1));		
	}
	
	/**
	 * Clears the message queue.
	 */
	public void clear()
	{
		this.messages = new ArrayList<String>();
		this.currentMessageIndex = -1;
	}
	
	/**
	 * Creates a click handler for the "next" scrolling button 
	 * @return
	 */
	private ClickHandler createNextMessageClickHandler()
	{
		final ScrollBanner banner = this;
		
		return new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				timer.cancel();
				banner.showNextMessage();
				timer.scheduleRepeating(messageScrollingPeriod);
			}			
		};
	}

	/**
	 * Creates a click handler for the "previous" scrolling button
	 * @return
	 */
	private ClickHandler createPreviousMessageClickHandler()
	{
		final ScrollBanner banner = this;
		
		return new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				banner.showPreviousMessage();
			}			
		};
	}
}