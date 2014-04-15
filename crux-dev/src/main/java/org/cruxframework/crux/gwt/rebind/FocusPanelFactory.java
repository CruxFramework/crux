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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllTouchHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDoubleClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.FocusPanel;

/**
 * A Factory for FocusPanel widgets
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="focusPanel", library="gwt", targetWidget=FocusPanel.class)
@TagChildren({
	@TagChild(FocusPanelFactory.WidgetContentProcessor.class)
})
public class FocusPanelFactory extends PanelFactory<WidgetCreatorContext>
	   implements HasAllMouseHandlersFactory<WidgetCreatorContext>, 
	   			  HasClickHandlersFactory<WidgetCreatorContext>, 
	   			  HasAllFocusHandlersFactory<WidgetCreatorContext>, 
	   			  HasDoubleClickHandlersFactory<WidgetCreatorContext>,
	   			  HasAllKeyHandlersFactory<WidgetCreatorContext>, 
	   			  FocusableFactory<WidgetCreatorContext>, 
	   			  HasAllTouchHandlersFactory<WidgetCreatorContext>
	   			
{
    
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
    
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}