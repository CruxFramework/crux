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
package org.cruxframework.crux.cruxfaces.rebind.label;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.cruxfaces.client.label.Label;
import org.cruxframework.crux.cruxfaces.rebind.event.SelectEvtBind;
import org.cruxframework.crux.cruxfaces.rebind.panel.Constants;



/**
 * A Factory for Label widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="label", library=Constants.LIBRARY_NAME, targetWidget=Label.class, description="A label class that support google fast buttons to simulate clicks on touch devices.")
@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false", 
				  description="If true, the label will call preventDefault on all touch events.")
})
@TagEvents({
	@TagEvent(SelectEvtBind.class)
})
public class LabelFactory extends WidgetCreator<WidgetCreatorContext> implements HasTextFactory<WidgetCreatorContext>, 
													HasWordWrapFactory<WidgetCreatorContext>, HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>
{	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
