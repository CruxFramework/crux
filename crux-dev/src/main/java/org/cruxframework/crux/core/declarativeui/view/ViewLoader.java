/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.declarativeui.view;

import java.util.List;

import org.cruxframework.crux.core.declarativeui.template.TemplateLoader;

import com.google.gwt.dev.resource.Resource;
/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ViewLoader
{
	TemplateLoader getTemplateLoader();
	
	Resource getView(String id) throws ViewException;
	
	/**
	 * Return a list with resource paths of all views
	 * @return
	 */
	List<String> getViews();
	
	class SimpleViewLoader implements ViewLoader
	{
		@Override
        public TemplateLoader getTemplateLoader()
        {
	        return null;
        }

		@Override
        public Resource getView(String id) throws ViewException
        {
	        return null;
        }

		@Override
        public List<String> getViews()
        {
	        return null;
        }
	}
}
