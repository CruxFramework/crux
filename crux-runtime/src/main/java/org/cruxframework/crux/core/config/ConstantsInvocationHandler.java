package org.cruxframework.crux.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;
import org.cruxframework.crux.core.i18n.MessageException;

import com.google.gwt.i18n.client.Messages.DefaultMessage;


/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
public abstract class ConstantsInvocationHandler implements InvocationHandler
{
	private Class<?> targetInterface;
	private Map<String, String> resolvedConstants = new ConcurrentHashMap<String, String>();
	private boolean isCacheable = true;
	
	/**
	 * 
	 * @param targetInterface
	 */
	public ConstantsInvocationHandler(Class<?> targetInterface, boolean isCacheable) 
	{
		this.targetInterface = targetInterface;
		this.isCacheable = isCacheable;
	}
	
	/**
	 * 
	 * @param targetInterface
	 */
	public ConstantsInvocationHandler(Class<?> targetInterface) 
	{
		this(targetInterface, true);
	}

	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		String name = method.getName();
		if (this.isCacheable && resolvedConstants.containsKey(name))
		{
			return resolvedConstants.get(name);
		}
		String message = null;
		try
		{
			if (isValidPropertySetter(method))
			{
				invokeSetter(method, args);
			}
			else
			{
				message = getMessageFromProperties(args, name);
				if (message == null)
				{
					message = getMessageFromAnnotation(method, args, name);
				}
			}
		}
		catch (Throwable e)
		{
			message = getMessageFromAnnotation(method, args, name);
		}
		
		return message;
	}

	/**
	 * @param args
	 * @param name
	 * @return
	 */
	protected String getMessageFromProperties(Object[] args, String name)
	{
		PropertyResourceBundle properties = getPropertiesForLocale(targetInterface);
		String message = null;
		if (properties != null)
		{
			message = MessageFormat.format(properties.getString(name),args);
			if (this.isCacheable)
			{
				resolvedConstants.put(name, message);
			}
		}
		return message;
	}

	/**
	 * @param method
	 * @param args
	 * @param name
	 * @return
	 */
	protected String getMessageFromAnnotation(Method method, Object[] args, String name)
	{
		DefaultServerMessage serverAnnot = method.getAnnotation(DefaultServerMessage.class);
		DefaultMessage clientAnnot = method.getAnnotation(DefaultMessage.class);
		
		String value = null;
		if (serverAnnot != null)
		{
			value = MessageFormat.format(serverAnnot.value(),args);
		} else if(clientAnnot != null) 
		{
			value = MessageFormat.format(clientAnnot.value(),args);
		}
		
		if (value != null)
		{
			if (this.isCacheable)
			{
				resolvedConstants.put(name, value);
			}
			return value;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	protected boolean isValidPropertySetter(Method method)
	{
		String methodName = method.getName();
		if (methodName.startsWith("set") && methodName.length() > 3)
		{
			String property = getPropertyFromSetterMethodName(methodName);
			if (this.isCacheable && resolvedConstants.containsKey(property))
			{
				return method.getParameterTypes().length == 1;
			}
			try
			{
				targetInterface.getMethod(property, new Class[]{});
				return true;
			}
			catch (Throwable e)
			{
				return false;
			}
		}
		
		return false;
	}

	/**
	 * 
	 * @param properties
	 * @param method
	 * @param args
	 */
	protected void invokeSetter(Method method, Object[] args)
	{
		if (this.isCacheable)
		{
			String property = getPropertyFromSetterMethodName(method.getName());
			Object value = args[0];
			if (value == null)
			{
				resolvedConstants.remove(property);
			}
			else
			{
				resolvedConstants.put(property, value.toString());
			}
		}
	}
	
	/**
	 * 
	 * @param targetInterface
	 * @param locale
	 * @return
	 */
	protected static PropertyResourceBundle loadProperties (Class<?> targetInterface, final Locale locale)
	{
		PropertyResourceBundle properties = null;
		try
		{
			properties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(targetInterface.getCanonicalName(), locale);
		}
		catch (Throwable e) 
		{
			try 
			{
				String resourceName = "/"+targetInterface.getName().replaceAll("\\.", "/") + ".properties";
				InputStream input = targetInterface.getClassLoader().getResourceAsStream(resourceName);
				if (input != null)
				{
					properties = new PropertyResourceBundle(input);
				}
			} 
			catch (IOException e1) 
			{
				throw new MessageException(e.getMessage(), e);
			}
		}
		return properties;
	}

	/**
	 * 
	 * @param methodName
	 * @return
	 */
	private String getPropertyFromSetterMethodName(String methodName)
	{
		String property = methodName.substring(3);
		if (property.length() == 1)
		{
			property = Character.toLowerCase(property.charAt(0))+"";
		}
		else
		{
			property = Character.toLowerCase(property.charAt(0)) + property.substring(1);
		}
		return property;
	}

	/**
	 * 
	 * @param <T>
	 * @param targetInterface
	 * @return
	 */
	protected abstract <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface); 
}