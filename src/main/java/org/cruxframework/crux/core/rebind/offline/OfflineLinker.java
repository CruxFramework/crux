/*
 * Copyright 2014 cruxframework.org.
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

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.linker.IFrameLinker;

/**
 * A GWT linker that modifies the generated html files to use a cache manifest.
 */
@LinkerOrder(Order.PRIMARY)
@Shardable
public class OfflineLinker extends IFrameLinker
{
	private String appcacheManifest = null;
	
	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException
	{
		if (onePermutation)
		{
			appcacheManifest = AppCacheLinker.getManifestName(AppCacheLinker.getPermutationName(artifacts));
		}
		else
		{
			appcacheManifest = null;	
		}
	    return super.link(logger, context, artifacts, onePermutation);
	}
	
	@Override
	protected String getModulePrefix(TreeLogger logger, LinkerContext context, String strongName) throws UnableToCompleteException
	{
		String result = super.getModulePrefix(logger, context, strongName);
		return appendManifestIntoHTML(result);
	}

	@Override
	protected String getModulePrefix(TreeLogger logger, LinkerContext context, String strongName, int numFragments) throws UnableToCompleteException
	{
		String result = super.getModulePrefix(logger, context, strongName, numFragments);
		return appendManifestIntoHTML(result);
	}

	private String appendManifestIntoHTML(String prefix)
	{
		return prefix.replaceFirst("<html>", "<html manifest=\""+appcacheManifest+"\">");
	}
	
}