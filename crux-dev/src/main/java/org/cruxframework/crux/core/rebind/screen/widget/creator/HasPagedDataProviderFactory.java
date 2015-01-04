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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;

/**
 * A helper class to help on HasData widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagChildren({
	@TagChild(HasPagedDataProviderFactory.PagedDataProviderChildren.class)
})
public abstract class HasPagedDataProviderFactory<C extends WidgetCreatorContext> extends HasDataProviderFactory<C>
{
	@TagChildren({
		@TagChild(DataProcessor.class),
		@TagChild(DataProviderProcessor.class),
		@TagChild(LazyDataProviderProcessor.class),
		@TagChild(StreamingDataProviderProcessor.class)
	})
	public static class PagedDataProviderChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

}

