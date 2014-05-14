package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * PromoBanner base implementation.
 * @author Thiago da Rosa de Bustamante
 *
 */
//TODO refatorar esta classe. Remover a table
abstract class BannerImpl extends Composite
{
	protected VerticalPanel promoBanner;
	protected FocusPanel focusPanel;
	protected FlowPanel banners;
	protected HorizontalPanel bullets;
	protected int autoTransitionInterval = 5000;
	protected AutoTransiteTimer autoTransiteTimer;
	protected boolean autoTransitionBlocked;
	
	
	protected BannerImpl()
	{
		promoBanner = new VerticalPanel();
		promoBanner.setWidth("100%");
		
		focusPanel = new FocusPanel();
		
		banners = new FlowPanel();
		banners.setStyleName("bannersArea");
		banners.getElement().getStyle().setPosition(Position.RELATIVE);
		
		focusPanel.add(banners);
		
		bullets = new HorizontalPanel();
		bullets.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		bullets.setStyleName("bullets");
		
		promoBanner.add(focusPanel);
		promoBanner.add(bullets);
		promoBanner.setCellHorizontalAlignment(bullets, HasHorizontalAlignment.ALIGN_CENTER);
		
		autoTransitionBlocked = true;
		autoTransiteTimer = new AutoTransiteTimer(this);
		autoTransiteTimer.reschedule();

		promoBanner.addAttachHandler(new Handler()
		{
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				autoTransitionBlocked = !event.isAttached();
			}
		});
		
