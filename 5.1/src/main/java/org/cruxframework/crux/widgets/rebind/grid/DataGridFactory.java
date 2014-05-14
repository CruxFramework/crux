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
package org.cruxframework.crux.widgets.rebind.grid;

import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.widgets.client.grid.Grid;

/**
 * The same as the {@link GridFactory}. This one exists just to create another 
 * declarative name for Grid widget that does not collide with any GWT widget.
 * We need this because of an old annoying Eclipse bug in XML auto-completion.
 * See eclipse bug #296714 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=296714) 
 * for more info.
 *  
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="dataGrid", library="widgets", targetWidget=Grid.class)
@TagChildren({
	@TagChild(value=GridFactory.ColumnProcessor.class, autoProcess=false),
	@TagChild(value=GridFactory.RowDetailsProcessor.class, autoProcess=false)
})
public class DataGridFactory extends GridFactory
{
}