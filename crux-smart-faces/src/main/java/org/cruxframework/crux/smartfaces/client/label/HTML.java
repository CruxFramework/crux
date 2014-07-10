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
package org.cruxframework.crux.smartfaces.client.label;

import org.cruxframework.crux.smartfaces.client.select.SelectableWidget;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasDirectionalHtml;
import com.google.gwt.user.client.ui.HasDirectionalSafeHtml;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasWordWrap;

/**
 * A cross device HTML, that use touch events on touch enabled devices to implement Google Fast Buttons
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTML extends SelectableWidget implements HasDirectionalText, HasWordWrap, 
											HasAutoHorizontalAlignment, HasDirectionalHtml, HasDirectionalSafeHtml
{
	private InternalHTML internalHTML;

	public HTML()
	{
		this(new InternalHTML());
		setStyleName("faces-HTML");
	}

	public HTML(SafeHtml html)
	{
		this();
		setHTML(html);
	}
	
	public HTML(String text)
	{
		this();
		setText(text);
	}

	public HTML(String text, Direction dir)
	{
		this();
		setText(text, dir);
	}
	
	public HTML(DivElement element)
	{
		this(new InternalHTML(element));
	}
	
	public HTML(SpanElement element)
	{
		this(new InternalHTML(element));
	}

	protected HTML(InternalHTML label) 
	{
		this.internalHTML = label;
		initWidget(label);
	}

	@Override
    public String getText()
    {
	    return internalHTML.getText();
    }

	@Override
    public void setText(String text)
    {
		internalHTML.setText(text);
    }

	@Override
    public Direction getTextDirection()
    {
	    return internalHTML.getTextDirection();
    }

	@Override
    public void setText(String text, Direction dir)
    {
		internalHTML.setText(text, dir);
    }

	@Override
    public HorizontalAlignmentConstant getHorizontalAlignment()
    {
	    return internalHTML.getHorizontalAlignment();
    }

	@Override
    public void setHorizontalAlignment(HorizontalAlignmentConstant align)
    {
		internalHTML.setHorizontalAlignment(align);
    }

	@Override
    public AutoHorizontalAlignmentConstant getAutoHorizontalAlignment()
    {
	    return internalHTML.getAutoHorizontalAlignment();
    }

	@Override
    public void setAutoHorizontalAlignment(AutoHorizontalAlignmentConstant autoHorizontalAlignment)
    {
		internalHTML.setAutoHorizontalAlignment(autoHorizontalAlignment);
    }

	@Override
    public boolean getWordWrap()
    {
	    return internalHTML.getWordWrap();
    }

	@Override
    public void setWordWrap(boolean wrap)
    {
		internalHTML.setWordWrap(wrap);
    }
	
	@Override
    public String getHTML()
    {
	    return internalHTML.getHTML();
    }

	@Override
    public void setHTML(String html)
    {
		internalHTML.setHTML(html);
    }

	@Override
    public void setHTML(SafeHtml html)
    {
		internalHTML.setHTML(html);
    }

	@Override
    public void setHTML(SafeHtml html, Direction dir)
    {
		internalHTML.setHTML(html, dir);
    }

	@Override
    public void setHTML(String html, Direction dir)
    {
		internalHTML.setHTML(html, dir);
    }	
	
	private static class InternalHTML extends com.google.gwt.user.client.ui.HTML
	{
		public InternalHTML()
		{
		}

		public InternalHTML(Element element)
		{
			super(element);
		}
	}
}
