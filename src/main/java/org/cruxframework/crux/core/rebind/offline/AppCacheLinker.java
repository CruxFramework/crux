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
package org.cruxframework.crux.core.rebind.offline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.rebind.screen.OfflineScreen;
import org.cruxframework.crux.core.rebind.screen.OfflineScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.utils.FilePatternHandler;
import org.w3c.dom.Document;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.impl.PermutationsUtil;
import com.google.gwt.util.tools.Utility;

/**
 * A GWT linker that produces an offline.appcache file describing what to cache in the application cache. It produces one appcache file for
 * each permutation.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@LinkerOrder(LinkerOrder.Order.POST)
@Shardable
public class AppCacheLinker extends AbstractLinker
{
	private final HashSet<String> cachedArtifacts = new HashSet<String>();

	private static Map<ArtifactsGroup, Set<String>> artifactsByGroup = Collections
	    .synchronizedMap(new HashMap<ArtifactsGroup, Set<String>>());

	private static Set<String> allArtifacts = Collections.synchronizedSet(new HashSet<String>());

	private static final List<String> acceptedFileExtensions = Arrays.asList(".html", ".js", ".css", ".png", ".jpg", ".gif", ".ico");

	private PermutationsUtil permutationsUtil;
	private static AtomicBoolean analyzed = new AtomicBoolean(false);

	@Override
	public String getDescription()
	{
		return "HTML5 appcache manifest generator";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation)
	    throws UnableToCompleteException
	{
		ArtifactSet artifactset = new ArtifactSet(artifacts);

		if (onePermutation)
		{
			aggroupPermutationArtifacts(artifactset);
			analyzed.set(true);
		}
		else
		{
			if (analyzed.get())
			{
				try
				{
					Set<String> offlinePages = OfflineScreens.getOfflineIds(context.getModuleName());
					if (offlinePages != null)
					{
						for (String offlineScreenID : offlinePages)
						{
							Document screen = OfflineScreens.getOfflineScreen(offlineScreenID);
							OfflineScreen offlineScreen = OfflineScreenFactory.getInstance().getOfflineScreen(offlineScreenID, screen);
							emitOfflineArtifacts(logger, context, artifactset, offlineScreen);
						}
					}
				}
				catch (Exception e)
				{
					logger.log(TreeLogger.ERROR, "Unable to create offline files", e);
					throw new UnableToCompleteException();
				}
			}
		}

		return artifactset;
	}

	private void emitOfflineArtifacts(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, OfflineScreen offlineScreen)
	    throws UnableToCompleteException
	{
		String screenID = getTargetScreenId(context, logger, offlineScreen.getRefScreen());
		emitMainAppCache(logger, context, artifacts);
		emitPermutationsAppCache(logger, context, artifacts, screenID, offlineScreen);
		emitOfflinePage(logger, context, artifacts, offlineScreen.getId());
	}

	private static void aggroupPermutationArtifacts(ArtifactSet artifacts)
	{
		String userAgent = getUserAgent(artifacts);
		String deviceFeatures = getDeviceFeatures(artifacts);

		SortedSet<String> hashSet = new TreeSet<String>();
		for (EmittedArtifact emitted : artifacts.find(EmittedArtifact.class))
		{
			if (emitted.getVisibility() == Visibility.Private)
			{
				continue;
			}
			String pathName = emitted.getPartialPath();
			if (acceptCachedResource(pathName))
			{
				hashSet.add(pathName);
				allArtifacts.add(pathName);
			}
		}

		ArtifactsGroup group = new ArtifactsGroup(userAgent, deviceFeatures);

		if (!artifactsByGroup.containsKey(group))
		{
			artifactsByGroup.put(group, new TreeSet<String>());
		}

		artifactsByGroup.get(group).addAll(hashSet);
	}

	/**************************************************
	 * manifest file manipulation methods
	 **************************************************/

	private void emitPermutationsAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, String startScreenId,
	    OfflineScreen offlineScreen) throws UnableToCompleteException
	{
		for (EmittedArtifact emitted : artifacts.find(EmittedArtifact.class))
		{
			if (emitted.getVisibility() == Visibility.Private)
			{
				continue;
			}
			String pathName = emitted.getPartialPath();
			if (acceptCachedResource(pathName))
			{
				if (!allArtifacts.contains(pathName))
				{
					// common stuff like clear.cache.gif, *.nocache.js, etc
					cachedArtifacts.add(pathName);
				}
			}
		}

		Set<ArtifactsGroup> keySet = artifactsByGroup.keySet();
		for (ArtifactsGroup group : keySet)
		{
			Set<String> set = artifactsByGroup.get(group);
			set.addAll(cachedArtifacts);
			artifacts.add(createCacheManifest(context, logger, set, group.getGroupId(), startScreenId, offlineScreen));
			artifacts.add(createCacheManifestLoader(context, logger, group.getGroupId(), startScreenId));
		}
	}

	private void emitMainAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();
		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time [" + getCurrentTimeTruncatingMiliseconds() + "]\n");
		builder.append("\nCACHE:\n");

		for (String fn : cachedArtifacts)
		{
			builder.append("/{context}/" + moduleName + "/" + fn + "\n");
		}

		Set<ArtifactsGroup> keySet = artifactsByGroup.keySet();
		for (ArtifactsGroup group : keySet)
		{
			builder.append("/{context}/" + moduleName + "/" + getManifestLoaderName(group.getGroupId()) + "\n");
		}

		builder.append("\nNETWORK:\n");
		builder.append("*\n");
		EmittedArtifact manifest = emitString(logger, builder.toString(), getManifestName());
		artifacts.add(manifest);
	}

	private Artifact<?> createCacheManifest(LinkerContext context, TreeLogger logger, Set<String> artifacts, String artifactGroupId,
	    String startScreenId, OfflineScreen offlineScreen) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time [" + getCurrentTimeTruncatingMiliseconds() + "]\n");
		builder.append("\nCACHE:\n");

		if (startScreenId != null)
		{
			builder.append("/{context}/" + moduleName + "/" + startScreenId + "\n");
		}

		FilePatternHandler pattern = new FilePatternHandler(offlineScreen.getIncludes(), offlineScreen.getExcludes());
		for (String fn : artifacts)
		{
			if (!fn.endsWith("hosted.html") && pattern.isValidEntry(moduleName + "/" + fn))
			{
				String path = fn.contains("\\") ? fn.replaceAll("\\\\", "/") : fn;
				builder.append("/{context}/" + moduleName + "/" + path + "\n");
			}
		}
		builder.append("\nNETWORK:\n");
		builder.append("*\n\n");

		return emitString(logger, builder.toString(), getManifestName(artifactGroupId));
	}

	private Artifact<?> createCacheManifestLoader(LinkerContext context, TreeLogger logger, String artifactGroupId, String startScreenId)
	    throws UnableToCompleteException
	{
		try
		{
			ViewProcessor.setForceIndent(true);
			ViewProcessor.setOutputCharset("UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Document screen = ScreenResourceResolverInitializer.getScreenResourceResolver().getRootView(startScreenId,
			    context.getModuleName(), null);
			if (screen == null)
			{
				logger.log(TreeLogger.ERROR, "Error generating offline app page. Can not found target screen. ScreenID[" + startScreenId
				    + "]");
				throw new UnableToCompleteException();
			}
			screen.getDocumentElement().setAttribute("manifest", getManifestName(artifactGroupId));
			ViewProcessor.generateHTML(startScreenId, screen, out);
			return emitString(logger, out.toString("UTF-8"), getManifestLoaderName(artifactGroupId));
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Error generating offline app page", e);
			throw new UnableToCompleteException();
		}
	}
	
	/**********************************************************
	 * javascript manipulation methods
	 **********************************************************/
	
	private void emitOfflinePage(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, String offlineScreenId)
	    throws UnableToCompleteException
	{
		permutationsUtil = new PermutationsUtil();
		permutationsUtil.setupPermutationsMap(artifacts);
		StringBuffer buffer = readFileToStringBuffer(getOfflinePageTemplate(logger, context), logger);
		
		int startPos = buffer.indexOf("// __OFFLINE_SELECTION_END__");
		if (startPos != -1)
		{
			String ss = generateSelectionScript(logger, context, artifacts);
			buffer.insert(startPos, ss);
		}
		replaceAll(buffer, "__MANIFEST_NAME__", getManifestName());
		artifacts.add(emitString(logger, buffer.toString(), offlineScreenId, System.currentTimeMillis()));
	}
	
	private String generateSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
	    throws UnableToCompleteException
	{
		String selectionScriptText;
		StringBuffer buffer = readFileToStringBuffer(getSelectionScriptTemplate(logger, context), logger);
		appendProcessMetas(logger, context, buffer);
		appendPageLoaderFunction(logger, context, buffer);
		selectionScriptText = fillSelectionScriptTemplate(buffer, logger, context, artifacts);
		// fix for some browsers like IE that cannot see the $doc variable outside the iframe tag.
		selectionScriptText = selectionScriptText.replace("$doc", "document");

		selectionScriptText = context.optimizeJavaScript(logger, selectionScriptText);
		return selectionScriptText;
	}
	
	private void appendProcessMetas(TreeLogger logger, LinkerContext context, StringBuffer buffer) throws UnableToCompleteException
	{
		int startPos = buffer.indexOf("// __PROCESS_METAS__");
		if (startPos != -1)
		{
			StringBuffer processMetas = readFileToStringBuffer(getJsProcessMetas(context), logger);
			buffer.insert(startPos, processMetas.toString());
		}
	}

	private void appendPageLoaderFunction(TreeLogger logger, LinkerContext context, StringBuffer buffer) throws UnableToCompleteException
	{
		int startPos = buffer.indexOf("// __PAGE_LOADER_FUNCTION__");
		if (startPos != -1)
		{
			StringBuffer pageLoader = readFileToStringBuffer(getPageLoadFunction(logger, context), logger);
			buffer.insert(startPos, pageLoader.toString());
		}
	}

	protected StringBuffer readFileToStringBuffer(String filename, TreeLogger logger) throws UnableToCompleteException
	{
		StringBuffer buffer;
		try
		{
			buffer = new StringBuffer(Utility.getFileFromClassPath(filename));
		}
		catch (IOException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to read file: " + filename, e);
			throw new UnableToCompleteException();
		}
		return buffer;
	}

	protected static void replaceAll(StringBuffer buf, String search, String replace)
	{
		int len = search.length();
		for (int pos = buf.indexOf(search); pos >= 0; pos = buf.indexOf(search, pos + 1))
		{
			buf.replace(pos, pos + len, replace);
		}
	}

	private String fillSelectionScriptTemplate(StringBuffer selectionScript, TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
	    throws UnableToCompleteException
	{
		permutationsUtil.addPermutationsJs(selectionScript, logger, context);
		replaceAll(selectionScript, "__MODULE_FUNC__", context.getModuleFunctionName());
		replaceAll(selectionScript, "__MODULE_NAME__", context.getModuleName());
		return selectionScript.toString();
	}
	
	/**
	 * Returns the name of the {@code JsProcessMetas} script. By default, returns
	 * {@code "com/google/gwt/core/ext/linker/impl/processMetas.js"}.
	 *
	 * @param context a LinkerContext
	 */
	protected String getJsProcessMetas(LinkerContext context)
	{
		return "org/cruxframework/crux/core/rebind/offline/processMetas.js";
	}

	protected String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
	{
		return "org/cruxframework/crux/core/rebind/offline/OfflineSelectionTemplate.js";
	}

	protected String getPageLoadFunction(TreeLogger logger, LinkerContext context)
	{
		return "org/cruxframework/crux/core/rebind/offline/LoadPageFunction.js";
	}

	protected String getOfflinePageTemplate(TreeLogger logger, LinkerContext context)
	{
		return "org/cruxframework/crux/core/rebind/offline/OfflinePage.html";
	}
	
	/**********************************************************
	 * utilities
	 **********************************************************/

	private static boolean acceptCachedResource(String filename)
	{
		if (filename.startsWith("compile-report/"))
		{
			return false;
		}
		for (String acceptedExtension : acceptedFileExtensions)
		{
			if (filename.endsWith(acceptedExtension))
			{
				return true;
			}
		}
		return false;
	}

	private String getTargetScreenId(LinkerContext context, TreeLogger logger, String screenID) throws UnableToCompleteException
	{
		if (StringUtils.isEmpty(screenID))
		{
			screenID = CruxBridge.getInstance().getLastPageRequested();
			try
			{
				screenID = ScreenFactory.getInstance().getScreen(screenID, null).getRelativeId();
			}
			catch (ScreenConfigException e)
			{
				logger.log(TreeLogger.ERROR, e.getMessage(), e);
				throw new UnableToCompleteException();
			}
		}
		// TODO checar se a pagina esta no lugar certo... aceitar apenas na raiz do modulo (/cruxsite/<nomePagina>.html)
		return screenID;
	}

	static long getCurrentTimeTruncatingMiliseconds()
	{
		long currentTime = (System.currentTimeMillis() / 1000) * 1000;
		return currentTime;
	}

	static String getManifestName()
	{
		return "offline.appcache";
	}

	private static String getManifestName(String artifactGroupId)
	{
		return artifactGroupId + ".appcache";
	}
	
	public static String getManifestName(ArtifactSet artifacts)
	{
		String userAgent = getUserAgent(artifacts);
		String deviceFeatures = getDeviceFeatures(artifacts);
		return getManifestName(new ArtifactsGroup(userAgent, deviceFeatures).getGroupId());
	}
	
	private static String getManifestLoaderName(String artifactGroupId)
	{
		return "offlineLoader_" + artifactGroupId + ".cache.html";
	}

	public ArtifactsGroup getGroup(ArtifactSet artifacts)
	{
		String userAgent = getUserAgent(artifacts);
		String deviceFeatures = getDeviceFeatures(artifacts);
		return new ArtifactsGroup(userAgent, deviceFeatures);
	}

	private static String getUserAgent(ArtifactSet artifacts)
	{
		return getProperty(artifacts, "user.agent");
	}

	private static String getDeviceFeatures(ArtifactSet artifacts)
	{
		return getProperty(artifacts, "device.features");
	}

	private static String getProperty(ArtifactSet artifacts, String property)
	{
		for (CompilationResult result : artifacts.find(CompilationResult.class))
		{
			if (result.getPropertyMap() != null && !result.getPropertyMap().isEmpty())
			{
				for (SortedMap<SelectionProperty, String> propertyMap : result.getPropertyMap())
				{
					for (SelectionProperty selectionProperty : propertyMap.keySet())
					{
						if (property.equals(selectionProperty.getName()))
						{
							return propertyMap.get(selectionProperty);
						}
					}
				}
			}

		}
		return null;
	}
}

