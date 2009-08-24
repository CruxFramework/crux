package br.com.sysmap.crux.advanced.client.paging;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	
	public SequentialPager()
	{
		this.panel = new HorizontalPanel();
		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		this.previousBtn = createPreviousButton();
		this.nextBtn = createNextButton();
		this.infoPanel = new SimplePanel();
		this.infoPanel.setWidget(createCurrentPageLabel(0));
		
		this.panel.add(previousBtn);
		this.panel.add(infoPanel);
		this.panel.add(nextBtn);		
		
		this.panel.setStyleName("crux-SequentialPager");
		
		initWidget(this.panel);		
	}
	
	private FocusPanel createPreviousButton()
	{
		FocusPanel panel = new FocusPanel();
		panel.setStyleName("previousButton");
		panel.addStyleDependentName("disabled");
		panel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event)
			{
				if(currentPage > 1)
				{
					checkPageable();
					setLoading();
					pageable.previousPage();
				}
			}
		});
		return panel;
	}
	
	private FocusPanel createNextButton()
	{
		FocusPanel panel = new FocusPanel();
		panel.setStyleName("nextButton");
		panel.addStyleDependentName("disabled");
		panel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event)
			{
				if(!isLastPage)
				{
					checkPageable();
					setLoading();
					pageable.nextPage();
				}
			}			
		});
		return panel;
	}

	public void update(int currentPage, boolean isLastPage)
	{
		this.currentPage = currentPage;
		this.isLastPage = isLastPage;
		
		Label currentPageLabel = createCurrentPageLabel(currentPage);
		
		if(this.currentPage <= 1)
		{
			this.previousBtn.addStyleDependentName("disabled");
		}
		else
		{
			this.previousBtn.removeStyleDependentName("disabled");
		}
		
		if(isLastPage)
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

	private Label createCurrentPageLabel(int currentPageNumber)
	{
		Label label = new Label("" + currentPageNumber);
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
		SimplePanel loading = new SimplePanel();
		loading.setStyleName("loading");
		infoPanel.clear();
		infoPanel.add(loading);
	}
	
	private void checkPageable()
	{
		if(this.pageable == null)
		{
			throw new RuntimeException(); // TODO
		}		
	}	
}
