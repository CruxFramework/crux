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
package org.cruxframework.crux.smartfaces.client.dialog;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;
import org.cruxframework.crux.smartfaces.client.dialog.animation.HasDialogAnimation;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * A View Container that render its views inside a floating dialog box.
 * @author Thiago da Rosa de Bustamante
 */
public class DialogViewContainer extends SingleViewContainer implements HasDialogAnimation
{
	private DialogBox dialog;
	private FlowPanel contentPanel; 
	private View innerView;
	private boolean unloadViewOnClose;
	
	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 */
	public static void show(String viewName)
	{
		DialogViewContainer container = createDialog(viewName, viewName, true);
		container.center();
	}

	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param animation animates the dialog while showing or hiding
	 */
	public static void show(String viewName, DialogAnimation animation)
	{
		DialogViewContainer container = createDialog(viewName, viewName, true);
		container.setAnimation(animation);
		container.center();
	}

	/**
	 * Opens a dialog container using a non static way. 
	 * @param viewName name of the view to be opened
	 * @param animation animates the dialog while showing or hiding
	 */
	public void showView(String viewName, DialogAnimation animation)
	{
		assert(dialog != null):"Dialog is not created yet.";
		dialog.setAnimation(animation);
		dialog.show();
		dialog.center();
	}
	
	/**
	 * Opens a dialog container using a non static way. 
	 * @param viewName name of the view to be opened
	 * @param animation animates the dialog while showing or hiding
	 */
	public void showView(String viewName)
	{
		assert(dialog != null):"Dialog is not created yet.";
		dialog.show();
		dialog.center();
	}
	
	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param viewId ID to be used to identify the opened view
	 * @param closable if true, a close button will be present at to top of dialog box
	 */
	public static void show(String viewName, String viewId, boolean closable)
	{
		DialogViewContainer container = createDialog(viewName, viewId, closable);
		container.center();
	}

	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param viewId ID to be used to identify the opened view
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param animation animates the dialog while showing or hiding
	 */
	public static void show(String viewName, String viewId, boolean closable, DialogAnimation animation)
	{
		DialogViewContainer container = createDialog(viewName, viewId, closable);
		container.setAnimation(animation);
		container.center();
	}

	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param viewId ID to be used to identify the opened view
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param baseStyleName the dialog base CSS class name
	 */
	public static void show(String viewName, String viewId, boolean movable, boolean resizable, boolean closable, boolean modal, String baseStyleName)
	{
		DialogViewContainer container = createDialog(viewName, viewId, movable, resizable, closable, modal, baseStyleName, null, null, -1, -1, null);				
		container.center();
	}
	
	/**
	 * Create a dialog container, load the given view into the container and open the dialog box. 
	 * @param viewName name of the view to be opened
	 * @param viewId ID to be used to identify the opened view
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param baseStyleName the dialog base CSS class name
	 * @param animation animates the dialog while showing or hiding
	 */
	public static void show(String viewName, String viewId, boolean movable, boolean resizable, boolean closable, boolean modal, String baseStyleName, DialogAnimation animation)
	{
		DialogViewContainer container = createDialog(viewName, viewId, movable, resizable, closable, modal, baseStyleName, null, null, -1, -1, null);				
		container.setAnimation(animation);
		container.center();
	}
	
