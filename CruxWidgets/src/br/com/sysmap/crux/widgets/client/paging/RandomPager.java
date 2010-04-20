package br.com.sysmap.crux.widgets.client.paging;

import br.com.sysmap.crux.widgets.client.event.paging.PageEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A pager which knows the total number of pages.  
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class RandomPager extends NavigationButtonsPager
{
	private HorizontalPanel panel;
	private ListBox listBox;
	private int pageCount;
	
	/**
	 * Constructor
	 */
	public RandomPager()
	{
		this.listBox = createListBox();	
		
		this.panel = new HorizontalPanel();
		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.panel.setSpacing(2);
				
		this.panel.add(createFirstPageButton());
		this.panel.add(createPreviousButton());
		this.panel.add(listBox);
		this.panel.add(createNextButton());
		this.panel.add(createLastPageButton());	
		
		this.panel.setStyleName("crux-RandomPager");
		initWidget(this.panel);		
	}

	@Override
	protected void onUpdate()
	{
		if(this.pageCount != getPageCount())
		{
			this.pageCount = getPageCount();
			this.listBox.clear();
			
			for (int i = 1; i <= getPageCount(); i++)
			{
				String page = "" + i;
				listBox.addItem(page, page);
			}
		}
		
		if(this.listBox.getItemCount() > 0)
		{
			listBox.setSelectedIndex(getCurrentPage() - 1);
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.AbstractPager#showLoading()
	 */
	@Override
	protected void showLoading()
	{
		listBox.setEnabled(false);
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.AbstractPager#hideLoading()
	 */
	@Override
	protected void hideLoading()
	{
		listBox.setEnabled(true);		
	}
	
	/**
	 * Creates a list box with page numbers
	 * @return
	 */
	private ListBox createListBox()
	{
		final ListBox list = new ListBox();
		list.setEnabled(false);
		list.addChangeHandler
		(
			new ChangeHandler()
			{
				public void onChange(ChangeEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(RandomPager.this, getCurrentPage() + 1);
						if(!pageEvent.isCanceled())
						{
							int selected = list.getSelectedIndex();
							String page = list.getValue(selected);
							goToPage(Integer.valueOf(page));
						}
						else
						{
							list.setSelectedIndex(getCurrentPage() - 1);
						}
					}
				}				
			}
		);
		
		return list;
	}
}