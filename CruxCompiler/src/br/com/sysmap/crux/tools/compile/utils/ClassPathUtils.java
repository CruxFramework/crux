package br.com.sysmap.crux.tools.compile.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.tools.CompilerMessages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassPathUtils 
{
	private static final Class<?>[] parameters = new Class[]{URL.class};
	private static CompilerMessages messages = MessagesFactory.getMessages(CompilerMessages.class);	

	public static void addURL(URL u) throws IOException 
	{

		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try 
		{
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });

			String classpath = System.getProperty("java.class.path");
	    	System.setProperty("java.class.path", classpath + File.pathSeparatorChar + new File(u.toURI()).getCanonicalPath());
		} 
		catch (Throwable t) 
		{
			throw new IOException(messages.errorAddingURLToSystemClassLoader());
		}
	}
}
