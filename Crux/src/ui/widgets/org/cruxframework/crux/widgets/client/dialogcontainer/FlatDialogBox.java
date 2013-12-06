package org.cruxframework.crux.widgets.client.dialogcontainer;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler;
import org.cruxframework.crux.widgets.client.event.openclose.HasBeforeCloseHandlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A simple dialog box built upon DIV elements.
 * @author Gesse Dafe
 */
public class FlatDialogBox extends Composite implements HasBeforeCloseHandlers
{
	private PopupPanel popup = new PopupPanel();
	private SimplePanel body = new SimplePanel();;
	private Label title = new Label();
	private Button closeBtn = new Button();
		
	public FlatDialogBox() 
	{
		initWidget(popup);
		
		popup.setModal(true);
		popup.setAutoHideEnabled(false);
		
		setStyleName("crux-FlatDialog");

		FlowPanel topBar = new FlowPanel();
		topBar.setStyleName("topBar");
		
		body.setStyleName("body");
		title.setStyleName("title");
		
		closeBtn.setStyleName("closeButton");
		
		topBar.add(title);
		topBar.add(closeBtn);
		
		FlowPanel popupParts = new FlowPanel();
		popupParts.setStyleName("titleBodySplit");
		popupParts.add(title);
		popupParts.add(body);
		
		popup.add(popupParts);
	}
	
	public void setContent(IsWidget widget)
	{
		body.setWidget(widget);
	}
	
	public void setTitle(String text)
	{
		title.setText(text);
	}
	
	public void setCloseble(boolean closeable)
	{
		closeBtn.setVisible(closeable);
	}
	
	public void setModal(boolean modal)
	{
		popup.setModal(modal);
	}
	
	public void setAutoHide(boolean autoHide)
	{
		popup.setAutoHideEnabled(autoHide);
	}
	
	public void open()
	{
		popup.show();
	}
	
	public void close()
	{
		popup.hide();
	}

	public void setPopupPosition(int left, int top) 
	{
		popup.setPopupPosition(left, top);		
	}

	public boolean isModal() 
	{
		return popup.isModal();
	}

	public void center() 
	{
		popup.center();		
	}
	
	@Override
	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler) 
	{
		return addHandler(handler, BeforeCloseEvent.getType());
	}
}
