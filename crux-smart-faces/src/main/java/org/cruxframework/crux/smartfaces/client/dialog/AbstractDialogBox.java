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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.label.HTML;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.MoveCapability;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.MoveCapability.Movable;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.ResizeCapability;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.ResizeCapability.Resizable;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog box built upon DIV elements.
 * @author Gesse Dafe
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractDialogBox extends PopupPanel implements Movable<Label>, Resizable<Label>
{
	public static final String DEFAULT_STYLE_NAMES = "faces-DialogBox faces-popup";
	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 50;
	private static List<AbstractDialogBox> openDialogs = new ArrayList<AbstractDialogBox>();
	private static HandlerRegistration closeHandler;
	private SimplePanel body = new SimplePanel();
	private HTML title = new HTML();
	private Button closeBtn = new Button();
	private Label moveHandle;
	private Label resizeHandle;
	
	/**
	 * Constructor
	 */
	public AbstractDialogBox()
	{
		this(true, true, true, false, DEFAULT_STYLE_NAMES);
	}
	
	/**
	 * Constructor
	 * @param movable
	 * @param resizable
	 * @param closable
	 */
	public AbstractDialogBox(boolean movable, boolean resizable, boolean closable, boolean modal) 
	{
		this(movable, resizable, closable, modal, DEFAULT_STYLE_NAMES);
	}
	
	/**
	 * Constructor
	 * @param movable
	 * @param resizable
	 * @param closable
	 * @param modal 
	 * @param baseStyleName
	 */
	protected AbstractDialogBox(boolean movable, boolean resizable, boolean closable, boolean modal, String baseStyleName) 
	{
		super(false, modal);
		setStyleName(baseStyleName);
		setGlassStyleName("faces-overlay");

		FlowPanel topBar = prepareTopBar(movable, closable);
		
		body.setStyleName("faces-body");

		FlowPanel split = new FlowPanel();
		split.setStyleName("dialogTitleBodySplit");
		split.add(topBar);
		split.add(body);
		
		if(resizable)
		{
			resizeHandle = prepareResizer();
			split.add(resizeHandle);
		}
		
		super.setWidget(split);
		
		if(movable)
		{
			MoveCapability.addMoveCapability(this);
		}
		
		if(resizable)
		{
			ResizeCapability.addResizeCapability(this, MIN_WIDTH, MIN_HEIGHT);
		}
		if (closeHandler == null)
		{
			closeHandler = Screen.addCloseHandler(new CloseHandler<Window>()
			{
				@Override
				public void onClose(CloseEvent<Window> event)
				{
					openDialogs.clear();
				}
			});
		}
	}

	/**
	 * Prepares the handle used to resize the dialog
	 * @return
	 */
	private Label prepareResizer() 
	{
		Label resizer = new Label();
		resizer.setStyleName("faces-resizer");
		return resizer;
	}

	/**
	 * Creates the dialog's title bar 
	 * @param movable
	 * @param closable
	 * @return
	 */
	private FlowPanel prepareTopBar(boolean movable, boolean closable) 
	{
		FlowPanel topBar = new FlowPanel();
		topBar.setStyleName("faces-header");
		
		title.setStyleName("faces-title");
		topBar.add(title);
		
		if(movable)
		{
			moveHandle = new Label();
			moveHandle.setStyleName("faces-dragger");
			topBar.add(moveHandle);
		}		
		
		if(closable)
		{
			if(closable)
			{
				closeBtn.setStyleName("faces-close");
				closeBtn.addSelectHandler(new SelectHandler() 
				{
					@Override
					public void onSelect(SelectEvent event) 
					{
						hide();
					}
				});
				topBar.add(closeBtn);
			}
		}
		
		return topBar;
	}
	
	/**
	 * Makes the dialog closable 
	 * @param closable
	 */
	public void setClosable(boolean closable)
	{
		closeBtn.setVisible(closable);
	}
	
	public void setDialogTitle(SafeHtml text)
	{
		title.setHTML(text);
	}
	
	public void setDialogTitle(String text)
	{
		title.setText(text);
	}
	
	@Override
	public void setWidget(IsWidget w) 
	{
		body.setWidget(w);
	}
	
	@Override
	public void setWidget(Widget w) 
	{
		body.setWidget(w);
	}
	
	@Override
	public boolean remove(Widget w) 
	{
		return body.remove(w);
	}
	
	@Override
	public boolean remove(IsWidget child) 
	{
		return body.remove(child);
	}
	
	@Override
	public void clear() 
	{
		body.clear();
	}
		
	@Override
	public Widget getWidget() 
	{
		return body.getWidget();
	}
	
	@Override
	public Label getHandle(DragAndDropFeature feature)
	{
		if(DragAndDropFeature.MOVE.equals(feature))
		{
			return moveHandle;
		}
		else
		{
			return resizeHandle;
		}
	}

	@Override
	public void setDimensions(int w, int h)
	{
		setPixelSize(w, h);
	}

	@Override
	public int getAbsoluteWidth()
	{
		return getElement().getOffsetWidth();
	}

	@Override
	public int getAbsoluteHeight()
	{
		return getElement().getOffsetHeight();
	}
	
	@Override
	public void show()
	{
	    super.show();
	    openDialogs.add(this);
	}

	@Override
	protected void hide(boolean autoClosed)
	{
	    super.hide(autoClosed);
	    openDialogs.remove(this);
	}
	
	/**
	 * Retrieve all opened dialogs
	 * @return
	 */
	public static Iterator<AbstractDialogBox> getOpenDialogs()
	{
		return openDialogs.iterator();
	}
	
	/**
	 * Hide all opened dialogs
	 */
	public static void hideAllDialogs()
	{
		while (openDialogs.size() > 0)
        {
			openDialogs.get(0).hide();
        }
	}
}
