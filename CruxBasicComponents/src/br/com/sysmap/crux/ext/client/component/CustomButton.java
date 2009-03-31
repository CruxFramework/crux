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
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.CustomButton.Face;

/**
 * 
 * @author Thiago Bustamante
 */
public class CustomButton extends FocusComponent 
{
	public static final String FACE_DOWN_DISABLED = "downDisabled";
	public static final String FACE_UP_DISABLED = "upDisabled";
	public static final String FACE_UP_HOVERING = "upHovering";
	public static final String FACE_DOWN_HOVERING = "downHovering";
	public static final String FACE_DOWN = "down";
	
	public static final String FACE_TYPE_HTML = "html";
	public static final String FACE_TYPE_TEXT = "text";
	public static final String FACE_TYPE_IMAGE = "image";
	
	protected com.google.gwt.user.client.ui.CustomButton customButtonWidget;

	public CustomButton(String id, com.google.gwt.user.client.ui.CustomButton widget) 
	{
		super(id, widget);
		customButtonWidget = (com.google.gwt.user.client.ui.CustomButton) widget;
	}

	public Face getDownDisabledFace() 
	{
		return customButtonWidget.getDownDisabledFace();
	}

	public Face getDownFace() 
	{
		return customButtonWidget.getDownFace();
	}

	public Face getDownHoveringFace() 
	{
		return customButtonWidget.getDownHoveringFace();
	}

	public Face getUpDisabledFace() 
	{
		return customButtonWidget.getUpDisabledFace();
	}

	public Face getUpFace() 
	{
		return customButtonWidget.getUpFace();
	}

	public Face getUpHoveringFace() 
	{
		return customButtonWidget.getUpHoveringFace();
	}

	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);
		
		NodeList<Element> facesCandidates = element.getElementsByTagName("span");
		for (int i=0; i<facesCandidates.getLength(); i++)
		{
			if (isValidFace(facesCandidates.getItem(i)))
			{
				processFaceDeclaration(facesCandidates.getItem(i));
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
	protected void processFaceDeclaration(Element element)
	{
		String faceType = element.getAttribute("_faceType");
		Face face = getFace(element.getAttribute("_face"));
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
	protected Face getFace(String face)
	{
		if (face == null || face.trim().length() == 0 || "up".equals(face))
		{
			return getUpFace();
		}
		else if (FACE_DOWN.equals(face))
		{
			return getDownFace();
		}
		else if (FACE_DOWN_HOVERING.equals(face))
		{
			return getDownHoveringFace();
		}
		else if (FACE_UP_HOVERING.equals(face))
		{
			return getUpHoveringFace();
		}
		else if (FACE_UP_DISABLED.equals(face))
		{
			return getUpDisabledFace();
		}
		else if (FACE_DOWN_DISABLED.equals(face))
		{
			return getDownDisabledFace();
		}
		return null;
	}
}