	/**
	 * Create a new DialogViewContainer and load the given view into the container
	 * @param viewName name of the view to be loaded
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName)
	{
		return createDialog(viewName, viewName, true, false, true, true, DialogBox.DEFAULT_STYLE_NAME, null, null, -1, -1, null);
	}
	
	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closable)
	{
		return createDialog(viewName, viewId, true, false, closable, true, DialogBox.DEFAULT_STYLE_NAME, null, null, -1, -1, null);
	}

	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param baseStyleName the dialog base CSS class name
	 * @param width dialog box width
	 * @param height dialog box height
	 * @param left left position of the dialog box
	 * @param top top position of the dialog box
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean movable, boolean resizable, boolean closable, boolean modal, 
												String baseStyleName, String width, String height, int left, int top)
	{
		return createDialog(viewName, viewId, movable, resizable, closable, modal, baseStyleName, width, height, left, top, null);
	}
	
	/**
	 * Create a new DialogViewContainer and load the given view into the container.
	 * @param viewName name of the view to be loaded
	 * @param viewId ID to be used to identify the opened view
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param baseStyleName the dialog base CSS class name
	 * @param width dialog box width
	 * @param height dialog box height
	 * @param left left position of the dialog box
	 * @param top top position of the dialog box
	 * @param parameter any object you want to pass as parameter to the view's activate and load event
	 * @return the container created
	 */
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean movable, boolean resizable, boolean closable, boolean modal, 
												String baseStyleName, String width, String height, int left, int top, Object parameter)
	{
		DialogViewContainer container = new DialogViewContainer(movable, resizable, closable, modal, baseStyleName);
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
	 * Default Constructor. The created dialog will be movable, resizable, closable and modal.
	 */
	public DialogViewContainer()
	{
		this(true, true, true, true, DialogBox.DEFAULT_STYLE_NAME);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param movable if true, the window can be dragged
	 * @param resizable if true, the window can be resized
	 * @param closable if true, a close button will be present at to top of dialog box
	 * @param modal if true, a modal dialog will be created
	 * @param baseStyleName the dialog base CSS class name
	 */
	public DialogViewContainer(boolean movable, boolean resizable, boolean closable, boolean modal, String baseStyleName)
	{
		super(null, true);
		unloadViewOnClose = true;

		dialog = new DialogBox(movable, resizable, closable, modal, baseStyleName);
		contentPanel = new FlowPanel();
		contentPanel.setWidth("100%");
		dialog.setWidget(contentPanel);
		dialog.addAttachHandler(new Handler()
		{
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					bindToDOM();
				}
				else
				{
					unbindToDOM();
				}
			}
		});
		
		initWidget(dialog);
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
	 * Enable or disable the autoHide feature. When enabled, the popup will be
	 * automatically hidden when the user clicks outside of it.
	 * 
	 * @param autoHide
	 *            true to enable autoHide, false to disable
	 */
	public void setAutoHideEnabled(boolean autoHide)
	{
		dialog.setAutoHideEnabled(autoHide);;
	}

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the user clicks outside of it.
	 * 
	 * @return true if autoHide is enabled, false if disabled
	 */
	public boolean isAutoHideEnabled()
	{
		return dialog.isAutoHideEnabled();
	}

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the history token changes, such as when the user presses the
	 * browser's back button.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isAutoHideOnHistoryEventsEnabled()
	{
		return dialog.isAutoHideOnHistoryEventsEnabled();
	}
	
	/**
	 * Enable or disable autoHide on history change events. When enabled, the
	 * popup will be automatically hidden when the history token changes, such
	 * as when the user presses the browser's back button. Disabled by default.
	 * 
	 * @param enabled
	 *            true to enable, false to disable
	 */
	public void setAutoHideOnHistoryEventsEnabled(boolean enabled) 
	{
		dialog.setAutoHideOnHistoryEventsEnabled(enabled);
	}

	/**
	 * Sets the style name to be used on the glass element. 
	 * 
	 * @param glassStyleName
	 *            the glass element's style name
	 */
	public void setGlassStyleName(String glassStyleName)
	{
		dialog.setGlassStyleName(glassStyleName);
	}

	/**
	 * Gets the style name to be used on the glass element. 
	 * 
	 * @return the glass element's style name
	 */
	public String getGlassStyleName()
	{
		return dialog.getGlassStyleName();
	}	

	/**
	 * Defines the animation used to animate popup entrances and exits
	 * @param animation
	 */
	public void setAnimation(DialogAnimation animation)
	{
		dialog.setAnimation(animation);
	}
	
	@Override
    public boolean isAnimationEnabled()
    {
	    return dialog.isAnimationEnabled();
    }

	@Override
    public void setAnimationEnabled(boolean enable)
    {
		dialog.setAnimationEnabled(enable);
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
		dialog.setPosition(left, top);
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
		assert(innerView != null):"There is no View loaded into this container.";
		dialog.center();
	}
	
	/**
	 * Read the current view loaded into this container.
	 * @return current view loaded into this container.
	 */
	public View getView()
	{
		return innerView;
	}
	
	/**
	 * Open the container dialog box. Assert that you previously loaded a view inside the container. 
	 */
	public void show()
	{
		assert(innerView != null):"There is no View loaded into this container.";
		dialog.show();
	}
	
	/**
	 * Close the container dialog.
	 */
	public void hide()
	{
		hide(unloadViewOnClose);
	}

	/**
	 * Close the container dialog. If unloadView parameter is true, try to unload the current view first.
	 * <p> If this view can not be unloaded (its unload event handler cancel the event), 
	 * this method does not close the container and returns false.</p>
	 * 
	 * @param unloadView if true, unload the current view before close the container.
	 * @return if the dialog was closed.
	 */
	public boolean hide(boolean unloadView)
	{
		if (unloadView)
		{
			if (!remove(innerView))
			{
				return false;
			}
		}
		dialog.hide();
		return true;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy, Object parameter)
	{
	    assert(views.isEmpty()):"DialogViewContainer can not contain more then one view";
	    innerView = view;
	    boolean added = super.doAdd(view, lazy, parameter);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	innerView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	innerView = null;
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
		this.dialog.setDialogTitle(title);
	}
}
