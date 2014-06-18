package org.cruxframework.crux.scanner;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.archiveiterator.Filter;

/**
 * A class scanner that builds an index of classes by annotations and
 * implemented interfaces.
 * 
 * Based on Scannotation library from Bill Burke.
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Thiago da Rosa de Bustamante
 */
public class AnnotationDB extends AbstractScanner implements Serializable
{
	private static final long serialVersionUID = 7685125058283200626L;
	protected Map<String, Set<String>> annotationIndex = new HashMap<String, Set<String>>();
	protected Map<String, Set<String>> implementsIndex = new HashMap<String, Set<String>>();
	protected Map<String, Set<String>> classIndex = new HashMap<String, Set<String>>();
	protected Set<URL> scannedURLs = new HashSet<URL>();
	protected Map<String, Set<String>> interfacesIndex = new HashMap<String, Set<String>>();
	protected Map<String, String> superClasses = new HashMap<String, String>();
	protected Map<String, Set<String>> classInterfaces = new HashMap<String, Set<String>>();

	protected transient boolean scanMethodAnnotations = true;
	protected transient boolean scanParameterAnnotations = true;
	protected transient boolean scanFieldAnnotations = true;

	/**
	 * returns a map keyed by the fully qualified string name of a annotation
	 * class. The Set returne is a list of classes that use that annotation
	 * somehow.
	 */
	public Map<String, Set<String>> getAnnotationIndex()
	{
		return annotationIndex;
	}

