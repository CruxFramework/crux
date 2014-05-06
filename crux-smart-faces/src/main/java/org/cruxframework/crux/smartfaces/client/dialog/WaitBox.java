package org.cruxframework.crux.smartfaces.client.dialog;

import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows a wait widget and a message
 * @author Thiago da Rosa de Bustamante
 */
public class WaitBox extends AbstractDialogBox
{
	public static final String DEFAULT_STYLE_NAME = "faces-WaitBox";

	/**
	 * Creates a wait box
	 */
	public WaitBox()
	{
		this(true);
	}
	
	/**
	 * Creates a wait box
	 * @param movable
	 */
	public WaitBox(boolean movable)
	{
		super(movable, false, false, true, DEFAULT_STYLE_NAME);
		setWidget(createInfiniteProgressBar());
	}

	/**
	 * Sets the message to be shown
	 * @param message
	 */
	public void setMessage(String message)
	{
		super.setDialogTitle(message);
	}
	
	/**
	 * Shows a wait box
	 * @param message the text to be displayed
	 */
	public static WaitBox show(String message)
	{
		return show(message, null);
	}
	
	/**
	 * Shows a wait box
	 * @param message the text to be displayed
	 * @param animation animates the dialog while showing or hiding
	 */
	public static WaitBox show(String message, DialogAnimation animation)
	{
		WaitBox waitBox = new WaitBox();
		if (animation != null)
		{
			waitBox.setAnimation(animation);
		}
		waitBox.setMessage(message);
		waitBox.center();
		return waitBox;
	}

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createInfiniteProgressBar() 
	{
		HTML bar = new HTML("<div class='progressBarSlot'></div><div class='progressBarFill'></div>");
		bar.setStyleName("faces-InfinityProgressBar");
		return bar;
	}	
}