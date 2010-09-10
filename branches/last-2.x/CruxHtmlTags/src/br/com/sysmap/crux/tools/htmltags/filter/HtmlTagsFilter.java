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
package br.com.sysmap.crux.tools.htmltags.filter;

import br.com.sysmap.crux.core.declarativeui.filter.DeclarativeUIFilter;

/**
 * Intercept requests to .html pages and redirect to the correspondent .crux.xml file. Then transform this xml  
 * into the expected .html file. Used only for development.
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @deprecated - Use DeclarativeUIFilter instead
 */
@Deprecated
public class HtmlTagsFilter extends DeclarativeUIFilter
{
}
