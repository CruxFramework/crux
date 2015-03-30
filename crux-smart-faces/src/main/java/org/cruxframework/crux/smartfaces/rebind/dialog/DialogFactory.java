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
package org.cruxframework.crux.smartfaces.rebind.dialog;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

/**
 * A helper class to help on HasDialogAnimation widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="autoHide", type=Boolean.class, 
				  property="autoHideEnabled", defaultValue="false",
				  description="If true, the dialog will be closed when user select anything out of the dialog area"),
	@TagAttribute(value="autoHideOnHistoryEventsEnabled", type=Boolean.class, 
	  			  description="If true, the dialog will be closed when any history event is fired, like when user press back on browser"),
	@TagAttribute(value="glassStyleName", supportsResources=true, 
				  description="the class name for the element placed above the dialog element")
})
public interface DialogFactory<C extends WidgetCreatorContext> extends HasDialogAnimationFactory<C>
{
}
