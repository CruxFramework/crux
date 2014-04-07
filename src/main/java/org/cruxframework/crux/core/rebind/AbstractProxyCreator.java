/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.rebind;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.config.ConfigurationFactory;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.CachedGeneratorResult;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JRealClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractProxyCreator
{
	protected GeneratorContext context;
	protected TreeLogger logger;
	protected boolean cacheable;
	protected boolean cacheableVersionFound;

	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractProxyCreator(TreeLogger logger, GeneratorContext context, boolean cacheable)
    {
		this.logger = logger;
		this.context = context;
		this.cacheable = cacheable;
    }
	
	protected boolean isAlreadyGenerated(String className)
	{
		return context.getTypeOracle().findType(className) != null;
	}
	
	/**
	 * Creates the proxy.
	 * 
	 * @return a proxy class name .
	 * @throws CruxGeneratorException 
	 */
	public String create() throws CruxGeneratorException
	{
		String className = getProxyQualifiedName();
	    if (this.cacheable)
	    {
	    	if (findCacheableImplementationAndMarkForReuseIfAvailable())
	    	{
	    		this.cacheableVersionFound = true;
	    		return className;
	    	}
	    }
		if (isAlreadyGenerated(className))
		{
			return className;
		}
		SourcePrinter printer = getSourcePrinter();
		if (printer == null)
		{
			return className;
		}

		generateSubTypes(printer);
		generateProxyContructor(printer);
		generateProxyMethods(printer);
		generateProxyFields(printer);

		printer.commit();
		return className;
	}

	/**
	 * @param srcWriter
	 */
	protected void generateLoggerField(SourcePrinter srcWriter)
    {
	    srcWriter.println("private static Logger _logger_ = Logger.getLogger("+getProxySimpleName()+".class.getName());");
    }	
	
	/**
	 * Generate the proxy constructor and delegate to the superclass constructor
	 * using the default address for the
	 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService}.
	 */
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}

	/**
	 * @param srcWriter
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Override this method to generate any nested type required by the proxy
	 * @param srcWriter
	 * @throws CruxGeneratorException 
	 */
	protected void generateSubTypes(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * @param sourcePrinter
	 * @param message
	 */
	protected void logDebugMessage(SourcePrinter sourcePrinter, String message)
    {
	    sourcePrinter.println("if (LogConfiguration.loggingIsEnabled()){");
		sourcePrinter.println("_logger_.log(Level.FINE, "+message+");");
		sourcePrinter.println("}");
    }
	
	/**
	 * @param method
	 * @return
	 */
	protected String getJsniSimpleSignature(JMethod method)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		sb.append("(");
		for (JParameter param : method.getParameters())
		{
			sb.append(param.getType().getJNISignature());
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	public abstract String getProxyQualifiedName();
	
	
	/**
	 * @return the simple name of the proxy object.
	 */
	public abstract String getProxySimpleName();
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected abstract SourcePrinter getSourcePrinter();
	
	/**
	 * @return
	 */
	protected boolean isCrux2OldInterfacesCompatibilityEnabled()
    {//TODO remover isso
		String value;
		try
        {
	        ConfigurationProperty property = context.getPropertyOracle().getConfigurationProperty("enableCrux2OldInterfacesCompatibility");
	        List<String> values = property.getValues();
	        if (values != null && values.size() > 0)
	        {
	        	value = values.get(0);
	        }
	        else
	        {
	            value = ConfigurationFactory.getConfigurations().enableCrux2OldInterfacesCompatibility();
	        }
        }
        catch (BadPropertyValueException e)
        {
            value = ConfigurationFactory.getConfigurations().enableCrux2OldInterfacesCompatibility();
        }
        return Boolean.parseBoolean(value);
    }
	
	/**
	 * @param w
	 * @param nameFactory
	 * @param method
	 */
	protected void generateMethodParameters(SourcePrinter w, NameFactory nameFactory, JMethod method)
	{
		boolean needsComma = false;
		JParameter[] params = method.getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			JParameter param = params[i];

			if (needsComma)
			{
				w.print(", ");
			}
			else
			{
				needsComma = true;
			}

			JType paramType = param.getType();
			paramType = paramType.getErasedType();

			w.print(paramType.getQualifiedSourceName());
			w.print(" ");

			String paramName = param.getName();
			nameFactory.addName(paramName);
			w.print(paramName);
		}
	}

	/**
	 * @param w
	 * @param methodThrows
	 */
	protected void generateMethodTrhowsClause(SourcePrinter w, JMethod method)
    {
	    boolean needsComma = false;
	    JType[] methodThrows = method.getThrows();
		
		if (methodThrows != null)
		for (JType methodThrow : methodThrows)
        {
			if (needsComma)
			{
				w.print(", ");
			}
			else
			{
				w.print(" throws ");
				needsComma = true;
			}
			JType throwType = methodThrow.getErasedType();
			w.print(throwType.getQualifiedSourceName());
        }
    }
	
	/**
	 * @param srcWriter
	 * @param method
	 * @param returnType
	 * @return
	 */
	protected List<JParameter> generateProxyWrapperMethodDeclaration(SourcePrinter srcWriter, JMethod method)
	{
		srcWriter.println();
		srcWriter.print("public void ");
		srcWriter.print(method.getName() + "(");

		boolean needsComma = false;
		List<JParameter> parameters = new ArrayList<JParameter>();
		JParameter[] params = method.getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			JParameter param = params[i];

			if (needsComma)
			{
				srcWriter.print(", ");
			}
			else
			{
				needsComma = true;
			}

			JType paramType = param.getType();
			if (i == (params.length - 1))
			{
				srcWriter.print("final ");
			}
			srcWriter.print(paramType.getParameterizedQualifiedSourceName());
			srcWriter.print(" ");

			String paramName = param.getName();
			parameters.add(param);
			srcWriter.print(paramName);
		}

		srcWriter.println(") {");
		return parameters;
	}
		
	/**
	 * @return
	 */
	protected boolean findCacheableImplementationAndMarkForReuseIfAvailable()
	{
		return false;
	}
	
	protected boolean findCacheableImplementationAndMarkForReuseIfAvailable(JClassType baseIntf)
	{
		CachedGeneratorResult lastResult = context.getCachedGeneratorResult();
		if (lastResult == null || !context.isGeneratorResultCachingEnabled())
		{
			return false;
		}

		String proxyName = getProxyQualifiedName();

		// check that it is available for reuse
		if (!lastResult.isTypeCached(proxyName))
		{
			return false;
		}

		try
		{
			long lastModified = 0L;
			if (baseIntf instanceof JRealClassType)
			{
				lastModified = ((JRealClassType)baseIntf).getLastModifiedTime();
			}

			if (lastModified != 0L && lastModified < lastResult.getTimeGenerated())
			{
				return context.tryReuseTypeFromCache(proxyName);
			}
		}
		catch (RuntimeException ex)
		{
			// could get an exception checking modified time
			return false;
		}

		return false;
	}
	
	
	protected boolean isCacheable()
	{
		return cacheable;
	}
	
	protected boolean cacheableVersionFound()
	{
		return cacheableVersionFound;
	}
	
    /**
     * Printer for screen creation codes.
     * 
     * @author Thiago da Rosa de Bustamante
     */
    public static class SourcePrinter
    {
    	private final TreeLogger logger;
		private final SourceWriter srcWriter;

    	/**
    	 * Constructor
    	 * @param srcWriter
    	 * @param logger
    	 */
    	public SourcePrinter(SourceWriter srcWriter, TreeLogger logger)
        {
			this.srcWriter = srcWriter;
			this.logger = logger;
        }
    	
    	
    	/**
    	 * Flushes the printed code into a real output (file).
    	 */
    	public void commit()
    	{
    		srcWriter.commit(logger);
    	}
    	
    	/**
    	 * Indents the next line to be printed
    	 */
    	public void indent()
    	{
    		srcWriter.indent();
    	}
    	
    	/**
    	 * Outdents the next line to be printed
    	 */
    	public void outdent()
    	{
    		srcWriter.outdent();
    	}
    	
    	/**
    	 * Prints an in-line code.
    	 * @param s
    	 */
    	public void print(String s)
    	{
    		srcWriter.print(s);
    	}
    	
    	/**
    	 * Prints a line of code into the output. 
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	public void println(String s)
    	{
    		String line = s.trim();
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};") || line.endsWith("}-*/;") || line.startsWith("}"))
    		{
    			outdent();
    		}
			
    		srcWriter.println(s);
    		
    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}


		public void println()
        {
			srcWriter.println();
        }
    }
}
