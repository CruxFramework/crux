package br.com.sysmap.crux.widgets.client.rollingtabs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gess� - Comment this
 * @author Gessé S. F. Dafé
 */
public class SimpleDecoratedPanel extends CellPanel
{
	private Element line;
	private Element leftCell;
	private Element centerCell;
	private Element rightCell;
	
	public SimpleDecoratedPanel()
	{
		getTable().setClassName("");
	
		line = DOM.createTR();
		leftCell = createTd("flapLeft", true);
		centerCell = createTd("flapCenter", false);
		centerCell.setPropertyInt("colSpan", 2);
		rightCell = createTd("flapRight", false);
		
	    DOM.appendChild(line, leftCell);
	    DOM.appendChild(line, centerCell);
	    DOM.appendChild(line, rightCell);
	    DOM.appendChild(getBody(), line);
	    	    
	    setSpacing(0);
	}
	
	/**
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		cleanEmptySpaces(centerCell);
		add(w, centerCell);
	}
	
	/**
	 * @param middleCenterCell2
	 */
	private void cleanEmptySpaces(Element cell)
	{
		String text = cell.getInnerText();
		
		if(text != null && text.trim().length() == 0)
		{
			cell.setInnerText("");
		}
	}

	/**
	 * Creates a TD with the given style name 
	 * @param styleName
	 * @return
	 */
	private Element createTd(String styleName, boolean fillWithBlank)
	{
		Element td = DOM.createTD();
		td.setClassName(styleName);
		
		if(fillWithBlank)
		{
			td.setInnerHTML("&nbsp;");
		}
		
		td.setPropertyString("align", "center");
		td.setPropertyString("valign", "middle");		
		return td;
	}
}