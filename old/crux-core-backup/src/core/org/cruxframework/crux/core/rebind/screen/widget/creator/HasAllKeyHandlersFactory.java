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
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyDownEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyPressEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.KeyUpEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * A helper class to help on HasAllKeyHandlers widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 */
@TagEvents({
	@TagEvent(value=KeyUpEvtBind.class, description="Inform the handler for onKeyUp event. This event is fired when the user release a keyboard key."),
	@TagEvent(value=KeyPressEvtBind.class, description="Inform the handler for onPress event. This event is fired when the user press a keyboard key."),
	@TagEvent(value=KeyDownEvtBind.class, description="Inform the handler for onKeyDow event. This event is fired when the user pressed a keyboard key, before he release the key.")
})	
public interface HasAllKeyHandlersFactory<C extends WidgetCreatorContext>
{
}
