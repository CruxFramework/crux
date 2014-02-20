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
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

/**
 * A helper class to help on HasHorizontalAlignment widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign", 
			processor=HorizontalAlignmentAttributeParser.class, 
			description="Sets the horizontal alignment.Accepts one value between:"
					+ "<ul>"
						+ "<li>center - widget's contents should be aligned in the center</li>"
						+ "<li>justify - widget's contents should be aligned as justify</li>"
						+ "<li>left - widget's contents should be aligned to the left</li>"
						+ "<li>right - widget's contents should be aligned to the right</li>"
						+ "<li>localeStart - In a RTL layout, specifies that the widget's contents should be aligned to the right. In a LTR layout, specifies that the widget's constants should be aligned to the left</li>"
						+ "<li>localeEnd - In a RTL layout, specifies that the widget's contents should be aligned to the left. In a LTR layout, specifies that the widget's constants should be aligned to the right</li>"
						+ "<li>defaultAlign - Synonym of localeStart</li>"
					+ "</ul>")
})	
public interface HasHorizontalAlignmentFactory<C extends WidgetCreatorContext>
{
}
