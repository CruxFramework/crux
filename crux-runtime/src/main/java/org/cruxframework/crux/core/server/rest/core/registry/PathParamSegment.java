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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.NotFoundException;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.PathHelper;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PathParamSegment extends Segment implements Comparable<PathParamSegment>
{
	protected String pathExpression;
	protected String regex;
	protected Pattern pattern;
	protected List<Group> groups = new ArrayList<Group>();
	protected int literalCharacters;
	protected int numCapturingGroups;
	protected int numNonDefaultGroups;

	private static class Group
	{
		int group;
		String name;

		private Group(int group, String name)
		{
			this.group = group;
			this.name = name;
		}
	}

	public int compareTo(PathParamSegment pathParamSegment)
	{
		// as per spec sort first by literal characters, then numCapturing
		// groups, then num non-default groups

		if (literalCharacters > pathParamSegment.literalCharacters)
			return -1;
		if (literalCharacters < pathParamSegment.literalCharacters)
			return 1;

		if (numCapturingGroups > pathParamSegment.numCapturingGroups)
			return -1;
		if (numCapturingGroups < pathParamSegment.numCapturingGroups)
			return 1;

		if (numNonDefaultGroups > pathParamSegment.numNonDefaultGroups)
			return -1;
		if (numNonDefaultGroups < pathParamSegment.numNonDefaultGroups)
			return 1;

		return 0;
	}

	// [^?] is in expression to ignore non-capturing group
	public static final Pattern GROUP = Pattern.compile("[^\\\\]\\([^?]");

	/**
	 * Find the number of groups in the regular expression don't count escaped
	 * '('
	 * 
	 * @param regex
	 * @return
	 */
	private static int groupCount(String regex)
	{
		regex = " " + regex; // add a space because GROUP regex trans to match a
							 // non-preceding slash.
		// if the grouping characters in the range block ignore them.
		int idxOpen = regex.indexOf('[');
		if (idxOpen != -1)
		{
			int idxClose = regex.indexOf(']', idxOpen);
			if (idxClose != -1)
			{
				regex = regex.substring(0, idxOpen) + regex.substring(idxClose + 1);
			}
		}
		Matcher matcher = GROUP.matcher(regex);
		int groupCount = 0;
		while (matcher.find())
			groupCount++;
		return groupCount;
	}

	public PathParamSegment(String segment)
	{
		this.pathExpression = segment;
		String replacedCurlySegment = PathHelper.replaceEnclosedCurlyBraces(segment);
		literalCharacters = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment).replaceAll("").length();

		String[] split = PathHelper.URI_PARAM_PATTERN.split(replacedCurlySegment);
		Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment);
		int i = 0;
		StringBuffer buffer = new StringBuffer();
		if (i < split.length)
			buffer.append(Pattern.quote(split[i++]));
		int groupNumber = 1;

		while (withPathParam.find())
		{
			String name = withPathParam.group(1);
			buffer.append("(");
			if (withPathParam.group(3) == null)
			{
				buffer.append("[^/]+");
				groups.add(new Group(groupNumber++, name));
			}
			else
			{
				String expr = withPathParam.group(3);
				expr = PathHelper.recoverEnclosedCurlyBraces(expr);
				buffer.append(expr);
				numNonDefaultGroups++;
				groups.add(new Group(groupNumber++, name));
				groupNumber += groupCount(expr);
			}
			buffer.append(")");
			if (i < split.length)
				buffer.append(Pattern.quote(split[i++]));
		}
		regex = buffer.toString();
		pattern = Pattern.compile(regex);
		numCapturingGroups = groups.size();
	}

	public String getRegex()
	{
		return regex;
	}

	public String getPathExpression()
	{
		return pathExpression;
	}

	protected void populatePathParams(HttpRequest request, Matcher matcher, String path)
	{
		UriInfo uriInfo = (UriInfo) request.getUri();
		for (Group group : groups)
		{
			String value = matcher.group(group.group);
			uriInfo.addEncodedPathParameter(group.name, value);
			int index = matcher.start(group.group);

			int start = 0;
			if (path.charAt(0) == '/')
				start++;
			int segmentIndex = 0;

			if (start < path.length())
			{
				int count = 0;
				for (int i = start; i < index && i < path.length(); i++)
				{
					if (path.charAt(i) == '/')
						count++;
				}
				segmentIndex = count;
			}

			int numSegments = 1;
			for (int i = 0; i < value.length(); i++)
			{
				if (value.charAt(i) == '/')
					numSegments++;
			}

			if (segmentIndex + numSegments > request.getUri().getPathSegments().size())
			{

				throw new BadRequestException("Number of matched segments greater than actual", "Can not invoke requested service");
			}
			PathSegment[] encodedSegments = new PathSegment[numSegments];
			PathSegment[] decodedSegments = new PathSegment[numSegments];
			for (int i = 0; i < numSegments; i++)
			{
				decodedSegments[i] = request.getUri().getPathSegments().get(segmentIndex + i);
				encodedSegments[i] = request.getUri().getPathSegments(false).get(segmentIndex + i);
			}
			uriInfo.getEncodedPathParameterPathSegments().add(group.name, encodedSegments);
			uriInfo.getPathParameterPathSegments().add(group.name, decodedSegments);
		}
	}

	public ResourceMethod matchPattern(HttpRequest request, String path, int start)
	{
		UriInfo uriInfo = (UriInfo) request.getUri();
		Matcher matcher = pattern.matcher(path);
		matcher.region(start, path.length());

		if (matcher.matches())
		{
			// we consumed entire path string
			ResourceMethod invoker = match(request.getHttpMethod(), request);
			if (invoker == null)
			{
				throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());
			}
			uriInfo.pushMatchedURI(path, Encode.decode(path));
			populatePathParams(request, matcher, path);
			return invoker;
		}
		throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());
	}

	public static int pathSegmentIndex(String string, int start, int stop)
	{
		if (start >= string.length())
			return 0;
		int count = 0;
		for (int i = start; i < stop && i < string.length(); i++)
		{
			if (string.charAt(i) == '/')
				count++;
		}
		return count;
	}

}