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
public abstract class AbstractSerializableProxyCreator extends AbstractProxyCreator
{
	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractSerializableProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseProxyType)
    {
		super(logger, context, baseProxyType);
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
	 * Add the implicit root types that are needed to make Cross Document
	 * invoker work.
	 */
	protected void addRequiredRoots(TypeOracle typeOracle, SerializableTypeOracleBuilder stob) throws NotFoundException
	{
		stob.addRootType(logger, typeOracle.getType(String.class.getName()));
	}	
	
	protected void addRoots(TypeOracle typeOracle, SerializableTypeOracleBuilder typesSentFromDocBuilder, 
			SerializableTypeOracleBuilder typesSentToDocBuilder) throws CruxGeneratorException
	{
		try
		{
			addRequiredRoots(typeOracle, typesSentFromDocBuilder);
			addRequiredRoots(typeOracle, typesSentToDocBuilder);

			addBaseTypeRootTypes(typeOracle, typesSentFromDocBuilder, typesSentToDocBuilder);
		}
		catch (NotFoundException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to find type referenced from base interface", e);
			throw new CruxGeneratorException();
		}
	}
	
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
		if (this.baseProxyType != null)
		{
			TypeOracle typeOracle = context.getTypeOracle();
			PropertyOracle propertyOracle = context.getPropertyOracle();
			try
            {
	            SerializableTypeOracleBuilder typesSentFromDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, typeOracle);
	            SerializableTypeOracleBuilder typesSentToDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, typeOracle);

	            addRoots(typeOracle, typesSentFromDocBuilder, typesSentToDocBuilder);

	            SerializableTypeOracle typesSentFromDoc = typesSentFromDocBuilder.build(logger);
	            SerializableTypeOracle typesSentToDoc = typesSentToDocBuilder.build(logger);

	            generateTypeSerializers(typesSentFromDoc, typesSentToDoc);
            }
            catch (UnableToCompleteException e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }
		}
    }

	/**
	 * Override this method to generate any nested serializable type required by the proxy
	 * @param logger
	 * @param context
	 * @param typesSentFromBrowser
	 * @param typesSentToBrowser
	 * @throws CruxGeneratorException
	 */
	protected abstract void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser,
			                            SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException;
	
}
