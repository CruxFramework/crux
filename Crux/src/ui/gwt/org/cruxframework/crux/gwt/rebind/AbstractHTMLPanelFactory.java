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

import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHTMLPanelFactory extends ComplexPanelFactory<WidgetCreatorContext>
{
	/**
	 * @param out
	 * @param context
	 * @throws CruxGeneratorException
	 */
	protected void createChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
    {
		out.println(context.getWidget()+".addAttachHandler(new "+Handler.class.getCanonicalName()+"(){");
		out.println("private boolean childrenCreated = false;");
		out.println("public void onAttachOrDetach("+AttachEvent.class.getCanonicalName()+" event){");
		out.println("if (!childrenCreated && event.isAttached()){");
		
		createPostProcessingScope();

		JSONArray children = ensureChildren(context.getWidgetElement(), true, context.getWidgetId());
		if (children != null)
		{
			for(int i=0; i< children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				if (isWidget(child))
				{
					String childWidget = createChildWidget(out, child, context);
					boolean childPartialSupport = hasChildPartialSupport(child);
					if (childPartialSupport)
					{
						out.println("if ("+getChildWidgetClassName(child)+".isSupported()){");
					}

					out.println(context.getWidget()+".addAndReplaceElement("+childWidget+", "+EscapeUtils.quote(ViewFactoryUtils.getEnclosingPanelPrefix())+
							"+"+getViewVariable()+".getPrefix()+"+EscapeUtils.quote(child.optString("id"))+");");
					if (childPartialSupport)
					{
						out.println("}");
					}
				}
			}
		}

		commitPostProcessing(out);
		
		out.println("childrenCreated = true;");
		out.println("}");
		out.println("}");
		out.println("});");
    }
	
	@Override
	public WidgetCreatorContext instantiateContext()
	{
	    return new WidgetCreatorContext();
	}
}
