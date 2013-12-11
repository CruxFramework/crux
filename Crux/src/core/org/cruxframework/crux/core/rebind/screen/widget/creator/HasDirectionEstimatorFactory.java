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
import org.cruxframework.crux.core.rebind.screen.widget.creator.direction.DirectionEstimator;
import org.cruxframework.crux.core.rebind.screen.widget.creator.direction.DirectionEstimatorAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

/**
 * A helper class to help on HasDirectionEstimator widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="directionEstimator", type=DirectionEstimator.class, defaultValue="defaultAlign", 
			processor=DirectionEstimatorAttributeParser.class,
			description="Set the SelectionModel used by this widget. Accepts one value between:"
				 		+ "<ul>"
				 		+ "<li>anyRtl - Estimates the direction of a given string using the \"any RTL\" heuristic: the return value is RTL if the string contains at least one RTL character. Otherwise, it is LTR.</li>"
				 		+ "<li>firstStrong -  Estimates the direction of a given string using the \"first strong\" heuristic: The return value is determined by the first character in the string with strong directionality. If there is no such character, the return value is DEFAULT.</li>"
				 		+ "<li>wordCount - Estimates the direction of a given string using the \"word count\" heuristic: If the number of RTL words is above a certain percentage of the total number of strongly directional words, returns RTL. Otherwise, if any words are strongly or weakly LTR, returns LTR. Otherwise, returns DEFAULT, which is used to mean \"neutral\". Numbers are counted as weakly LTR.</li>"
				 		+ "<li>defaultAlign - use the default estimator</li>"
				 		+ "</ul>")
})	
public interface HasDirectionEstimatorFactory<C extends WidgetCreatorContext>
{
}
