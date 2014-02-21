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

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseDownEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseMoveEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseOutEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseOverEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseUpEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.MouseWheelEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * A helper class to help on HasAllMouseHandlers widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagEvents({
	@TagEvent(value=MouseDownEvtBind.class, description="Inform the handler for onMouseDow event. This event is fired when the user pressed a mouse key over the widget, before he releases the key."),
	@TagEvent(value=MouseUpEvtBind.class, description="Inform the handler for onMouseUp event. This event is fired when the user released a mouse key over the widget."),
	@TagEvent(value=MouseOverEvtBind.class, description="Inform the handler for onMouseOver event. This event is fired when the user puts a mouse over the widget."),
	@TagEvent(value=MouseOutEvtBind.class, description="Inform the handler for onMouseOut event. This event is fired when the user puts a mouse out from the widget."),
	@TagEvent(value=MouseMoveEvtBind.class, description="Inform the handler for onMouseMove event. This event is fired when the user moves a mouse over the widget."),
	@TagEvent(value=MouseWheelEvtBind.class, description="Inform the handler for onMouseWheel event. This event is fired when the user moves a mouse wheel over the widget.")
})	
public interface HasAllMouseHandlersFactory<C extends WidgetCreatorContext>
{
}
