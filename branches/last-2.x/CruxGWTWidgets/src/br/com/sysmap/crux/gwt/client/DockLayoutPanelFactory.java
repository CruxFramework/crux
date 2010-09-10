/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dockLayoutPanel", library="gwt")
public class DockLayoutPanelFactory extends AbstractDockLayoutPanelFactory<DockLayoutPanel>
{
	@Override
	public DockLayoutPanel instantiateWidget(Element element, String widgetId)
	{
		Unit unit = getUnit(element.getAttribute("_unit"));
		return new DockLayoutPanel(unit);
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="unit", type=Unit.class)
	})
	public void processAttributes(WidgetFactoryContext<DockLayoutPanel> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(DockLayoutPanelProcessor.class)
	})		
	public void processChildren(WidgetFactoryContext<DockLayoutPanel> context) throws InterfaceConfigException {}
	
	public static class DockLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<DockLayoutPanel>
	{
		@Override
		@TagChildren({
			@TagChild(DockLayoutPanelWidgetProcessor.class)
		})		
		public void processChildren(WidgetChildProcessorContext<DockLayoutPanel> context) throws InterfaceConfigException
		{
			super.processChildren(context);
		}
	}
	
	public static class DockLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<DockLayoutPanel> {}
}
