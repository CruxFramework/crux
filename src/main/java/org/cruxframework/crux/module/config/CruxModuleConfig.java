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
package org.cruxframework.crux.module.config;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.i18n.DefaultServerMessage;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public interface CruxModuleConfig
{
	@DefaultServerMessage("org.cruxframework.crux.module.validation.DecimalNotationVersionChecker")
	String cruxModuleVersionChecker();
	
	void setCruxModuleVersionChecker(String value);
}
