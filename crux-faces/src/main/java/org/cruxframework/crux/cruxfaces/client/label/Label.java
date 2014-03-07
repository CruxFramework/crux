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
package org.cruxframework.crux.cruxfaces.client.label;

import org.cruxframework.crux.cruxfaces.client.select.SelectableWidget;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasWordWrap;

/**
 * A cross device label, that use touch events on touch enabled devices to implement Google Fast Buttons
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Label extends SelectableWidget implements HasDirectionalText, HasWordWrap, HasAutoHorizontalAlignment
{
	private InternalLabel label;

	public Label()
	{
		this(new InternalLabel());
		setStyleName("faces-Label");
	}

	public Label(String text)
	{
		this();
		setText(text);
	}

	public Label(String text, Direction dir)
	{
		this();
		setText(text, dir);
	}
	
	public Label(DivElement element)
	{
		this(new InternalLabel(element));
	}
	
	public Label(SpanElement element)
	{
		this(new InternalLabel(element));
	}

	protected Label(InternalLabel label) 
	{
		this.label = label;
		initWidget(label);
	}

	@Override
    public String getText()
    {
	    return label.getText();
    }

	@Override
    public void setText(String text)
    {
		label.setText(text);
    }

	@Override
    public Direction getTextDirection()
    {
	    return label.getTextDirection();
    }

	@Override
    public void setText(String text, Direction dir)
    {
		label.setText(text, dir);
    }

	@Override
    public HorizontalAlignmentConstant getHorizontalAlignment()
    {
	    return label.getHorizontalAlignment();
    }

	@Override
    public void setHorizontalAlignment(HorizontalAlignmentConstant align)
    {
		label.setHorizontalAlignment(align);
    }

	@Override
    public AutoHorizontalAlignmentConstant getAutoHorizontalAlignment()
    {
	    return label.getAutoHorizontalAlignment();
    }

	@Override
    public void setAutoHorizontalAlignment(AutoHorizontalAlignmentConstant autoHorizontalAlignment)
    {
		label.setAutoHorizontalAlignment(autoHorizontalAlignment);
    }

	@Override
    public boolean getWordWrap()
    {
	    return label.getWordWrap();
    }

	@Override
    public void setWordWrap(boolean wrap)
    {
		label.setWordWrap(wrap);
    }
	
	private static class InternalLabel extends com.google.gwt.user.client.ui.Label
	{
		public InternalLabel()
		{
		}

		public InternalLabel(Element element)
		{
			super(element);
		}
	}
}
