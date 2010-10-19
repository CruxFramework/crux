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
package br.com.sysmap.crux.gadget.rebind.gadget;

import java.util.HashSet;
import java.util.Set;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.module.Module;
import br.com.sysmap.crux.core.rebind.scanner.module.Modules;
import br.com.sysmap.crux.gadget.client.Gadget;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.ContainerFeature;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.Feature;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.NeedsFeatures;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Set<String> neededFeatures = new HashSet<String>();
	private JClassType moduleMetaClass;
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(Gadget.class.getCanonicalName()));
		try
		{
			br.com.sysmap.crux.core.rebind.scanner.screen.Screen screen = getRequestedScreen();
			Module module = Modules.getInstance().getModule(screen.getModule());
			moduleMetaClass = baseIntf.getOracle().getType(module.getFullName());
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Error generating gadget proxy", e);//TODO message here
			throw new CruxGeneratorException();
		}
    }
	
	/**
	 * @param srcWriter
	 */
	protected void generateFeatureInitialization(SourceWriter srcWriter)
	{
		generateFeaturesInitialization(srcWriter, moduleMetaClass);
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateFeaturesInitialization(SourceWriter srcWriter, JClassType moduleMetaClass)
	{
		NeedsFeatures needsFeatures = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				initializeFeature(srcWriter, feature.value());
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public " + getProxySimpleName() + "(){");
		srcWriter.indent();
		srcWriter.println("this.userPreferences = GWT.create("+UserPreferences.class.getSimpleName()+".class);");
		
		generateFeatureInitialization(srcWriter);
		
		srcWriter.outdent();
		srcWriter.println("}");
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private " + UserPreferences.class.getSimpleName() + " userPreferences = null;");
		
		for (ContainerFeature feature : ContainerFeature.values())
		{
			srcWriter.println("private " + feature.getClass().getCanonicalName() + " "+feature.toString()+"Feature = null;");
		}
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
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
	private void initializeFeature(SourceWriter srcWriter, ContainerFeature feature)
	{
		srcWriter.println("this."+feature.toString()+"Feature = GWT.create("+feature.getClass()+".class);");
		neededFeatures.add(feature.getFeatureName());
	}
}
