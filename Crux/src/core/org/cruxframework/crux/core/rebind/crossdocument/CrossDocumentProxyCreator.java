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
package org.cruxframework.crux.core.rebind.crossdocument;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamWriter;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocumentException;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocumentProxy;
import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocumentProxy.CrossDocumentReader;
import org.cruxframework.crux.core.client.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.AbstractSerializableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.SerializationUtils;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.Shared;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.TypeSerializerCreator;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * This class generates a proxy for cross document invocations.
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CrossDocumentProxyCreator extends AbstractSerializableProxyCreator
{
	private static final String CROSS_DOC_SUFFIX = "_CrossDocProxy";
	private static final Map<String, CrossDocumentReader> JPRIMITIVETYPE_TO_READER = new HashMap<String, CrossDocumentReader>();
	static 
	{
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.BOOLEAN.getSimpleSourceName(), CrossDocumentReader.BOOLEAN);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.BYTE.getSimpleSourceName(), CrossDocumentReader.BYTE);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.CHAR.getSimpleSourceName(), CrossDocumentReader.CHAR);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.DOUBLE.getSimpleSourceName(), CrossDocumentReader.DOUBLE);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.FLOAT.getSimpleSourceName(), CrossDocumentReader.FLOAT);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.INT.getSimpleSourceName(), CrossDocumentReader.INT);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.LONG.getSimpleSourceName(), CrossDocumentReader.LONG);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.SHORT.getSimpleSourceName(), CrossDocumentReader.SHORT);
		JPRIMITIVETYPE_TO_READER.put(JPrimitiveType.VOID.getSimpleSourceName(), CrossDocumentReader.VOID);
	}
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param baseProxyType
	 */
	public CrossDocumentProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseProxyType)
	{
		super(logger, context, baseProxyType);
	}

	/**
	 * Override this method to add additional interfaces to implements list of
	 * the proxy.
	 * 
	 * @param composerFactory
	 */
	protected void addAdditionalInterfaces(ClassSourceFileComposerFactory composerFactory)
	{
	}

	/**
	 * Generate the proxy constructor and delegate to the superclass constructor
	 * using the default address for the
	 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService}.
	 */
	@Override
	protected void generateProxyContructor(SourceWriter srcWriter)
	{
		srcWriter.println("public " + getProxySimpleName() + "() {");
		srcWriter.indent();
		srcWriter.println("super(SERIALIZER);");
		srcWriter.outdent();
		srcWriter.println("}");
	}
	
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
		String controllerName = getControllerName(context.getTypeOracle());
		// Initialize a field with binary name of the remote service interface
		srcWriter.println("private static final String CONTROLLER_NAME = " + "\"" + controllerName + "\";");
		String typeSerializerName = SerializationUtils.getTypeSerializerQualifiedName(baseProxyType);
		srcWriter.println("private static final " + typeSerializerName + " SERIALIZER = new " + typeSerializerName + "();");
		srcWriter.println();
	}

	/**
	 * Generates the client's asynchronous proxy method.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		w.println();

		JType returnType = method.getReturnType().getErasedType();
		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		generateCallToSelfBlock(w, method);

		w.print(SerializationStreamWriter.class.getSimpleName());
		w.print(" ");
		String streamWriterName = nameFactory.createName("streamWriter");
		w.println(streamWriterName + " = createStreamWriter();");

		w.println("try {");
		w.indent();

		JParameter[] params = method.getParameters();
		for (int i = 0; i < params.length ; ++i)
		{
			JParameter param = params[i];
			w.print(streamWriterName + ".");
			w.print(Shared.getStreamWriteMethodNameFor(param.getType()));
			w.println("(" + param.getName() + ");");
		}

		String payloadName = nameFactory.createName("payload");
		w.println("String " + payloadName + " = CONTROLLER_NAME+\"|" + getJsniSimpleSignature(method) + "|\"+"+ streamWriterName + ".toString();");

		if (returnType != JPrimitiveType.VOID)
		{
			if (returnType.isPrimitive() != null)
			{
				w.print("return ("+returnType.isPrimitive().getQualifiedBoxedSourceName()+") ");
			}
			else
			{
				w.print("return ("+returnType.getQualifiedSourceName()+") ");
			}
		}
		w.println("doInvoke("+payloadName+", "+"CrossDocumentReader." + getReaderFor(returnType).name()+");");
		
		w.outdent();
	    generateDoInvokeCatchBlock(w, method, nameFactory);

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

			generateProxyMethod(w, method);
		}
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
	        										SerializationUtils.getTypeSerializerQualifiedName(baseProxyType),
	        						                SerializationUtils.getTypeSerializerSimpleName(baseProxyType));
	        
	        tsc.realize(logger);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	
	/**
	 * @param typeOracle
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected JClassType getControllerClass(TypeOracle typeOracle) throws CruxGeneratorException
	{
		String crossDocInterfaceName = baseProxyType.getQualifiedSourceName();
		if (!crossDocInterfaceName.endsWith("CrossDoc"))
		{
			throw new CruxGeneratorException("Cross document interface ["+crossDocInterfaceName+"] does not follow the name pattern for cross document objects.");
			
		}
		
		JClassType controllerClass = typeOracle.findType(crossDocInterfaceName.substring(0, crossDocInterfaceName.length()-8));
		if (controllerClass == null)
		{
			throw new CruxGeneratorException("Could not find the cross document controller for the interface ["+crossDocInterfaceName+"].");
		}
		return controllerClass;
	}
	
	/**
	 * @param typeOracle
	 * @return
	 * @throws CruxGeneratorException 
	 */
	protected String getControllerName(TypeOracle typeOracle) throws CruxGeneratorException
	{
		String crossDocInterfaceName = baseProxyType.getQualifiedSourceName();
		JClassType controllerClass = getControllerClass(typeOracle);
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		if (controllerAnnot == null)
		{
			throw new CruxGeneratorException("The controller found for the interface ["+crossDocInterfaceName+"] does not have the annotation @Controller.");
		}
		
		return controllerAnnot.value();
	}
	
	/**
	 * @return
	 */
	protected String[] getImports()
    {
	    String[] imports = new String[] { getProxySupertype().getCanonicalName(), getStreamWriterClass().getCanonicalName(), SerializationStreamWriter.class.getCanonicalName(), GWT.class.getCanonicalName(),
		        SerializationException.class.getCanonicalName(), Impl.class.getCanonicalName(), CrossDocumentException.class.getCanonicalName(), 
		        Crux.class.getCanonicalName()};
	    return imports;
    }

	/**
	 * @return the full qualified name of the proxy object.
	 */
	public String getProxyQualifiedName()
	{
		return baseProxyType.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	public String getProxySimpleName()
	{
		return baseProxyType.getSimpleSourceName() + CROSS_DOC_SUFFIX;
	}
	
	/**
	 * @return the proxy supertype
	 */
	protected Class<?> getProxySupertype()
	{
		return CrossDocumentProxy.class;
	}
	
	/**
	 * @param returnType
	 * @return
	 */
	protected CrossDocumentReader getReaderFor(JType returnType)
	{
		if (returnType.isPrimitive() != null)
		{
			return JPRIMITIVETYPE_TO_READER.get(returnType.isPrimitive().getSimpleSourceName());
		}

		if (returnType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			return CrossDocumentReader.STRING;
		}

		return CrossDocumentReader.OBJECT;
	}

	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourceWriter getSourceWriter()
	{
		JPackage crossDocIntfPkg = baseProxyType.getPackage();
		String packageName = crossDocIntfPkg == null ? "" : crossDocIntfPkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		composerFactory.setSuperclass(getProxySupertype().getCanonicalName());
		composerFactory.addImplementedInterface(baseProxyType.getQualifiedSourceName());

		addAdditionalInterfaces(composerFactory);

		return composerFactory.createSourceWriter(context, printWriter);
	}
	  
	/**
	 * @return the class used for serialization.
	 */
	protected Class<? extends SerializationStreamWriter> getStreamWriterClass()
	{
		return ClientSerializationStreamWriter.class;
	}

	/**
	 * @param w
	 * @param method
	 * @throws CruxGeneratorException 
	 */
	private void generateCallToSelfBlock(SourceWriter w, JMethod method) throws CruxGeneratorException
    {
		JClassType controllerClass = getControllerClass(context.getTypeOracle());
		JType returnType = method.getReturnType().getErasedType();
		
		w.println("if (this.target != null && this.target.equals("+Target.class.getCanonicalName()+".SELF)){");
		w.indent();
		String controllerClassName = controllerClass.getQualifiedSourceName();
		w.println(controllerClassName + " controllerOnSelf = "+ScreenFactory.class.getCanonicalName()+
				".getInstance().getRegisteredControllers().getController(CONTROLLER_NAME);");    
		w.println("if (controllerOnSelf == null){");
		w.indent();
		w.println("throw new CrossDocumentException(Crux.getMessages().eventProcessorClientControllerNotFound(CONTROLLER_NAME));");
		w.outdent();
		w.println("}");
		
		if (returnType != JPrimitiveType.VOID)
		{
			w.print("return ");
		}
		w.println("controllerOnSelf."+method.getName()+"(");
		JParameter[] params = method.getParameters();
		boolean needsComma = false;
		for (int i = 0; i < params.length ; ++i)
		{
			JParameter param = params[i];
			if (needsComma)
			{
				w.print(", ");
			}
			needsComma = true;
			w.print(param.getName());
		}
		w.println(");");
		if (returnType == JPrimitiveType.VOID)
		{
			w.print("return;");
		}

		w.outdent();		
		w.println("}");
    }

	/**
	 * @param w
	 * @param method
	 * @param nameFactory
	 */
	private void generateDoInvokeCatchBlock(SourceWriter w, JMethod method, NameFactory nameFactory)
    {
	    w.print("} catch (Throwable ");
	    String exceptionName = nameFactory.createName("ex");
	    w.println(exceptionName + ") {");
		w.indent();
		generateRethrowForInvocationMethod(w, method, exceptionName);
		w.println("throw new CrossDocumentException("+exceptionName+".getMessage(), "+exceptionName+");");
		w.outdent();
		w.println("}");
    }

	/**
	 * @param w
	 * @param method
	 * @param exceptionName
	 */
	private void generateRethrowForInvocationMethod(SourceWriter w, JMethod method, String exceptionName)
    {
	    JType[] methodThrows = method.getThrows();
		if (methodThrows != null)
		{
			for (JType jType : methodThrows)
            {
				String exceptionTypeName = jType.getErasedType().getQualifiedSourceName();
				w.println("if ("+exceptionName+" instanceof "+ exceptionTypeName+"){");
				w.indent();
				w.println("throw ("+exceptionTypeName+")"+exceptionName+";");
				w.outdent();
				w.println("}");
            }
		}
    }
}
