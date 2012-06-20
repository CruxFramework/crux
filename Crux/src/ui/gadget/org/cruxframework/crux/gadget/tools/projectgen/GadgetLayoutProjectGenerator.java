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
package org.cruxframework.crux.gadget.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.gadget.client.features.UserPreferences;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsAdsFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsDynamicHeightFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsGoogleAnalyticsFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsLockedDomain;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsOsapiFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsRpcFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsSetPrefsFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsSetTitleFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsViewFeature;
import org.cruxframework.crux.tools.projectgen.AbstractLayoutProjectGenerator;
import org.cruxframework.crux.tools.projectgen.CruxProjectGeneratorOptions;
import org.cruxframework.crux.tools.projectgen.CruxProjectGeneratorOptions.GeneratorOption;
import org.cruxframework.crux.tools.projectgen.LayoutProjectGeneratorException;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetLayoutProjectGenerator extends AbstractLayoutProjectGenerator
{
	private CruxProjectGeneratorOptions generatorOptions;

	@Override
    public void createProjectRootFiles() throws IOException
    {
		super.createProjectRootFiles();
		createFile(options.getProjectDir(), options.getProjectName() + ".launch", "gadget/launch.xml");
		
		File shindigSourceDir = new File(options.getLibDir().getParentFile(), "shindig/shindig.war");
		File shindigDestDir = createDir(options.getProjectDir(), "shindig.war");

		FileUtils.copyFilesFromDir(shindigSourceDir, shindigDestDir);
	}

	@Override
    public void createSources() throws IOException
    {
		super.createSources();
		
		File sourceDir = createDir(options.getProjectDir(), "src");
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);
		File clientPackage = createDir(moduleDir, "client");

		createFile(sourceDir, "Crux.properties", "gadget/crux.properties.txt");
		createFile(moduleDir, this.options.getModuleSimpleName() + ".gwt.xml", "gadget/module.xml");
		createFile(clientPackage,  this.options.getProjectName()+".java", "gadget/GadgetDescriptor.java.txt");
		
		String userPreferencesClass = getCruxProjectGeneratorOptions().getOption("gadgetUserPreferences").getValue();
		
		if (userPreferencesClass != null && !userPreferencesClass.equals(UserPreferences.class.getCanonicalName()))
		{
			int dotIndex = userPreferencesClass.lastIndexOf('.');
			File preferencesPackageDir;
			String simpleClassName;
			
			if (dotIndex > 0 && dotIndex < userPreferencesClass.length() -1)
			{
				simpleClassName = userPreferencesClass.substring(dotIndex + 1);
				String packageName = userPreferencesClass.substring(0, dotIndex);
				preferencesPackageDir = createDir(sourceDir, packageName.replaceAll("\\.", "/"));
				getReplacements().add(new String[]{"customUserPreferencesPackage", "package "+packageName+";"});
			}
			else
			{
				simpleClassName = userPreferencesClass;
				preferencesPackageDir = sourceDir;
				getReplacements().add(new String[]{"customUserPreferencesPackage", ""});
			}
			getReplacements().add(new String[]{"customUserPreferencesClass", simpleClassName});
			
			createFile(preferencesPackageDir, simpleClassName+".java", "gadget/customUserPreferences.java.txt");
		}
		
    }

	@Override
    public void createdBuildFiles() throws IOException
    {
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "build"), buildLibDir);
		File webInfLibDir = getWebInfLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "gadget/build"), buildLibDir);
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "gadget/web-inf"), webInfLibDir);
		createFile(buildLibDir.getParentFile(), "build.xml", "gadget/build.xml");
    }

	@Override
    public void createWebRootFiles() throws IOException
    {
		super.createWebRootFiles();
		
		String pageName = getPageName();
		createFile(getWebInfLibDir().getParentFile(), "web.xml", "gadget/web.xml");
		createFile(getWarDir(), pageName, "gadget/index.crux.xml");
    }

    /**
     * @see org.cruxframework.crux.tools.projectgen.LayoutProjectGenerator#getProjectLayout()
     */
    public String getProjectLayout()
    {
	    return "GADGET_APP";
    }

    @Override
    public void loadGeneratorOptions(Properties config)
    {
    	super.loadGeneratorOptions(config);
    	CruxProjectGeneratorOptions options = getCruxProjectGeneratorOptions();
    	
        loadOption(config, options, "gadgetUserPreferences");
        loadOption(config, options, "gadgetUseLongManifestName");
        loadOption(config, options, "gadgetAuthor");
        loadOption(config, options, "gadgetAuthorAboutMe");
        loadOption(config, options, "gadgetAuthorAffiliation");
        loadOption(config, options, "gadgetAuthorEmail");
        loadOption(config, options, "gadgetAuthorLink");
        loadOption(config, options, "gadgetAuthorLocation");
        loadOption(config, options, "gadgetAuthorPhoto");
        loadOption(config, options, "gadgetAuthorQuote");
        loadOption(config, options, "gadgetDescription");
        loadOption(config, options, "gadgetDirectoryTitle");
        loadOption(config, options, "gadgetHeight");
        loadOption(config, options, "gadgetWidth");
        loadOption(config, options, "gadgetScreenshot");
        loadOption(config, options, "gadgetThumbnail");
        loadOption(config, options, "gadgetTitle");
        loadOption(config, options, "gadgetTitleUrl");
        loadOption(config, options, "gadgetScrolling");
        loadOption(config, options, "gadgetSingleton");
        loadOption(config, options, "gadgetScaling");
        loadOption(config, options, "gadgetLocales");
        loadOption(config, options, "gadgetFeatures");
    }

    /**
     * 
     * @param config
     * @param options
     * @param option
     */
	private void loadOption(Properties config, CruxProjectGeneratorOptions options, String option)
    {
	    String value = config.getProperty(option);
    	if (value != null && value.length() > 0)
    	{
    		GeneratorOption opt = options.getOption(option);
    		if (opt != null)
    		{
    			opt.setValue(value);
    		}
    	}
    }

    @Override
    protected CruxProjectGeneratorOptions getCruxProjectGeneratorOptions(File workspaceDir, String projectName, String hostedModeStartupModule)
    {
	    try
        {
	    	if (generatorOptions == null)
	    	{
	    		generatorOptions = new CruxProjectGeneratorOptions(workspaceDir, projectName, hostedModeStartupModule);

	    		generatorOptions.addOption("gadgetUserPreferences", UserPreferences.class.getCanonicalName(), String.class);
	    		generatorOptions.addOption("gadgetUseLongManifestName", "false", Boolean.class);
	    		generatorOptions.addOption("gadgetAuthor", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorAboutMe", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorAffiliation", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorEmail", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorLink", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorLocation", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorPhoto", "", String.class);
	    		generatorOptions.addOption("gadgetAuthorQuote", "", String.class);
	    		generatorOptions.addOption("gadgetDescription", "", String.class);
	    		generatorOptions.addOption("gadgetDirectoryTitle", "", String.class);
	    		generatorOptions.addOption("gadgetHeight", "200", Integer.class);
	    		generatorOptions.addOption("gadgetWidth", "320", Integer.class);
	    		generatorOptions.addOption("gadgetScreenshot", "", String.class);
	    		generatorOptions.addOption("gadgetThumbnail", "", String.class);
	    		generatorOptions.addOption("gadgetTitle", "", String.class);
	    		generatorOptions.addOption("gadgetTitleUrl", "", String.class);
	    		generatorOptions.addOption("gadgetScrolling", "", String.class);
	    		generatorOptions.addOption("gadgetSingleton", "", String.class);
	    		generatorOptions.addOption("gadgetScaling", "", String.class);
	    		generatorOptions.addOption("gadgetLocales", "", String.class);
	    		generatorOptions.addOption("gadgetFeatures", "", String.class);
	    	}
			return generatorOptions;
        }
        catch (Exception e)
        {
        	throw new LayoutProjectGeneratorException("Can not create the generator options object.", e);
        }
    }
	
	@Override
	public List<String[]> getReplacements()
	{
		if(this.replacements == null)
		{
			this.replacements = super.getReplacements();
			
			this.replacements.add(new String[]{"gadgetHostedModeStartupURL", getGadgetHostedModeStartupURL()});
			
			Iterator<String> optionNames = this.options.iterateOptionNames();
			
			while (optionNames.hasNext())
			{
				String option = optionNames.next();
				if (option.equals("gadgetFeatures"))
				{
					this.replacements.add(new String[]{option, getFeaturesReplacement(this.options.getOption(option).getValue())});
				}
				else if (option.equals("gadgetLocales"))
				{
					this.replacements.add(new String[]{option, getLocalesReplacement(this.options.getOption(option).getValue())});
				}
				else
				{
					this.replacements.add(new String[]{option, this.options.getOption(option).getValue()});
				}
			}
		}
		
		return this.replacements;
	}

	/**
	 * @return
	 */
	private String getGadgetHostedModeStartupURL()
    {
		String moduleSimpleName = this.options.getModuleSimpleName().toLowerCase();
		String useLongManifestNameProperty = this.options.getOption("gadgetUseLongManifestName").getValue();
		boolean useLongManifestName = useLongManifestNameProperty!= null && "true".equals(useLongManifestNameProperty);
		
		String descriptorName;
		if (useLongManifestName)
		{
			descriptorName = this.options.getModulePackage().replace('.', '/')+"/client/"+this.options.getProjectName();
		}
		else
		{
			descriptorName = this.options.getProjectName();
		}
	    return "http://localhost:8080/gadgets/ifr?nocache=1&amp;parent=http://localhost:8080/&amp;url=http://127.0.0.1:8888/"+moduleSimpleName+"/"+descriptorName+".gadget.xml";
    }

	/**
	 * @param value
	 * @return
	 */
	private String getLocalesReplacement(String value)
    {
	    return "{}";
    }

	private static Map<String, String> featuresMap = null;

	private String getFeatureClass(String featureName)
	{
		if (featuresMap == null)
		{
			featuresMap = new HashMap<String, String>();
			featuresMap.put("ads", NeedsAdsFeature.class.getCanonicalName());
			featuresMap.put("dynamic-height", NeedsDynamicHeightFeature.class.getCanonicalName());
			featuresMap.put("com.google.gadgets.analytics", NeedsGoogleAnalyticsFeature.class.getCanonicalName());
			featuresMap.put("locked-domain", NeedsLockedDomain.class.getCanonicalName());
			featuresMap.put("osapi", NeedsOsapiFeature.class.getCanonicalName());
			featuresMap.put("rpc", NeedsRpcFeature.class.getCanonicalName());
			featuresMap.put("setprefs", NeedsSetPrefsFeature.class.getCanonicalName());
			featuresMap.put("settitle", NeedsSetTitleFeature.class.getCanonicalName());
			featuresMap.put("views", NeedsViewFeature.class.getCanonicalName());
		}
		
		return featuresMap.get(featureName);
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String getFeaturesReplacement(String value)
    {
		StringBuilder result = new StringBuilder();
		if (value != null && value.trim().length() > 0)
		{
			String[] features = value.split(",");
			if (features.length > 0)
			{
				for (String feature : features)
                {
					result.append(", ");
					result.append(getFeatureClass(feature)+"\n");
                }
			}
		}
		
	    return result.toString();
    }

	@Override
    protected void applyReplacementsToClassPathFileTemplate() throws IOException
    {
		createFile(options.getProjectDir(), ".classpath", "classpath.xml");
    }
	
}
