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

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * Represents an Anchor component
 * @author Thiago Bustamante
 */
public class Anchor extends FocusComponent
{
	protected com.google.gwt.user.client.ui.Anchor anchorWidget; 
	
	public Anchor(String id) 
	 {
		this(id, new com.google.gwt.user.client.ui.Anchor());
	}

	protected Anchor(String id, com.google.gwt.user.client.ui.Anchor widget) 
	{
		super(id, widget);
		this.anchorWidget = widget;
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

		String href = element.getAttribute("_href");
		if (href != null && href.trim().length() > 0)
		{
			setHref(href);
		}

		String target = element.getAttribute("_target");
		if (target != null && target.trim().length() > 0)
		{
			setTarget(target);
		}

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			setWordWrap(Boolean.parseBoolean(wordWrap));
		}
	}
	
	public Direction getDirection() 
	{
		return anchorWidget.getDirection();
	}

	public HorizontalAlignmentConstant getHorizontalAlignment() 
	{
		return anchorWidget.getHorizontalAlignment();
	}

	/**
	 * Gets the anchor's href (the url to which it links).
	 * 
	 * @return the anchor's href
	 */
	public String getHref() 
	{
		return anchorWidget.getHref();
	}

	public String getHTML() 
	{
		return anchorWidget.getHTML();
	}

	public String getName() 
	{
		return anchorWidget.getName();
	}
	
	/**
	 * Gets the anchor's target frame (the frame in which navigation will occur
	 * when the link is selected).
	 * 
	 * @return the target frame
	 */
	public String getTarget() 
	{
		return anchorWidget.getTarget();
	}

	public String getText() 
	{
		return anchorWidget.getText();
	}

	public boolean getWordWrap() 
	{
		return anchorWidget.getWordWrap();
	}

	public void setDirection(Direction direction) 
	{
		anchorWidget.setDirection(direction);
	}

	public void setHorizontalAlignment(HorizontalAlignmentConstant align) 
	{
		anchorWidget.setHorizontalAlignment(align);
	}

	/**
	 * Sets the anchor's href (the url to which it links).
	 * 
	 * @param href the anchor's href
	 */
	public void setHref(String href) 
	{
		anchorWidget.setHref(href);
	}

	public void setHTML(String html) 
	{
		anchorWidget.setHTML(html);
	}

	public void setName(String name) 
	{
		anchorWidget.setName(name);
	}

	/**
	 * Sets the anchor's target frame (the frame in which navigation will occur
	 * when the link is selected).
	 * 
	 * @param target the target frame
	 */
	public void setTarget(String target) 
	{
		anchorWidget.setTarget(target);
	}

	public void setText(String text) 
	{
		anchorWidget.setText(text);
	}

	public void setWordWrap(boolean wrap) 
	{
		anchorWidget.setWordWrap(wrap);
	}
}
