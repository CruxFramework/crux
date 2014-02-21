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
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.gadget.client.features.UserPreferences;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.ContainerFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.Feature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsFeatures;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.WantsFeatures;
import org.cruxframework.crux.gadget.client.widget.GadgetView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetInfoProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetInfoProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf, false);
    }
	

	/**
	 * @param srcWriter
	 */
	protected void generateFeatureMethods(SourcePrinter srcWriter)
	{
		HashSet<String> added = new HashSet<String>();
		generateRequiredFeaturesMethods(srcWriter, baseIntf, added);
		generateOptionalFeaturesMethods(srcWriter, baseIntf, added);
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateRequiredFeaturesMethods(SourcePrinter srcWriter, JClassType moduleMetaClass, Set<String> added)
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
						generateFeatureMethod(srcWriter, feature.value());
					}
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		JClassType[] interfaces = moduleMetaClass.getImplementedInterfaces();
		if (interfaces != null)
		{
			for (JClassType interfaceType : interfaces)
            {
				generateRequiredFeaturesMethods(srcWriter, interfaceType, added);
            }
		}
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateOptionalFeaturesMethods(SourcePrinter srcWriter, JClassType moduleMetaClass, Set<String> added)
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
						generateFeatureMethod(srcWriter, feature.value());
					}
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		JClassType[] interfaces = moduleMetaClass.getImplementedInterfaces();
		if (interfaces != null)
		{
			for (JClassType interfaceType : interfaces)
            {
				generateOptionalFeaturesMethods(srcWriter, interfaceType, added);
            }
		}
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public " + UserPreferences.class.getCanonicalName() + " getUserPreferences(){");
		srcWriter.println("return GadgetView.getGadget().getUserPreferences();");
		srcWriter.println("}");
		generateFeatureMethods(srcWriter);
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				UserPreferences.class.getCanonicalName(),
				GadgetView.class.getCanonicalName(),
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
	private void generateFeatureMethod(SourcePrinter srcWriter, ContainerFeature feature)
	{
		Class<?> featureClass = feature.getFeatureClass();
		srcWriter.println("public " + featureClass.getCanonicalName() + " get"+featureClass.getSimpleName()+"(){");
		srcWriter.println("return GadgetView.getGadget().get"+featureClass.getSimpleName()+"();");
		srcWriter.println("}");
	}
}
