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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * Represents a Label Component
 * @author Thiago Bustamante
 *
 */
public class Label extends Component
{
	protected com.google.gwt.user.client.ui.Label labelWidget;
	
	public Label(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.Label());
	}

	protected Label(String id, com.google.gwt.user.client.ui.Label widget) 
	{
		super(id, widget);
		labelWidget = widget;
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String direction = element.getAttribute("_direction");
		if (direction != null && direction.trim().length() > 0)
		{
			setDirection(Direction.valueOf(direction));
		}
		
		String horizontalAlignment = element.getAttribute("_horizontalAlignment");
		if (horizontalAlignment != null && horizontalAlignment.trim().length() > 0)
		{
			if (HasHorizontalAlignment.ALIGN_CENTER.equals(horizontalAlignment))
			{
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.equals(horizontalAlignment))
			{
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.equals(horizontalAlignment))
			{
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.equals(horizontalAlignment))
			{
				setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			setWordWrap(Boolean.parseBoolean(wordWrap));
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		ClickEvtBind.bindEvent(element, labelWidget, getId());
		MouseEvtBind.bindEvents(element, labelWidget, getId());
	}
	
	public Direction getDirection() 
	{
		return labelWidget.getDirection();
	}

	public HorizontalAlignmentConstant getHorizontalAlignment() 
	{
		return labelWidget.getHorizontalAlignment();
	}

	public String getText() 
	{
		return labelWidget.getText();
	}

	public boolean isWordWrap() 
	{
		return labelWidget.getWordWrap();
	}

	public void setDirection(Direction direction) 
	{
		labelWidget.setDirection(direction);
	}

	public void setHorizontalAlignment(HorizontalAlignmentConstant align) 
	{
		labelWidget.setHorizontalAlignment(align);
	}

	public void setText(String text) 
	{
		labelWidget.setText(text);
	}

	public void setWordWrap(boolean wrap) 
	{
		labelWidget.setWordWrap(wrap);
	}
}
