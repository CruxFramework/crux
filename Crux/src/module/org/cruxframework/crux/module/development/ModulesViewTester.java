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
package org.cruxframework.crux.module.development;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.development.ViewTester;
import org.cruxframework.crux.module.CruxModuleBridge;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModulesViewTester extends ViewTester
{
    private static final long serialVersionUID = -393266133739612955L;

	@Override
    protected void processRequest(HttpServletResponse resp, String moduleName) throws IOException, UnsupportedEncodingException
    {
		CruxModuleBridge.getInstance().registerCurrentModule(moduleName);
        super.processRequest(resp, moduleName);
    }
}
