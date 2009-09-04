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
package br.com.sysmap.crux.basic.client;

import java.util.List;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.CustomButton.Face;

/**
 * 
 * @author Thiago Bustamante
 */
public abstract class CustomButtonFactory<T extends CustomButton> extends FocusWidgetFactory<T> 
			implements HasTextFactory<T>
{
	public static final String FACE_DOWN_DISABLED = "downDisabled";
	public static final String FACE_UP_DISABLED = "upDisabled";
	public static final String FACE_UP_HOVERING = "upHovering";
	public static final String FACE_DOWN_HOVERING = "downHovering";
	public static final String FACE_DOWN = "down";
	public static final String FACE_UP = "up";
	
	public static final String FACE_TYPE_HTML = "html";
	public static final String FACE_TYPE_TEXT = "text";
	public static final String FACE_TYPE_IMAGE = "image";
	
	protected com.google.gwt.user.client.ui.CustomButton customButtonWidget;

	/**
	 * Render component attributes
	 * @throws InterfaceConfigException 
	 */
	@Override
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		Element element = context.getElement();
		T widget = context.getWidget();
		
		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}		
	
	/**
	 * 
	 */
	@Override
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		T widget = context.getWidget();
		
		List<Element> facesCandidates = ensureChildrenSpans(element, true);
		
		for (Element child : facesCandidates)
		{
			if (isValidFace(child))
			{
				processFaceDeclaration(widget, child);
			}
		}
	}	
	
	/**
	 * Verify if the span tag found is a valid face declaration for customButtons
	 * @param element
	 * @return
	 */
	protected boolean isValidFace(Element element)
	{
		if ("span".equalsIgnoreCase(element.getTagName()))
		{
			String type = element.getAttribute("_faceType");
			if (type != null && type.trim().length() > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Process Face declaration for customButton
	 * @param element
	 */
	protected void processFaceDeclaration(T widget, Element element)
	{
		String faceType = element.getAttribute("_faceType");
		Face face = getFace(widget, element.getAttribute("_face"));
		if (face != null)
		{
			if (FACE_TYPE_IMAGE.equals(faceType))
			{
				String leftStr = element.getAttribute("_left");
				String topStr = element.getAttribute("_top");
				String widthStr = element.getAttribute("_width");
				String heightStr = element.getAttribute("_height");
				if (leftStr == null || topStr == null || widthStr == null || heightStr ==null)
				{
					face.setImage(new Image(element.getAttribute("_url")));
				}
				else
				{
					face.setImage(new Image(element.getAttribute("_url"), Integer.parseInt(leftStr), 
							Integer.parseInt(topStr), 
							Integer.parseInt(widthStr), 
							Integer.parseInt(heightStr)));
				}
			}
			else if (FACE_TYPE_TEXT.equals(faceType))
			{
				face.setText(element.getAttribute("_value"));
			}
			else if (FACE_TYPE_HTML.equals(faceType))
			{
				face.setHTML(element.getInnerHTML());
			}
		}
	}

	/**
	 * Return face associated with label in tag 
	 * @param face
	 * @return
	 */
	protected Face getFace(T widget, String face)
	{
		if (face == null || face.trim().length() == 0 || FACE_UP.equals(face))
		{
			return widget.getUpFace();
		}
		else if (FACE_DOWN.equals(face))
		{
			return widget.getDownFace();
		}
		else if (FACE_DOWN_HOVERING.equals(face))
		{
			return widget.getDownHoveringFace();
		}
		else if (FACE_UP_HOVERING.equals(face))
		{
			return widget.getUpHoveringFace();
		}
		else if (FACE_UP_DISABLED.equals(face))
		{
			return widget.getUpDisabledFace();
		}
		else if (FACE_DOWN_DISABLED.equals(face))
		{
			return widget.getDownDisabledFace();
		}
		return null;
	}
}
