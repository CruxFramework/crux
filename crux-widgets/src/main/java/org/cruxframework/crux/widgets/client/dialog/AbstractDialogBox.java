package org.cruxframework.crux.widgets.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability.Movable;
import org.cruxframework.crux.widgets.client.util.draganddrop.ResizeCapability;
import org.cruxframework.crux.widgets.client.util.draganddrop.ResizeCapability.Resizable;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The base implementation for dialog boxes.
 * @author Gesse Dafe
 */
public abstract class AbstractDialogBox extends PopupPanel implements Movable<Label>, Resizable<Label>, HasCloseHandlers<PopupPanel>, HasOpenHandlers<PopupPanel>
{
	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 50;
	
	private SimplePanel body = new SimplePanel();
	private Label title = new Label();
	private Button closeBtn = new Button();
	private Label moveHandle;
	private Label resizeHandle;
	
	private static List<CloseHandler<PopupPanel>> defaultCloseHandlers = new ArrayList<CloseHandler<PopupPanel>>();
	private static List<OpenHandler<PopupPanel>> defaultOpenHandlers = new ArrayList<OpenHandler<PopupPanel>>();
	
	public AbstractDialogBox()
	{
		this(true, true, true);
	}
	
	public AbstractDialogBox(boolean movable, boolean resizable, boolean closable) 
	{
		setStyleName("crux-Dialog");
		setGlassEnabled(true);
		setGlassStyleName("dialogGlass");

		FlowPanel topBar = prepareTopBar(movable, closable);
		
		body.setStyleName("dialogBody");

		FlowPanel split = new FlowPanel();
		split.setStyleName("dialogTitleBodySplit");
		split.add(topBar);
		split.add(body);
		
		if(defaultCloseHandlers != null)
		{
			for(CloseHandler<PopupPanel> closeHandler : defaultCloseHandlers)
			{
				this.addCloseHandler(closeHandler);
			}
		}
		
		if(defaultOpenHandlers != null)
		{
			for(OpenHandler<PopupPanel> openHandler : defaultOpenHandlers)
			{
				this.addOpenHandler(openHandler);
			}
		}
		
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
	}

	/**
	 * Prepares the handle used to resize the dialog
	 * @return
	 */
	private Label prepareResizer() 
	{
		Label resizer = new Label();
		resizer.setStyleName("dialogResizer");
		return resizer;
	}

	@Override
	public void show() 
	{
		super.show();
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
		{
			@Override
			public boolean execute() 
			{
				if(isShowing())
				{
					return true;
				}
				OpenEvent.fire(AbstractDialogBox.this, AbstractDialogBox.this);				
				return false;
			}
		}, 100);
	}
	
	@Override
	public void hide() 
	{
		super.hide();
		CloseEvent.fire(AbstractDialogBox.this, AbstractDialogBox.this);
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
		topBar.setStyleName("dialogTopBar");
		
		title.setStyleName("dialogTitle");
		topBar.add(title);
		
		if(movable)
		{
			moveHandle = new Label();
			moveHandle.setStyleName("dialogTopBarDragHandle");
			topBar.add(moveHandle);
		}		
		
		if(closable)
		{
			if(closable)
			{
				closeBtn.setStyleName("dialogCloseButton");
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
	 * @param closeable
	 */
	public void setClosable(boolean closable)
	{
		closeBtn.setVisible(closable);
	}
	
	@Override
	public void setTitle(String text)
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
	public void setWidth(String width) 
	{
		getElement().getStyle().setProperty("width", width);
	}
	
	@Override
	public void setHeight(String height) 
	{
		getElement().getStyle().setProperty("height", height);
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
	public void setPosition(int x, int y)
	{
		setPopupPosition(x, y);
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
	public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler)
	{
		return addHandler(handler, CloseEvent.getType());
	}
	
	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<PopupPanel> handler) 
	{
		return addHandler(handler, OpenEvent.getType());
	}
	
	/**
	 * Add a default open handler that will be appended to each created object
	 * @param defaultOpenHandler
	 */
	public static void addDefaultOpenHandler(OpenHandler<PopupPanel> defaultOpenHandler) 
	{
		if(defaultOpenHandlers == null)
		{
			defaultOpenHandlers = new ArrayList<OpenHandler<PopupPanel>>();
		}
		defaultOpenHandlers.add(defaultOpenHandler);
	}
	
	/**
	 * Add a default close handler that will be appended to each created object
	 * @param defaultCloseHandler
	 */
	public static void addDefaultCloseHandler(CloseHandler<PopupPanel> defaultCloseHandler) 
	{
		if(defaultCloseHandlers == null)
		{
			defaultCloseHandlers = new ArrayList<CloseHandler<PopupPanel>>();
		}
		defaultCloseHandlers.add(defaultCloseHandler);
	}
}
