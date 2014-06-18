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