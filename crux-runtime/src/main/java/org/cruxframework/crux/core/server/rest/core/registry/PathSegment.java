package org.cruxframework.crux.core.server.rest.core.registry;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.server.rest.util.Encode;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathSegment
{
	private String path;
	private String original;

	/**
	 * @param segment
	 *            encoded path segment
	 * @param decode
	 *            whether or not to decode values
	 */
	public PathSegment(String segment, boolean decode)
	{
		this.original = segment;
		this.path = segment;
		if (decode)
			this.path = Encode.decodePath(this.path);
	}

	public String getOriginal()
	{
		return original;
	}

	public String getPath()
	{
		return path;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		if (path != null)
			buf.append(path);
		return buf.toString();
	}

	/**
	 * 
	 * @param path
	 *            encoded full path
	 * @param decode
	 *            whether or not to decode each segment
	 * @return
	 */
	public static List<PathSegment> parseSegments(String path, boolean decode)
	{
		List<PathSegment> pathSegments = new ArrayList<PathSegment>();

		if (path.startsWith("/"))
			path = path.substring(1);
		String[] paths = path.split("/");
		for (String p : paths)
		{
			pathSegments.add(new PathSegment(p, decode));
		}
		return pathSegments;
	}

}