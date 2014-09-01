/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.tools.schema;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface SchemaMessages
{
	@DefaultServerMessage("Accepts any valid widget")
	String anyWidgetsDescription();

	@DefaultServerMessage("Screen tag defines a root view, associated with the document page.")
	String screenDescription();

	@DefaultServerMessage("Points to a resource on the application that must be manually insert into the application cache manifest list.")
	String offlineContentDescription();

	@DefaultServerMessage("Define a new offline page. Offline pages associates themselves to a normal page and give it offline capabilities.")
	String offlineScreenDescription();

	@DefaultServerMessage("A splash screen component that is show while the Crux module is loading.")
	String splashScreenDescription();

	@DefaultServerMessage("A block of code that allow decision about what render on view, based on the device used to access the application.")
	String crossDeviceDescription();

	@DefaultServerMessage("A condition for a crossDevice tag.")
	String crossDeviceConditionDescription();

	@DefaultServerMessage("A set of conditions for a crossDevice tag.")
	String crossDeviceConditionsDescription();

	@DefaultServerMessage("A parameter for a crossDevice tag condition.")
	String crossDeviceConditionParameterDescription();

	@DefaultServerMessage("Template allow you to define a block of declarative code that can be used on any other declarative file.")
	String templateDescription();

	@DefaultServerMessage("A section defines a new tag child into the generated template XSD. The child tag will be inserted into the position where is placed the section tag declaration.")
	String templateSectionDescription();

	@DefaultServerMessage("A view that describes a cross device widget on Crux.")
	String xdeviceDescription();

	@DefaultServerMessage("A view represents a page on Crux. It contains widgets to compose the page visual and references controllers to handle view events.")
	String viewDescription();

	@DefaultServerMessage("Crux {0} Documentation")
	String documentationTitle(String file);

	@DefaultServerMessage("Elements")
	String globalDeclarationsTitle();

	@DefaultServerMessage("Widgets on this library")
	String globalDeclarationsWidgetsTitle();

	@DefaultServerMessage("See more")
	String moreInfoDescription();

	@DefaultServerMessage("Image")
	String illustrationDescription();

}
