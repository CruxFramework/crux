/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This interface can be overridden to declare a generator for a Crux project, 
 * according with a custom layout. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface LayoutProjectGenerator
{
	/**
	 * An initializer method, called by LayoutProjectGeneratorFactory just after the object instantiation.
	 * @param workspaceDir
	 * @param projectName
	 * @param hostedModeStartupModule
	 */
	void init(File workspaceDir, String projectName, String hostedModeStartupModule);
	
	
	/**
	 * Called by CruxProjectGenerator to create the project.
	 * @throws IOException
	 */
	void generate() throws IOException;

	/**
	 * Retrieve the generation options, used by the generator.
	 * @return
	 */
	CruxProjectGeneratorOptions getCruxProjectGeneratorOptions();

	/**
	 * Return an identifier for this layout generator.
	 * @return
	 */
	String getProjectLayout();
	
	/**
	 * Load the generator options from a properties file.
	 * @param config
	 * @throws IOException
	 */
	void loadGeneratorOptions(Properties config) throws IOException;
}
