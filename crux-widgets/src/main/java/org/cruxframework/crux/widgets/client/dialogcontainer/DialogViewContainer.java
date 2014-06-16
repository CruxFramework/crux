/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.dialogcontainer;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * A View Container that render its views inside a floating dialog box.
 * @author Thiago da Rosa de Bustamante
 */
public class DialogViewContainer extends SingleViewContainer 
{
	public static final String DEFAULT_STYLE_NAME = "crux-DialogViewContainer";
	private DialogBox dialog;
	private FlowPanel contentPanel; 
	private boolean unloadViewOnClose;
	
	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 */
	public static void openDialog(String viewName)
	{
		DialogViewContainer container = createDialog(viewName, viewName, true);
		container.openDialog();
	}

	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param viewId ID to be used to identify the opened view
	 * @param closeable if true, a close button will be present at to top of dialog box
	 */
	public static void openDialog(String viewName, String viewId, boolean closeable)
	{
		DialogViewContainer container = createDialog(viewName, viewId, closeable);
		container.openDialog();
	}

	/**
	 * Create a new DialogViewContainer and load the given view into the container
	 * @param viewName name of the view to be loaded
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName)
	{
		return createDialog(viewName, viewName, true);
	}
	
	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param closeable if true, a close button will be present at to top of dialog box
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable)
	{
		return createDialog(viewName, viewName, true, true, null, null, -1, -1);
	}

	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param closeable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param width dialog box width
	 * @param height dialog box height
	 * @param left left position of the dialog box
	 * @param top top position of the dialog box
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable, boolean modal, String width, String height, int left, int top)
	{
		return createDialog(viewName, viewId, closeable, modal, width, height, left, top, null);
	}
	
	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param closeable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param width dialog box width
	 * @param height dialog box height
	 * @param left left position of the dialog box
	 * @param top top position of the dialog box
	 * @param parameter any object you want to pass as parameter to the view's activate and load event
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable, boolean modal, String width, String height, int left, int top, Object parameter)
	{
		DialogViewContainer container = new DialogViewContainer(closeable, modal);
		container.showView(viewName, viewId, parameter);
		if (!StringUtils.isEmpty(width) && !StringUtils.isEmpty(height))
		{
			container.setSize(width, height);
		}
		if (left >= 0 && top >= 0)
		{
			container.setPosition(left, top);
		}
		return container;
	}
	
	/**
	 * Default Constructor. The created dialog will be closeable and modal.
	 */
	public DialogViewContainer()
	{
		this(true, true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param closeable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 */
	public DialogViewContainer(boolean closeable, boolean modal)
	{
		super(null, true);
		dialog = new DialogBox(false, modal);
		dialog.setStyleName(DEFAULT_STYLE_NAME);
		
		contentPanel = new FlowPanel();
		contentPanel.setWidth("100%");
		
		unloadViewOnClose = true;
		
		FlowPanel bodyPanel = new FlowPanel();
		bodyPanel.setWidth("100%");
		bodyPanel.setHeight("100%");
		
		if (closeable)
		{
			final Button closeBtn = new Button(" ", new SelectHandler()
			{
				public void onSelect(SelectEvent event)
				{
					closeDialog();
				}
			});
			closeBtn.setStyleName("closeButton");
			closeBtn.getElement().getStyle().setFloat(Float.RIGHT);
			
			FlowPanel headerPanel = new FlowPanel();
			headerPanel.setWidth("100%");
			headerPanel.add(closeBtn);
						
			bodyPanel.add(headerPanel);
		}
		bodyPanel.add(contentPanel);
		dialog.add(bodyPanel);

		initWidget(new Label());
	}

	/**
	 * Read the unloadViewOnClose property value. This property tells the container if it must unload the containing 
	 * view when the dialog is closed. 
	 * @return unloadViewOnClose property value
	 */
	public boolean isUnloadViewOnClose()
    {
    	return unloadViewOnClose;
    }

	/**
	 * Write the unloadViewOnClose property value. This property tells the container if it must unload the containing 
	 * view when the dialog is closed. 
	 * 
	 * @param unloadViewOnClose unloadViewOnClose property value
	 */
	public void setUnloadViewOnClose(boolean unloadViewOnClose)
    {
    	this.unloadViewOnClose = unloadViewOnClose;
    }

	/**
	 * Write the modal property value. This property tells the container if the opened dialog should be modal. 
	 * @param modal modal property value
	 */
	public void setModal(boolean modal)
	{
		dialog.setModal(modal);
	}

	/**
	 * Read the modal property value. This property tells the container if the opened dialog should be modal. 
	 * @return modal property value
	 */
	public boolean isModal()
	{
		return dialog.isModal();
	}

	/**
	 * Set the dialog dimensions.
	 * @param width dialog width
	 * @param height dialog height
	 */
	public void setSize(String width, String height)
	{
		dialog.setSize(width, height);
	}

	/**
	 * Set the dialog width
	 * @param width dialog width
	 */
	@Override
	public void setWidth(String width)
	{
		dialog.setWidth(width);
	}

	/**
	 * Set the dialog height
	 * @param width dialog height
	 */
	@Override
	public void setHeight(String height)
	{
		dialog.setHeight(height);
	}

	/**
	 * Set the dialog position
	 * @param left dialog left position
	 * @param top dialog top position
	 */
	public void setPosition(int left, int top)
	{
		dialog.setPopupPosition(left, top);
	}
	
	
	@Override
	public void setStyleName(String style)
	{
		dialog.setStyleName(style);
	}

	@Override
	public void setStyleDependentName(String styleSuffix, boolean add)
	{
		dialog.setStyleDependentName(styleSuffix, add);
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		dialog.setStyleName(style, add);
	}

	@Override
	public void setStylePrimaryName(String style)
	{
		dialog.setStylePrimaryName(style);
	}

	/**
	 * Open the dialog container and center it on the screen. If the container is already opened, just center it on the screen.
	 */
	public void center()
	{
		assert(getActiveView() != null):"There is no View loaded into this container.";
		bindToDOM();
		dialog.center();
	}
	
	/**
	 * Read the current view loaded into this container.
	 * @return current view loaded into this container.
	 */
	public View getView()
	{
		return getActiveView();
	}
	
	/**
	 * Open the container dialog box. Assert that you previously loaded a view inside the container. 
	 */
	public void openDialog()
	{
		assert(getActiveView() != null):"There is no View loaded into this container.";
		bindToDOM();
		dialog.show();
	}
	
	/**
	 * Close the container dialog.
	 */
	public void closeDialog()
	{
		closeDialog(unloadViewOnClose);
	}

	/**
	 * Close the container dialog. If unloadView parameter is true, try to unload the current view first.
	 * <p> If this view can not be unloaded (its unload event handler cancel the event), 
	 * this method does not close the container and returns false.</p>
	 * 
	 * @param unloadView if true, unload the current view before close the container.
	 * @return if the dialog was closed.
	 */
	public boolean closeDialog(boolean unloadView)
	{
		if (unloadView)
		{
			if (!remove(getActiveView()))
			{
				return false;
			}
		}
		dialog.hide();
		unbindToDOM();
		return true;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
	    assert(views.isEmpty()):"DialogViewContainer can not contain more then one view";
	    activeView = view;
	    boolean added = super.doAdd(view, lazy, parameter);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	activeView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	activeView = null;
	    }
		return removed;
	}
	
	@Override
    protected Panel getContainerPanel(View view)
    {
	    return getContainerPanel();
    }

    protected Panel getContainerPanel()
    {
	    return contentPanel;
    }
	
	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		this.dialog.setText(title);
	}
}
