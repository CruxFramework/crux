package br.com.sysmap.crux.core.server.scan;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public class ClassScanner
{
	private static final Log logger = LogFactory.getLog(ClassScanner.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();

	private ScannerDB scannerDB;
	private boolean indexBuilt = false;
	private static final ClassScanner instance = new ClassScanner();
	
	private ClassScanner() 
	{
		scannerDB = new ScannerDB();
		scannerDB.setIgnoredPackages(new String[]{"javax", "java", "sun", "com.sun", "org.apache", "com.google", "javassist", "br.com.sysmap.crux.core", "org.json", "com.metaparadigm", "junit"});
		scannerDB.setScanFieldAnnotations(false);
		scannerDB.setScanMethodAnnotations(false);
		scannerDB.setScanParameterAnnotations(false);
		scannerDB.setScanClassAnnotations(true);
	}
	
	
	private void buildIndex(URL[] urls) throws ClassScannerException
	{
		if (indexBuilt)
		{
			return;
		}
		lock.lock();
		try
		{
			if (indexBuilt)
			{
				return;
			}
			if (logger.isInfoEnabled())logger.info(messages.annotationScannerBuildIndex());

			scannerDB.scanArchives(urls);
			indexBuilt = true;
		}
		catch (IOException e)
		{
			throw new ClassScannerException(messages.annotationScannerBuildIndexError(e.getLocalizedMessage()), e);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Search into the internal index for the set of classes that contains the given annotation.
	 * @param annotationClass
	 * @return
	 */
	public Set<String> searchClassesByAnnotation(Class<? extends Annotation> annotationClass)
	{
		if (!indexBuilt)
		{
			throw new ClassScannerException(messages.annotationScannerIndexNotFound());
		}
		
		return scannerDB.getAnnotationIndex().get(annotationClass.getName());
	}
	
	/**
	 * Search into the internal index for the set of classes that implements the given interface.
	 * @param annotationClass
	 * @return
	 */
	public Set<String> searchClassesByInterface(Class<?> interfaceClass)
	{
		if (!indexBuilt)
		{
			throw new ClassScannerException(messages.annotationScannerIndexNotFound());
		}
		
		return scannerDB.getInterfacesIndex().get(interfaceClass.getName());
	}
	
	/**
	 * Get a singleton instance of Annotation scanner.
	 * @return
	 */
	public static ClassScanner getInstance(URL[] urls)
	{
		if (!instance.indexBuilt)
		{
			instance.buildIndex(urls);
		}
		return instance;
	}
}
