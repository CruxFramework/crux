package br.com.sysmap.crux.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import br.com.sysmap.crux.core.i18n.DefaultMessage;
import br.com.sysmap.crux.core.i18n.MessageException;

/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gessé S. F. Dafé <code>gessedafe@gmail.com</code>
 */
public abstract class ConstantsInvocationHandler implements InvocationHandler
{
	private Class<?> targetInterface;
	
	public ConstantsInvocationHandler(Class<?> targetInterface) 
	{
		this.targetInterface = targetInterface;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		try
		{
			PropertyResourceBundle properties = getPropertiesForLocale(targetInterface);
			if (properties != null && properties.getString(method.getName()) != null)
			{
				return MessageFormat.format(properties.getString(method.getName()),args);
			}
			else
			{
				DefaultMessage annot = method.getAnnotation(DefaultMessage.class);
				if (annot != null)
				{
					return MessageFormat.format(annot.value(),args);
				}
				return null;
			}
		}
		catch (Throwable e)
		{
			return null;
		}
	}
	
	protected abstract <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface); 
	
	protected static PropertyResourceBundle loadProperties (Class<?> targetInterface, final Locale locale)
	{
		PropertyResourceBundle properties = null;
		try
		{
			properties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(targetInterface.getSimpleName(), locale);
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
}