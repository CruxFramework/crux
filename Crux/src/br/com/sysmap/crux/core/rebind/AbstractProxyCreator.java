/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import br.com.sysmap.crux.core.i18n.MessagesFactory;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractProxyCreator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	protected final JClassType baseProxyType;
	protected final GeneratorContext context;
	protected final TreeLogger logger;

	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseProxyType)
    {
		this.logger = logger;
		this.context = context;
		this.baseProxyType = baseProxyType;

    }
	
	/**
	 * Creates the cross document proxy.
	 * 
	 * @param logger
	 * @param context
	 * @return a proxy class for cross document invoking.
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
	protected abstract void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException;	

	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	protected abstract void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException;	
	
	/**
	 * @param w
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	protected abstract void generateProxyMethods(SourceWriter w) throws CruxGeneratorException;	
	
	/**
	 * Override this method to generate any nested type required by the proxy
	 * @param srcWriter
	 * @throws CruxGeneratorException 
	 */
	protected abstract void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException;
	
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
	protected abstract String getProxyQualifiedName();
	
	/**
	 * @return the simple name of the proxy object.
	 */
	protected abstract String getProxySimpleName();
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected abstract SourceWriter getSourceWriter();
	
}
