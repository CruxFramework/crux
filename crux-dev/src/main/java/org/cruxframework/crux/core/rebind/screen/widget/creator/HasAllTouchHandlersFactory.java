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
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.TouchCancelEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.TouchEndEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.TouchMoveEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.TouchStartEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * A helper class to help on HasAllTouchHandlers widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagEvents({
	@TagEvent(value=TouchStartEvtBind.class, description="Inform the handler for onTouchStarch event. This event is fired when the user start touching over the widget."),
	@TagEvent(value=TouchMoveEvtBind.class, description="Inform the handler for onTouchMove event. This event is fired when the user moves his fingers touching over the widget."),
	@TagEvent(value=TouchEndEvtBind.class, description="Inform the handler for onTouchEnd event. This event is fired when the user release his fingers after touching over the widget."),
	@TagEvent(value=TouchCancelEvtBind.class, description="Inform the handler for onTouchCancel event. This event is fired if any external event with more priority than our website (e.g., an alert window, an incoming call, or a push notification) cancels the operation.")
})	
public interface HasAllTouchHandlersFactory<C extends WidgetCreatorContext>
{

}
//HasTouchStartHandlers,
//HasTouchMoveHandlers, HasTouchEndHandlers, HasTouchCancelHandlers

