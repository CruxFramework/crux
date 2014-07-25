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
package org.cruxframework.crux.module.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.declarativeui.filter.DeclarativeUIFilter;


/**
 * Used to locate the resources inside the modules structure. Used only for development. When this filter is used, you need 
 * to remove CruxFilter and DeclarativeUIFilter from your web.xml
 * 
 * @author Thiago da Rosa de Bustamante
 */
@Deprecated
@Legacy
public class ModulesDeclarativeUIFilter extends DeclarativeUIFilter
{
	private static final Log log = LogFactory.getLog(ModulesDeclarativeUIFilter.class);
	@Override
	public void init(FilterConfig config) throws ServletException
	{
	    super.init(config);
	    log.warn("ModulesDeclarativeUIFilter is deprecated. Use "+DeclarativeUIFilter.class.getCanonicalName());
	}
}
