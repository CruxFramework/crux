package org.cruxframework.crux.core.server.rest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.cruxframework.crux.core.server.rest.core.dispatch.CacheInfo;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.HttpMethod;
import org.cruxframework.crux.core.shared.rest.annotation.POST;
import org.cruxframework.crux.core.shared.rest.annotation.PUT;
import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpMethodHelper
{
	/**
	 * 
	 * @param method
	 * @return
	 * @throws InvalidRestMethod 
	 */
	public static String getHttpMethod(Annotation[] annotations) throws InvalidRestMethod
	{
		return getHttpMethod(annotations, true);
	}
	
	public static String getHttpMethod(Annotation[] annotations, boolean allowNull) throws InvalidRestMethod
	{
		String httpMethod = null;
		for (Annotation annotation : annotations)
		{
			HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
			if (http != null)
			{
				if (httpMethod != null)
				{
					throw new InvalidRestMethod("Crux REST methods can not be bound to more than one HTTP Method");
				}
				httpMethod = http.value();
			}
		}
		if (!allowNull && httpMethod == null)
		{
			throw new InvalidRestMethod("Crux REST methods must be bound to one HTTP Method");
		}
		return httpMethod;
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	public static CacheInfo getCacheInfoForGET(Method method)
	{
		GET get = method.getAnnotation(GET.class);
		if (get != null)
		{
			return CacheInfo.parseCacheInfo(get);
		}
		
		return null;
	}
	
	public static StateValidationModel getStateValidationModel(Method method)
    {
		PUT put = method.getAnnotation(PUT.class);
		if (put != null)
		{
			return put.validatePreviousState();
		}
		POST post = method.getAnnotation(POST.class);
		if (post != null)
		{
			return post.validatePreviousState();
		}

	    return null;
    }
}