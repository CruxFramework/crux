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
package br.com.sysmap.crux.core.rebind.context;

import br.com.sysmap.crux.core.client.context.ContextManager;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.crossdocument.CrossDocumentProxyCreator;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializationUtils;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.Shared;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.TypeSerializerCreator;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * This class generates a proxy for context access.
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class ContextProxyCreator extends CrossDocumentProxyCreator
{
	private static final String CONTEXT_SUFFIX = "_ContextProxy";
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param baseProxyType
	 */
	public ContextProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseProxyType)
	{
		super(logger, context, baseProxyType);
	}
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
		String typeSerializerName = SerializationUtils.getTypeSerializerQualifiedName(baseProxyType);
		srcWriter.println("private static final " + typeSerializerName + " SERIALIZER = new " + typeSerializerName + "();");
		srcWriter.println();
	}

	/**
	 * Generates the method for context reading.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyGetMethod(SourceWriter w, JMethod method, int propPrefixLength) throws CruxGeneratorException
	{
		w.println();

		String propertyName = method.getName().substring(propPrefixLength);
		propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);

		JType returnType = method.getReturnType().getErasedType();
		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.println("try {");
		w.indent();

		String retName = nameFactory.createName("ret");
		w.println("String "+retName + "="+ContextManager.class.getName()+".getContextHandler().read(\""+propertyName+"\");");
		w.println("if (!"+StringUtils.class.getCanonicalName()+".isEmpty("+retName+")){");
		w.indent();
		w.print("return ("+returnType.getQualifiedSourceName()+") ");
		w.println("CrossDocumentReader." + getReaderFor(returnType).name()+".read(createStreamReader("+retName+"));");;
		w.outdent();
		w.println("}");
		w.println("else{");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		
		w.outdent();
		generateSerializationCatchBlock(w, nameFactory);

		w.outdent();
		w.println("}");
	}

	/**
	 * @param w
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	@Override
	protected void generateProxyMethods(SourceWriter w) throws CruxGeneratorException
	{
		JMethod[] syncMethods = baseProxyType.getOverridableMethods();
		for (JMethod method : syncMethods)
		{
			JClassType enclosingType = method.getEnclosingType();
			JParameterizedType isParameterizedType = enclosingType.isParameterized();
			if (isParameterizedType != null)
			{
				JMethod[] methods = isParameterizedType.getMethods();
				for (int i = 0; i < methods.length; ++i)
				{
					if (methods[i] == method)
					{
						method = isParameterizedType.getBaseType().getMethods()[i];
					}
				}
			}

			JType returnType = method.getReturnType().getErasedType();
			String name = method.getName();
			if (name.startsWith("get") && method.getParameters().length == 0 && 
			   (returnType != JPrimitiveType.VOID))
			{
				generateProxyGetMethod(w, method, 3);
			}
			else if (name.startsWith("is") && method.getParameters().length == 0 && 
					 (returnType != JPrimitiveType.VOID))
			{
				generateProxyGetMethod(w, method, 2);
			}
			else if (name.startsWith("set") && method.getParameters().length == 1 && 
					(returnType == JPrimitiveType.VOID))
			{
				generateProxySetMethod(w, method);
			}
			else
			{
				logger.log(TreeLogger.ERROR, messages.errorContextWrapperInvalidSignature(method.getJsniSignature()));
				throw new CruxGeneratorException();
			}
		}
	}

	/**
	 * Generates method for context writing.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxySetMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		w.println();

		String propertyName = method.getName().substring(3);
		propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
		
		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.print(SerializationStreamWriter.class.getSimpleName());
		w.print(" ");
		String streamWriterName = nameFactory.createName("streamWriter");
		w.println(streamWriterName + " = createStreamWriter();");

		w.println("try {");
		w.indent();

		JParameter[] params = method.getParameters();
		JParameter param = params[0];

		w.println("if ("+param.getName()+" == null){");
		w.indent();
		w.println(ContextManager.class.getName()+".getContextHandler().erase(\""+propertyName+"\");");
		w.outdent();
		w.println("} else {");
		w.indent();
		w.print(streamWriterName + ".");
		w.print(Shared.getStreamWriteMethodNameFor(param.getType()));
		w.println("(" + param.getName() + ");");
		String payloadName = nameFactory.createName("payload");
		w.println("String " + payloadName + " = "+ streamWriterName + ".toString();");
		w.println(ContextManager.class.getName()+".getContextHandler().write(\""+propertyName+"\", "+payloadName+");");
		w.outdent();
		w.println("}");
		
		w.outdent();
		generateSerializationCatchBlock(w, nameFactory);

		w.outdent();
		w.println("}");
	}

	/**
	 * @param logger
	 * @param context
	 * @param typesSentFromBrowser
	 * @param typesSentToBrowser
	 * @throws CruxGeneratorException
	 */
	@Override
	protected void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser,
			                            SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException
	{
		try
        {
	        TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentFromBrowser, typesSentToBrowser, context, 
	        										SerializationUtils.getTypeSerializerQualifiedName(baseProxyType));
	        tsc.realize(logger);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @return the full qualified name of the proxy object.
	 */
	protected String getProxyQualifiedName()
	{
		return baseProxyType.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	protected String getProxySimpleName()
	{
		return ClassUtils.getSourceName(baseProxyType) + CONTEXT_SUFFIX;
	}
	
	/**
	 * @param w
	 * @param nameFactory
	 */
	private void generateSerializationCatchBlock(SourceWriter w, NameFactory nameFactory)
    {
	    w.print("} catch (SerializationException ");
	    String exceptionName = nameFactory.createName("ex");
	    w.println(exceptionName + ") {");
		w.indent();
		w.println("throw new CrossDocumentException(ex.getMessage(), ex);");
		w.outdent();
	    w.println("}");
    }
}
