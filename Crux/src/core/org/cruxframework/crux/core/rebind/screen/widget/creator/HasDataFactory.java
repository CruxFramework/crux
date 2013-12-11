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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.SelectionChangeEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * A helper class to help on HasData widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="selectionModel", processor=HasDataFactory.SelectionModelProcessor.class, type=HasDataFactory.SelectionModel.class, required=true,
				 description="Set the SelectionModel used by this widget. Accepts one value between:"
				 		+ "<ul>"
				 		+ "<li>multiSelection - allows multiple items to be selected</li>"
				 		+ "<li>noSelection -  does not allow selection, but fires selection change events. Use this model if you want to know when a user selects an item, but do not want the view to update based on the selection</li>"
				 		+ "<li>singleSelection - allows only one item to be selected a time</li>"
				 		+ "</ul>")
})
@TagEvents({
	@TagEvent(value=SelectionChangeEvtBind.class, description="Inform the handler for onSelectionChange event. This event is fired when the widget's selection is changed.")
})
public interface HasDataFactory<C extends WidgetCreatorContext> extends HasRowsFactory<C>, HasCellPreviewHandlersFactory<C>
{
	enum SelectionModel{multiSelection, noSelection, singleSelection}
	
	class SelectionModelProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public SelectionModelProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			SelectionModel selectionModel = SelectionModel.valueOf(attributeValue);
			switch(selectionModel)
			{
				case multiSelection:
					out.println(context.getWidget()+".setSelectionModel(new "+MultiSelectionModel.class.getCanonicalName()+"("+context.getWidget()+".getKeyProvider()));");
				break;
				case singleSelection:
					out.println(context.getWidget()+".setSelectionModel(new "+SingleSelectionModel.class.getCanonicalName()+"("+context.getWidget()+".getKeyProvider()));");
				break;
				case noSelection:
					out.println(context.getWidget()+".setSelectionModel(new "+NoSelectionModel.class.getCanonicalName()+"("+context.getWidget()+".getKeyProvider()));");
				break;
			}
        }
	}
}
