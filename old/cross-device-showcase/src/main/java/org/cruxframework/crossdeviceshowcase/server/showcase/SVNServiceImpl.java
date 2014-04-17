package org.cruxframework.crossdeviceshowcase.server.showcase;

import java.util.List;

import org.cruxframework.crossdeviceshowcase.client.remote.showcase.SVNService;
import org.cruxframework.crossdeviceshowcase.client.remote.showcase.ShowcaseException;

/**
 * @author Gesse
 *
 */
public class SVNServiceImpl implements SVNService
{
	@Override
	public String getSourceFile(String path) throws ShowcaseException
	{
		try
		{
			return SVNExplorer.getSourceFile(path);
		}
		catch(Exception e)
		{
			throw new ShowcaseException(e);
		}
	}

	@Override
	public List<String> listSourceFilesForView(String viewName) throws ShowcaseException
	{
		try
		{
			return SVNExplorer.listSourceFilesForView(viewName);
		}
		catch(Exception e)
		{
			throw new ShowcaseException(e);
		}
	}	
}