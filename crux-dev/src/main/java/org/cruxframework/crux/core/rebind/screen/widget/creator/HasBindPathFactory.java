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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

/**
 * A helper class to help on widgets that supports binding with dataObjects on View creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="bindPath", description="Sets a property path (eg. property1.proerty2) on the View's associated dataObject. Crux will handle data binding between this dataObject property and this widget. To transfer data between objects and views, cast your view to BindableView and call getData and setData methods."),	
	@TagAttributeDeclaration(value="bindConverter", description="Specify a type converter to handle type differences betwwen the dataObject property type and the type expected by the bound widget. You can use one of the standard converters on TypeConverters utility class or create your own, extending TypeConverter interface.")	
})	
public interface HasBindPathFactory<C extends WidgetCreatorContext>
{
}
