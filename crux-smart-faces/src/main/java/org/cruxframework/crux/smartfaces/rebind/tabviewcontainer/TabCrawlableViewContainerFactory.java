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
package org.cruxframework.crux.smartfaces.rebind.tabviewcontainer;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.CrawlableViewContainerFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.smartfaces.client.tabviewcontainer.TabCrawlableViewContainer;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * @author Bruno Medeiros Rafael (bruno@triggolabs.com)
 *
 */
@DeclarativeFactory(id="tabCrawlableViewContainer", library=Constants.LIBRARY_NAME, targetWidget=TabCrawlableViewContainer.class)
@TagChildren({
	@TagChild(TabCrawlableViewContainerFactory.TabsProcessor.class)
})
public class TabCrawlableViewContainerFactory extends TabContainerFactory implements CrawlableViewContainerFactory<WidgetCreatorContext>
{
	
}