/**
 * Class description: Implements a artifacts group. A group is defined by user.agent and locale of an artifact.
 * 
 * @author alexandre.costa
 */
class ArtifactsGroup
{
	private static final String DEFAULT = "default";
	
	private final String userAgent;

	private final String deviceFeatures;

	/**
	 * Constructor.
	 * 
	 * @param userAgent user agent
	 * @param deviceFeatures device features
	 */
	public ArtifactsGroup(String userAgent, String deviceFeatures)
	{
		this.userAgent = userAgent == null || userAgent.trim().equals("") ? DEFAULT : userAgent;
		this.deviceFeatures = deviceFeatures == null || deviceFeatures.trim().equals("") ? DEFAULT : deviceFeatures;
	}
	
	public String getGroupId()
	{
		return userAgent + "_" + deviceFeatures;
	}
	
	/****************************************
	 * hashCode and equals
	 ****************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceFeatures == null) ? 0 : deviceFeatures.hashCode());
		result = prime * result + ((userAgent == null) ? 0 : userAgent.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ArtifactsGroup other = (ArtifactsGroup) obj;
		if (deviceFeatures == null)
		{
			if (other.deviceFeatures != null)
			{
				return false;
			}
		}
		else if (!deviceFeatures.equals(other.deviceFeatures))
		{
			return false;
		}
		if (userAgent == null)
		{
			if (other.userAgent != null)
			{
				return false;
			}
		}
		else if (!userAgent.equals(other.userAgent))
		{
			return false;
		}
		return true;
	}
	
	/****************************************
	 * getters and setters
	 ***************************************/

	/**
	 * @return the userAgent
	 */
	public String getUserAgent()
	{
		return userAgent;
	}

	/**
	 * @return the locale
	 */
	public String getDeviceFeatures()
	{
		return deviceFeatures;
	}
}