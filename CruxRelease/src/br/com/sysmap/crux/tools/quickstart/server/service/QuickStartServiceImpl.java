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
package br.com.sysmap.crux.tools.quickstart.server.service;

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator;
import br.com.sysmap.crux.tools.projectgen.CruxProjectGeneratorOptions;
import br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator.Names;
import br.com.sysmap.crux.tools.quickstart.client.dto.ProjectInfo;
import br.com.sysmap.crux.tools.quickstart.client.remote.QuickStartService;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class QuickStartServiceImpl implements QuickStartService
{
	private static final Log logger = LogFactory.getLog(QuickStartServiceImpl.class);

	/**
	 * @see br.com.sysmap.crux.tools.quickstart.client.remote.QuickStartService#getProjectInfoDefaultValues()
	 */
	public ProjectInfo getProjectInfoDefaultValues()
    {
		ProjectInfo info = new ProjectInfo();

		try
        {
	        Properties config = new Properties();
	        config.load(CruxProjectGenerator.class.getResourceAsStream("/project.properties"));
	        
	        String projectName = config.getProperty(Names.projectName);
	        String hostedModeStartupModule = config.getProperty(Names.hostedModeStartupModule);
	        String hostedModeStartupURL = config.getProperty(Names.hostedModeStartupURL);
	        boolean useCruxModuleExtension = Boolean.parseBoolean(config.getProperty(Names.useCruxModuleExtension));
	        String hostedModeVMArgs = config.getProperty(Names.hostedModeVMArgs);
	        String cruxModuleDescription = config.getProperty(Names.cruxModuleDescription);
	        
	        info.setWorkspaceDir(new File(".").getCanonicalPath());
	        info.setProjectName(projectName);
	        info.setCruxModuleDescription(cruxModuleDescription);
	        info.setHostedModeStartupModule(hostedModeStartupModule);
	        info.setHostedModeStartupURL(hostedModeStartupURL);
	        info.setUseCruxModuleExtension(useCruxModuleExtension);
	        info.setHostedModeVMArgs(hostedModeVMArgs);
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
		
	    return info;
    }

	/**
	 * @see br.com.sysmap.crux.tools.quickstart.client.remote.QuickStartService#generateProject(br.com.sysmap.crux.tools.quickstart.client.dto.ProjectInfo)
	 */
	public Boolean generateProject(ProjectInfo projectInfo)
    {
		try
        {
			CruxProjectGeneratorOptions options = new CruxProjectGeneratorOptions(new File(projectInfo.getWorkspaceDir()), 
					projectInfo.getProjectName(), projectInfo.getHostedModeStartupModule(), 
					projectInfo.getHostedModeStartupURL(), projectInfo.getHostedModeVMArgs(), 
					projectInfo.isUseCruxModuleExtension(), projectInfo.getCruxModuleDescription());
			
	        new CruxProjectGenerator(options).generate();
	        return true;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        	return false;
        }
    }
}
