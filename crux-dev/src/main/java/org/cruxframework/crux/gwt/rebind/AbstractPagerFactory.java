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

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute.WidgetReference;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.view.client.HasRows;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="display", required=true, processingTime=ProcessingTime.afterAllWidgetsOnView, 
				 type=WidgetReference.class, widgetType=HasRows.class),
	@TagAttribute(value="pageSize", type=Integer.class, processingTime=ProcessingTime.afterAllWidgetsOnView),
	@TagAttribute(value="page", type=Integer.class, processingTime=ProcessingTime.afterAllWidgetsOnView),
	@TagAttribute(value="pageStart", type=Integer.class, processingTime=ProcessingTime.afterAllWidgetsOnView),
	@TagAttribute(value="rangeLimited", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="pageSize", type=Integer.class)
})
public abstract class AbstractPagerFactory extends WidgetCreator<WidgetCreatorContext>  
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}

