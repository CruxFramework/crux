/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.maskedlabel;

import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class MaskedLabel extends Composite implements HasFormatter, HasHorizontalAlignment, 
HasWordWrap, HasDirection, HasClickHandlers, HasAllMouseHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-MaskedLabel" ;

	private Formatter formatter;
	private Label label; 

	/**
	 * 
	 * @param element
	 * @param formatter
	 * @return
	 */
	public static MaskedLabel wrap(Element element, Formatter formatter) 
	{
		return new MaskedLabel(Label.wrap(element), formatter);
	}
	
	/**
	 * 
	 * @param formatter
	 */
	public MaskedLabel(Formatter formatter)
	{
		this(new Label(), formatter);
	}
	
	/**
	 * 
	 * @param label
	 * @param formatter
	 */
	public MaskedLabel(Label label, Formatter formatter)
	{
		this.label = label;
		this.label.setStyleName(DEFAULT_STYLE_NAME);
		setFormatter(formatter);
		initWidget(this.label);
	}

	/**
	 * Sets a formatter for widget.
	 * @param formatter
	 */
	public void setFormatter(Formatter formatter)
	{
		this.formatter = formatter;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.formatter.HasFormatter#getFormatter()
	 */
	public Formatter getFormatter()
	{
		return this.formatter;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.formatter.HasFormatter#getUnformattedValue()
	 */
	public Object getUnformattedValue()
	{
		if (this.formatter != null)
		{
			return this.formatter.unformat(getText());
			
		}
		else
		{
			return getText();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.formatter.HasFormatter#setUnformattedValue(java.lang.Object)
	 */
	public void setUnformattedValue(Object value)
	{
		if (this.formatter != null)
		{
			label.setText(this.formatter.format(value));
		}
		else
		{
			label.setText(value!= null?value.toString():"");
		}
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasHorizontalAlignment#getHorizontalAlignment()
	 */
	public HorizontalAlignmentConstant getHorizontalAlignment()
	{
		return label.getHorizontalAlignment();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasHorizontalAlignment#setHorizontalAlignment(com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant)
	 */
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
	{
		label.setHorizontalAlignment(align);
	}

	/**
	 * @return
	 */
	public String getText()
	{
		return label.getText();
	}	

	/**
	 * @see com.google.gwt.user.client.ui.HasWordWrap#getWordWrap()
	 */
	public boolean getWordWrap()
	{
		return label.getWordWrap();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasWordWrap#setWordWrap(boolean)
	 */
	public void setWordWrap(boolean wrap)
	{
		label.setWordWrap(wrap);
	}

	/**
	 * @see com.google.gwt.i18n.client.HasDirection#getDirection()
	 */
	public Direction getDirection()
	{
		return label.getDirection();
	}

	/**
	 * @see com.google.gwt.i18n.client.HasDirection#setDirection(com.google.gwt.i18n.client.HasDirection.Direction)
	 */
	public void setDirection(Direction direction)
	{
		label.setDirection(direction);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return label.addClickHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.dom.client.MouseDownHandler)
	 */
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
	{
		return label.addMouseDownHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.client.MouseUpHandler)
	 */
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return label.addMouseUpHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return label.addMouseOutHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return label.addMouseOverHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
	 */
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return label.addMouseMoveHandler(handler);
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
	 */
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
	{
		return label.addMouseWheelHandler(handler);
	}
}
