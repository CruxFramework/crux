/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

class CustomButtonContext extends WidgetCreatorContext
{
	String face;
}

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class CustomButtonFactory extends FocusWidgetFactory<CustomButtonContext> 
			implements HasHTMLFactory<CustomButtonContext>
{
	@TagConstraints(tagName="up")
	abstract static class AbstractUpFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpFace";
		}
	}
	
	@TagConstraints(tagName="upDisabled")
	abstract static class AbstractUpDisabledFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpDisabledFace";
		}
	}

	@TagConstraints(tagName="upHovering")
	abstract static class AbstractUpHoveringFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpHoveringFace";
		}
	}

	@TagConstraints(tagName="down")
	abstract static class AbstractDownFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownFace";
		}
	}
	
	@TagConstraints(tagName="downDisabled")
	abstract static class AbstractDownDisabledFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownDisabledFace";
		}
	}

	@TagConstraints(tagName="downHovering")
	abstract static class AbstractDownHoveringFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownHoveringFace";
		}
	}
	
	@TagConstraints(tagName="textFace")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="value", required=true)
	})
	abstract static class AbstractTextFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			out.println(context.getWidget()+"get"+context.face+"().setText("+EscapeUtils.quote(context.readChildProperty("value"))+");");
		}
	}
	
	@TagConstraints(tagName="htmlFace", type=HTMLTag.class)
	abstract static class AbstractHTMLFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			out.println(context.getWidget()+"get"+context.face+"().setHTML("+getWidgetCreator().
					ensureHtmlChild(context.getChildElement(), true, context.getWidgetId())+");");
		}
	}
	
	@TagConstraints(tagName="imageFace")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="url", required=true, supportsResources=true),
		@TagAttributeDeclaration("visibleRect")
	})
	abstract static class AbstractImageFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			String visibleRect = context.readChildProperty("visibleRect");

			if (StringUtils.isEmpty(visibleRect))
			{
				String url = context.readChildProperty("url");
				if (getWidgetCreator().isResourceReference(url))
				{				
			    	String resource = ViewFactoryCreator.createVariableName("resource");
			    	out.println(ImageResource.class.getCanonicalName()+" "+resource+" = "+getWidgetCreator().getResourceAccessExpression(url)+";");
					out.println(context.getWidget()+"get"+context.face+"().setImage(new "+Image.class.getCanonicalName()+
							"(Screen.rewriteUrl("+resource+".getSafeUri().asString())), "+resource+".getLeft(), "+
							resource+".getTop(), "+resource+".getWidth(), "+resource+".getHeight());");
				}
				else
				{
					out.println(context.getWidget()+"get"+context.face+"().setImage(new "+Image.class.getCanonicalName()+
							"(Screen.rewriteUrl("+EscapeUtils.quote(url)+")));");
				}
			}
			else
			{
				String[] coord = visibleRect.split(",");
				
				if (coord != null && coord.length == 4)
				{
					out.println(context.getWidget()+"get"+context.face+"().setImage(new "+Image.class.getCanonicalName()+
							"(Screen.rewriteUrl("+EscapeUtils.quote(context.readChildProperty("url"))+"), "+
							Integer.parseInt(coord[0].trim())+","+Integer.parseInt(coord[1].trim())+","+ 
							Integer.parseInt(coord[2].trim())+","+Integer.parseInt(coord[3].trim())+"));");
				}
			}
		}
	}
		
	@Override
	public CustomButtonContext instantiateContext()
	{
	    return new CustomButtonContext();
	}
}