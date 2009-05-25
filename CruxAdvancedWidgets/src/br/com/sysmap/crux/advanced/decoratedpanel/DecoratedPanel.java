package br.com.sysmap.crux.advanced.decoratedpanel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel based on a 3x3 table, useful to build rounded corners boxes. 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedPanel extends CellPanel
{
	public static final String STYLE_NAME = "crux-DecoratedPanel" ;
	
	private Element topLine;
	private Element topLeftCell;
	private Element topCenterCell;
	private Element topRightCell;
	
	private Element middleLine;
	private Element middleLeftCell;
	private Element middleCenterCell;
	private Element middleRightCell;
	
	private Element bottomLine;
	private Element bottomLeftCell;
	private Element bottomCenterCell;
	private Element bottomRightCell;
	
	public DecoratedPanel(String width, String height)
	{
		getTable().setPropertyString("width", width);
		getTable().setPropertyString("height", height);
		
		topLine = DOM.createTR();
		topLeftCell = createTd("topLeftCell");
		topCenterCell = createTd("topCenterCell");
		topRightCell = createTd("topRightCell");
		
		middleLine = DOM.createTR();
		middleLeftCell = createTd("middleLeftCell");
		middleCenterCell = createTd("middleCenterCell");
		middleRightCell = createTd("middleRightCell");
		
		bottomLine = DOM.createTR();
		bottomLeftCell = createTd("bottomLeftCell");
		bottomCenterCell = createTd("bottomCenterCell");
		bottomRightCell = createTd("bottomRightCell");
		
		DOM.appendChild(topLine, topLeftCell);
		DOM.appendChild(topLine, topCenterCell);
		DOM.appendChild(topLine, topRightCell);
		DOM.appendChild(getBody(), topLine);
	   	    
	    DOM.appendChild(middleLine, middleLeftCell);
	    DOM.appendChild(middleLine, middleCenterCell);
	    DOM.appendChild(middleLine, middleRightCell);
	    DOM.appendChild(getBody(), middleLine);
	    	    
	    DOM.appendChild(bottomLine, bottomLeftCell);
	    DOM.appendChild(bottomLine, bottomCenterCell);
	    DOM.appendChild(bottomLine, bottomRightCell);
	    DOM.appendChild(getBody(), bottomLine);
	}
	
	/**
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		add(w, middleCenterCell);
	}
	
	/**
	 * Adds text to the body of the panel (middle center cell)
	 * @param text
	 */
	public void setContentText(String text)
	{
		middleCenterCell.setInnerText(text);
	}
	
	/**
	 * Adds HTML to the body of the panel (middle center cell)
	 * @param html
	 */
	public void setContentHtml(String html)
	{
		middleCenterCell.setInnerHTML(html);
	}

	/**
	 * Creates a TD with the given style name 
	 * @param styleName
	 * @return
	 */
	private Element createTd(String styleName)
	{
		Element td = DOM.createTD();
		td.setClassName(styleName);
		return td;
	}

	/**
	 * @return the topLine
	 */
	public Element getTopLine()
	{
		return topLine;
	}

	/**
	 * @return the middleLine
	 */
	public Element getMiddleLine()
	{
		return middleLine;
	}

	/**
	 * @return the bottomLine
	 */
	public Element getBottomLine()
	{
		return bottomLine;
	}

	/**
	 * @return the topLeftCell
	 */
	public Element getTopLeftCell()
	{
		return topLeftCell;
	}

	/**
	 * @return the topCenterCell
	 */
	public Element getTopCenterCell()
	{
		return topCenterCell;
	}

	/**
	 * @return the topRightCell
	 */
	public Element getTopRightCell()
	{
		return topRightCell;
	}

	/**
	 * @return the middleLeftCell
	 */
	public Element getMiddleLeftCell()
	{
		return middleLeftCell;
	}

	/**
	 * @return the middleCenterCell
	 */
	public Element getMiddleCenterCell()
	{
		return middleCenterCell;
	}

	/**
	 * @return the middleRightCell
	 */
	public Element getMiddleRightCell()
	{
		return middleRightCell;
	}

	/**
	 * @return the bottomLeftCell
	 */
	public Element getBottomLeftCell()
	{
		return bottomLeftCell;
	}

	/**
	 * @return the bottomCenterCell
	 */
	public Element getBottomCenterCell()
	{
		return bottomCenterCell;
	}

	/**
	 * @return the bottomRightCell
	 */
	public Element getBottomRightCell()
	{
		return bottomRightCell;
	}
}