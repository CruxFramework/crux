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
package org.cruxframework.crux.widgets.rebind.select;

import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.gwt.rebind.AbstractListBoxFactory;
import org.cruxframework.crux.widgets.client.select.SingleSelect;


/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="singleSelect", library="widgets", targetWidget=SingleSelect.class)
@TagChildren({
	@TagChild(SingleSelectFactory.SelectItemsProcessor.class)
})

public class SingleSelectFactory extends AbstractListBoxFactory
{
	public static class SelectItemsProcessor extends ItemsProcessor
	{
	}
}