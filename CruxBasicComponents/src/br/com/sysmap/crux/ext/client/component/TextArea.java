package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class TextArea extends Component
{
	public TextArea(String id, Widget widget) 
	{
		super(id, widget);
	}

	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		String rows = DOM.getElementAttribute(element, "_rows");
		if (rows != null && rows.trim().length() > 0)
		{
			((com.google.gwt.user.client.ui.TextArea)widget).setVisibleLines(Integer.parseInt(rows));
		}
	}
}
