package org.cruxframework.crux.widgets.client.dialog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows a progress message
 * @author Gesse Dafe
 */
public class ProgressBox extends AbstractDialogBox
{
	/**
	 * Creates a progress box
	 */
	public ProgressBox()
	{
		super(true, false, false);
		setStyleName("crux-ProgressBox");
		setWidget(createInfiniteProgressBar());
	}
	
	/**
	 * Sets the message to be shown
	 * @param message
	 */
	private void setMessage(String message)
	{
		super.setTitle(message);
	}
	
	/**
	 * Shows a progress dialog
	 * @param message the text to be displayed
	 */
	public static ProgressBox show(String message)
	{
		ProgressBox progressDialog = new ProgressBox(); 
		progressDialog.setMessage(message);
		progressDialog.show();
		progressDialog.center();
		return progressDialog;
	}

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createInfiniteProgressBar() 
	{
		FlowPanel bar = new FlowPanel();
		bar.setStyleName("crux-InfinityProgressBar");
		
		SimplePanel slot = new SimplePanel();
		slot.setStyleName("progressBarSlot");
		bar.add(slot);
		
		SimplePanel fill = new SimplePanel();
		fill.setStyleName("progressBarFill");
		bar.add(fill);

		return bar;
	}	
}