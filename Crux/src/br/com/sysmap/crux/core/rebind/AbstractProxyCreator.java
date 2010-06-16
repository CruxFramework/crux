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

import br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializableTypeOracleBuilder;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractProxyCreator
{
	protected final GeneratorContext context;
	protected final TreeLogger logger;
	protected final JClassType baseProxyType;

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
	 * Add the implicit root types that are needed to make Cross Document
	 * invoker work.
	 */
	protected void addRequiredRoots(TypeOracle typeOracle, SerializableTypeOracleBuilder stob) throws NotFoundException
	{
		stob.addRootType(logger, typeOracle.getType(String.class.getName()));
	}

	protected void addRoots(TypeOracle typeOracle, SerializableTypeOracleBuilder typesSentFromDocBuilder, 
			SerializableTypeOracleBuilder typesSentToDocBuilder) throws UnableToCompleteException
	{
		try
		{
			addRequiredRoots(typeOracle, typesSentFromDocBuilder);
			addRequiredRoots(typeOracle, typesSentToDocBuilder);

			addBaseTypeRootTypes(typeOracle, typesSentFromDocBuilder, typesSentToDocBuilder);
		}
		catch (NotFoundException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to find type referenced from cross document", e);
			throw new UnableToCompleteException();
		}
	}	
	
	/**
	 * Adds a root type for each type that appears in the CrossDocument
	 * interface methods.
	 */
	protected void addBaseTypeRootTypes(TypeOracle typeOracle, SerializableTypeOracleBuilder typesSentFromDoc, 
			                                      SerializableTypeOracleBuilder typesSentToDoc) throws NotFoundException
	{
		JMethod[] methods = baseProxyType.getOverridableMethods();
		JClassType exceptionClass = typeOracle.getType(Exception.class.getName());

		for (JMethod method : methods)
		{
			JType returnType = method.getReturnType();
			if (returnType != JPrimitiveType.VOID)
			{
				typesSentToDoc.addRootType(logger, returnType);
			}

			JParameter[] params = method.getParameters();
			for (JParameter param : params)
			{
				JType paramType = param.getType();
				typesSentFromDoc.addRootType(logger, paramType);
			}

			JType[] exs = method.getThrows();
			if (exs.length > 0)
			{
				for (JType ex : exs)
				{
					if (!exceptionClass.isAssignableFrom(ex.isClass()))
					{
						logger.log(TreeLogger.WARN, "'" + ex.getQualifiedSourceName() + "' is not a checked exception; only checked exceptions may be used", null);
					}

					typesSentToDoc.addRootType(logger, ex);
				}
			}
		}
	}
	
	/**
	 * Creates the cross document proxy.
	 * 
	 * @param logger
	 * @param context
	 * @return a proxy class for cross document invoking.
	 * @throws UnableToCompleteException 
	 */
	public String create() throws UnableToCompleteException
	{
		TypeOracle typeOracle = context.getTypeOracle();

		SourceWriter srcWriter = getSourceWriter();
		if (srcWriter == null)
		{
			return getProxyQualifiedName();
		}

		final PropertyOracle propertyOracle = context.getPropertyOracle();

		if (this.baseProxyType != null)
		{
			SerializableTypeOracleBuilder typesSentFromDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, typeOracle);
			SerializableTypeOracleBuilder typesSentToDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, typeOracle);

			addRoots(typeOracle, typesSentFromDocBuilder, typesSentToDocBuilder);

			SerializableTypeOracle typesSentFromDoc = typesSentFromDocBuilder.build(logger);
			SerializableTypeOracle typesSentToDoc = typesSentToDocBuilder.build(logger);

			generateTypeHandlers(typesSentFromDoc, typesSentToDoc);
		}
		generateProxyFields(srcWriter);
		generateProxyContructor(srcWriter);
		generateProxyMethods(srcWriter);

		srcWriter.commit(logger);
		return getProxyQualifiedName();
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
	 * @return a sourceWriter for the proxy class
	 */
	protected abstract SourceWriter getSourceWriter();	
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	protected abstract String getProxyQualifiedName();
	
	/**
	 * @param logger
	 * @param context
	 * @param typesSentFromBrowser
	 * @param typesSentToBrowser
	 * @throws UnableToCompleteException
	 */
	protected abstract void generateTypeHandlers(SerializableTypeOracle typesSentFromBrowser,
			                            SerializableTypeOracle typesSentToBrowser) throws UnableToCompleteException;
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws UnableToCompleteException 
	 */
	protected abstract void generateProxyFields(SourceWriter srcWriter) throws UnableToCompleteException;
	
	
	/**
	 * Generate the proxy constructor and delegate to the superclass constructor
	 * using the default address for the
	 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService}.
	 */
	protected abstract void generateProxyContructor(SourceWriter srcWriter);
	
	
	/**
	 * @param w
	 * @param serializableTypeOracle
	 */
	protected abstract void generateProxyMethods(SourceWriter w);
	
}
