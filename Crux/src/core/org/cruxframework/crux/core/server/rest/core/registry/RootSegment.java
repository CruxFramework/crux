/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.cruxframework.crux.core.server.rest.core.MultivaluedMapImpl;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.server.rest.spi.NotFoundException;
import org.cruxframework.crux.core.server.rest.util.PathHelper;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RootSegment extends Segment
{
	protected Map<String, SimpleSegment> simpleSegments = new HashMap<String, SimpleSegment>();
	protected Map<String, PathParamSegment> resourceExpressions = new HashMap<String, PathParamSegment>();
	protected List<PathParamSegment> sortedResourceExpressions = new ArrayList<PathParamSegment>();
	protected Map<String, PathParamSegment> locatorExpressions = new HashMap<String, PathParamSegment>();
	protected List<PathParamSegment> sortedLocatorExpressions = new ArrayList<PathParamSegment>();
	protected Map<String, List<ResourceMethod>> bounded = new LinkedHashMap<String, List<ResourceMethod>>();

	/**
	 * Return a map of paths and what resource methods they are bound to
	 * 
	 * @return
	 */
	public Map<String, List<ResourceMethod>> getBounded()
	{
		return bounded;
	}

	@Override
	protected boolean isEmpty()
	{
		return super.isEmpty() && simpleSegments.size() == 0 && resourceExpressions.size() == 0 && locatorExpressions.size() == 0;
	}

	protected void addPath(String[] segments, int index, ResourceMethod invoker)
	{
		String segment = segments[index];
		// Regular expressions can have '{' and '}' characters. Replace them to
		// do match
		String replacedCurlySegment = PathHelper.replaceEnclosedCurlyBraces(segment);
		Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment);
		if (withPathParam.find())
		{
			String expression = recombineSegments(segments, index);

			PathParamSegment segmentNode = resourceExpressions.get(expression);
			if (segmentNode == null)
			{
				segmentNode = new PathParamSegment(expression);
				resourceExpressions.put(segmentNode.getPathExpression(), segmentNode);
				sortedResourceExpressions.add(segmentNode);
				Collections.sort(sortedResourceExpressions);
			}
			segmentNode.addMethod(invoker);
		}
		else
		{
			SimpleSegment segmentNode = simpleSegments.get(segment);
			if (segmentNode == null)
			{
				segmentNode = new SimpleSegment(segment);
				simpleSegments.put(segment, segmentNode);
			}
			if (segments.length > index + 1)
			{
				segmentNode.addPath(segments, index + 1, invoker);
			}
			else
			{
				segmentNode.addMethod(invoker);
			}
		}

	}

	private String recombineSegments(String[] segments, int index)
	{
		String expression = "";
		boolean first = true;
		for (int i = index; i < segments.length; i++)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				expression += "/";
			}
			expression += segments[i];
		}
		return expression;
	}

	public void addPath(String path, ResourceMethod invoker)
	{
		List<ResourceMethod> list = bounded.get(path);
		if (list == null)
		{
			list = new ArrayList<ResourceMethod>();
			bounded.put(path, list);
		}
		list.add(invoker);
		if (path.startsWith("/"))
			path = path.substring(1);

		MultivaluedMapImpl<String, String> pathParamExpr = new MultivaluedMapImpl<String, String>();
		StringBuffer newPath = pullPathParamExpressions(path, pathParamExpr);
		path = newPath.toString();
		String[] segments = path.split("/");

		for (int i = 0; i < segments.length; i++)
		{
			segments[i] = putBackPathParamExpressions(segments[i], pathParamExpr);
		}
		addPath(segments, 0, invoker);
	}


	protected ResourceMethod matchChildren(HttpRequest request, String path, int start)
	{
		String simpleSegment = null;
		if (start == path.length())
		{
			simpleSegment = "";
		}
		else
		{
			int endOfSegmentIndex = path.indexOf('/', start);
			if (endOfSegmentIndex > -1)
				simpleSegment = path.substring(start, endOfSegmentIndex);
			else
				simpleSegment = path.substring(start);
		}

		RestFailure lastFailure = null;

		SimpleSegment segment = simpleSegments.get(simpleSegment);
		if (segment != null)
		{
			try
			{
				return segment.matchSimple(request, path, start);
			}
			catch (RestFailure e)
			{
				lastFailure = e;
			}
		}

		for (PathParamSegment pathParamSegment : sortedResourceExpressions)
		{
			try
			{
				return pathParamSegment.matchPattern(request, path, start);
			}
			catch (RestFailure e)
			{
				// try and propagate matched path that threw non-404 responses,
				// i.e. MethodNotAllowed, etc.
				if (lastFailure == null || lastFailure instanceof NotFoundException)
					lastFailure = e;
			}
		}
		for (PathParamSegment pathParamSegment : sortedLocatorExpressions)
		{
			try
			{
				return pathParamSegment.matchPattern(request, path, start);
			}
			catch (RestFailure e)
			{
				// try and propagate matched path that threw non-404 responses,
				// i.e. MethodNotAllowed, etc.
				if (lastFailure == null || lastFailure instanceof NotFoundException)
					lastFailure = e;
			}
		}
		if (lastFailure != null)
			throw lastFailure;
		throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());
	}

	public ResourceMethod matchRoot(HttpRequest request)
	{
		int start = 0;
		return matchRoot(request, start);
	}

	public ResourceMethod matchRoot(HttpRequest request, int start)
	{
		String path = request.getUri().getMatchingPath();
		if (start < path.length() && path.charAt(start) == '/')
			start++;
		return matchChildren(request, path, start);
	}

	private static StringBuffer pullPathParamExpressions(String path, MultivaluedMapImpl<String, String> pathParamExpr)
	{
		// Regular expressions can have '{' and '}' characters. Replace them to
		// do match
		path = PathHelper.replaceEnclosedCurlyBraces(path);

		Matcher matcher = PathHelper.URI_PARAM_WITH_REGEX_PATTERN.matcher(path);
		StringBuffer newPath = new StringBuffer();
		while (matcher.find())
		{
			String name = matcher.group(1);
			String regex = matcher.group(3);
			// Regular expressions can have '{' and '}' characters. Recover
			// original replacement
			pathParamExpr.add(name, PathHelper.recoverEnclosedCurlyBraces(regex));
			matcher.appendReplacement(newPath, "{$1:x}");
		}
		matcher.appendTail(newPath);
		return newPath;
	}

	private static String putBackPathParamExpressions(String path, MultivaluedMapImpl<String, String> pathParamExpr)
	{
		Matcher matcher = PathHelper.URI_PARAM_WITH_REGEX_PATTERN.matcher(path);
		StringBuffer newPath = new StringBuffer();
		while (matcher.find())
		{
			String name = matcher.group(1);
			String val = pathParamExpr.get(name).remove(0);
			// double encode slashes, so that slashes stay where they are
			val = val.replace("\\", "\\\\");
			val = val.replace("$", "\\$");
			matcher.appendReplacement(newPath, "{$1:" + val + "}");
		}
		matcher.appendTail(newPath);
		return newPath.toString();
	}

}