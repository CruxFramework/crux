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
package org.cruxframework.crux.widgets.rebind.dialog;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.dialog.Confirm;
import org.cruxframework.crux.widgets.rebind.event.CancelEvtBind;
import org.cruxframework.crux.widgets.rebind.event.OkEvtBind;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="confirm", library="widgets", targetWidget=Confirm.class)
@TagAttributes({
	@TagAttribute(value="title", supportsI18N=true),
	@TagAttribute(value="message", supportsI18N=true),
	@TagAttribute(value="okButtonText", supportsI18N=true),
	@TagAttribute(value="cancelButtonText", supportsI18N=true)
})
@TagEvents({
	@TagEvent(CancelEvtBind.class),
	@TagEvent(OkEvtBind.class)
})
public class ConfirmFactory extends WidgetCreator<WidgetCreatorContext> implements HasAnimationFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
