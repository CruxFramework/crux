package org.cruxframework.crux.widgets.client.dialog;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple dialog which shows messages
 * @author Gesse Dafe
 */
public class FlatMessageBox extends AbstractDialogBox
{
	public static enum MessageType
	{
		SUCCESS, INFO, WARN, ERROR
	}
	
	private Label msgLabel;
	private Button hideButton;

	/**
	 * Creates a message box
	 */
	public FlatMessageBox()
	{
		super(true, true, false);
		addStyleName("crux-FlatMessageBox");
		Widget content = createMessagePanel();
		setWidget(content);
	}
	
	/**
	 * Shows a message box
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	public static FlatMessageBox show(String message, MessageType type)
	{
		FlatMessageBox msgBox = new FlatMessageBox(); 
		msgBox.setMessage(message, type);
		msgBox.show();
		msgBox.center();
		return msgBox;
	}
	
	/**
	 * Sets the message to be shown
	 * @param message the text to be displayed
	 * @param type the message type, used to apply a particular style
	 */
	private void setMessage(String message, MessageType type)
	{
		this.msgLabel.setText(message);
		for(MessageType anyType : MessageType.values())
		{
			this.removeStyleDependentName(anyType.name().toLowerCase());
		}
		this.addStyleDependentName(type.name().toLowerCase());
	}
	
	/**
	 * Changes the hide button's text
	 * @param btnText
	 */
	public void setButtonText(String btnText)
	{
		hideButton.setText(btnText);
	}

	/**
	 * Creates a progress bar animation to be inserted in progress box
	 * @return
	 */
	private Widget createMessagePanel() 
	{
		FlowPanel contents = new FlowPanel();
		contents.setStyleName("messageBoxContents");
		
		msgLabel = new Label();
		contents.add(msgLabel);
		
		hideButton = new Button();
		hideButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				hide();
			}
		});
		hideButton.setText("OK");
		contents.add(hideButton);

		return contents;
	}	
}