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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.i18n.DefaultServerMessage;
import org.cruxframework.crux.core.i18n.MessageException;


/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
@Legacy(value=ConstantsInvocationHandler.class)
public abstract class ConstantsInvocationHandlerLegacy implements InvocationHandler
{
	/**
	 * @param method
	 * @param args
	 * @param name
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String getMessageFromAnnotation(Method method, Object[] args, String name)
	{
		DefaultServerMessage serverAnnot = method.getAnnotation(DefaultServerMessage.class);
		if (serverAnnot != null)
		{
			String value = MessageFormat.format(serverAnnot.value(),args);
			if (this.isCacheable)
			{
				resolvedConstants.put(name, value);
			}
			return value;
		}
		else
		{
			org.cruxframework.crux.core.i18n.DefaultMessage annot = method.getAnnotation(org.cruxframework.crux.core.i18n.DefaultMessage.class);
			if (annot != null)
			{
				String value = MessageFormat.format(annot.value(),args);
				if (this.isCacheable)
				{
					resolvedConstants.put(name, value);
				}
				return value;
			}
		}
		return null;
	}
}