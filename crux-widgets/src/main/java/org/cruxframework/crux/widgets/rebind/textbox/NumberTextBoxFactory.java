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
package org.cruxframework.crux.widgets.rebind.textbox;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.widgets.client.textbox.NumberTextBox;

/**
 * A Factory for NumberTextBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="numberTextBox", library="widgets", targetWidget=NumberTextBox.class, 
	description="A standard numeric box.")
@TagAttributes({
	@TagAttribute(value="value", type=Integer.class),
	@TagAttribute(value="maxLength", type=Integer.class)
})
public class NumberTextBoxFactory extends WidgetCreator<WidgetCreatorContext> implements HasValueChangeHandlersFactory<WidgetCreatorContext>, 
									HasNameFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>, HasAllFocusHandlersFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }	
}
