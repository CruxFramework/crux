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
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.ViewActivateEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.ViewDeactivateEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.ViewLoadEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.ViewUnloadEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * A helper class to help on ViewContainers creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagEvents({
	@TagEvent(value=ViewLoadEvtBind.class, description="Inform the handler for onViewLoad event. This event is fired when a view is loded on this container."),
	@TagEvent(value=ViewUnloadEvtBind.class, description="Inform the handler for onViewUnload event. This event is fired when a view is unloded on this container."),
	@TagEvent(value=ViewActivateEvtBind.class, description="Inform the handler for onViewActivate event. This event is fired when a view is activated on this container."),
	@TagEvent(value=ViewDeactivateEvtBind.class, description="Inform the handler for onViewDeactivate event. This event is fired when a view is deactivated on this container.")
})	
public interface HasViewHandlersFactory<C extends WidgetCreatorContext>
{
}
