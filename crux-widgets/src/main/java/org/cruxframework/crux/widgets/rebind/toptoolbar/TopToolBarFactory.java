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
package org.cruxframework.crux.widgets.rebind.toptoolbar;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.ComplexPanelFactory;
import org.cruxframework.crux.widgets.client.toptoolbar.TopToolBar;

@DeclarativeFactory(library="widgets", id="topToolBar", targetWidget=TopToolBar.class, attachToDOM=false, 
		description="A tool bar located on the top of the screen.")
@TagChildren({
	@TagChild(value=TopToolBarFactory.GripProcessor.class, supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch}),
	@TagChild(TopToolBarFactory.CanvasProcessor.class)
})
@TagAttributes({
	@TagAttribute(value="gripHeight", type=Integer.class, required=true, supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch})
})

public class TopToolBarFactory  extends ComplexPanelFactory<WidgetCreatorContext> implements HasSelectionHandlersFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="1", maxOccurs="unbounded", tagName="grip")
	@TagChildren({
		@TagChild(GripWidgetProcessor.class)
	})
    public static class GripProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(widgetProperty="gripWidget")
	public static class GripWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(minOccurs="1", maxOccurs="unbounded", tagName="canvas")
	@TagChildren({
		@TagChild(CanvasWidgetProcessor.class)
	})
    public static class CanvasProcessor extends  WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	public static class CanvasWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
