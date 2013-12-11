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
package org.cruxframework.crux.core.rebind.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Validate;
import org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader;
import org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamWriter;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;
import org.cruxframework.crux.core.client.event.CrossDocumentInvoker;
import org.cruxframework.crux.core.client.event.CruxEvent;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInvocableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.SerializationUtils;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.Shared;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.TypeSerializerCreator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Legacy(value=ControllerProxyCreator.class)
public class ControllerProxyCreatorLegacy extends AbstractInvocableProxyCreator
{
	@Legacy
	@Deprecated
	private final boolean isAutoBindEnabled;
	@Legacy
	@Deprecated
	private final boolean isCrossDoc;
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public ControllerProxyCreatorLegacy(TreeLogger logger, GeneratorContext context, JClassType controllerClass)
	{
		super(logger, context, getCrossDocumentInterface(logger, context, controllerClass), controllerClass);
		this.controllerClass = controllerClass;
        try
        {
        	JClassType crossDocumentType = controllerClass.getOracle().getType(CrossDocument.class.getCanonicalName());
        	this.isCrossDoc = (crossDocumentType.isAssignableFrom(controllerClass));
        	if (isCrossDoc && this.baseProxyType == null)
        	{
        		throw new CruxGeneratorException("The Controller ["+controllerClass.getQualifiedSourceName()+"] is a CrossDocument controller." +
        				" However, it does not implement the required interface ["+controllerClass.getQualifiedSourceName()+"CrossDoc]");
        	}
        }
        catch (NotFoundException e)
        {
    		throw new CruxGeneratorException("The Controller ["+controllerClass.getQualifiedSourceName()+"] is a CrossDocument controller. " +
    				"However, it does not implement the required interface ["+controllerClass.getQualifiedSourceName()+"CrossDoc]");
        }
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		this.isAutoBindEnabled = (controllerAnnot == null || controllerAnnot.autoBind());
		this.controllerName = controllerAnnot.value();
	}
	
