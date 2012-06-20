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

import org.cruxframework.crux.core.rebind.crossdocument.gwt.SerializableTypeOracleBuilder;

import com.google.gwt.core.ext.GeneratorContextExt;
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
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractSerializableProxyCreator extends AbstractProxyCreator
{
	protected final JClassType baseProxyType;
	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractSerializableProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseProxyType)
    {
		super(logger, context);
		this.baseProxyType = baseProxyType;
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
	 * @param srcWriter
	 */
	protected void generateLoggerField(SourceWriter srcWriter)
    {
	    srcWriter.println("private static Logger _logger_ = Logger.getLogger("+getProxySimpleName()+".class.getName());");
    }	
	
	/**
	 * Generates the signature for the proxy method
	 * 
	 * @param w
	 * @param nameFactory
	 * @param method
	 */
	protected void generateProxyMethodSignature(SourceWriter w, NameFactory nameFactory, JMethod method)
	{
		// Write the method signature
		JType returnType = method.getReturnType().getErasedType();
		w.print("public ");
		w.print(returnType.getQualifiedSourceName());
		w.print(" ");
		w.print(method.getName() + "(");
		generateMethodParameters(w, nameFactory, method);
		w.print(")");
		generateMethodTrhowsClause(w, method);
		w.println();
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
		if (this.baseProxyType != null)
		{
			TypeOracle typeOracle = context.getTypeOracle();
			PropertyOracle propertyOracle = context.getPropertyOracle();
			try
            {
	            SerializableTypeOracleBuilder typesSentFromDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, context);
	            SerializableTypeOracleBuilder typesSentToDocBuilder = new SerializableTypeOracleBuilder(logger, propertyOracle, context);

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
	
	/**
	 * @param sourceWriter
	 * @param message
	 */
	protected void logDebugMessage(SourceWriter sourceWriter, String message)
    {
	    sourceWriter.println("if (LogConfiguration.loggingIsEnabled()){");
		sourceWriter.indent();
		sourceWriter.println("_logger_.log(Level.FINE, "+message+");");
		sourceWriter.outdent();
		sourceWriter.println("}");
    }
	
	
	/**
	 * @param w
	 * @param nameFactory
	 * @param method
	 */
	protected void generateMethodParameters(SourceWriter w, NameFactory nameFactory, JMethod method)
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
	protected void generateMethodTrhowsClause(SourceWriter w, JMethod method)
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
	
}
