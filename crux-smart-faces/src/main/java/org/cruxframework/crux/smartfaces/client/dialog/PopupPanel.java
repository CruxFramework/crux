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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A panel that can "pop up" over other widgets. It overlays the browser's
 * client area (and any previously-created popups).
 * 
 * <p>
 * A PopupPanel should not generally be added to other panels; rather, it should
 * be shown and hidden using the {@link #show()} and {@link #hide()} methods.
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class PopupPanel extends SimplePanel implements HasAnimation, HasCloseHandlers<PopupPanel>, NativePreviewHandler
{
	private static final String DEFAULT_GLASS_STYLE_NAME = "faces-PopupPanelGlass";
	
	private HandlerRegistration nativePreviewHandlerRegistration;
	private HandlerRegistration historyHandlerRegistration;
	private boolean autoHideOnHistoryEvents;
	private boolean showing;
	private boolean animationEnabled;
	private boolean modal;
	private boolean autoHide;
	private FastList<Element> autoHidePartners;
	private Element glass;
	private String glassStyleName = DEFAULT_GLASS_STYLE_NAME;
	private boolean glassShowing;
	private boolean centered;
	private com.google.gwt.user.client.Element containerElement;
	private DialogAnimation animation;
	private boolean animating;
	
	
	/**
	 * Creates an empty popup panel. A child widget must be added to it before
	 * it is shown.
	 */
	public PopupPanel()
	{
		this(false);
	}
	
	/**
	 * Creates an empty popup panel, specifying its "auto-hide" property.
	 * 
	 * @param autoHide
	 *            <code>true</code> if the popup should be automatically hidden
	 *            when the user clicks outside of it or the history token
	 *            changes.
	 */
	public PopupPanel(boolean autoHide)
	{
		this(autoHide, false);
	}
	
	/**
	 * Creates an empty popup panel, specifying its "auto-hide" and "modal"
	 * properties.
	 * 
	 * @param autoHide
	 *            <code>true</code> if the popup should be automatically hidden
	 *            when the user clicks outside of it or the history token
	 *            changes.
	 * @param modal
	 *            <code>true</code> if keyboard or mouse events that do not
	 *            target the PopupPanel or its children should be ignored
	 */
	public PopupPanel(boolean autoHide, boolean modal)
	{
		this.autoHide = autoHide;
		this.autoHideOnHistoryEvents = autoHide;
		this.modal = modal;
		if (modal)
		{
			glass = Document.get().createDivElement();
			glass.setClassName(glassStyleName);

			Style style = glass.getStyle();
			style.setPosition(Position.FIXED);
			style.setLeft(0, Unit.PX);
			style.setTop(0, Unit.PX);
			style.setBottom(0, Unit.PX);
			style.setRight(0, Unit.PX);
		}
		
		containerElement = Document.get().createDivElement().cast();
		super.getContainerElement().appendChild(containerElement);
		getElement().getStyle().setPosition(Position.ABSOLUTE);
	    setPopupPosition(0, 0);
	    setStyleName(getContainerElement(), "popupContent");
	    
	}
	
	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 * 
	 * @param partner
	 *            the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner)
	{
		assert partner != null : "partner cannot be null";
		if (autoHidePartners == null)
		{
			autoHidePartners = new FastList<Element>();
		}
		autoHidePartners.add(partner);
	}

	/**
	 * Remove an autoHide partner.
	 * 
	 * @param partner
	 *            the auto hide partner to remove
	 */
	public void removeAutoHidePartner(Element partner)
	{
		assert partner != null : "partner cannot be null";
		if (autoHidePartners != null)
		{
			autoHidePartners.remove(partner);
		}
	}

	/**
	 * Determines whether or not this popup is showing.
	 * 
	 * @return <code>true</code> if the popup is showing
	 * @see #show()
	 * @see #hide()
	 */
	public boolean isShowing()
	{
		return showing;
	}

	/**
	 * Shows the popup and attach it to the page. It must have a child widget
	 * before this method is called.
	 */
	public void show()
	{
		doShow(isAnimationEnabled());
	}

	/**
	 * Normally, the popup is positioned directly below the relative target,
	 * with its left edge aligned with the left edge of the target. Depending on
	 * the width and height of the popup and the distance from the target to the
	 * bottom and right edges of the window, the popup may be displayed directly
	 * above the target, and/or its right edge may be aligned with the right
	 * edge of the target.
	 * 
	 * @param target
	 *            the target to show the popup below
	 */
	public final void showRelativeTo(final UIObject target)
	{
		setVisible(false);
		doShow(false);
		setPopupPosition(getLeftRelativeObject(target), getTopRelativeObject(target));
		setVisible(true);
		if (isAnimationEnabled())
		{
			runEntranceAnimation(null);
		}
	}

	/**
	 * Centers the popup in the browser window and shows it. If the popup was
	 * already showing, then it is centered.
	 */
	public void center()
	{
		if (!centered)
		{
			if (animating)
			{
				fixPositionToCenter();
				Scheduler.get().scheduleFixedPeriod(new RepeatingCommand()
				{
					@Override
					public boolean execute()
					{
						if (animating)
						{
							return true;
						}
						centralizeMe();
						return false;
					}
				}, 10);
			}
			else
			{
				centralizeMe();
				if (!showing)
				{
					show();
				}
			}
		}
	}	
	
	/**
	 * Hides the popup and detaches it from the page. This has no effect if it
	 * is not currently showing.
	 */
	public void hide()
	{
		hide(false);
	}

	/**
	 * Sets the style name to be used on the glass element. 
	 * 
	 * @param glassStyleName
	 *            the glass element's style name
	 */
	public void setGlassStyleName(String glassStyleName)
	{
		this.glassStyleName = glassStyleName;
		if (glass != null)
		{
			glass.setClassName(glassStyleName);
		}
	}

	/**
	 * Gets the style name to be used on the glass element. 
	 * 
	 * @return the glass element's style name
	 */
	public String getGlassStyleName()
	{
		return glassStyleName;
	}	

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler)
	{
		return addHandler(handler, CloseEvent.getType());
	}

	/**
	 * Defines the animation used to animate popup entrances and exits
	 * @param animation
	 */
	public void setAnimation(DialogAnimation animation)
	{
		this.animation = animation;
		setAnimationEnabled(animation != null);
	}
	
	@Override
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	@Override
	public void setAnimationEnabled(boolean enable)
	{
		animationEnabled = enable;
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
		this.autoHide = autoHide;
	}

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the user clicks outside of it.
	 * 
	 * @return true if autoHide is enabled, false if disabled
	 */
	public boolean isAutoHideEnabled()
	{
		return autoHide;
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
		return autoHideOnHistoryEvents;
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
		this.autoHideOnHistoryEvents = enabled;
	}

	@Override
	protected com.google.gwt.user.client.Element getContainerElement()
	{
	    return containerElement;
	}
	
	/**
	 * Sets the popup's position relative to the browser's client area. The
	 * popup's position may be set before calling {@link #show()}.
	 * 
	 * @param left
	 *            the left position, in pixels
	 * @param top
	 *            the top position, in pixels
	 */
	public void setPopupPosition(int left, int top)
	{
		if (centered)
		{
			uncentralizeMe();
		}

		// Account for the difference between absolute position and the
		// body's positioning context.
		Document document = Document.get();
		left -= document.getBodyOffsetLeft();
		top -= document.getBodyOffsetTop();

		Style style = getElement().getStyle();
		style.setPropertyPx("left", left);
		style.setPropertyPx("top", top);
	}
	
	/**
	 * Sets whether this object is visible. This method just sets the
	 * <code>visibility</code> style attribute. You need to call {@link #show()}
	 * to actually attached/detach the {@link PopupPanel} to the page.
	 * 
	 * @param visible
	 *            <code>true</code> to show the object, <code>false</code> to
	 *            hide it
	 * @see #show()
	 * @see #hide()
	 */
	@Override
	public void setVisible(boolean visible)
	{
		// We use visibility here instead of UIObject's default of display
		// Because the panel is absolutely positioned, this will not create
		// "holes" in displayed contents and it allows normal layout passes
		// to occur so the size of the PopupPanel can be reliably determined.
		getElement().getStyle().setVisibility(visible?Visibility.VISIBLE:Visibility.HIDDEN);

		if (glass != null)
		{
			glass.getStyle().setVisibility(visible?Visibility.VISIBLE:Visibility.HIDDEN);
		}
	}	
	
	/**
	 * Determines whether or not this popup is visible. Note that this just
	 * checks the <code>visibility</code> style attribute, which is set in the
	 * {@link #setVisible(boolean)} method. If you want to know if the popup is
	 * attached to the page, use {@link #isShowing()} instead.
	 * 
	 * @return <code>true</code> if the object is visible
	 * @see #setVisible(boolean)
	 */
	@Override
	public boolean isVisible()
	{
		return !getElement().getStyle().getVisibility().equals(Visibility.HIDDEN.getCssName());
	}
	
	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event)
	{
		if (event.isCanceled())
		{
			return;
		}

		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());
		boolean eventTargetsPopupOrPartner = eventTargetsPopup(nativeEvent) || eventTargetsPartner(nativeEvent);
		if (eventTargetsPopupOrPartner)
		{
			event.consume();
		}

		// Switch on the event type
		int type = nativeEvent.getTypeInt();
		switch (type)
		{
			case Event.ONMOUSEDOWN:
			case Event.ONTOUCHSTART:
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null)
				{
					event.consume();
					return;
				}
	
				if (!eventTargetsPopupOrPartner && autoHide)
				{
					hide(true);
					return;
				}
				break;
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONCLICK:
			case Event.ONDBLCLICK:
			case Event.ONTOUCHEND:
			{
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null)
				{
					event.consume();
					return;
				}
				break;
			}
		}
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		// Just to be sure, we perform cleanup when the popup is unloaded (i.e.
		// removed from the DOM). This is normally taken care of in hide(), but
		// it
		// can be missed if someone removes the popup directly from the
		// RootPanel.
		if (isShowing())
		{
			setState(false, true, false, null);
		}
	}
	
	/**
	 * Hides the popup and detaches it from the page. This has no effect if it
	 * is not currently showing.
	 * 
	 * @param autoClosed
	 *            the value that will be passed to
	 *            {@link CloseHandler#onClose(CloseEvent)} when the popup is
	 *            closed
	 */
	protected void hide(final boolean autoClosed)
	{
		doHide(true, autoClosed, isAnimationEnabled());
	}

	private void runEntranceAnimation(final StateChangeCallback callback)
    {
	    animating = true;
	    getDialogAnimation().animateEntrance(this, new Animation.Callback()
	    {
	    	@Override
	    	public void onAnimationCompleted()
	    	{
	    		animating = false;
	    		if (callback != null)
	    		{
	    			callback.onStateChange();
	    		}
	    	}
	    });
    }
	
	private DialogAnimation getDialogAnimation()
	{
		if (animation == null)
		{
			animation = DialogAnimation.bounce;
		}
		return animation;
	}
	
	private void doHide(boolean fireEvent, final boolean autoClosed, boolean animated)
    {
	    if (!showing)
		{
			return;
		}
		if (animated && centered)
		{
			fixPositionToCenter();
		}
		if (fireEvent)
		{
			setState(false, false, animated, new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					CloseEvent.fire(PopupPanel.this, PopupPanel.this, autoClosed);
				}
			});
		}
		else
		{
			setState(false, false, animated, null);
		}
    }

	private void fixPositionToCenter()
    {
		int left = getPopupLeftToCenter();
		int top = getPopupTopToCenter();
		setPopupPosition(left, top);
    }

	/**
	 * Gets the popup's left position relative to the browser's center area.
	 * 
	 * @return the popup's left position
	 */
	private int getPopupLeftToCenter()
	{
		int windowLeft = Window.getScrollLeft();
		int windowWidth = Window.getClientWidth();
		int centerLeft = (windowWidth / 2) + windowLeft;
		
		int offsetWidth = getOffsetWidth();
		return centerLeft - (offsetWidth / 2);
	}

	/**
	 * Gets the popup's top position relative to the browser's center area.
	 * 
	 * @return the popup's top position
	 */
	private int getPopupTopToCenter()
	{
		int windowTop = Window.getScrollTop();
		int windowHeight = Window.getClientHeight();
		int centerTop = (windowHeight / 2) + windowTop;

		int offsetHeight = getOffsetHeight();
		return centerTop - (offsetHeight / 2);
	}

	private void doShow(final boolean animated)
	{
		if (showing)
		{
			return;
		}
		else if (isAttached())
		{
			// The popup is attached directly to another panel, so we need to
			// remove
			// it from its parent before showing it. This is a weird use case,
			// but
			// since PopupPanel is a Widget, its legal.
			this.removeFromParent();
		}
		if (centered && animated)
		{
			setVisible(false);
			setState(true, false, false, null);
			fixPositionToCenter();
			setVisible(true);
			runEntranceAnimation(new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					centralizeMe();
				}
			});
		}
		else
		{
			setState(true, false, animated, null);
		}
	}

	private void centralizeMe()
	{
		Style style = getElement().getStyle();
		style.setLeft(50, Unit.PCT);
		style.setTop(50, Unit.PCT);
		style.setProperty("webkitTransform", "translateY(-50%) translateX(-50%)");
		style.setProperty("transform", "translateY(-50%) translateX(-50%)");
		centered = true;
	}
	
	private void uncentralizeMe()
	{
		Style style = getElement().getStyle();
		style.clearProperty("webkitTransform");
		style.clearProperty("transform");
		centered = false;
	}
	
	private void setState(boolean showing, boolean unloading, boolean animated, final StateChangeCallback callback)
    {
	    this.showing = showing;
		updateHandlers();

		maybeShowGlass();
		if (showing)
		{
			if (animated)
			{
				animating = true;
				getDialogAnimation().animateEntrance(this, new Animation.Callback()
				{
					@Override
                    public void onAnimationCompleted()
                    {
						animating = false;
						if (callback != null)
						{
							callback.onStateChange();
						}
                    }
				});
			}
			RootPanel.get().add(this);
			if (!animated && callback != null)
			{
				callback.onStateChange();
			}
		}
		else
		{
			if (!unloading)
			{
				if (animated)
				{
					animating = true;
					getDialogAnimation().animateExit(this, new Animation.Callback(){
						@Override
						public void onAnimationCompleted()
						{
							animating = false;
							RootPanel.get().remove(PopupPanel.this);
							if (callback != null)
							{
								callback.onStateChange();
							}
						}
					});
				}
				else
				{
					RootPanel.get().remove(PopupPanel.this);
					if (callback != null)
					{
						callback.onStateChange();
					}
				}
			}
		}
	}

	/**
	 * Show or hide the glass.
	 */
	private void maybeShowGlass()
	{
		if (showing)
		{
			if (modal)
			{
				Document.get().getBody().appendChild(glass);
				glassShowing = true;
			}
		}
		else if (glassShowing)
		{
			Document.get().getBody().removeChild(glass);
			glassShowing = false;
		}
	}

	/**
	 * Does the event target one of the partner elements?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets a partner
	 */
	private boolean eventTargetsPartner(NativeEvent event)
	{
		if (autoHidePartners == null)
		{
			return false;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target))
		{
			for (int i=0; i < autoHidePartners.size(); i++)
			{
				Element elem = autoHidePartners.get(i);
				if (elem.isOrHasChild(Element.as(target)))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Does the event target this popup?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets the popup
	 */
	private boolean eventTargetsPopup(NativeEvent event)
	{
		EventTarget target = event.getEventTarget();
		if (Element.is(target))
		{
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}

	/**
	 * Register or unregister the handlers used by {@link PopupPanel}.
	 */
	private void updateHandlers()
	{
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null)
		{
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}
		if (historyHandlerRegistration != null)
		{
			historyHandlerRegistration.removeHandler();
			historyHandlerRegistration = null;
		}

		// Create handlers if showing.
		if (showing)
		{
			if (!modal)
			{
				nativePreviewHandlerRegistration = Event.addNativePreviewHandler(this);
			}
			historyHandlerRegistration = History.addValueChangeHandler(new ValueChangeHandler<String>()
			{
				public void onValueChange(ValueChangeEvent<String> event)
				{
					if (autoHideOnHistoryEvents)
					{
						hide();
					}
				}
			});
		}
	}
	
	private int getLeftRelativeObject(final UIObject relativeObject)
	{
		int offsetWidth = getOffsetWidth();
		int relativeElemOffsetWidth = relativeObject.getOffsetWidth();
		int offsetWidthDiff = offsetWidth - relativeElemOffsetWidth;
		int left;

		if (LocaleInfo.getCurrentLocale().isRTL())
		{ // RTL case

			int relativeElemAbsoluteLeft = relativeObject.getAbsoluteLeft();
			left = relativeElemAbsoluteLeft - offsetWidthDiff;
			if (offsetWidthDiff > 0)
			{
				int windowRight = Window.getClientWidth() + Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();

				int relativeElemLeftValForRightEdge = relativeElemAbsoluteLeft + relativeElemOffsetWidth;
				int distanceToWindowRight = windowRight - relativeElemLeftValForRightEdge;
				int distanceFromWindowLeft = relativeElemLeftValForRightEdge - windowLeft;
				if (distanceFromWindowLeft < offsetWidth && distanceToWindowRight >= offsetWidthDiff)
				{
					left = relativeElemAbsoluteLeft;
				}
			}
		}
		else
		{ // LTR case

			left = relativeObject.getAbsoluteLeft();
			if (offsetWidthDiff > 0)
			{
				int windowRight = Window.getClientWidth() + Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();
				int distanceToWindowRight = windowRight - left;
				int distanceFromWindowLeft = left - windowLeft;
				if (distanceToWindowRight < offsetWidth && distanceFromWindowLeft >= offsetWidthDiff)
				{
					left -= offsetWidthDiff;
				}
			}
		}
		return left;
	}
	
	private int getTopRelativeObject(final UIObject relativeObject)
	{
		int offsetHeight = getOffsetHeight();
		int top = relativeObject.getAbsoluteTop();

		int windowTop = Window.getScrollTop();
		int windowBottom = Window.getScrollTop() + Window.getClientHeight();

		int distanceFromWindowTop = top - windowTop;
		int distanceToWindowBottom = windowBottom - (top + relativeObject.getOffsetHeight());

		if (distanceToWindowBottom < offsetHeight && distanceFromWindowTop >= offsetHeight)
		{
			top -= offsetHeight;
		}
		else
		{
			top += relativeObject.getOffsetHeight();
		}
		return top;
	}
	
	private static interface StateChangeCallback
	{
		void onStateChange();
	}
}
