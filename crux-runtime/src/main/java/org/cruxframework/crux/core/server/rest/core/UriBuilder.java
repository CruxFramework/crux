package org.cruxframework.crux.core.server.rest.core;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.PathHelper;
import org.cruxframework.crux.core.shared.rest.annotation.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriBuilder
{
	private String host;
	private String scheme;
	private int port = -1;

	private String userInfo;
	private String path;
	private String query;
	private String fragment;
	private String ssp;

	public UriBuilder clone()
	{
		UriBuilder impl = new UriBuilder();
		impl.host = host;
		impl.scheme = scheme;
		impl.port = port;
		impl.userInfo = userInfo;
		impl.path = path;
		impl.query = query;
		impl.fragment = fragment;
		impl.ssp = ssp;

		return impl;
	}

	private static final Pattern uriPattern = Pattern.compile("([a-zA-Z0-9+.-]+)://([^/:]+)(:(\\d+))?(/[^?]*)?(\\?([^#]+))?(#(.*))?");
	private static final Pattern sspPattern = Pattern.compile("([^:/]+):(.+)");
	private static final Pattern pathPattern = Pattern.compile("([^?]*)?(\\?([^#]+))?(#(.*))?");

	/**
	 * You may put path parameters anywhere within the uriTemplate except port
	 * 
	 * @param uriTemplate
	 * @return
	 */
	public UriBuilder uriTemplate(String uriTemplate)
	{
		Matcher match = uriPattern.matcher(uriTemplate);
		if (match.matches())
		{
			scheme(match.group(1));
			String host = match.group(2);
			if (host != null)
			{
				int at = host.indexOf('@');
				if (at > -1)
				{
					String user = host.substring(0, at);
					host = host.substring(at + 1);
					userInfo(user);
				}
			}
			host(host);
			if (match.group(4) != null)
				port(Integer.valueOf(match.group(4)));
			if (match.group(5) != null)
				path(match.group(5));
			if (match.group(7) != null)
				replaceQuery(match.group(7));
			if (match.group(9) != null)
				fragment(match.group(9));
			return this;
		}
		match = sspPattern.matcher(uriTemplate);
		if (match.matches())
		{
			scheme(match.group(1));
			schemeSpecificPart(match.group(2));
			return this;
		}

		match = pathPattern.matcher(uriTemplate);
		if (match.matches())
		{
			if (match.group(1) != null)
				path(match.group(1));
			if (match.group(3) != null)
				replaceQuery(match.group(3));
			if (match.group(5) != null)
				fragment(match.group(5));
			return this;
		}
		throw new RuntimeException("Illegal uri template: " + uriTemplate);

	}

	public UriBuilder uri(String uriTemplate) throws IllegalArgumentException
	{
		return uriTemplate(uriTemplate);
	}

	public UriBuilder uri(URI uri) throws IllegalArgumentException
	{
		if (uri == null)
			throw new IllegalArgumentException("URI was null");

		if (uri.getScheme() != null)
			scheme = uri.getScheme();

		if (uri.getRawSchemeSpecificPart() != null && uri.getRawPath() == null)
		{
			ssp = uri.getRawSchemeSpecificPart();
		}
		else
		{
			this.ssp = null;
			if (uri.getHost() != null)
				host = uri.getHost();
			if (uri.getPort() != -1)
				port = uri.getPort();
			if (uri.getUserInfo() != null)
				userInfo = uri.getRawUserInfo();
			if (uri.getPath() != null && !uri.getPath().equals(""))
				path = uri.getRawPath();
			if (uri.getQuery() != null)
				query = uri.getRawQuery();
			if (uri.getFragment() != null)
				fragment = uri.getRawFragment();
		}
		return this;
	}

	public UriBuilder scheme(String scheme) throws IllegalArgumentException
	{
		this.scheme = scheme;
		return this;
	}

	public UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException
	{
		if (ssp == null)
			throw new IllegalArgumentException("schemeSpecificPart was null");

		StringBuilder sb = new StringBuilder();
		if (scheme != null)
			sb.append(scheme).append(':');
		if (ssp != null)
			sb.append(ssp);
		if (fragment != null && fragment.length() > 0)
			sb.append('#').append(fragment);
		URI uri = URI.create(sb.toString());

		if (uri.getRawSchemeSpecificPart() != null && uri.getRawPath() == null)
		{
			this.ssp = uri.getRawSchemeSpecificPart();
		}
		else
		{
			this.ssp = null;
			userInfo = uri.getRawUserInfo();
			host = uri.getHost();
			port = uri.getPort();
			path = uri.getRawPath();
			query = uri.getRawQuery();

		}
		return this;

	}

	public UriBuilder userInfo(String ui)
	{
		this.userInfo = ui;
		return this;
	}

	public UriBuilder host(String host) throws IllegalArgumentException
	{
		if (host == null)
			throw new IllegalArgumentException("schemeSpecificPart was null");
		if (host.equals(""))
			throw new IllegalArgumentException("invalid host");
		this.host = host;
		return this;
	}

	public UriBuilder port(int port) throws IllegalArgumentException
	{
		if (port < -1)
			throw new IllegalArgumentException("Invalid port value");
		this.port = port;
		return this;
	}

	protected static String paths(boolean encode, String basePath, String... segments)
	{
		String path = basePath;
		if (path == null)
			path = "";
		for (String segment : segments)
		{
			if ("".equals(segment))
				continue;
			if (path.endsWith("/"))
			{
				if (segment.startsWith("/"))
				{
					segment = segment.substring(1);
					if ("".equals(segment))
						continue;
				}
				if (encode)
					segment = Encode.encodePath(segment);
				path += segment;
			}
			else
			{
				if (encode)
					segment = Encode.encodePath(segment);
				if ("".equals(path))
				{
					path = segment;
				}
				else if (segment.startsWith("/"))
				{
					path += segment;
				}
				else
				{
					path += "/" + segment;
				}
			}

		}
		return path;
	}

	public UriBuilder path(String segment) throws IllegalArgumentException
	{
		if (segment == null)
			throw new IllegalArgumentException("path was null");
		path = paths(true, path, segment);
		return this;
	}

	public UriBuilder path(Class<?> resource) throws IllegalArgumentException
	{
		if (resource == null)
			throw new IllegalArgumentException("path was null");
		Path ann = (Path) resource.getAnnotation(Path.class);
		if (ann != null)
		{
			String[] segments = new String[] { ann.value() };
			path = paths(true, path, segments);
		}
		else
		{
			throw new IllegalArgumentException("Class must be annotated with @Path to invoke path(Class)");
		}
		return this;
	}

	public UriBuilder path(Method method) throws IllegalArgumentException
	{
		if (method == null)
			throw new IllegalArgumentException("method was null");
		Path ann = method.getAnnotation(Path.class);
		if (ann != null)
		{
			path = paths(true, path, ann.value());
		}
		else
		{
			throw new IllegalArgumentException("method is not annotated with @Path");
		}
		return this;
	}

	public UriBuilder replaceQuery(String query) throws IllegalArgumentException
	{
		if (query == null)
		{
			this.query = null;
			return this;
		}
		this.query = Encode.encodeQueryString(query);
		return this;
	}

	public UriBuilder fragment(String fragment) throws IllegalArgumentException
	{
		this.fragment = Encode.encodeFragment(fragment);
		return this;
	}

	public URI buildFromMap(Map<String, ? extends Object> paramMap, boolean fromEncodedMap) throws IllegalArgumentException, UriBuilderException
	{
		String buf = buildString(paramMap, fromEncodedMap, false);
		try
		{
			return URI.create(buf);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to create URI: " + buf, e);
		}
	}

	private String buildString(Map<String, ? extends Object> paramMap, boolean fromEncodedMap, boolean isTemplate)
	{
		StringBuffer buffer = new StringBuffer();

		if (scheme != null)
			replaceParameter(paramMap, fromEncodedMap, isTemplate, scheme, buffer).append(":");
		if (ssp != null)
		{
			buffer.append(ssp);
		}
		else if (userInfo != null || host != null || port != -1)
		{
			buffer.append("//");
			if (userInfo != null)
				replaceParameter(paramMap, fromEncodedMap, isTemplate, userInfo, buffer).append("@");
			if (host != null)
				replaceParameter(paramMap, fromEncodedMap, isTemplate, host, buffer);
			if (port != -1)
				buffer.append(":").append(Integer.toString(port));
		}
		if (path != null)
		{
			StringBuffer tmp = new StringBuffer();
			replaceParameter(paramMap, fromEncodedMap, isTemplate, path, tmp);
			String tmpPath = tmp.toString();
			if (userInfo != null || host != null)
			{
				if (!tmpPath.startsWith("/"))
					buffer.append("/");
			}
			buffer.append(tmpPath);
		}
		if (query != null)
		{
			buffer.append("?");
			replaceQueryStringParameter(paramMap, fromEncodedMap, isTemplate, query, buffer);
		}
		if (fragment != null)
		{
			buffer.append("#");
			replaceParameter(paramMap, fromEncodedMap, isTemplate, fragment, buffer);
		}
		return buffer.toString();
	}

	protected StringBuffer replacePathParameter(String name, String value, boolean isEncoded, String string, StringBuffer buffer)
	{
		Matcher matcher = createUriParamMatcher(string);
		while (matcher.find())
		{
			String param = matcher.group(1);
			if (!param.equals(name))
				continue;
			if (!isEncoded)
			{
				value = Encode.encodePath(value);
			}
			else
			{
				value = Encode.encodeNonCodes(value);
			}
			// if there is a $ then we must backslash it or it will screw up
			// regex group substitution
			value = value.replace("$", "\\$");
			matcher.appendReplacement(buffer, value);
		}
		matcher.appendTail(buffer);
		return buffer;
	}

	public static Matcher createUriParamMatcher(String string)
	{
		Matcher matcher = PathHelper.URI_PARAM_PATTERN.matcher(PathHelper.replaceEnclosedCurlyBraces(string));
		return matcher;
	}

	protected StringBuffer replaceParameter(Map<String, ? extends Object> paramMap, boolean fromEncodedMap, boolean isTemplate, String string, StringBuffer buffer)
	{
		Matcher matcher = createUriParamMatcher(string);
		while (matcher.find())
		{
			String param = matcher.group(1);
			Object valObj = paramMap.get(param);
			if (valObj == null && !isTemplate)
			{
				throw new IllegalArgumentException("NULL value for template parameter: " + param);
			}
			else if (valObj == null && isTemplate)
			{
				matcher.appendReplacement(buffer, matcher.group());
				continue;
			}
			String value = valObj.toString();
			if (value != null)
			{
				if (!fromEncodedMap)
				{
					value = Encode.encodePathAsIs(value);
				}
				else
				{
					value = Encode.encodePathSaveEncodings(value);
				}
				matcher.appendReplacement(buffer, Matcher.quoteReplacement(value));
			}
			else
			{
				throw new IllegalArgumentException("path param " + param + " has not been provided by the parameter map");
			}
		}
		matcher.appendTail(buffer);
		return buffer;
	}

	protected StringBuffer replaceQueryStringParameter(Map<String, ? extends Object> paramMap, boolean fromEncodedMap, boolean isTemplate, String string, StringBuffer buffer)
	{
		Matcher matcher = createUriParamMatcher(string);
		while (matcher.find())
		{
			String param = matcher.group(1);
			Object valObj = paramMap.get(param);
			if (valObj == null && !isTemplate)
			{
				throw new IllegalArgumentException("NULL value for template parameter: " + param);
			}
			else if (valObj == null && isTemplate)
			{
				matcher.appendReplacement(buffer, matcher.group());
				continue;
			}
			String value = valObj.toString();
			if (value != null)
			{
				if (!fromEncodedMap)
				{
					value = Encode.encodeQueryParamAsIs(value);
				}
				else
				{
					value = Encode.encodeQueryParamSaveEncodings(value);
				}
				matcher.appendReplacement(buffer, value);
			}
			else
			{
				throw new IllegalArgumentException("path param " + param + " has not been provided by the parameter map");
			}
		}
		matcher.appendTail(buffer);
		return buffer;
	}

	/**
	 * Return a unique order list of path params
	 * 
	 * @return
	 */
	public List<String> getPathParamNamesInDeclarationOrder()
	{
		List<String> params = new ArrayList<String>();
		HashSet<String> set = new HashSet<String>();
		if (scheme != null)
			addToPathParamList(params, set, scheme);
		if (userInfo != null)
			addToPathParamList(params, set, userInfo);
		if (host != null)
			addToPathParamList(params, set, host);
		if (path != null)
			addToPathParamList(params, set, path);
		if (query != null)
			addToPathParamList(params, set, query);
		if (fragment != null)
			addToPathParamList(params, set, fragment);

		return params;
	}

	private void addToPathParamList(List<String> params, HashSet<String> set, String string)
	{
		Matcher matcher = PathHelper.URI_PARAM_PATTERN.matcher(PathHelper.replaceEnclosedCurlyBraces(string));
		while (matcher.find())
		{
			String param = matcher.group(1);
			if (set.contains(param))
				continue;
			else
			{
				set.add(param);
				params.add(param);
			}
		}
	}

	public URI build(Object... values) throws IllegalArgumentException, UriBuilderException
	{
		return buildFromValues(false, values);
	}

	protected URI buildFromValues(boolean encoded, Object... values)
	{
		List<String> params = getPathParamNamesInDeclarationOrder();
		if (values.length < params.size())
			throw new IllegalArgumentException("You did not supply enough values to fill path parameters");

		Map<String, Object> pathParams = new HashMap<String, Object>();

		for (int i = 0; i < params.size(); i++)
		{
			String pathParam = params.get(i);
			Object val = values[i];
			if (val == null)
				throw new IllegalArgumentException("A value was null");
			pathParams.put(pathParam, val.toString());
		}
		return buildFromMap(pathParams, encoded);
	}

	public String getHost()
	{
		return host;
	}

	public String getScheme()
	{
		return scheme;
	}

	public int getPort()
	{
		return port;
	}

	public String getUserInfo()
	{
		return userInfo;
	}

	public String getPath()
	{
		return path;
	}

	public String getQuery()
	{
		return query;
	}

	public String getFragment()
	{
		return fragment;
	}

	public UriBuilder replacePath(String path)
	{
		if (path == null)
		{
			this.path = null;
			return this;
		}
		this.path = Encode.encodePath(path);
		return this;
	}

	/**
	 * Create a new instance initialized from an existing URI.
	 * 
	 * @param uri
	 *            a URI that will be used to initialize the UriBuilder.
	 * @return a new UriBuilder.
	 * @throws IllegalArgumentException
	 *             if uri is {@code null}.
	 */
	public static UriBuilder fromUri(URI uri)
	{
		return new UriBuilder().uri(uri);
	}

	/**
	 * Create a new instance initialized from an existing URI.
	 * 
	 * @param uriTemplate
	 *            a URI template that will be used to initialize the UriBuilder,
	 *            may contain URI parameters.
	 * @return a new UriBuilder.
	 * @throws IllegalArgumentException
	 *             if {@code uriTemplate} is not a valid URI template or is
	 *             {@code null}.
	 */
	public static UriBuilder fromUri(String uriTemplate)
	{
		return new UriBuilder().uri(uriTemplate);
	}
}