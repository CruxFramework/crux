/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.dialog;


import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.WidgetMsgFactory;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.label.HTML;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple question dialog box
 * @author Thiago da Rosa de Bustamante
 */
public class QuestionBox extends AbstractDialogBox
{
	public static final String DEFAULT_STYLE_NAMES = "faces-QuestionBox faces-popup";

	private HTML msgLabel;
	private NavPanel buttonsPanel;

	/**
	 * Creates a question box
	 */
	public QuestionBox()
	{
		this(true, false, false, DEFAULT_STYLE_NAMES);
	}
	
	/**
	 * Creates a question dialog box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be closed
	 * @param modal if true this dialog disables events that does not target the dialog 
	 * @param styleName the dialog base CSS class name
	 */
	public QuestionBox(boolean movable, boolean resizable, boolean closable, String styleName)
	{
		super(movable, resizable, closable, true, styleName);
		Widget content = createMessagePanel();
		super.setWidget(content);
	}
	
	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box 
	 * @param message the text to be displayed in the body of the question box
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, String[] labels, SelectHandler... handlers)
	{
		return show(title, message, true, false, false, DEFAULT_STYLE_NAMES, null, labels, handlers);
	}
	
	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box 
	 * @param message the text to be displayed in the body of the question box
	 * @param animation animates the dialog while showing or hiding the question box
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, DialogAnimation animation, String[] labels, SelectHandler... handlers)
	{
		return show(title, message, true, false, false, DEFAULT_STYLE_NAMES, animation, labels, handlers);
	}

	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box 
	 * @param message the text to be displayed in the body of the question box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be closed
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, boolean movable, boolean resizable, boolean closable, 
								String[] labels, SelectHandler... handlers)
	{
		return show(title, message, movable, resizable, closable, DEFAULT_STYLE_NAMES, null, labels, handlers);
	}
	
	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box 
	 * @param message the text to be displayed in the body of the question box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be closed
	 * @param animation animates the dialog while showing or hiding the question box
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, boolean movable, boolean resizable, boolean closable, 
							DialogAnimation animation, String[] labels, SelectHandler... handlers)
	{
		return show(title, message, movable, resizable, closable, DEFAULT_STYLE_NAMES, animation, labels, handlers);
	}


	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box
	 * @param message the text to be displayed in the body of the question box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be closed
	 * @param styleName the name of the CSS class to be applied in the question box element 
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, boolean movable, boolean resizable, boolean closable, String styleName, 
								String[] labels, SelectHandler... handlers)
	{
		return show(title, message, movable, resizable, closable, styleName, null, labels, handlers);
	}
	
	/**
	 * Shows a question box
	 * @param title the text to be displayed as the caption of the question box 
	 * @param message the text to be displayed in the body of the question box
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, the window can be closed
	 * @param styleName the name of the CSS class to be applied in the question box element 
	 * @param animation animates the dialog while showing or hiding the question box
	 * @param labels the question buttons labels
	 * @param handlers the question buttons event handlers
	 */
	public static QuestionBox show(String title, String message, boolean movable, boolean resizable, boolean closable, String styleName, 
									DialogAnimation animation, String[] labels, SelectHandler... handlers)
	{
		QuestionBox questionBox = new QuestionBox(movable, resizable, closable, styleName); 
		questionBox.setDialogTitle(title);
		questionBox.setMessage(message);
		questionBox.setAnimation(animation);
		
		assert(labels!= null && handlers!= null && labels.length == handlers.length):"You must informe one label for each selectHandler provided";
		
		for(int i=0; i < labels.length; i++)
		{
			questionBox.addAnswer(labels[i], handlers[i]);
		}
		
		questionBox.center();
		return questionBox;
	}

	@Override
	public void setWidget(IsWidget w)
	{
		throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().canNotAddWidgetOnThisDialog());
	}
	
	@Override
	public void setWidget(Widget w)
	{
		throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().canNotAddWidgetOnThisDialog());
	}

	/**
	 * Sets the message to be shown
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public void setMessage(SafeHtml message)
	{
		this.msgLabel.setHTML(message);
	}
	
	/**
	 * Sets the message to be shown
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public void setMessage(String message)
	{
		this.msgLabel.setText(message);
	}
	
	/**
	 * Add a new button with an answer
	 * @param label button label
	 * @param handler button select event handler
	 */
	public void addAnswer(String label, SelectHandler handler)
	{
		addAnswer(label, handler, null);
	}

	/**
	 * Add a new button with an answer
	 * @param label button label
	 * @param handler button select event handler
	 * @param styleName button styleName
	 */
	public void addAnswer(String label, final SelectHandler handler, String styleName)
	{
		Button button = new Button();
		if (handler != null)
		{
			button.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					try
					{
						hide();
						handler.onSelect(event);
					}
					catch(Exception e)
					{
						Crux.getErrorHandler().handleError(e);
					}
				}
			}); 
		}
		button.setText(label);
		if (!StringUtils.isEmpty(styleName))
		{
			button.setStyleName(styleName);
		}
		buttonsPanel.add(button);
	}
	
	/**
	 * Creates the message panel to be inserted in question box
	 * @return
	 */
	private Widget createMessagePanel() 
	{
		FlowPanel contents = new FlowPanel();
		contents.setStyleName("questionContents");
		
		msgLabel = new HTML();
		contents.add(msgLabel);
		
		buttonsPanel = new NavPanel();
		buttonsPanel.setStyleName("questionButtons");
		
		contents.add(buttonsPanel);

		return contents;
	}
}