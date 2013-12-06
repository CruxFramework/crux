/*
 * Copyright 2010 cruxframework.org.
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
package org.cruxframework.crux.gadget.linker;

import java.util.Iterator;

import org.cruxframework.crux.gadget.util.HangoutUtils;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

/**
 * A Gadget does not use the {@code .nocache.js} file for the bootstrap. All bootstrap code is inserted
 * inside the gadget manifest file (the {@code .gadget.xml} file).
 * <p>
 * The linker also needs to change some script templates to integrate the application with the gadget container.
 * All requests for resources must be piped through the gadget proxy (using {@code gadgets.io.getProxyUrl()} 
 * method). 
 * @author Thiago da Rosa de Bustamante
 */
@LinkerOrder(Order.PRIMARY)
@Shardable
public final class GadgetLinker extends CrossSiteIframeLinker
{
	private static final String SCRIPT_SRC_HANGOUTSAPI_SANDBOX = "<script src=\"//hangoutsapi.talkgadget.google.com/hangouts/_/api/hangout.js?v=1.3\" type=\"text/javascript\"></script>";
	private static final String SCRIPT_SRC_HANGOUTSAPI = "<script src=\"//talkgadget.google.com/hangouts/_/api/hangout.js?v=1.3\" type=\"text/javascript\"></script>";
	private static final String GADGET_LINKER_TEMPLATE_JS = "org/cruxframework/crux/gadget/linker/GadgetTemplate.js";
	private static final String GADGET_COMPUTE_SCRIPT_BASE_JS = "org/cruxframework/crux/gadget/linker/computeScriptBase.js";
	private static final String GADGET_INSTALL_SCRIPT_JS = "org/cruxframework/crux/gadget/linker/installScriptEarlyDownload.js";
	private static final String GADGET_PROCESS_METAS_JS = "org/cruxframework/crux/gadget/linker/processMetas.js";
	private static final String GADGET_WAIT_FOR_BODY_LOADED_JS = "org/cruxframework/crux/gadget/linker/waitForBodyLoaded.js";
	private static final String GADGET_SET_LOCALE_JS = "org/cruxframework/crux/gadget/linker/setGadgetLocale.js";
	private static final String GADGET_COMPUT_URL_FOR_RESOURCE_JS = "org/cruxframework/crux/gadget/linker/computeUrlForGadgetResource.js";
	private static final String HANGOUT_GADGET_COMPUT_URL_FOR_RESOURCE_JS = "org/cruxframework/crux/gadget/linker/computeUrlForHangoutGadgetResource.js";

