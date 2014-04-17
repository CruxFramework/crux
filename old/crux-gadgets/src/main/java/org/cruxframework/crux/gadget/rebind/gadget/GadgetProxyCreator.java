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
package org.cruxframework.crux.gadget.rebind.gadget;

import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.gadget.client.Gadget;
import org.cruxframework.crux.gadget.client.features.UserPreferences;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.ContainerFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.Feature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsFeatures;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.WantsFeatures;
import org.cruxframework.crux.gadget.linker.GadgetManifestGenerator;
import org.cruxframework.crux.gadget.rebind.gwt.GadgetUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Set<String> neededFeatures = new HashSet<String>();
	private Class<?> moduleMetaClass;
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(Gadget.class.getCanonicalName()), true);
	    if (!cacheableVersionFound())
	    {
	    	try
	    	{
	    		GadgetManifestGenerator gadgetManifestGenerator = new GadgetManifestGenerator(logger, getModule());
	    		this.moduleMetaClass = gadgetManifestGenerator.getModuleMetaClass();
	    	}
	    	catch (Exception e)
	    	{
	    		logger.log(TreeLogger.ERROR, "Error generating gadget proxy.", e);
	    		throw new CruxGeneratorException();
	    	}
		}
    }
	
	/**
	 * @param srcWriter
	 */
	private void generateOptionalFeatureInitialization(SourcePrinter srcWriter)
	{
		generateOptionalFeaturesInitialization(srcWriter, moduleMetaClass, new HashSet<String>());
	}


	/**
	 * @param srcWriter
	 */
	private void generateRequiredFeatureInitialization(SourcePrinter srcWriter)
	{
		generateRequiredFeaturesInitialization(srcWriter, moduleMetaClass, new HashSet<String>());
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	private void generateRequiredFeaturesInitialization(SourcePrinter srcWriter, Class<?> moduleMetaClass, Set<String> added)
	{
		NeedsFeatures needsFeatures = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				if (!added.contains(feature.value().getFeatureName()))
				{
					if (feature.value().getFeatureClass() != null)
					{
						initializeFeature(srcWriter, feature.value());
					}
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		Class<?>[] interfaces = moduleMetaClass.getInterfaces();
		if (interfaces != null)
		{
			for (Class<?> interfaceType : interfaces)
            {
				generateRequiredFeaturesInitialization(srcWriter, interfaceType, added);
            }
		}
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	private void generateOptionalFeaturesInitialization(SourcePrinter srcWriter, Class<?> moduleMetaClass, Set<String> added)
	{
		WantsFeatures needsFeatures = moduleMetaClass.getAnnotation(WantsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				if (!added.contains(feature.value().getFeatureName()))
				{
					if (feature.value().getFeatureClass() != null)
					{
						initializeOptionalFeature(srcWriter, feature.value());
					}
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		Class<?>[] interfaces = moduleMetaClass.getInterfaces();
		if (interfaces != null)
		{
			for (Class<?> interfaceType : interfaces)
            {
				generateOptionalFeaturesInitialization(srcWriter, interfaceType, added);
            }
		}
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
    protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public " + getProxySimpleName() + "(){");
		srcWriter.println("this.userPreferences = GWT.create("+GadgetUtils.getUserPrefsType(logger, moduleMetaClass).getCanonicalName()+".class);");
		
		generateRequiredFeatureInitialization(srcWriter);
		generateOptionalFeatureInitialization(srcWriter);
		
		srcWriter.println("}");
    }
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private " + UserPreferences.class.getSimpleName() + " userPreferences = null;");
		
		for (ContainerFeature feature : ContainerFeature.values())
		{
			Class<?> featureClass = feature.getFeatureClass();
			if (featureClass != null)
			{
				srcWriter.println("private " + featureClass.getCanonicalName() + " "+feature.toString()+"Input = null;");
			}
		}
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public " + UserPreferences.class.getSimpleName() + " getUserPreferences(){");
		srcWriter.println("return userPreferences;");
		srcWriter.println("}");
		for (ContainerFeature feature : ContainerFeature.values())
		{
			
			Class<?> featureClass = feature.getFeatureClass();
			if (featureClass != null)
			{
				srcWriter.println("public " + featureClass.getCanonicalName() + " get"+featureClass.getSimpleName()+"(){");
				srcWriter.println("return "+feature.toString()+"Input;");
				srcWriter.println("}");
			}
		}
		
		srcWriter.println("public native boolean hasFeature(String featureName)/*-{");
		srcWriter.println("return $wnd.gadgets.util.hasFeature(featureName);");
		srcWriter.println("}-*/;");
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				UserPreferences.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @param srcWriter
	 * @param feature
	 */
	private void initializeFeature(SourcePrinter srcWriter, ContainerFeature feature)
	{
		srcWriter.println("this."+feature.toString()+"Input = GWT.create("+feature.getFeatureClass().getCanonicalName()+".class);");
		neededFeatures.add(feature.getFeatureName());
	}
	
	/**
	 * @param srcWriter
	 * @param feature
	 */
	private void initializeOptionalFeature(SourcePrinter srcWriter, ContainerFeature feature)
	{
		srcWriter.println("if (hasFeature("+EscapeUtils.quote(feature.getFeatureName())+")){");
		srcWriter.println("this."+feature.toString()+"Input = GWT.create("+feature.getFeatureClass().getCanonicalName()+".class);");
		srcWriter.println("}");
		neededFeatures.add(feature.getFeatureName());
	}
}
