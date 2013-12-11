package org.cruxframework.crux.widgets.client.dialogcontainer;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability.Movable;
import org.cruxframework.crux.widgets.client.util.draganddrop.ResizeCapability;
import org.cruxframework.crux.widgets.client.util.draganddrop.ResizeCapability.Resizable;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog box built upon DIV elements.
 * @author Gesse Dafe
 */
public class FlatDialogBox extends PopupPanel implements Movable<Label>, Resizable<Label>
{
	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 50;
	
	private SimplePanel body = new SimplePanel();;
	private Label title = new Label();
	private Button closeBtn = new Button();
	private Label moveHandle;
	private Label resizeHandle;
		
	public FlatDialogBox() 
	{
		setStyleName("crux-FlatDialog");
		setGlassEnabled(true);
		setGlassStyleName("dialogGlass");
		
		body.setStyleName("dialogBody");

		FlowPanel topBar = prepareTopBar();
		resizeHandle = prepareResizer();
		
		FlowPanel split = new FlowPanel();
		split.setStyleName("dialogTitleBodySplit");
		split.add(topBar);
		split.add(body);
		split.add(resizeHandle);
		
		super.setWidget(split);
		
		MoveCapability.addMoveCapability(this);
		ResizeCapability.addResizeCapability(this, MIN_WIDTH, MIN_HEIGHT);
	}

	private Label prepareResizer() 
	{
		Label resizer = new Label();
		resizer.setStyleName("dialogResizer");
		return resizer;
	}

	private FlowPanel prepareTopBar() 
	{
		FlowPanel topBar = new FlowPanel();
		topBar.setStyleName("dialogTopBar");
		
		title.setStyleName("dialogTitle");
		
		closeBtn.setStyleName("dialogCloseButton");
		closeBtn.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				hide();
			}
		});
		
		moveHandle = new Label();
		moveHandle.setStyleName("dialogTopBarDragHandle");
		
		topBar.add(title);
		topBar.add(moveHandle);
		topBar.add(closeBtn);
		
		return topBar;
	}
	
	public void setTitle(String text)
	{
		title.setText(text);
	}
	
	public void setCloseble(boolean closeable)
	{
		closeBtn.setVisible(closeable);
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
}
