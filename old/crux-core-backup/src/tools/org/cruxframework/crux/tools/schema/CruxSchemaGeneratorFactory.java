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
package org.cruxframework.crux.tools.schema;

import java.io.File;
import java.lang.reflect.Constructor;

import org.cruxframework.crux.tools.ToolsConfigurationFactory;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxSchemaGeneratorFactory
{
	public static CruxSchemaGenerator createSchemaGenerator(File projectBaseDir, File destDir, File webDir) throws SchemaGeneratorException
	{
		Class<?> generatorClass;
        try
        {
	        generatorClass = Class.forName(ToolsConfigurationFactory.getConfigurations().schemaGeneratorClass());
	        Constructor<?> constructor = generatorClass.getConstructor(File.class, File.class, File.class);
	        
	        CruxSchemaGenerator schemaGenerator = (CruxSchemaGenerator) constructor.newInstance(projectBaseDir, destDir, webDir);
	        return schemaGenerator;
        }
        catch (Exception e)
        {
	        throw new SchemaGeneratorException("Error, creating the SchemaGenerator class: "+e.getLocalizedMessage(),e);
        }
		
	}
}
