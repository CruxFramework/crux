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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

/**
 * A helper class to help on FocusableWidgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 */
@TagAttributes({
	@TagAttribute(value="tabIndex", type=Integer.class, description="Sets the widget's position in the tab index. If more than one widget has the same tab index, each such widget will receive focus in an arbitrary order. Setting the tab index to <code>-1</code> will cause this widget to be removed from the tab order."),
	@TagAttribute(value="accessKey", type=Character.class, description="Sets the widget's 'access key'. This key is used (in conjunction with a browser-specific modifier key) to automatically focus the widget."),
	@TagAttribute(value="focus", type=Boolean.class, description="Explicitly focus/unfocus this widget. Only one widget can have focus at a time, and the widget that does will receive all keyboard events.")
})
public interface FocusableFactory<C extends WidgetCreatorContext>
{
}
