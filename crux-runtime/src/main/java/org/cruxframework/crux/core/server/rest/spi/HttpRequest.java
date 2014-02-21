package org.cruxframework.crux.core.server.rest.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.cruxframework.crux.core.server.rest.core.HttpHeaders;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMap;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMapImpl;
import org.cruxframework.crux.core.server.rest.util.Encode;

/**
 * Abstraction for an inbound http request on the server, or a response from a
 * server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client
 * framework and serverside framework
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpRequest
{
	protected final HttpHeaders httpHeaders;
	protected final HttpServletRequest request;
	protected final UriInfo uri;
	protected final String httpMethod;
	protected MultivaluedMap<String, String> formParameters;
	protected MultivaluedMap<String, String> decodedFormParameters;

	public HttpRequest(HttpServletRequest request, HttpHeaders httpHeaders, UriInfo uri, String httpMethod)
	{
		this.request = request;
		this.httpHeaders = httpHeaders;
		this.httpMethod = httpMethod;
		this.uri = uri;
	}
	
	public MultivaluedMap<String, String> getPutFormParameters()
	{
		if (formParameters != null)
			return formParameters;
		if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(getHttpHeaders().getMediaType()))
		{
			try
			{
				formParameters = parseForm(getInputStream());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Request media type is not application/x-www-form-urlencoded");
		}
		return formParameters;
	}

	public MultivaluedMap<String, String> getPutDecodedFormParameters()
	{
		if (decodedFormParameters != null)
			return decodedFormParameters;
		decodedFormParameters = Encode.decode(getFormParameters());
		return decodedFormParameters;
	}

	public Object getAttribute(String attribute)
	{
		return request.getAttribute(attribute);
	}

	public void setAttribute(String name, Object value)
	{
		request.setAttribute(name, value);
	}

	public void removeAttribute(String name)
	{
		request.removeAttribute(name);
	}

	public Principal getUserPrincipal()
	{
		return request.getUserPrincipal();
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames()
	{
		return request.getAttributeNames();
	}

	public MultivaluedMap<String, String> getFormParameters()
	{
		if (formParameters != null)
			return formParameters;
		// Tomcat does not set getParameters() if it is a PUT request
		// so pull it out manually
		if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
		{
			return getPutFormParameters();
		}
		formParameters = Encode.encode(getDecodedFormParameters());
		return formParameters;
	}

	@SuppressWarnings("unchecked")
    public MultivaluedMap<String, String> getDecodedFormParameters()
	{
		if (decodedFormParameters != null)
			return decodedFormParameters;
		// Tomcat does not set getParameters() if it is a PUT request
		// so pull it out manually
		if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
		{
			return getPutDecodedFormParameters();
		}
		decodedFormParameters = new MultivaluedMapImpl<String, String>();
		Map<String, String[]> params = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : params.entrySet())
		{
			String name = entry.getKey();
			String[] values = entry.getValue();
			MultivaluedMap<String, String> queryParams = uri.getQueryParameters();
			List<String> queryValues = queryParams.get(name);
			if (queryValues == null)
			{
				for (String val : values)
					decodedFormParameters.add(name, val);
			}
			else
			{
				for (String val : values)
				{
					if (!queryValues.contains(val))
					{
						decodedFormParameters.add(name, val);
					}
				}
			}
		}
		return decodedFormParameters;

	}

	public HttpHeaders getHttpHeaders()
	{
		return httpHeaders;
	}

	public InputStream getInputStream()
	{
		try
		{
			return request.getInputStream();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public UriInfo getUri()
	{
		return uri;
	}

	public String getHttpMethod()
	{
		return httpMethod;
	}
	
	public HttpSession getSession()
	{
		return request.getSession();
	}

	public HttpSession getSession(boolean create)
	{
		return request.getSession(create);
	}

	protected MultivaluedMap<String, String> parseForm(InputStream entityStream) throws IOException
	{
		char[] buffer = new char[100];
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));

		int wasRead = 0;
		do
		{
			wasRead = reader.read(buffer, 0, 100);
			if (wasRead > 0)
				buf.append(buffer, 0, wasRead);
		}
		while (wasRead > -1);

		String form = buf.toString();

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl<String, String>();
		String[] params = form.split("&");

		for (String param : params)
		{
			if (param.indexOf('=') >= 0)
			{
				String[] nv = param.split("=");
				String val = nv.length > 1 ? nv[1] : "";
				formData.add(nv[0], val);
			}
			else
			{
				formData.add(param, "");
			}
		}
		return formData;
	}

	/**
	 * @see request.getLocale();
	 * @return the current locale.
	 */
	public Locale getLocale() 
	{
		return request.getLocale();
	}
	
	/**
	 * @see request.getRemoteHost();
	 * @return a <code>String</code> container the remote host.
	 */
	public String getRemoteHost() 
	{
		return request.getRemoteHost();
	}

	/**
	 * @see request.getRemoteAddr();
	 * @return a <code>String</code> containing the fully 
     *			qualified name of the client
	 */
	public String getRemoteAddr() 
	{
		return request.getRemoteAddr();
	}
	
	/**
	 * @see request.getRemotePort();
	 * @return a <code>int</code> container the remote port.
	 */
	public int getRemotePort() 
	{
		return request.getRemotePort();
	}
	
	/**
	 * @see request.getRemoteUser();
	 * @return a <code>String</code> container the remote user.
	 */
	public String getRemoteUser() 
	{
		return request.getRemoteUser();
	}
}