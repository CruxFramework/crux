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
package org.cruxframework.crux.widgets.rebind.transferlist;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.CompositeFactory;
import org.cruxframework.crux.widgets.client.transferlist.TransferList;
import org.cruxframework.crux.widgets.client.transferlist.TransferList.Item;
import org.cruxframework.crux.widgets.client.transferlist.TransferList.ItemLocation;
import org.cruxframework.crux.widgets.rebind.event.BeforeMoveItemsEvtBind;
import org.cruxframework.crux.widgets.rebind.event.MoveItemsEvtBind;


/**
 * Factory for Transfer List widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="transferList", library="widgets", targetWidget=TransferList.class)
@TagAttributes({
	@TagAttribute(value="leftToRightButtonText", supportsI18N=true),
	@TagAttribute(value="rightToLeftButtonText", supportsI18N=true),
	@TagAttribute(value="allLeftToRightButtonText", supportsI18N=true),
	@TagAttribute(value="allRightToLeftButtonText", supportsI18N=true),
	@TagAttribute(value="leftListLabel", supportsI18N=true),
	@TagAttribute(value="rightListLabel", supportsI18N=true),
	@TagAttribute(value="visibleItemCount", type=Integer.class),
	@TagAttribute(value="multiTransferFromLeft", type=Boolean.class, defaultValue="true"),
	@TagAttribute(value="multiTransferFromRight", type=Boolean.class, defaultValue="true"),
	@TagAttribute(value="showAllTransferButtons", type=Boolean.class, defaultValue="false")
})
@TagEvents({
	@TagEvent(BeforeMoveItemsEvtBind.class),
	@TagEvent(MoveItemsEvtBind.class)
})
@TagChildren({
	@TagChild(TransferListFactory.TransferListItemProcessor.class)
})
public class TransferListFactory extends CompositeFactory<WidgetCreatorContext>
{

	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", required=true),
		@TagAttributeDeclaration(value="value", required=true),
		@TagAttributeDeclaration(value="location", required=true, type=ItemLocation.class)
	})
	public static class TransferListItemProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			out.println(context.getWidget()+".addItem(new "+Item.class.getCanonicalName()+"("+
					EscapeUtils.quote(context.readChildProperty("label"))+","+EscapeUtils.quote(context.readChildProperty("value"))+","+
					ItemLocation.class.getCanonicalName()+"."+context.readChildProperty("location")+"));");
		}
	}


	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}