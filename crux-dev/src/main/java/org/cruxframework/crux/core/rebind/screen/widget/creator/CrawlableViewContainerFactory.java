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
 * Add properties on ViewContainerFactories that uses crawlable features 
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="historyControlPrefix", required=true, description="Define the prefix that will be used on history tokens created by this container. It will be visible on URL hashbang token."), 
	@TagAttribute(value="historyControlEnabled", type=Boolean.class, defaultValue="true", description="Enable or disable the history control provided by this container. If enabled, every time a view is rendered by the container, a new history state is saved, to enable back/forward buttons to work properly.")
})
public interface CrawlableViewContainerFactory<C extends WidgetCreatorContext>
{
}
