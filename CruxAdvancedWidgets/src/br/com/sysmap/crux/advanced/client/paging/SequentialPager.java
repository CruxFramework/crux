package br.com.sysmap.crux.advanced.client.paging;

import br.com.sysmap.crux.advanced.client.event.paging.PageEvent;
import br.com.sysmap.crux.advanced.client.event.paging.PageHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class SequentialPager extends Composite implements Pager
{
	private HorizontalPanel panel;
	private Pageable pageable;
	
	private int currentPage = 0;
	boolean isLastPage = true;
	
	private SimplePanel infoPanel;
	private FocusPanel previousBtn;
	private FocusPanel nextBtn;
	
	private boolean enabled = true;
	
	public SequentialPager()
	{
		this.panel = new HorizontalPanel();
		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		this.previousBtn = createPreviousButton();
		this.nextBtn = createNextButton();
		this.infoPanel = new SimplePanel();
		this.infoPanel.setWidget(createCurrentPageLabel("" + 0));
		
		this.panel.add(previousBtn);
		this.panel.add(infoPanel);
		this.panel.add(nextBtn);		
		
		this.panel.setStyleName("crux-SequentialPager");
		
		initWidget(this.panel);		
	}
	
	private FocusPanel createPreviousButton()
	{
		final Pager myself = this; 
		
		FocusPanel panel = new FocusPanel();
		panel.setStyleName("previousButton");
		panel.addStyleDependentName("disabled");
		panel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event)
			{
				if(isEnabled())
				{
					PageEvent pageEvent = PageEvent.fire(myself, currentPage - 1);
					if(!pageEvent.isCanceled())
					{
						if(currentPage > 1)
						{
							checkPageable();
							setLoading();
							pageable.previousPage();
						}
					}
				}
			}
		});
		return panel;
	}
	
	private FocusPanel createNextButton()
	{
		final Pager myself = this; 
		
		FocusPanel panel = new FocusPanel();
		panel.setStyleName("nextButton");
		panel.addStyleDependentName("disabled");
		panel.addClickHandler(new ClickHandler(){			
			public void onClick(ClickEvent event)
			{
				if(isEnabled())
				{
					PageEvent pageEvent = PageEvent.fire(myself, currentPage + 1);
					if(!pageEvent.isCanceled())
					{
						if(!isLastPage)
						{
							checkPageable();
							setLoading();
							pageable.nextPage();
						}
					}
				}
			}			
		});
		return panel;
	}

	public void update(int currentPage, boolean isLastPage)
	{
		this.currentPage = currentPage;
		this.isLastPage = isLastPage;
		
		Label currentPageLabel = createCurrentPageLabel("" + currentPage);
		
		if(this.currentPage <= 1 || !isEnabled())
		{
			this.previousBtn.addStyleDependentName("disabled");
		}
		else
		{
			this.previousBtn.removeStyleDependentName("disabled");
		}
		
		if(isLastPage || !isEnabled())
		{
			this.nextBtn.addStyleDependentName("disabled");
		}
		else
		{
			this.nextBtn.removeStyleDependentName("disabled");
		}
				
		this.infoPanel.clear();
		this.infoPanel.add(currentPageLabel);
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		update(this.currentPage, this.isLastPage);
	}
	
	private Label createCurrentPageLabel(String currentPageNumber)
	{
		Label label = new Label(currentPageNumber);
		label.setStyleName("currentPageLabel");
		return label;
	}

	/**
	 * @param pageable the pageable to set
	 */
	public void setPageable(Pageable pageable)
	{
		this.pageable = pageable;
		pageable.setPager(this);
	}

	private void setLoading()
	{
		this.infoPanel.clear();
		this.infoPanel.add(createCurrentPageLabel("..."));
	}
	
	private void checkPageable()
	{
		if(this.pageable == null)
		{
			throw new RuntimeException(); // TODO
		}		
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.paging.HasPageHandlers#addPageHandler(br.com.sysmap.crux.advanced.client.event.paging.PageHandler)
	 */
	public HandlerRegistration addPageHandler(PageHandler handler)
	{
		return addHandler(handler, PageEvent.getType());
	}

	/**
	 * @return the enabled
	 */
	protected boolean isEnabled()
	{
		return enabled;
	}	
}
