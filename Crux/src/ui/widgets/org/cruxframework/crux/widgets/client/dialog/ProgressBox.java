package org.cruxframework.crux.widgets.client.dialog;

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
		addStyleName("crux-ProgressBox");
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
}