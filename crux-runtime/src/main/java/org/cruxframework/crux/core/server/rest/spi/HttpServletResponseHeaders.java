package org.cruxframework.crux.core.server.rest.spi;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.rest.core.CacheControl;
import org.cruxframework.crux.core.server.rest.core.Cookie;
import org.cruxframework.crux.core.server.rest.core.EntityTag;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMap;
import org.cruxframework.crux.core.server.rest.core.NewCookie;
import org.cruxframework.crux.core.server.rest.util.CaseInsensitiveMap;
import org.cruxframework.crux.core.server.rest.util.header.CacheControlHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.CookieHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.EntityTagHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.LocaleHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.MediaTypeHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.NewCookieHeaderParser;
import org.cruxframework.crux.core.server.rest.util.header.UriHeaderParser;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseHeaders implements MultivaluedMap<String, Object>
{
	@SuppressWarnings("rawtypes")
    private CaseInsensitiveMap cachedHeaders = new CaseInsensitiveMap();
	private HttpServletResponse response;

	public HttpServletResponseHeaders(HttpServletResponse response)
	{
		this.response = response;
	}

	@Override
	public void addAll(String key, Object... newValues)
	{
		for (Object value : newValues)
		{
			add(key, value);
		}
	}

	@Override
	public void addAll(String key, List<Object> valueList)
	{
		for (Object value : valueList)
		{
			add(key, value);
		}
	}

	@Override
	public void addFirst(String key, Object value)
	{
		List<Object> list = get(key);
		if (list == null)
		{
			add(key, value);
			return;
		}
		else
		{
			list.add(0, value);
		}
	}

	@SuppressWarnings("unchecked")
    public void putSingle(String key, Object value)
	{
		cachedHeaders.putSingle(key, value);
		if (value instanceof Cookie)
		{
			response.setHeader(key, CookieHeaderParser.toString((Cookie) value));
		}
		else if (value instanceof NewCookie)
		{
			response.setHeader(key, NewCookieHeaderParser.toString((NewCookie) value));
		}
		else if (value instanceof CacheControl)
		{
			response.setHeader(key, CacheControlHeaderParser.toString((CacheControl) value));
		}
		else if (value instanceof URI)
		{
			response.setHeader(key, UriHeaderParser.toString((URI) value));
		}
		else if (value instanceof Locale)
		{
			response.setHeader(key, LocaleHeaderParser.toString((Locale) value));
		}
		else if (value instanceof MediaType)
		{
			response.setHeader(key, MediaTypeHeaderParser.toString((MediaType) value));
		}
		else if (value instanceof EntityTag)
		{
			response.setHeader(key, EntityTagHeaderParser.toString((EntityTag) value));
		}
		else
		{
			response.setHeader(key, value.toString());
		}
	}

	@SuppressWarnings("unchecked")
    public void add(String key, Object value)
	{
		cachedHeaders.add(key, value);
		addResponseHeader(key, value);
	}

	protected void addResponseHeader(String key, Object value)
	{
		if (value instanceof Cookie)
		{
			response.addHeader(key, CookieHeaderParser.toString((Cookie) value));
		}
		else if (value instanceof NewCookie)
		{
			response.addHeader(key, NewCookieHeaderParser.toString((NewCookie) value));
		}
		else if (value instanceof CacheControl)
		{
			response.addHeader(key, CacheControlHeaderParser.toString((CacheControl) value));
		}
		else if (value instanceof URI)
		{
			response.addHeader(key, UriHeaderParser.toString((URI) value));
		}
		else if (value instanceof Locale)
		{
			response.addHeader(key, LocaleHeaderParser.toString((Locale) value));
		}
		else if (value instanceof MediaType)
		{
			response.addHeader(key, MediaTypeHeaderParser.toString((MediaType) value));
		}
		else if (value instanceof EntityTag)
		{
			response.addHeader(key, EntityTagHeaderParser.toString((EntityTag) value));
		}
		else
		{
			response.addHeader(key, value.toString());
		}
	}

	public Object getFirst(String key)
	{
		return cachedHeaders.getFirst(key);
	}

	public int size()
	{
		return cachedHeaders.size();
	}

	public boolean isEmpty()
	{
		return cachedHeaders.isEmpty();
	}

	public boolean containsKey(Object o)
	{
		return cachedHeaders.containsKey(o);
	}

	public boolean containsValue(Object o)
	{
		return cachedHeaders.containsValue(o);
	}

	@SuppressWarnings("unchecked")
    public List<Object> get(Object o)
	{
		return cachedHeaders.get(o);
	}

	@SuppressWarnings("unchecked")
    public List<Object> put(String s, List<Object> objs)
	{
		for (Object obj : objs)
		{
			addResponseHeader(s, obj);
		}
		return cachedHeaders.put(s, objs);
	}

	public List<Object> remove(Object o)
	{
		throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
	}

	public void putAll(Map<? extends String, ? extends List<Object>> map)
	{
		for (Map.Entry<? extends String, ? extends List<Object>> entry : map.entrySet())
		{
			List<Object> objs = entry.getValue();
			for (Object obj : objs)
			{
				add(entry.getKey(), obj);
			}
		}
	}

	public void clear()
	{
		throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
	}

	@SuppressWarnings("unchecked")
    public Set<String> keySet()
	{
		return cachedHeaders.keySet();
	}

	@SuppressWarnings("unchecked")
    public Collection<List<Object>> values()
	{
		return cachedHeaders.values();
	}

	@SuppressWarnings("unchecked")
    public Set<Entry<String, List<Object>>> entrySet()
	{
		return cachedHeaders.entrySet();
	}

	public boolean equals(Object o)
	{
		return cachedHeaders.equals(o);
	}

	public int hashCode()
	{
		return cachedHeaders.hashCode();
	}

	@SuppressWarnings("unchecked")
    @Override
	public boolean equalsIgnoreValueOrder(MultivaluedMap<String, Object> otherMap)
	{
		return cachedHeaders.equalsIgnoreValueOrder(otherMap);
	}
	
	public void addDateHeader(String name, long date)
	{
		response.addDateHeader(name, date);
	}
}