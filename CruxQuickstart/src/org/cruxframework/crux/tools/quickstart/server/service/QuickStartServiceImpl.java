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
package org.cruxframework.crux.tools.quickstart.server.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.tools.projectgen.CruxProjectGenerator;
import org.cruxframework.crux.tools.projectgen.CruxProjectGenerator.Names;
import org.cruxframework.crux.tools.projectgen.CruxProjectGeneratorOptions;
import org.cruxframework.crux.tools.quickstart.client.dto.DirectoryInfo;
import org.cruxframework.crux.tools.quickstart.client.dto.ProjectInfo;
import org.cruxframework.crux.tools.quickstart.client.remote.QuickStartService;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class QuickStartServiceImpl implements QuickStartService
{
	private static final Log logger = LogFactory.getLog(QuickStartServiceImpl.class);
	private static QuickStartServerMessages messages = MessagesFactory.getMessages(QuickStartServerMessages.class);

	/**
	 * @see org.cruxframework.crux.tools.quickstart.client.remote.QuickStartService#getProjectInfoDefaultValues()
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
	        String projectLayout = config.getProperty(Names.projectLayout);
	        String hostedModeVMArgs = config.getProperty(Names.hostedModeVMArgs);
	        String appDescription = config.getProperty(Names.appDescription);
	        
	        info.setProjectName(projectName);
	        info.setAppDescription(appDescription);
	        info.setHostedModeStartupModule(hostedModeStartupModule);
	        info.setHostedModeStartupURL(hostedModeStartupURL);
	        info.setProjectLayout(projectLayout);
	        info.setHostedModeVMArgs(hostedModeVMArgs);
	        
	        info.addProjectLayout(messages.projectLayoutMonolithicApp(), "MONOLITHIC_APP");
	        info.addProjectLayout(messages.projectLayoutModuleApp(), "MODULE_APP");
	        info.addProjectLayout(messages.projectLayoutModuleContainerApp(), "MODULE_CONTAINER_APP");
	        info.addProjectLayout(messages.projectLayoutGadgetApp(), "GADGET_APP");
	        
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
		
        return info;
    }

	/**
	 * @see org.cruxframework.crux.tools.quickstart.client.remote.QuickStartService#generateProject(org.cruxframework.crux.tools.quickstart.client.dto.ProjectInfo)
	 */
	public Boolean generateProject(ProjectInfo projectInfo)
	{
		try
		{
			CruxProjectGenerator cruxProjectGenerator = new CruxProjectGenerator(new File(projectInfo.getWorkspaceDir()), projectInfo.getProjectName(), 
					projectInfo.getHostedModeStartupModule(), projectInfo.getProjectLayout());

			CruxProjectGeneratorOptions layoutParameters = cruxProjectGenerator.getLayoutParameters();

			layoutParameters.setHostedModeStartupURL(projectInfo.getHostedModeStartupURL());
			layoutParameters.setHostedModeVMArgs(projectInfo.getHostedModeVMArgs());
			layoutParameters.setAppDescription(projectInfo.getAppDescription());

			if ("GADGET_APP".equals(projectInfo.getProjectLayout()))
			{
				layoutParameters.getOption("gadgetUseLongManifestName").setValue(Boolean.toString(projectInfo.getGadgetInfo().isUseLongManifestName()));
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthor()))
				{
					layoutParameters.getOption("gadgetAuthor").setValue(projectInfo.getGadgetInfo().getAuthor());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorAboutMe()))
				{
					layoutParameters.getOption("gadgetAuthorAboutMe").setValue(projectInfo.getGadgetInfo().getAuthorAboutMe());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorAffiliation()))
				{
					layoutParameters.getOption("gadgetAuthorAffiliation").setValue(projectInfo.getGadgetInfo().getAuthorAffiliation());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorEmail()))
				{
					layoutParameters.getOption("gadgetAuthorEmail").setValue(projectInfo.getGadgetInfo().getAuthorEmail());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorLink()))
				{
					layoutParameters.getOption("gadgetAuthorLink").setValue(projectInfo.getGadgetInfo().getAuthorLink());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorLocation()))
				{
					layoutParameters.getOption("gadgetAuthorLocation").setValue(projectInfo.getGadgetInfo().getAuthorLocation());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorPhoto()))
				{
					layoutParameters.getOption("gadgetAuthorPhoto").setValue(projectInfo.getGadgetInfo().getAuthorPhoto());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorQuote()))
				{
					layoutParameters.getOption("gadgetAuthorQuote").setValue(projectInfo.getGadgetInfo().getAuthorQuote());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorQuote()))
				{
					layoutParameters.getOption("getDescription").setValue(projectInfo.getGadgetInfo().getDescription());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getAuthorQuote()))
				{
					layoutParameters.getOption("getDirectoryTitle").setValue(projectInfo.getGadgetInfo().getDirectoryTitle());
				}
				layoutParameters.getOption("gadgetHeight").setValue(Integer.toString(projectInfo.getGadgetInfo().getHeight()));
				layoutParameters.getOption("gadgetWidth").setValue(Integer.toString(projectInfo.getGadgetInfo().getWidth()));
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getScreenshot()))
				{
					layoutParameters.getOption("gadgetScreenshot").setValue(projectInfo.getGadgetInfo().getScreenshot());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getThumbnail()))
				{
					layoutParameters.getOption("gadgetThumbnail").setValue(projectInfo.getGadgetInfo().getThumbnail());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getTitle()))
				{
					layoutParameters.getOption("gadgetTitle").setValue(projectInfo.getGadgetInfo().getTitle());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getTitleUrl()))
				{
					layoutParameters.getOption("gadgetTitleUrl").setValue(projectInfo.getGadgetInfo().getTitleUrl());
				}
				layoutParameters.getOption("gadgetScrolling").setValue(Boolean.toString(projectInfo.getGadgetInfo().isScrolling()));
				layoutParameters.getOption("gadgetSingleton").setValue(Boolean.toString(projectInfo.getGadgetInfo().isSingleton()));
				layoutParameters.getOption("gadgetScaling").setValue(Boolean.toString(projectInfo.getGadgetInfo().isScaling()));
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getLocales()))
				{
					layoutParameters.getOption("gadgetLocales").setValue(projectInfo.getGadgetInfo().getLocales());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getFeatures()))
				{
					layoutParameters.getOption("gadgetFeatures").setValue(projectInfo.getGadgetInfo().getFeatures());
				}
				if (!StringUtils.isEmpty(projectInfo.getGadgetInfo().getUserPreferences()))
				{
					layoutParameters.getOption("gadgetUserPreferences").setValue(projectInfo.getGadgetInfo().getUserPreferences());
				}
			}
    		cruxProjectGenerator.generate();
	        return true;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        	return false;
        }
    }

	/**
	 * @see org.cruxframework.crux.tools.quickstart.client.remote.QuickStartService#getDirectoryInfo(java.lang.String)
	 */
	public DirectoryInfo getDirectoryInfo(String directoryPath)
    {
		try
        {
			boolean isRoot = directoryPath.equals("/") || directoryPath.equals("/..") || 
			                 (directoryPath.length() == 5 && directoryPath.charAt(1) == ':' && directoryPath.substring(2).equals("/.."));
			DirectoryInfo result = new DirectoryInfo();
			File dir = new File(directoryPath + "/");
			
			if (!isRoot)
			{
				result.setFullPath(getFullPath(dir));
				result.setHasParent(true);
				result.setContents(getDirContents(dir));
			}
			else
			{
				result.setFullPath("/");
				result.setHasParent(false);
				if (isUnix())
				{
					result.setContents(getDirContents(dir));
				}
				else
				{
					result.setContents(getAllDrives());
				}
			}
			
			return result;
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	private String getFullPath(File dir) throws IOException
	{
		String fullPath = dir.getCanonicalPath();
		return fullPath.replace('\\', '/');
	}

	/**
	 * @param
	 * @return String[]
	 * @throws IOException
	 */
	public String[] getAllDrives() throws IOException
	{
		File[] roots = File.listRoots();
		String[] drives = new String[roots.length];
		for (int i = 0; i < roots.length; i++)
		{
			drives[i] = roots[i].getAbsolutePath().replaceAll("[\\\\/]", "");
		}
		return drives;
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	private boolean isUnix() throws IOException
	{
		File[] roots = File.listRoots();
		return roots == null || (roots.length == 1 && roots[0].getCanonicalPath().equals("/"));
	}

	/**
	 * @param dir
	 * @return
	 */
	private String[] getDirContents(File dir)
    {
	    File[] files = dir.listFiles(new FileFilter()
	    {
	    	public boolean accept(File pathname)
	    	{
	    		return pathname.isDirectory();
	    	}
	    });
	    
	    List<String> contents = new ArrayList<String>();
	    
	    if (files != null)
	    {
	    	for (File file : files)
	    	{
	    		contents.add(file.getName());
	    	}
	    }
	    Collections.sort(contents);
	    
	    return contents.toArray(new String[contents.size()]);
    }
}
