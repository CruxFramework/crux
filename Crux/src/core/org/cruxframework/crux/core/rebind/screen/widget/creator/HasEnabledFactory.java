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

import org.cruxframework.crux.core.client.permission.Permissions;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="enabled", type=Boolean.class, description="Sets whether this widget is enabled."),
	@TagAttribute(value="editPermission", type=String.class, processor=HasEnabledFactory.EditPermissionAttributeProcessor.class, 
				  description="A role that must be checked to verify if user can edit this widget on the Screen. You must define a RoleManager to handle these permission validations.")
})	
public interface HasEnabledFactory<C extends WidgetCreatorContext>
{
	/**
	 * Process editPermission attribute
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	class EditPermissionAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public EditPermissionAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			out.println("if (!"+Permissions.class.getCanonicalName()+".hasRole("+EscapeUtils.quote(attributeValue)+")){");
			out.println(Permissions.class.getCanonicalName()+".markAsUnauthorizedForEdition("+context.getWidget()+");");
			out.println("}");
        }
	}
}
