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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.rebind.event.SelectEndEvtBind;
import org.cruxframework.crux.core.rebind.event.SelectEvtBind;
import org.cruxframework.crux.core.rebind.event.SelectStartEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * A helper class to help on HasAllMouseHandlers widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagEvents({
	@TagEvent(value=SelectEvtBind.class, description="Inform the handler for onSelect event. This event is fired when the user select the widget."),
	@TagEvent(value=SelectStartEvtBind.class, description="Inform the handler for onSelectStart event. This event is fired when the user start to select the widget."),
	@TagEvent(value=SelectEndEvtBind.class, description="Inform the handler for onSelectEnd event. This event is fired when the user stop to select the widget.")
})
@TagAttributes
({
	@TagAttribute(value="tapEventThreshold", type=Integer.class, defaultValue="5", 
		description="Allow to set the tap event threshold.")
})
public interface HasAllSelectHandlersFactory<C extends WidgetCreatorContext>
{
}
