/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import br.com.sysmap.crux.core.i18n.DefaultMessage;

/**
 * Messages from generator.
 * @author Thiago
 *
 */
public interface GeneratorMessages 
{
	@DefaultMessage("[generator 001] - WidgetFactory not registered: ")
	String errorGeneratingRegisteredWidgetFactoryNotRegistered();

	@DefaultMessage("[generator 002] - Error for register client event handler. WidgetFactory: {0}. Error:{1}")
	String errorGeneratingRegisteredClientHandler(String widget, String errMesg);

	@DefaultMessage("[generator 003] - Error for register client formatter. WidgetFactory: {0}. Error:{1}")
	String errorGeneratingRegisteredFormatter(String widget, String errMesg);

	@DefaultMessage("[generator 004] - Error for register client class {0}:")
	String errorGeneratingRegisteredElement(String errMsg);

	@DefaultMessage("[generator 005] - Error retrieving screen Identifier.")
	String errorGeneratingRegisteredElementInvalidScreenID();

	@DefaultMessage("[controller 001] - Method Not Found: ")
	String errorinvokingGeneratedMethod();
}