	/**
	 * @param logger
	 * @param context
	 * @param controllerClass
	 * @return
	 */
	@Legacy
	private static JClassType getCrossDocumentInterface(TreeLogger logger, GeneratorContext context, JClassType controllerClass) 
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);

		JClassType crossDocument = typeOracle.findType(controllerClass.getQualifiedSourceName()+"CrossDoc");
		return crossDocument==null?null:crossDocument.isInterface();
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected void generateProxyContructor(SourcePrinter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public " + getProxySimpleName() + "("+View.class.getCanonicalName()+" view) {");
		srcWriter.println("this.__view = view;");
		generateAutoCreateFields(srcWriter, "this", isAutoBindEnabled);
		srcWriter.println("}");
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		super.generateProxyFields(srcWriter);
		if (isCrossDoc)
		{
			String typeSerializerName = SerializationUtils.getTypeDeserializerQualifiedName(baseProxyType);
			srcWriter.println("private static final " + typeSerializerName + " SERIALIZER = new " + typeSerializerName + "();");
		}
		generateLoggerField(srcWriter);
		
		srcWriter.println();
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		super.generateProxyMethods(srcWriter);
		
		generateInvokeMethod(srcWriter);
		generateControllerOverideExposedMethods(srcWriter);
		
		//TODO: create a interface screenBinder, que possua os metodos de bind entre screen e controller.
		// A screen deve definir um binder para si e eassociar ao controller... desta forma nao precisa de 
		// codigos em runtime para verificar se existe a widget a ser amarrada, se ela eh um HasFormatter ou 
		// um HasText, etc
		generateScreenUpdateWidgetsFunction(controllerClass, srcWriter);
		generateControllerUpdateObjectsFunction(controllerClass, srcWriter);
		generateIsAutoBindEnabledMethod(srcWriter, isAutoBindEnabled);
		
		if (isCrossDoc)
		{
			generateCrossDocInvokeMethod(srcWriter);
			generateCreateStreamReaderMethod(srcWriter);
			generateCreateStreamWriterMethod(srcWriter);
		}
	}

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateTypeSerializers(SerializableTypeOracle, SerializableTypeOracle)
	 */
	@Override
	@Legacy
	protected void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser, SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException
	{
		if (this.baseProxyType != null)
		{
			try
            {
	            TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentToBrowser, typesSentFromBrowser, context, 
	            		SerializationUtils.getTypeDeserializerQualifiedName(baseProxyType),
		                SerializationUtils.getTypeDeserializerSimpleName(baseProxyType));
	            tsc.realize(logger);
            }
            catch (Exception e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }
		}
	}

	/**
	 * @return
	 */
	@Legacy
	protected String getClientSerializationStreamReaderClassName()
	{
		return ClientSerializationStreamReader.class.getCanonicalName();
	}
	
	/**
	 * @return
	 */
	@Legacy
	protected String getClientSerializationStreamWriterClassName()
	{
		return ClientSerializationStreamWriter.class.getCanonicalName();
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
    protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		FastMap.class.getCanonicalName(),
    		CruxEvent.class.getCanonicalName(),
    		GwtEvent.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(),
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.EventProcessor.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		SerializationException.class.getCanonicalName(), 
    		SerializationStreamReader.class.getCanonicalName(),
    		SerializationStreamWriter.class.getCanonicalName(), 
    		Logger.class.getCanonicalName(),
    		LogConfiguration.class.getCanonicalName(), 
    		Level.class.getCanonicalName()
		};
	    return imports;
    }
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@SuppressWarnings("deprecation")
	protected SourcePrinter getSourcePrinter()
	{
		JPackage crossDocIntfPkg = controllerClass.getPackage();
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

		composerFactory.setSuperclass(controllerClass.getQualifiedSourceName());
        String baseInterface = isCrossDoc?CrossDocumentInvoker.class.getCanonicalName():org.cruxframework.crux.core.client.event.ControllerInvoker.class.getCanonicalName();
		composerFactory.addImplementedInterface(baseInterface);
		composerFactory.addImplementedInterface(ViewAware.class.getCanonicalName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	/**
	 * @param srcWriter
	 */
	private void generateCreateStreamReaderMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected SerializationStreamReader createStreamReader(String encoded) throws SerializationException {");
		String readerClassName = getClientSerializationStreamReaderClassName();

		srcWriter.println(readerClassName+" clientSerializationStreamReader = new "+readerClassName+"(SERIALIZER);");
		srcWriter.println("clientSerializationStreamReader.prepareToRead(encoded);");
		srcWriter.println("return clientSerializationStreamReader;");
		srcWriter.println("}");
	}
	
	/**
	 * @param srcWriter
	 */
	private void generateCreateStreamWriterMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected SerializationStreamWriter createStreamWriter(){");
		String writerClassName = getClientSerializationStreamWriterClassName();

		srcWriter.println(writerClassName+" clientSerializationStreamWriter = new "+writerClassName+"(SERIALIZER);");
		srcWriter.println("clientSerializationStreamWriter.prepareToWrite();");
		srcWriter.println("return clientSerializationStreamWriter;");
		srcWriter.println("}");	
	}

	/**
	 * @param sourceWriter
	 * @param method
	 */
	private void generateCrosDocInvokeBlockForMethod(SourcePrinter sourceWriter, JMethod method)
	{
		JParameter[] params = method.getParameters();
		JType returnType = method.getReturnType();

		sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".unsafeEquals(\""+getJsniSimpleSignature(method)+"\", methodCalled)){");

    	if (returnType != JPrimitiveType.VOID)
    	{
        	sourceWriter.print("streamWriter."+Shared.getStreamWriteMethodNameFor(returnType)+"(");
    		
    	}
    	sourceWriter.println(method.getName()+EXPOSED_METHOD_SUFFIX+"(");

		for (int i = 0; i < params.length ; ++i)
		{
			JParameter param = params[i];
			if (i > 0)
			{
				sourceWriter.print(",");
			}
			sourceWriter.println("("+param.getType().getParameterizedQualifiedSourceName()+")streamReader."+Shared.getStreamReadMethodNameFor(param.getType())+"()");
		}
    	sourceWriter.print(")");
		if (returnType != JPrimitiveType.VOID)
		{
			sourceWriter.print(")");
		}
		sourceWriter.println(";");

    	sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	private void generateCrossDocInvokeExceptionHandlingBlock(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("try{");
    	sourceWriter.println("isExecutionOK = false;");
		sourceWriter.println("streamWriter.writeObject(ex);");
		sourceWriter.println("}catch (Exception ex2){");
		sourceWriter.println("return Crux.getMessages().crossDocumentInvocationGeneralError(Screen.getId(), ex.getMessage());"); 
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 */
	private void generateCrossDocInvokeMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public String invoke(String serializedData){ ");

		sourceWriter.println("boolean isExecutionOK = true;");

		sourceWriter.println(SerializationStreamWriter.class.getSimpleName()+" streamWriter = createStreamWriter();");
		sourceWriter.println("try{");

		generateMethodIdentificationBlock(sourceWriter);
		
		sourceWriter.println(SerializationStreamReader.class.getSimpleName()+" streamReader = null;");
		sourceWriter.println("if(serializedData.length() > 0){");
		sourceWriter.println("streamReader = createStreamReader(serializedData);");
		sourceWriter.println("}");
		
		logDebugMessage(sourceWriter, "\"Calling cross document method: Screen[\"+Screen.getId()+\"], Controller["+controllerName+"], Method[\"+methodCalled+\"]\"");
		
		if (isAutoBindEnabled)
		{
			sourceWriter.println("updateControllerObjects();");
		}

		boolean first = true;
		JMethod[] methods = baseProxyType.getOverridableMethods();
		for (JMethod method : methods)
		{
			if (!first)
			{
				sourceWriter.print("else ");
			}

			generateCrosDocInvokeBlockForMethod(sourceWriter, method);

			first = false;
		}
		if (!first)
		{
			sourceWriter.println(" else {");
		}
		
		logDebugMessage(sourceWriter, "\"Error calling cross document method: Screen[\"+Screen.getId()+\"], Controller["+controllerName+"], Method[\"+methodCalled+\"]. NotFound\"");
		sourceWriter.println("return Crux.getMessages().crossDocumentMethodNotFound();"); 
		
    	if (!first)
		{
			sourceWriter.println("}");
		}

		if (!first && isAutoBindEnabled)
		{
			sourceWriter.println("updateScreenWidgets();");
		}
		logDebugMessage(sourceWriter, "\"Cross document method executed: Screen[\"+Screen.getId()+\"], Controller["+controllerName+"], Method[\"+methodCalled+\"]\"");

		sourceWriter.println("}catch (Exception ex){");
		generateCrossDocInvokeExceptionHandlingBlock(sourceWriter);
		sourceWriter.println("}");


		sourceWriter.println("return (isExecutionOK?\"//OK\":\"//EX\")+streamWriter.toString();");
		sourceWriter.println("}");
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @param method
	 */
    @SuppressWarnings("deprecation")
	private void generateInvokeBlockForMethod(SourcePrinter sourceWriter, JMethod method)
    {
	    if (method.getAnnotation(org.cruxframework.crux.core.client.controller.ExposeOutOfModule.class) != null)
	    {
	    	sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".unsafeEquals(\""+method.getName()+"\",metodo)) {");
	    }
	    else
	    {
	    	sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".unsafeEquals(\""+method.getName()+"\",metodo) && !fromOutOfModule) {");
	    }
	    
	    Validate annot = method.getAnnotation(Validate.class);
	    boolean mustValidade = annot != null; 
	    if (mustValidade)
	    {
	    	sourceWriter.println("try{");
	    	String validateMethod = annot.value();
	    	if (validateMethod == null || validateMethod.length() == 0)
	    	{
	    		String methodName = method.getName();
	    		methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
	    		validateMethod = "validate"+ methodName;
	    	}
	    	generateValidateMethodCall(method, validateMethod, sourceWriter);
	    	sourceWriter.println("}catch (Throwable e){");
	    	sourceWriter.println("__runMethod = false;");
	    	sourceWriter.println("eventProcessor.setValidationMessage(e.getMessage());");
	    	sourceWriter.println("}");
	    	sourceWriter.println("if (__runMethod){");
	    }
	    
	    boolean hasReturn = method.getReturnType().getErasedType() != JPrimitiveType.VOID;
		if (hasReturn)
	    {
	    	sourceWriter.println("eventProcessor.setHasReturn(true);");
	    	sourceWriter.println("eventProcessor.setReturnValue(");
	    }
	    generateMethodCall(method, sourceWriter, !hasReturn);
		if (hasReturn)
	    {
	    	sourceWriter.println(");");
	    }
	    
	    if (mustValidade)
	    {
		    sourceWriter.println("}");
	    }

	    sourceWriter.println("}");
    }
	    
	/**
	 * 
	 * @param method
	 * @param validateMethod
	 * @param sourceWriter
	 */
    @Legacy
	private void generateValidateMethodCall(JMethod method, String validateMethod, SourcePrinter sourceWriter)
	{
		JParameter[] params = method.getParameters();
		try
		{
			JMethod validate = null;
			if (params != null && params.length == 1)
			{
				validate = controllerClass.findMethod(validateMethod, new JType[]{params[0].getType()});
				if(validate == null)
				{
					validate = controllerClass.findMethod(validateMethod, new JType[]{});
				}
			}
			else
			{
				validate = controllerClass.findMethod(validateMethod, new JType[]{});
			}
			generateMethodCall(validate, sourceWriter, true);
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Error generating widget factory. invalid validation method: ["+validateMethod+"].", e);
		}
	}
    
	/** 
	 * Generates the controller method call.
	 * @param method
	 * @param sourceWriter
	 */
	private void generateMethodCall(JMethod method, SourcePrinter sourceWriter, boolean finalizeCommand)
	{
		JParameter[] params = method.getParameters();
		if (params != null && params.length == 1)
		{
			sourceWriter.print(method.getName()+"(("+params[0].getType().getParameterizedQualifiedSourceName()+")sourceEvent)");
		}
		else 
		{
			sourceWriter.print(method.getName()+"()");
		}
		if (finalizeCommand)
		{
			sourceWriter.println(";");
		}
	}	

	/**
	 * @param sourceWriter
	 */
	private void generateControllerOverideExposedMethods(SourcePrinter sourceWriter)
	{
		List<JMethod> methods = new ArrayList<JMethod>();
		JMethod[] controllerMethods = controllerClass.getOverridableMethods();
		for (JMethod jMethod : controllerMethods)
        {
			if (isControllerMethodSignatureValid(jMethod))
			{
				methods.add(jMethod);
			}
        }
		
		if (isCrossDoc)
		{
			JMethod[] crossDocumentMethods = baseProxyType.getOverridableMethods();
			for (JMethod jMethod : crossDocumentMethods)
	        {
				methods.add(jMethod);
	        }
		}
		Set<String> processed = new HashSet<String>();
		
		for (JMethod method: methods) 
		{
			String methodSignature = method.getReadableDeclaration(true, true, true, true, true);
			if (!processed.contains(methodSignature))
			{
				processed.add(methodSignature);
				
				generateProxyExposedMethodSignature(sourceWriter, new NameFactory(), method);
				sourceWriter.println("{");
				
				logDebugMessage(sourceWriter, "\"Calling client event: Controller["+controllerName+"], Method["+method.getName()+"]\"");

				if (isAutoBindEnabled)
				{
					sourceWriter.println("updateControllerObjects();");
				}
				JType returnType = method.getReturnType().getErasedType();
				boolean hasReturn = returnType != JPrimitiveType.VOID;

			    Validate annot = method.getAnnotation(Validate.class);
			    boolean mustValidade = annot != null; 
			    if (mustValidade)
			    {
			    	sourceWriter.println("try{");
			    	String validateMethod = annot.value();
			    	if (validateMethod == null || validateMethod.length() == 0)
			    	{
			    		String methodName = method.getName();
			    		methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
			    		validateMethod = "validate"+ methodName;
			    	}
			    	generateMethodvalidationCall(sourceWriter, method, validateMethod);
			    	sourceWriter.println("}catch (Throwable e1){");
			    	sourceWriter.println("Crux.getValidationErrorHandler().handleValidationError(e1.getLocalizedMessage());");
					logDebugMessage(sourceWriter, "\"Client event not called due to a Validation error: Controller["+controllerName+"], Method["+method.getName()+"]\"");
			    	if (hasReturn)
			    	{
				    	sourceWriter.println("return null;");
			    	}
			    	else
			    	{
				    	sourceWriter.println("return;");
			    	}
			    	sourceWriter.println("}");
			    }
			    
		    	if (hasReturn)
		    	{
					sourceWriter.print(returnType.getQualifiedSourceName()+" ret = ");
		    	}
		    	
		    	generateExposedMethodCall(sourceWriter, method);
		    		
				if (isAutoBindEnabled)
				{
					sourceWriter.println("updateScreenWidgets();");
				}		
				
				logDebugMessage(sourceWriter, "\"Client event executed: Controller["+controllerName+"], Method["+method.getName()+"]\"");
		    	if (hasReturn)
		    	{
					sourceWriter.println("return ret;");
		    	}
		    	
				sourceWriter.println("}");
			}
		}
    }

    /**
	 * @param sourceWriter
	 */
	private void generateInvokeMethod(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("public void invoke(String metodo, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception{ ");

		if (!isCrux2OldInterfacesCompatibilityEnabled())
		{
			sourceWriter.println("throw new Exception("+EscapeUtils.quote("To use this feature you need to enable crux2 old interfaces compatibility.")+");");
		}
		else
		{
			sourceWriter.println("boolean __runMethod = true;");
			sourceWriter.println("boolean methodNotFound = false;");

			if (isAutoBindEnabled)
			{
				sourceWriter.println("updateControllerObjects();");
			}


			sourceWriter.println("try{");

			boolean first = true;
			JMethod[] methods = controllerClass.getOverridableMethods(); 
			for (JMethod method: methods) 
			{
				if (isControllerMethodSignatureValid(method))
				{
					if (!first)
					{
						sourceWriter.print("else ");
					}

					generateInvokeBlockForMethod(sourceWriter, method);

					first = false;
				}
			}
			if (!first)
			{
				sourceWriter.println(" else ");
			}
			sourceWriter.println("methodNotFound = true;");

			sourceWriter.println("}catch (Throwable e){");
			sourceWriter.println("eventProcessor.setException(e);");
			sourceWriter.println("}");

			sourceWriter.println("if (methodNotFound){");
			sourceWriter.println("throw new Exception(\"Method Not Found: \"+metodo);");
			sourceWriter.println("}");

			if (!first && isAutoBindEnabled)
			{
				sourceWriter.println("updateScreenWidgets();");
			}		
		}
		sourceWriter.println("}");
    }
	
	/**
	 * @param sourceWriter
	 */
	@Legacy
	private void generateMethodIdentificationBlock(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("String methodCalled = null;");
		sourceWriter.println("int idx = serializedData.indexOf('|');");
		sourceWriter.println("if (idx > 0){");
		sourceWriter.println("methodCalled = serializedData.substring(0,idx);");
		sourceWriter.println("serializedData = serializedData.substring(idx+1);");
		sourceWriter.println("}else{");
		sourceWriter.println("return Crux.getMessages().crossDocumentCanNotIdentifyMethod();"); 
		sourceWriter.println("}");
    }	

	/**
	 * Verify if a method must be included in the list of callable methods in the 
	 * generated invoker class
	 * @param method
	 * @return
	 */
	@SuppressWarnings("deprecation")
    private boolean isControllerMethodSignatureValid(JMethod method)
	{
		try
        {
	        if (!method.isPublic())
	        {
	        	return false;
	        }
	        
	        JParameter[] parameters = method.getParameters();
	        if (parameters != null && parameters.length != 0 && parameters.length != 1)
	        {
	        	return false;
	        }
	        if (parameters != null && parameters.length == 1)
	        {
	        	JClassType gwtEventType = controllerClass.getOracle().getType(GwtEvent.class.getCanonicalName());
	        	JClassType cruxEventType = controllerClass.getOracle().getType(CruxEvent.class.getCanonicalName());
	        	JClassType parameterType = parameters[0].getType().isClassOrInterface();
	        	if (parameterType == null || (!gwtEventType.isAssignableFrom(parameterType) && !cruxEventType.isAssignableFrom(parameterType)))
	        	{
	        		return false;
	        	}
	        }
	        
	        JClassType objectType = controllerClass.getOracle().getType(Object.class.getCanonicalName());
	        if (method.getEnclosingType().equals(objectType))
	        {
	        	return false;
	        }
	        
	        if (method.getAnnotation(Expose.class) == null && method.getAnnotation(org.cruxframework.crux.core.client.controller.ExposeOutOfModule.class) == null)
	        {
	        	return false;
	        }
	        
	        return true;
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
}
