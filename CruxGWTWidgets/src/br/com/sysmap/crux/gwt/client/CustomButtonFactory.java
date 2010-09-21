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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.core.client.utils.StringUtils;

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
	/**
	 * Render component attributes
	 * @throws InterfaceConfigException 
	 */
	@Override
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		T widget = context.getWidget();
		
		String innerHtml = context.getElement().getInnerHTML();
		String text = context.readWidgetProperty("text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(ScreenFactory.getInstance().getDeclaredMessage(innerHtml));
		}
	}		
	
	@TagChildAttributes(tagName="up")
	abstract static class AbstractUpFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getUpFace());
		}
	}
	
	@TagChildAttributes(tagName="upDisabled")
	abstract static class AbstractUpDisabledFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getUpDisabledFace());
		}
	}

	@TagChildAttributes(tagName="upHovering")
	abstract static class AbstractUpHoveringFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getUpHoveringFace());
		}
	}

	@TagChildAttributes(tagName="down")
	abstract static class AbstractDownFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getDownFace());
		}
	}
	
	@TagChildAttributes(tagName="downDisabled")
	abstract static class AbstractDownDisabledFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getDownDisabledFace());
		}
	}

	@TagChildAttributes(tagName="downHovering")
	abstract static class AbstractDownHoveringFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			context.setAttribute("face", context.getRootWidget().getDownHoveringFace());
		}
	}
	
	@TagChildAttributes(tagName="textFace")
	abstract static class AbstractTextFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="value", required=true)
		})
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			Face face = (Face)context.getAttribute("face");
			face.setText(context.readChildProperty("value"));
		}
	}
	
	@TagChildAttributes(tagName="htmlFace")
	abstract static class AbstractHTMLFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			Face face = (Face)context.getAttribute("face");
			face.setText(context.getChildElement().getInnerHTML());
		}
	}
	
	@TagChildAttributes(tagName="imageFace")
	abstract static class AbstractImageFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration(value="left", type=Integer.class),
			@TagAttributeDeclaration(value="top", type=Integer.class),
			@TagAttributeDeclaration(value="width", type=Integer.class),
			@TagAttributeDeclaration(value="height", type=Integer.class)
		})
		public void processChildren(WidgetChildProcessorContext<W> context) throws InterfaceConfigException 
		{
			Face face = (Face)context.getAttribute("face");
			String leftStr = context.readChildProperty("left");
			String topStr = context.readChildProperty("top");
			String widthStr = context.readChildProperty("width");
			String heightStr = context.readChildProperty("height");

			if (StringUtils.isEmpty(leftStr)  || StringUtils.isEmpty(topStr) || StringUtils.isEmpty(widthStr) || StringUtils.isEmpty(heightStr))
			{
				face.setImage(new Image(context.readChildProperty("url")));
			}
			else
			{
				face.setImage(new Image(context.readChildProperty("url"), Integer.parseInt(leftStr), 
						Integer.parseInt(topStr), 
						Integer.parseInt(widthStr), 
						Integer.parseInt(heightStr)));
			}
		}
	}
}
