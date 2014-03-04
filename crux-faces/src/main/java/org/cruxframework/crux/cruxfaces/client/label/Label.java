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

import org.cruxframework.crux.cruxfaces.client.event.HasSelectHandlers;
import org.cruxframework.crux.cruxfaces.client.event.SelectEvent;
import org.cruxframework.crux.cruxfaces.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasWordWrap;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Label extends Composite implements HasSelectHandlers, HasDirectionalText, HasWordWrap, HasAutoHorizontalAlignment
{
	private LabelEventsHandler impl;
	private InternalLabel label;

	//Extrair esse handler pra que possa reaproveitar parar todos os componentes 
	// que queriam implementar o fast click. Criar um componente abstrato, que estenda
	// o composite e que possa ser o cara base pra esse handler generico. Eh importante
	//que o handler generico tenha set e is enabled. componentes como o label, que nao 
	//sao hasEnabled, nao chamam essas propriedades do handler e nao sao impactados desta
	//forma.
	static abstract class LabelEventsHandler
	{
		protected Label label;
		protected boolean preventDefaultTouchEvents = false;

		protected void setLabel(Label label)
		{
			this.label = label;
		}
		
		protected void select()
		{
			SelectEvent.fire(label);
		}

		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}

		protected abstract void handleLabel();
	}

	/**
	 * Implementation for non touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class LabelEventsHandlerNoTouchImpl extends LabelEventsHandler
	{
		public void handleLabel()
		{
			label.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					select();
				}
			});
		}
	}

	/**
	 * Implementation for touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class LabelEventsHandlerTouchImpl extends LabelEventsHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public void handleLabel()
		{
			label.addTouchStartHandler(this);
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			select();
			resetHandlers();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			if (Math.abs(touch.getClientX() - this.startX) > TAP_EVENT_THRESHOLD || Math.abs(touch.getClientY() - this.startY) > TAP_EVENT_THRESHOLD) 
			{
				this.resetHandlers();
			}
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			startX = touch.getClientX();
			startY = touch.getClientY();
			touchMoveHandler = label.addTouchMoveHandler(this);
			touchEndHandler = label.addTouchEndHandler(this);
		}

		private void resetHandlers()
		{
			if(touchMoveHandler != null)
			{
				touchMoveHandler.removeHandler();
				touchMoveHandler = null;
			}
			if(touchEndHandler != null)
			{
				touchEndHandler.removeHandler();
				touchEndHandler = null;
			}
		}
	}

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
		impl = GWT.create(LabelEventsHandler.class);
		impl.setLabel(this);
		impl.handleLabel();
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler, SelectEvent.getType());
	}
	
	public void select()
	{
		impl.select();
	}
	
	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}

	protected HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	protected HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	protected HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return addDomHandler(handler, TouchStartEvent.getType());
	}
	
	protected HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
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
