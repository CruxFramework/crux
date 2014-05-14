package org.cruxframework.crux.widgets.client.paging;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Pageable
{
	/**
	 * Moves the pageable's cursor to the next page 
	 */
	void nextPage();
	
	/**
	 * Moves the pageable's cursor to the previous page 
	 */	
	void previousPage();
	
	/**
	 * Return the total number of pages
	 * @return number of pages, -1 if unknown.
	 */
	int getPageCount();
	
	/**
	 * Sets the pager for call back
	 * @param pager
	 */
	void setPager(Pager pager);
	
	/**
	 * Moves the pageable's cursor to an arbitrary page
	 * @param page page number
	 */
	void goToPage(int page);
	
	/**
	 * Checks if pageable data is already available
	 * @param page
	 */
	boolean isDataLoaded();	
}
