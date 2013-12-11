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
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagEvents({
	@TagEvent(MouseDownEvtBind.class),
	@TagEvent(MouseUpEvtBind.class),
	@TagEvent(MouseOverEvtBind.class),
	@TagEvent(MouseOutEvtBind.class),
	@TagEvent(MouseMoveEvtBind.class),
	@TagEvent(MouseWheelEvtBind.class)
})	
public interface HasAllMouseHandlersFactory<C extends WidgetCreatorContext>
{
}