	private ArtifactSet toLink;

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Crux Gadget";
	}

	
	/**
	 * We need to save the original artifactSet received here to be able to re-emit the selection 
	 * script when {@link #relink(TreeLogger, LinkerContext, ArtifactSet)} method is called.
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#link(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet, boolean)
	 */
	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException
	{
		toLink = new ArtifactSet(artifacts);

		ArtifactSet result = super.link(logger, context, toLink, onePermutation);
		return result;
	}

	/**
	 * We must re-emit the selection script here, once the script is placed on the gadget manifest file.
	 * <p>
	 * It is necessary because the HTML code for the page is also placed on this same file. If we do not
	 * re-generate the manifest file, hot deployment would not work for {@code .crux.xml} files.
	 * @see com.google.gwt.core.ext.Linker#relink(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	public ArtifactSet relink(TreeLogger logger, LinkerContext context, ArtifactSet newArtifacts) throws UnableToCompleteException
	{
		permutationsUtil.setupPermutationsMap(toLink);
		ArtifactSet toReturn = new ArtifactSet(toLink);
		
		Iterator<Artifact<?>> iterator = newArtifacts.iterator();
		while (iterator.hasNext())
		{
			toReturn.add(iterator.next());
		}
		
		EmittedArtifact art = emitSelectionScript(logger, context, toLink);
		if (art != null) {
			toReturn.add(art);
		}
		maybeOutputPropertyMap(logger, context, toReturn);
		maybeAddHostedModeFile(logger, context, toReturn, null);
		return toReturn;
	}
	
	/**
	 * This method changes the gwt default behavior to emit the selection script into the manifest file.
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#emitSelectionScript(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	protected EmittedArtifact emitSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{

		logger = logger.branch(TreeLogger.DEBUG, "Building gadget manifest", null);

		String bootstrap = "<script>" + generateSelectionScript(logger, context, artifacts) + 
		"</script>";
		if (HangoutUtils.isHangoutGadget())
		{
			if (HangoutUtils.useHangoutSandbox())
			{
				bootstrap = SCRIPT_SRC_HANGOUTSAPI_SANDBOX + bootstrap;
			}
			else
			{
				bootstrap = SCRIPT_SRC_HANGOUTSAPI + bootstrap;
			}

		}
		
		StringBuffer manifest = new StringBuffer();
		
		GadgetManifestGenerator gadgetManifestGenerator = new GadgetManifestGenerator(logger, context.getModuleName());
		manifest.append(gadgetManifestGenerator.generateGadgetManifestFile());
		String manifestName = gadgetManifestGenerator.getManifestName();
		replaceAll(manifest, "__BOOTSTRAP__", bootstrap);

		return emitString(logger, manifest.toString(), manifestName);
	}

	/**
	 * Locale handling is different for gadgets, so we need to insert a specific script
	 * for it. 
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#fillSelectionScriptTemplate(java.lang.StringBuffer, com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet, com.google.gwt.core.ext.linker.CompilationResult)
	 */
	@Override
	protected String fillSelectionScriptTemplate(StringBuffer selectionScript, TreeLogger logger, 
			LinkerContext context, ArtifactSet artifacts, CompilationResult result) throws UnableToCompleteException
	{
		super.fillSelectionScriptTemplate(selectionScript, logger, context, artifacts, result);

		/*   
		 * Gadget iframe URLs are generated with the locale in the URL as a
		 * lang/country parameter pair (e.g. lang=en&country=US) in lieu of the
		 * single locale parameter.
		 * ($wnd.__gwt_Locale is read by the property provider in I18N.gwt.xml)
		 */
		includeJs(selectionScript, logger, getJsSetGadgetLocale(context), "__GADGET_SET_LOCALE__");
		/*
		 * Hangout gadgets need to use absolute urls for any script or css inclusions. 
		 */
		if (HangoutUtils.isHangoutGadget())
		{
			String gadgetDeployURL = HangoutUtils.getDeployURL();
			replaceAll(selectionScript, "__DEPLOY_URL__", gadgetDeployURL);
		}
		return selectionScript.toString();
	}

	/**
	 * 
	 */
	@Override
	protected String getJsComputeUrlForResource(LinkerContext context) 
	{
		if (HangoutUtils.isHangoutGadget())
		{
			return HANGOUT_GADGET_COMPUT_URL_FOR_RESOURCE_JS;
		}
		else
		{
			return GADGET_COMPUT_URL_FOR_RESOURCE_JS;
		}
	}
	
	/**
	 * Gets the setLocale template for gadgets
	 */
	protected String getJsSetGadgetLocale(LinkerContext context)
	{
		return GADGET_SET_LOCALE_JS;
	}
	
	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getJsComputeScriptBase(com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getJsComputeScriptBase(LinkerContext context)
	{
		return GADGET_COMPUTE_SCRIPT_BASE_JS;
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getJsInstallScript(com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getJsInstallScript(LinkerContext context)
	{
		return GADGET_INSTALL_SCRIPT_JS;
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getJsProcessMetas(com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getJsProcessMetas(LinkerContext context)
	{
		return GADGET_PROCESS_METAS_JS;
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getJsWaitForBodyLoaded(com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getJsWaitForBodyLoaded(LinkerContext context)
	{
		return GADGET_WAIT_FOR_BODY_LOADED_JS;
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getSelectionScriptTemplate(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
	{
		return GADGET_LINKER_TEMPLATE_JS;
	}
}
