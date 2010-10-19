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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.HasWidgetsHandler;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.LazyFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which content is only rendered when it becomes visible for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="lazyPanel", library="gwt", lazy=true)
public class LazyPanelFactory extends PanelFactory<LazyPanel> implements LazyFactory<LazyPanel>, HasWidgetsFactory<LazyPanel>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class LazyPanelImpl extends LazyPanel implements br.com.sysmap.crux.core.client.screen.LazyPanel{
		private boolean initialized = false;
		private String innerHTML;
		private final String wizardId;
		
		public LazyPanelImpl(String innerHTML, String wizardId)
		{
			this.innerHTML = innerHTML;
			this.wizardId = wizardId;
		}
		
		/**
		 * @see com.google.gwt.user.client.ui.LazyPanel#ensureWidget()
		 */
		@Override
		public void ensureWidget()
		{
			if (!initialized)
			{
				cleanLazyDependentWidgets(wizardId);
				initialized = true;
			}
			super.ensureWidget();
		}

		/**
		 * @see com.google.gwt.user.client.ui.LazyPanel#createWidget()
		 */
		@Override
		protected Widget createWidget() 
		{
			if (isScreenParsing())
			{
				createWidgetAsync();
				return null;
			}
			else
			{
				doCreateWidget();
				return getWidget();
			}
		}			

		/**
		 * 
		 */
		private void createWidgetAsync()
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					doCreateWidget();
				}
			});
		}
				
		/**
		 * 
		 */
		private void doCreateWidget()
		{
			getElement().setInnerHTML(innerHTML);
			innerHTML = null;
			parseDocument();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(LazyPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		parent.add(child);
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	@Override
	public LazyPanel instantiateWidget(final Element element, String widgetId) 
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			maybeBuildLazyDependencyList(element, widgetId);
		}		
		
		LazyPanel result =  new LazyPanelImpl(element.getInnerHTML(), widgetId);
		element.setInnerHTML("");
		HasWidgetsHandler.handleWidgetElement(result, widgetId, "gwt_lazyPanel");
		return result;
	}

	/**
	 * @param element
	 * @param externalId
	 * @return
	 */
	private Element getParentLazyPanelElement(Element element, String externalId) 
	{
		Element elementParent = element.getParentElement();
		while (elementParent != null && (elementParent.getId() == null || !elementParent.getId().equals(externalId)))
		{
			String type = elementParent.getAttribute("_type");
			if (type != null && type.equals("gwt_lazyPanel"))//TODO: pegar todos os possiveis lazy, via um generator
			{
				return elementParent;
			}
			if ("body".equalsIgnoreCase(elementParent.getTagName()))
			{
				return null;
			}
			elementParent = elementParent.getParentElement();
		}
			
		return elementParent;	
	}
	
	/**
	 * @param element
	 * @param widgetId
	 */
	private void maybeBuildLazyDependencyList(final Element element, String widgetId)
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			if (getParentLazyPanelElement(element, widgetId) == null)
			{
				// Only the most external lazy panels must be initialized, once they also
				// initialize their internal lazy children.
				NodeList<Element> spanElements = element.getElementsByTagName("span");

				int spansLength = spanElements.getLength();
				for (int i=0; i<spansLength; i++)
				{
					Element spanElement = spanElements.getItem(i);
					if (isWidget(spanElement))
					{
						Element parentLazyWidget = getParentLazyPanelElement(spanElement, widgetId);
						addLazyWidgetDependency(spanElement.getId(), parentLazyWidget.getId());
					}
				}
			}
		}
	}	
}