	/**
	 * returns a map keyed by the list of classes scanned. The value set
	 * returned is a list of annotations used by that class.
	 */
	public Map<String, Set<String>> getClassIndex()
	{
		return classIndex;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getInterfacesIndex()
	{
		return interfacesIndex;
	}

	/**
	 * Wheter or not you want AnnotationDB to scan for method level annotations
	 * 
	 * @param scanMethodAnnotations
	 */
	public void setScanMethodAnnotations(boolean scanMethodAnnotations)
	{
		this.scanMethodAnnotations = scanMethodAnnotations;
	}

	/**
	 * Whether or not you want AnnotationDB to scan for parameter level
	 * annotations
	 * 
	 * @param scanParameterAnnotations
	 */
	public void setScanParameterAnnotations(boolean scanParameterAnnotations)
	{
		this.scanParameterAnnotations = scanParameterAnnotations;
	}

	/**
	 * Whether or not you want AnnotationDB to scan for parameter level
	 * annotations
	 * 
	 * @param scanFieldAnnotations
	 */
	public void setScanFieldAnnotations(boolean scanFieldAnnotations)
	{
		this.scanFieldAnnotations = scanFieldAnnotations;
	}

	@Override
	public Filter getScannerFilter()
	{
		return new Filter()
		{
			public boolean accepts(String fileName)
			{
				if (fileName.endsWith(".class"))
				{
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public ScannerCallback getScannerCallback()
	{
		return new ScannerCallback()
		{
			@Override
			public void onFound(List<ScannerMatch> scanResult)
			{
				for (ScannerMatch match : scanResult)
				{
					URL found = match.getMatch();
					if (!scannedURLs.contains(found))
					{
						scannedURLs.add(found);
						URLStreamManager manager = new URLStreamManager(found);
						try
						{
							scanClass(manager.open());
						}
						catch (IOException e)
						{
							throw new ScannerException("Error creating index of annotations.", e);
						}

						manager.close();
					}
				}
				ClassScanner.setInitialized();
				populateInterfacesFromSuperClass();
			}
		};
	}

	/**
	 * Scan a url that represents an "archive" this is a classpath directory or
	 * jar file
	 * 
	 * @param urls
	 *            variable list of URLs to scan as archives
	 */
	public void scanArchives()
	{
		annotationIndex.clear();
		implementsIndex.clear();
		classIndex.clear();
		scannedURLs.clear();
		interfacesIndex.clear();
		superClasses.clear();
		classInterfaces.clear();
		runScanner();
	}

	@Override
	public void resetScanner()
	{
		annotationIndex.clear();
		implementsIndex.clear();
		classIndex.clear();
		scannedURLs.clear();
		interfacesIndex.clear();
		superClasses.clear();
		classInterfaces.clear();
		ClassScanner.reset();
	}

	/**
	 * Parse a .class file for annotations
	 * 
	 * @param bits
	 *            input stream pointing to .class file bits
	 * @throws IOException
	 */
	public void scanClass(InputStream bits) throws IOException
	{
		DataInputStream dstream = new DataInputStream(new BufferedInputStream(bits));
		ClassFile cf = null;
		try
		{
			cf = new ClassFile(dstream);
			classIndex.put(cf.getName(), new HashSet<String>());
			scanClass(cf);
			if (scanMethodAnnotations || scanParameterAnnotations)
			{
				scanMethods(cf);
			}
			if (scanFieldAnnotations)
			{
				scanFields(cf);
			}

			// create an index of interfaces the class implements
			if (cf.getInterfaces() != null)
			{
				Set<String> intfs = new HashSet<String>();
				for (String intf : cf.getInterfaces())
				{
					intfs.add(intf);
				}
				implementsIndex.put(cf.getName(), intfs);
			}
		}
		finally
		{
			dstream.close();
			bits.close();
		}
	}

	protected void populateInterfaces(ClassFile cf)
	{
		String className = cf.getName();
		String superClassName = cf.getSuperclass();

		populateInterfaces(cf.getInterfaces(), className);

		superClasses.put(className, superClassName);
	}

	protected void populateInterfaces(String[] interfaces, String className)
	{
		if (interfaces == null)
		{
			return;
		}
		Set<String> classesIndex = classIndex.get(className);
		Set<String> classesInterfaces = classInterfaces.get(className);
		if (classesInterfaces == null)
		{
			classesInterfaces = new HashSet<String>();
			classInterfaces.put(className, classesInterfaces);
		}
		for (String str : interfaces)
		{
			Set<String> classes = interfacesIndex.get(str);
			if (classes == null)
			{
				classes = new HashSet<String>();
				interfacesIndex.put(str, classes);
			}
			classes.add(className);
			classesIndex.add(str);
			classesInterfaces.add(str);
		}
	}

	protected void populateInterfacesFromSuperClass()
	{
		Set<String> processedClasses = new HashSet<String>();
		for (String className : superClasses.keySet())
		{
			Set<String> interfacesFromSuperClass = getInterfacesFromSuperClass(className, processedClasses);
			if (interfacesFromSuperClass != null)
			{
				for (String interfaceName : interfacesFromSuperClass)
				{
					Set<String> classes = interfacesIndex.get(interfaceName);
					if (classes == null)
					{
						classes = new HashSet<String>();
						interfacesIndex.put(interfaceName, classes);
					}
					classes.add(className);
				}
			}
		}
	}

	protected Set<String> getInterfacesFromSuperClass(String className, Set<String> processedClasses)
	{
		if (processedClasses.contains(className))
		{
			return classInterfaces.get(className);
		}
		else
		{
			Set<String> result = new HashSet<String>();
			String superClassName = superClasses.get(className);
			if (superClassName != null)
			{
				Set<String> superClassesInterfaces = classInterfaces.get(superClassName);
				if (superClassesInterfaces != null)
				{
					result.addAll(superClassesInterfaces);
				}
				Set<String> interfacesFromSuperClass = getInterfacesFromSuperClass(superClassName, processedClasses);
				if (interfacesFromSuperClass != null && interfacesFromSuperClass.size() > 0)
				{
					result.addAll(interfacesFromSuperClass);
				}
			}
			Set<String> interfaces = classInterfaces.get(className);
			if (result.size() > 0)
			{
				if (interfaces == null)
				{
					interfaces = new HashSet<String>();
					classInterfaces.put(className, interfaces);
				}

				interfaces.addAll(result);
			}

			processedClasses.add(className);
			return result;
		}
	}

	protected void scanClass(ClassFile cf)
	{
		String className = cf.getName();
		AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.invisibleTag);

		if (visible != null)
		{
			populate(visible.getAnnotations(), className);
		}
		if (invisible != null)
		{
			populate(invisible.getAnnotations(), className);
		}
		populateInterfaces(cf);
	}

	/**
	 * Scanns both the method and its parameters for annotations.
	 * 
	 * @param cf
	 */
	protected void scanMethods(ClassFile cf)
	{
		List<?> methods = cf.getMethods();
		if (methods == null)
		{
			return;
		}
		for (Object obj : methods)
		{
			MethodInfo method = (MethodInfo) obj;
			if (scanMethodAnnotations)
			{
				AnnotationsAttribute visible = (AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.visibleTag);
				AnnotationsAttribute invisible = (AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.invisibleTag);

				if (visible != null)
				{
					populate(visible.getAnnotations(), cf.getName());
				}
				if (invisible != null)
				{
					populate(invisible.getAnnotations(), cf.getName());
				}
			}
			if (scanParameterAnnotations)
			{
				ParameterAnnotationsAttribute paramsVisible = (ParameterAnnotationsAttribute) method.getAttribute(ParameterAnnotationsAttribute.visibleTag);
				ParameterAnnotationsAttribute paramsInvisible = (ParameterAnnotationsAttribute) method.getAttribute(ParameterAnnotationsAttribute.invisibleTag);

				if (paramsVisible != null && paramsVisible.getAnnotations() != null)
				{
					for (Annotation[] anns : paramsVisible.getAnnotations())
					{
						populate(anns, cf.getName());
					}
				}
				if (paramsInvisible != null && paramsInvisible.getAnnotations() != null)
				{
					for (Annotation[] anns : paramsInvisible.getAnnotations())
					{
						populate(anns, cf.getName());
					}
				}
			}
		}
	}

	protected void scanFields(ClassFile cf)
	{
		List<?> fields = cf.getFields();
		if (fields == null)
		{
			return;
		}
		for (Object obj : fields)
		{
			FieldInfo field = (FieldInfo) obj;
			AnnotationsAttribute visible = (AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.visibleTag);
			AnnotationsAttribute invisible = (AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.invisibleTag);

			if (visible != null)
			{
				populate(visible.getAnnotations(), cf.getName());
			}
			if (invisible != null)
			{
				populate(invisible.getAnnotations(), cf.getName());
			}
		}
	}

	protected void populate(Annotation[] annotations, String className)
	{
		if (annotations == null)
		{
			return;
		}
		Set<String> classAnnotations = classIndex.get(className);
		for (Annotation ann : annotations)
		{
			Set<String> classes = annotationIndex.get(ann.getTypeName());
			if (classes == null)
			{
				classes = new HashSet<String>();
				annotationIndex.put(ann.getTypeName(), classes);
			}
			classes.add(className);
			classAnnotations.add(ann.getTypeName());
		}
	}

	/**
	 * Prints out annotationIndex
	 * 
	 * @param writer
	 */
	public void outputAnnotationIndex(PrintWriter writer)
	{
		for (String ann : annotationIndex.keySet())
		{
			writer.print(ann);
			writer.print(": ");
			Set<String> classes = annotationIndex.get(ann);
			Iterator<String> it = classes.iterator();
			while (it.hasNext())
			{
				writer.print(it.next());
				if (it.hasNext())
				{
					writer.print(", ");
				}
			}
			writer.println();
		}
	}
}
