package br.com.sysmap.crux.core.server.scan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.scannotation.AnnotationDB;

public class ScannerDB extends AnnotationDB
{
	private static final long serialVersionUID = -4158104211395455896L;
	
    protected Map<String, Set<String>> interfacesIndex = new HashMap<String, Set<String>>();

    public Map<String, Set<String>> getInterfacesIndex()
	{
		return interfacesIndex;
	}

    @Override
    protected void scanClass(ClassFile cf)
    {
    	super.scanClass(cf);
    	populateInterfaces(cf.getInterfaces(), cf.getName());
    }
    
    protected void populateInterfaces(String[] interfaces, String className)
    {
       if (interfaces == null) return;
       Set<String> classInterfaces = classIndex.get(className);
       for (String str : interfaces)
       {
          Set<String> classes = interfacesIndex.get(str);
          if (classes == null)
          {
             classes = new HashSet<String>();
             interfacesIndex.put(str, classes);
          }
          classes.add(className);
          classInterfaces.add(str);
       }
    }
}