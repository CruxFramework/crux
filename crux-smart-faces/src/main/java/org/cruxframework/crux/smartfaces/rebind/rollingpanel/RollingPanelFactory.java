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
package org.cruxframework.crux.smartfaces.rebind.rollingpanel;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.ComplexPanelFactory;
import org.cruxframework.crux.smartfaces.client.rollingpanel.RollingPanel;
import org.cruxframework.crux.smartfaces.rebind.Constants;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="rollingPanel", library=Constants.LIBRARY_NAME, targetWidget=RollingPanel.class, 
					description="A panel that can be used to display multiples widgets with an adaptive scroll that changes, according with the device used by the client")
@TagAttributes({
	@TagAttribute(value="scrollToAddedWidgets", type=Boolean.class, 
			description="If true, when a new widget is added to this panel, the panel scrolls to ensure that the new widget is visible")
})
@TagChildren({
	@TagChild(RollingPanelFactory.WidgetContentProcessor.class)
})		
public class RollingPanelFactory extends ComplexPanelFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="unbounded")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
	public WidgetCreatorContext instantiateContext()
	{
	    return new WidgetCreatorContext();
	}
}
