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
package br.com.sysmap.crux.core.rebind.controller;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Validate;
import br.com.sysmap.crux.core.client.controller.document.invoke.CrossDocument;
import br.com.sysmap.crux.core.client.event.ControllerInvoker;
import br.com.sysmap.crux.core.client.event.CrossDocumentInvoker;
import br.com.sysmap.crux.core.client.event.CruxEvent;
import br.com.sysmap.crux.core.client.event.EventProcessor;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractProxyCreator;
import br.com.sysmap.crux.core.rebind.ClientInvokableGeneratorHelper;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializableTypeOracle;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializationUtils;
import br.com.sysmap.crux.core.rebind.crossdocument.gwt.TypeSerializerCreator;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ControllerProxyCreator extends AbstractProxyCreator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static final String CONTROLLER_PROXY_SUFFIX = "_ControllerProxy";
	
	private final Class<?> controllerClass;
	private final boolean isAutoBindEnabled;
	private final boolean isCrossDoc;
	private final boolean isSingleton;

	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public ControllerProxyCreator(TreeLogger logger, GeneratorContext context, Class<?> controllerClass)
	{
		super(logger, context, getCrossDocumentInterface(logger, context, controllerClass));
		this.controllerClass = controllerClass;
		this.isCrossDoc = (CrossDocument.class.isAssignableFrom(controllerClass));
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		this.isSingleton = (controllerAnnot == null || controllerAnnot.statefull());
		this.isAutoBindEnabled = (controllerAnnot == null || controllerAnnot.autoBind());
	}
	
	/**
	 * @param logger
	 * @param context
	 * @param controllerClass
	 * @return
	 */
	private static JClassType getCrossDocumentInterface(TreeLogger logger, GeneratorContext context, Class<?> controllerClass) 
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);

		JClassType crossDocument = typeOracle.findType(controllerClass.getCanonicalName()+"CrossDoc");
		return crossDocument==null?null:crossDocument.isInterface();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyContructor(SourceWriter srcWriter)
	{
		srcWriter.println("public " + getProxySimpleName() + "() {");
		srcWriter.println("}");
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws UnableToCompleteException
	{
		if (isSingleton)
		{
			srcWriter.println(getProxySimpleName()+" wrapper = null;");
		}
		srcWriter.println(Map.class.getName()+"<String, Boolean> __runningMethods = new "+HashMap.class.getCanonicalName()+"<String, Boolean>();");
	}
	
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter)
	{
		generateInvokeMethod(srcWriter);
		ClientInvokableGeneratorHelper.generateScreenUpdateWidgetsFunction(logger, controllerClass, srcWriter);
		ClientInvokableGeneratorHelper.generateControllerUpdateObjectsFunction(logger, controllerClass, srcWriter);
		ClientInvokableGeneratorHelper.generateIsAutoBindEnabledMethod(srcWriter, isAutoBindEnabled);
		
		if (isCrossDoc)
		{
			generateCrossDocInvokeMethod(srcWriter);
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateTypeHandlers(br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializableTypeOracle, br.com.sysmap.crux.core.rebind.crossdocument.gwt.SerializableTypeOracle)
	 */
	@Override
	protected void generateTypeHandlers(SerializableTypeOracle typesSentFromBrowser, SerializableTypeOracle typesSentToBrowser) throws UnableToCompleteException
	{
		if (this.baseProxyType != null)
		{
			TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentToBrowser, typesSentFromBrowser, context, 
					SerializationUtils.getTypeDeserializerQualifiedName(baseProxyType));
			tsc.realize(logger);
		}
	}

	
	
	/**
	 * @return
	 */
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		br.com.sysmap.crux.core.client.screen.Screen.class.getCanonicalName(),
    		CruxEvent.class.getCanonicalName(),
    		GwtEvent.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(),
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		EventProcessor.class.getCanonicalName(),
    		Crux.class.getCanonicalName()
		};
	    return imports;
    }
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	protected String getProxyQualifiedName()
	{
		return controllerClass.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	protected String getProxySimpleName()
	{
		return controllerClass.getSimpleName() + CONTROLLER_PROXY_SUFFIX;
	}
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected SourceWriter getSourceWriter()
	{
		Package crossDocIntfPkg = controllerClass.getPackage();
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

		composerFactory.setSuperclass(controllerClass.getCanonicalName());
		String baseInterface = isCrossDoc?CrossDocumentInvoker.class.getCanonicalName():ControllerInvoker.class.getCanonicalName();
		composerFactory.addImplementedInterface(baseInterface);

		return composerFactory.createSourceWriter(context, printWriter);
	}
	
	/**
	 * @param sourceWriter
	 */
	private void generateCrossDocInvokeMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public String invoke(String serializedData){ ");

		if (isSingleton)
		{
			sourceWriter.println("if (this.wrapper == null){");
			sourceWriter.println("this.wrapper = new " + getProxySimpleName() + "();");
			ClientInvokableGeneratorHelper.generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println(getProxySimpleName() + " wrapper = new " + getProxySimpleName() + "();");
			ClientInvokableGeneratorHelper.generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
		}

		if (isAutoBindEnabled)
		{
			sourceWriter.println("wrapper.updateControllerObjects();");
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
			sourceWriter.println(" else ");
		}
		sourceWriter.println("throw new Exception(\"" + messages.errorInvokingGeneratedMethod() + " \"+metodo);");

		if (!first && isAutoBindEnabled)
		{
			sourceWriter.println("wrapper.updateScreenWidgets();");
		}

		sourceWriter.println("}");
	}	
	
	
	/**
	 * @param sourceWriter
	 * @param method
	 */
	private void generateCrosDocInvokeBlockForMethod(SourceWriter sourceWriter, JMethod method)
	{
    	sourceWriter.println("//TODO: deserialize and call method");
	}
	
	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @param method
	 */
	private void generateInvokeBlockForMethod(SourceWriter sourceWriter, Method method)
    {
	    if (method.getAnnotation(ExposeOutOfModule.class) != null)
	    {
	    	sourceWriter.println("if (\""+method.getName()+"\".equals(metodo)) {");
	    }
	    else
	    {
	    	sourceWriter.println("if (\""+method.getName()+"\".equals(metodo) && !fromOutOfModule) {");
	    }
	    
	    boolean allowMultipleClicks = isAllowMultipleClicks(method);
	    if (!allowMultipleClicks)
	    {
			sourceWriter.println("if (!__runningMethods.containsKey(metodo)){");
	    	sourceWriter.println("__runningMethods.put(metodo,true);");
	    	sourceWriter.println("try{");
	    }
	    
	    Validate annot = method.getAnnotation(Validate.class);
	    if (annot != null)
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
	    }
	    sourceWriter.println("if (__runMethod){");
	    sourceWriter.println("try{");
	    
	    boolean hasReturn = !method.getReturnType().getName().equals("void") && 
	    	!method.getReturnType().getName().equals("java.lang.Void");
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
	    
	    sourceWriter.println("}catch (Throwable e){");
	    sourceWriter.println("eventProcessor.setException(e);");
	    sourceWriter.println("}");
	    sourceWriter.println("}");

	    if (!allowMultipleClicks)
	    {
	    	sourceWriter.println("}finally{");
	    	sourceWriter.println("__runningMethods.remove(metodo);");
	    	sourceWriter.println("}");
	    	sourceWriter.println("}");
	    }
	    
	    sourceWriter.println("}");
    }
	
	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @param className
	 * @param methods
	 * @param singleton
	 * @param autoBindEnabled
	 */
	private void generateInvokeMethod(SourceWriter sourceWriter)
    {
	    sourceWriter.println("public void invoke(String metodo, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("boolean __runMethod = true;");
		
		if (isSingleton)
		{
			sourceWriter.println("if (this.wrapper == null){");
			sourceWriter.println("this.wrapper = new "+getProxySimpleName()+"();");
			ClientInvokableGeneratorHelper.generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println(getProxySimpleName()+" wrapper = new "+getProxySimpleName()+"();");
			ClientInvokableGeneratorHelper.generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
		}
		

		if (isAutoBindEnabled)
		{
			sourceWriter.println("if (!__runningMethods.containsKey(metodo)){");
			sourceWriter.println("wrapper.updateControllerObjects();");
			sourceWriter.println("}");
		}
		
		boolean first = true;
		Method[] methods = controllerClass.getMethods(); 
		for (Method method: methods) 
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
		sourceWriter.println("throw new Exception(\""+messages.errorInvokingGeneratedMethod()+" \"+metodo);");

		if (!first && isAutoBindEnabled)
		{
			sourceWriter.println("wrapper.updateScreenWidgets();");
		}		
		
		sourceWriter.println("}");
    }	
	
	/** 
	 * Generates the controller method call.
	 * @param method
	 * @param sourceWriter
	 */
	private void generateMethodCall(Method method, SourceWriter sourceWriter, boolean finalizeCommand)
	{
		Class<?>[] params = method.getParameterTypes();
		if (params != null && params.length == 1)
		{
			sourceWriter.print("wrapper."+method.getName()+"(("+params[0].getCanonicalName()+")sourceEvent)");
		}
		else 
		{
			sourceWriter.print("wrapper."+method.getName()+"()");
		}
		if (finalizeCommand)
		{
			sourceWriter.print(";");
		}
	}	

	/**
	 * 
	 * @param logger
	 * @param controllerClass
	 * @param method
	 * @param validateMethod
	 * @param sourceWriter
	 */
	private void generateValidateMethodCall(Method method, String validateMethod, SourceWriter sourceWriter)
	{
		Class<?>[] params = method.getParameterTypes();
		try
		{
			Method validate = null;
			if (params != null && params.length == 1)
			{
				validate = ClassUtils.getMethod(controllerClass, validateMethod, params[0]);
				if(validate == null)
				{
					validate = ClassUtils.getMethod(controllerClass, validateMethod, new Class[]{});
				}
			}
			else
			{
				validate = ClassUtils.getMethod(controllerClass, validateMethod, new Class[]{});
			}
			generateMethodCall(validate, sourceWriter, true);
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredControllerInvalidValidateMethod(validateMethod), e);
		}
	}
	
	/**
	 * @param method
	 * @return
	 */
	private boolean isAllowMultipleClicks(Method method)
    {
	    Expose exposeAnnot = method.getAnnotation(Expose.class);
	    if (exposeAnnot != null)
	    {
	    	return exposeAnnot.allowMultipleCalls();
	    }
		
	    ExposeOutOfModule exposeOutAnnot = method.getAnnotation(ExposeOutOfModule.class);
	    if (exposeOutAnnot != null)
	    {
	    	return exposeOutAnnot.allowMultipleCalls();
	    }
		return false;
    }
	
	/**
	 * Verify if a method must be included in the list of callable methods in the 
	 * generated invoker class
	 * @param method
	 * @return
	 */
	private boolean isControllerMethodSignatureValid(Method method)
	{
		if (!Modifier.isPublic(method.getModifiers()))
		{
			return false;
		}
		
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters != null && parameters.length != 0 && parameters.length != 1)
		{
			return false;
		}
		if (parameters != null && parameters.length == 1)
		{
			if (!GwtEvent.class.isAssignableFrom(parameters[0]) && !CruxEvent.class.isAssignableFrom(parameters[0]))
			{
				return false;
			}
		}
		
		if (method.getDeclaringClass().equals(Object.class))
		{
			return false;
		}
		
		if (method.getAnnotation(Expose.class) == null && method.getAnnotation(ExposeOutOfModule.class) == null)
		{
			return false;
		}
		
		return true;
	}
}
