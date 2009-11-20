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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Comment
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class ScrollBanner extends Composite
{
	public static final String DEFAULT_STYLE_NAME = "crux-ScrollBanner" ;
	
	private HorizontalPanel basePannel;
	private Label messagesLabel;
	private Label messageCountLabel;
	private FocusPanel nextMsgButton;
	private FocusPanel previousMsgButton;
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
	 * @param messageScrollingInterval
	 */
	public ScrollBanner(int messageScrollingPeriod)
	{
		this();
		this.messageScrollingPeriod = messageScrollingPeriod;
		timer.scheduleRepeating(this.messageScrollingPeriod);
	}
	
	/**
	 * 
	 */
	public ScrollBanner()
	{
		VerticalPanel controllersPanel = new VerticalPanel();
		controllersPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		controllersPanel.setHeight("100%");
		
		Label previousMsgButtonFace = new Label(" ");
		TextSelectionUtils.makeUnselectable(previousMsgButtonFace.getElement());
		previousMsgButton = new FocusPanel(previousMsgButtonFace);
		previousMsgButton.setStyleName("previousMessageButton");
		previousMsgButton.addClickHandler(createPreviousMessageClickHandler());
		controllersPanel.add(previousMsgButton);
		controllersPanel.setCellVerticalAlignment(previousMsgButton, HasVerticalAlignment.ALIGN_TOP);
		
		messageCountLabel = new Label(" ");
		messageCountLabel.setStyleName("messageCount");
		controllersPanel.add(messageCountLabel);
		controllersPanel.setCellVerticalAlignment(messageCountLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label nextMsgButtonFace = new Label(" ");
		TextSelectionUtils.makeUnselectable(nextMsgButtonFace.getElement());
		nextMsgButton = new FocusPanel(nextMsgButtonFace);
		nextMsgButton.setStyleName("nextMessageButton");
		nextMsgButton.addClickHandler(createNextMessageClickHandler());		
		controllersPanel.add(nextMsgButton);
		controllersPanel.setCellVerticalAlignment(nextMsgButton, HasVerticalAlignment.ALIGN_BOTTOM);
		
		basePannel = new HorizontalPanel();
		basePannel.setStyleName(DEFAULT_STYLE_NAME);
		basePannel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		messagesLabel = new Label(" ");
		messagesLabel.setStyleName("message");
		messagesLabel.getElement().getStyle().setProperty("overflow", "hidden");
		basePannel.add(messagesLabel);
		basePannel.setCellHorizontalAlignment(messagesLabel, HasHorizontalAlignment.ALIGN_CENTER);
		basePannel.setCellVerticalAlignment(messagesLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		basePannel.add(controllersPanel);
		basePannel.setCellWidth(controllersPanel, "30");
		basePannel.setCellHorizontalAlignment(controllersPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		basePannel.getElement().getStyle().setProperty("tableLayout", "fixed");
	
		initWidget(basePannel);
	}
	
	/**
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
	 * 
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
	 * 
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
	 * @param index
	 */
	private void showMessage(int index)
	{
		int parentHeight = this.messagesLabel.getParent().getOffsetHeight();
		int parentWidth = this.messagesLabel.getParent().getOffsetWidth();
		
		this.messagesLabel.setText(this.messages.get(index));
		this.messagesLabel.setTitle(this.messages.get(index));
			
		adjustDimensions(messagesLabel, parentHeight, parentWidth);		
		
		this.messageCountLabel.setText("" + (index + 1));		
	}

	/**
	 * @param widget 
	 * @param parentWidth 
	 * @param parentHeight 
	 * @param parentHeight
	 * @param parentWidth
	 */
	private void adjustDimensions(Widget widget, int parentHeight, int parentWidth)
	{
		if(widget.getOffsetHeight() > parentHeight)
		{
			widget.getElement().getStyle().setProperty("height", "100%");
		}
		else
		{
			widget.getElement().getStyle().setProperty("height", "");
		}
		
		if(widget.getOffsetWidth() > parentWidth)
		{
			widget.getElement().getStyle().setProperty("width", "100%");
		}
		else
		{
			widget.getElement().getStyle().setProperty("width", "");
		}
	}
	
	/**
	 * 
	 */
	public void clear()
	{
		this.messages = new ArrayList<String>();
		this.currentMessageIndex = -1;
	}
	
	/**
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