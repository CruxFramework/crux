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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dockLayoutPanel", library="gwt", targetWidget=DockLayoutPanel.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="unit", type=Unit.class)
})
@TagChildren({
	@TagChild(DockLayoutPanelFactory.DockLayoutPanelProcessor.class)
})		
public class DockLayoutPanelFactory extends AbstractDockLayoutPanelFactory<DockLayoutPanelContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, DockLayoutPanelContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		Unit unit = getUnit(context.readWidgetProperty("unit"));
		out.println(className + " " + context.getWidget()+" = new "+className+"("+Unit.class.getCanonicalName()+"."+unit.toString()+");");
	}
	
	@TagChildren({
		@TagChild(DockLayoutPanelWidgetProcessor.class)
	})		
	public static class DockLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<DockLayoutPanelContext> {}
	
	public static class DockLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<DockLayoutPanelContext> {}
	
    @Override
    public DockLayoutPanelContext instantiateContext()
    {
        return new DockLayoutPanelContext();
    }
}