		initWidget(promoBanner);
		setStyleName("crux-Banner");
	}
	
	public void addDefaultBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
	{
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}

	public void addDefaultBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
	{
		addBanner(image, title, text, styleName, buttonLabel, onclick);
	}

	public void setAutoTransitionInterval(int autoTransitionInterval)
	{
		this.autoTransitionInterval = autoTransitionInterval;
		autoTransiteTimer.reschedule();
	}

	public int getAutoTransitionInterval()
	{
		return autoTransitionInterval;
	}

	public void showBanner(int i, boolean slideToRight)
	{
		autoTransiteTimer.reschedule();

		if(i > getBannersCount() - 1)
		{
			i = 0;
		}

		if(i < 0)
		{
			i = getBannersCount() - 1;
		}

		showWidget(i, slideToRight);
		switchActiveBullet(i);
	}

	protected void setBannersHeight(String height)
    {
        banners.setHeight(height);
    }


	protected String getBannersHeight()
    {
        return banners.getElement().getStyle().getHeight();
    }
	
	protected void addBanner(ImageResource image, String title, String text,  String styleName, String buttonLabel, final SelectHandler selectHandler)
	{
		SimplePanel panel = new SimplePanel();
		Style style = panel.getElement().getStyle();
		style.setProperty("background", "url(" + Screen.rewriteUrl(image.getSafeUri().asString()) + ") no-repeat " + (-image.getLeft() + "px ") + (-image.getTop() + "px"));
		style.setPropertyPx("width", image.getWidth());
		style.setPropertyPx("height", image.getHeight());
		
		doAddBanner(title, text, styleName, buttonLabel, selectHandler, panel);
	}
	
	protected void addBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, final SelectHandler selectHandler)
	{
		SimplePanel panel = new SimplePanel();
		panel.getElement().getStyle().setBackgroundImage("url(" + Screen.rewriteUrl(imageURL) + ")");
		panel.setStyleName("promoBannerImage");
		doAddBanner(title, text, styleName, buttonLabel, selectHandler, panel);
	}

	/**
	 * 
	 * @param title
	 * @param text
	 * @param styleName
	 * @param buttonLabel
	 * @param selectHandler
	 * @param panel
	 */
	private void doAddBanner(String title, String text, String styleName, String buttonLabel, final SelectHandler selectHandler, SimplePanel panel)
    {
	    if(styleName != null)
		{
			panel.setStyleName(styleName);
		}

		panel.setHeight(getBannersHeight());
		panel.setWidth("100%");

		boolean hasTitle = !StringUtils.isEmpty(title);
		boolean hasText = !StringUtils.isEmpty(text);
		boolean hasButtonLabel = !StringUtils.isEmpty(buttonLabel) ;
		if (hasTitle || hasText || hasButtonLabel || selectHandler != null)
		{
			VerticalPanel messagePanel = createMessagePanel(title, text, buttonLabel, selectHandler, hasTitle, hasText, hasButtonLabel);
			panel.add(messagePanel);
		}
		doAddBanner(panel);

		Label bullet = new Label();
		final int targetIndex = getBannersCount() - 1;
		bullet.addClickHandler(
			new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					showBanner(targetIndex);
				}
			}
		);
		bullet.setStyleName("bullet");
		bullets.add(bullet);

		if(!hasVisibleBanner())
		{
			showBanner(0);
		}
    }

	protected VerticalPanel createMessagePanel(String title, String text, String buttonLabel, final SelectHandler selectHandler, boolean hasTitle, 
											boolean hasText, boolean hasButtonLabel)
    {
	    VerticalPanel messagePanel = new VerticalPanel();
	    messagePanel.setStyleName("messagePanel");

	    if (hasTitle)
	    {
	    	Label titleLbl = new Label(title);
	    	titleLbl.setStyleName("title");
	    	messagePanel.add(titleLbl);
	    }
	    if (hasText)
	    {
	    	Label textLbl = new Label(text);
	    	textLbl.setStyleName("text");
	    	messagePanel.add(textLbl);
	    }
	    
	    if(selectHandler != null && hasButtonLabel)
	    {
	    	final Button btn = new Button();
	    	boolean iosDevice = Screen.isIos();
	    	btn.setPreventDefaultTouchEvents(!iosDevice); //iOS bug when using Fast Anchor on a TouchSlider
	    	btn.setStyleName("button");
	    	btn.setText(buttonLabel);
	    	if (iosDevice)
	    	{
	    		// There is a bug on iPad that makes this blur and this timer necessary
	    		btn.addSelectHandler(new SelectHandler()
	    		{
	    			@Override
	    			public void onSelect(final SelectEvent event)
	    			{
	    				btn.setFocus(false);
	    				Scheduler.get().scheduleDeferred(new ScheduledCommand()
	    				{
	    					@Override
	    					public void execute()
	    					{
	    						selectHandler.onSelect(event);
	    					}
	    				});
	    			}
	    		});
	    	}
	    	else
	    	{
	    		btn.addSelectHandler(selectHandler);
	    	}
	    	messagePanel.add(btn);
	    }
	    return messagePanel;
    }
	
	protected void showBanner(int i)
	{
		autoTransiteTimer.reschedule();

		if(i > getBannersCount() - 1)
		{
			i = 0;
		}

		if(i < 0)
		{
			i = getBannersCount() - 1;
		}

		showWidget(i);
		switchActiveBullet(i);
	}

	protected void switchActiveBullet(int i)
	{
		for(int b = 0; b < bullets.getWidgetCount(); b++)
		{
			Widget bullet = bullets.getWidget(b);
			if(b == i)
			{
				bullet.addStyleDependentName("active");
			}
			else
			{
				bullet.removeStyleDependentName("active");
			}
		}
	}

	protected void nextBanner()
    {
        showBanner(getNextBanner(), true);
    }

	protected void previousBanner()
    {
        showBanner(getPreviousBanner(), false);
    }

	public abstract void setLargeBannersHeight(String height);
	public abstract String getLargeBannersHeight();
	public abstract void setSmallBannersHeight(String height);
	public abstract String getSmallBannersHeight();
	public abstract void setTransitionDuration(int transitionDuration);
	public abstract int getTransitionDuration();
	public abstract void showWidget(int i, boolean slideToRight);
	public abstract void showWidget(int i);
	public abstract int getNextBanner();
	public abstract int getPreviousBanner();
	public abstract int getBannersCount();
	public abstract void doAddBanner(SimplePanel panel);
	public abstract void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
	public abstract void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
	public abstract void addSmallBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
	public abstract void addLargeBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
	public abstract boolean hasVisibleBanner();

	protected static class AutoTransiteTimer extends Timer
	{
		private BannerImpl promoBanner;

		public AutoTransiteTimer(BannerImpl promoBanner)
		{
			this.promoBanner = promoBanner;
		}

		@Override
		public void run()
		{
			if (!promoBanner.autoTransitionBlocked)
			{
				promoBanner.nextBanner();
			}
		}

		public void reschedule()
		{
			this.cancel();
			this.scheduleRepeating(promoBanner.autoTransitionInterval);
		}
	}
}