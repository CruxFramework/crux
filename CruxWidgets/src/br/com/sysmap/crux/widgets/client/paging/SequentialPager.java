package br.com.sysmap.crux.widgets.client.paging;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A pager which does not know the total number of pages. So, it can only move the cursor to next or to previous page.  
 * @author Gesse S. F. Dafe
 */
public class SequentialPager extends NavigationButtonsPager
{
	private HorizontalPanel panel;
	private SimplePanel infoPanel;
	
	/**
	 * Constructor
	 */
	public SequentialPager()
	{
		this.panel = new HorizontalPanel();
		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.infoPanel = new SimplePanel();
		this.infoPanel.setWidget(createCurrentPageLabel("" + 0));
		
		this.panel.add(createPreviousButton());
		this.panel.add(infoPanel);
		this.panel.add(createNextButton());		
		
		this.panel.setStyleName("crux-SequentialPager");
		
		initWidget(this.panel);		
	}

	@Override
	protected void onUpdate()
	{
		Label currentPageLabel = createCurrentPageLabel("" + getCurrentPage());
		this.infoPanel.clear();
		this.infoPanel.add(currentPageLabel);
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.AbstractPager#showLoading()
	 */
	@Override
	protected void showLoading()
	{
		this.infoPanel.clear();
		this.infoPanel.add(createCurrentPageLabel("..."));
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.AbstractPager#hideLoading()
	 */
	@Override
	protected void hideLoading()
	{
		// does nothing
	}
	
	/**
	 * Creates the label that shows the current showing page
	 * @param currentPageNumber
	 * @return
	 */
	private Label createCurrentPageLabel(String currentPageNumber)
	{
		Label label = new Label(currentPageNumber);
		label.setStyleName("currentPageLabel");
		return label;
	}
}