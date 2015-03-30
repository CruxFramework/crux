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
package org.cruxframework.crux.widgets.rebind.paging;

import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.widgets.client.paging.SequentialPager;


/**
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="sequentialPager", library="widgets", targetWidget=SequentialPager.class, 
		description="A dataSource pager that do not know the size of the set of data. So it allows page changes only in a sequential way.")
public class SequentialPagerFactory extends AbstractPagerFactory
{
}