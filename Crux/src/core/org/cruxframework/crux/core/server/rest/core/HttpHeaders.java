package org.cruxframework.crux.core.server.rest.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.cruxframework.crux.core.server.rest.util.DateUtil;
import org.cruxframework.crux.core.server.rest.util.LocaleHelper;
import org.cruxframework.crux.core.server.rest.util.MediaTypeHelper;
import org.cruxframework.crux.core.server.rest.util.WeightedLanguage;

public class HttpHeaders
{
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_CHARSET = "Accept-Charset";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ALLOW = "Allow";
	public static final String AUTHORIZATION = "Authorization";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String CONTENT_LANGUAGE = "Content-Language";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_LOCATION = "Content-Location";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String DATE = "Date";
	public static final String ETAG = "ETag";
	public static final String EXPIRES = "Expires";
	public static final String HOST = "Host";
	public static final String IF_MATCH = "If-Match";
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String IF_NONE_MATCH = "If-None-Match";
	public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	public static final String LAST_MODIFIED = "Last-Modified";
	public static final String LOCATION = "Location";
	public static final String LINK = "Link";
	public static final String RETRY_AFTER = "Retry-After";
	public static final String USER_AGENT = "User-Agent";
	public static final String VARY = "Vary";
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	public static final String COOKIE = "Cookie";
	public static final String SET_COOKIE = "Set-Cookie";

	private MultivaluedMap<String, String> requestHeaders;
	private List<MediaType> acceptableMediaTypes;
	private MediaType mediaType;
	private Locale language;
	private Map<String, Cookie> cookies;
	private List<Locale> acceptableLanguages;

	public MultivaluedMap<String, String> getRequestHeaders()
	{
		return requestHeaders;
	}

	public void setRequestHeaders(MultivaluedMap<String, String> requestHeaders)
	{
		this.requestHeaders = requestHeaders;
	}

	public List<MediaType> getAcceptableMediaTypes()
	{
		return acceptableMediaTypes;
	}

	public void setAcceptableMediaTypes(List<MediaType> acceptableMediaTypes)
	{
		this.acceptableMediaTypes = acceptableMediaTypes;
		if (acceptableMediaTypes != null)
			MediaTypeHelper.sortByWeight(acceptableMediaTypes);
	}

	public MediaType getMediaType()
	{
		return mediaType;
	}

	public void setMediaType(MediaType mediaType)
	{
		this.mediaType = mediaType;
	}

	public Locale getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		if (language == null)
			return;
		this.language = LocaleHelper.extractLocale(language);
	}

	public void setAcceptableLanguages(List<String> acceptableLanguages)
	{
		if (acceptableLanguages == null)
		{
			this.acceptableLanguages = null;
		}
		else
		{
			this.acceptableLanguages = new ArrayList<Locale>(acceptableLanguages.size());
			List<WeightedLanguage> languages = new ArrayList<WeightedLanguage>(acceptableLanguages.size());
			for (String lang : acceptableLanguages)
			{
				languages.add(WeightedLanguage.parse(lang));
			}
			Collections.sort(languages);

			for (WeightedLanguage lang : languages)
				this.acceptableLanguages.add(lang.getLocale());
		}
	}

	public Map<String, Cookie> getCookies()
	{
		return cookies;
	}

	public void setCookies(Map<String, Cookie> cookies)
	{
		this.cookies = cookies;
	}

	public List<String> getRequestHeader(String name)
	{
		return requestHeaders.get(name);
	}

	public List<Locale> getAcceptableLanguages()
	{
		if (acceptableLanguages == null)
			acceptableLanguages = new ArrayList<Locale>();
		return acceptableLanguages;
	}

	public String getHeaderString(String name)
	{
		return requestHeaders.getFirst(name);
	}

	public Date getDate()
	{
		String date = requestHeaders.getFirst(DATE);
		if (date == null)
			return null;
		return DateUtil.parseDate(date);
	}

	public int getLength()
	{
		String cl = requestHeaders.getFirst(CONTENT_LENGTH);
		if (cl == null)
			return -1;
		return Integer.parseInt(cl);
	}
}