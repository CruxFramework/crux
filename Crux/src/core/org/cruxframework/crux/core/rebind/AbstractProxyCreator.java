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

import java.util.List;

import org.cruxframework.crux.core.config.ConfigurationFactory;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractProxyCreator
{
	protected final GeneratorContextExt context;
	protected final TreeLogger logger;

	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
		this.logger = logger;
		this.context = context;
    }
	
	/**
	 * Creates the proxy.
	 * 
	 * @return a proxy class name .
	 * @throws CruxGeneratorException 
	 */
	public String create() throws CruxGeneratorException
	{
		SourceWriter srcWriter = getSourceWriter();
		if (srcWriter == null)
		{
			return getProxyQualifiedName();
		}

		generateSubTypes(srcWriter);
		generateProxyFields(srcWriter);
		generateProxyContructor(srcWriter);
		generateProxyMethods(srcWriter);

		srcWriter.commit(logger);
		return getProxyQualifiedName();
	}

	/**
	 * Generate the proxy constructor and delegate to the superclass constructor
	 * using the default address for the
	 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService}.
	 */
	protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
		
	}

	/**
	 * @param srcWriter
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Override this method to generate any nested type required by the proxy
	 * @param srcWriter
	 * @throws CruxGeneratorException 
	 */
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
	{
		
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
	protected abstract SourceWriter getSourceWriter();
	
	/**
	 * @return
	 */
	protected boolean isCrux2OldInterfacesCompatibilityEnabled()
    {
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
	 * @return
	 */
	protected boolean isCacheable()
	{
		return false;
	}
}
