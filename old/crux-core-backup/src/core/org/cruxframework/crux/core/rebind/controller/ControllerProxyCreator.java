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
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Validate;
import org.cruxframework.crux.core.client.event.BaseEvent;
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ControllerProxyCreator extends AbstractProxyCreator
{
	public static final String CONTROLLER_PROXY_SUFFIX = "_ControllerProxy";
	public static final String EXPOSED_METHOD_SUFFIX = "_Exposed_";
	
	private final JClassType controllerClass;
	private String controllerName;

	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public ControllerProxyCreator(TreeLogger logger, GeneratorContext context, JClassType controllerClass)
	{
		super(logger, context, false);
		this.controllerClass = controllerClass;
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		this.controllerName = controllerAnnot.value();
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourcePrinter)
	 */
    @Override
	protected void generateProxyContructor(SourcePrinter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public " + getProxySimpleName() + "("+View.class.getCanonicalName()+" view) {");
		srcWriter.println("this.__view = view;");
		srcWriter.println("}");
	}
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourcePrinter)
	 */
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		super.generateProxyFields(srcWriter);
		srcWriter.println("private " + View.class.getCanonicalName() + " __view;");
		generateLoggerField(srcWriter);
		
		srcWriter.println();
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		super.generateProxyMethods(srcWriter);
		generateGetViewMethod(srcWriter);
		generateControllerOverideExposedMethods(srcWriter);
	}

	/**
	 * 
	 * @param srcWriter
	 */
	protected void generateGetViewMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public String getBoundCruxViewId(){");
		srcWriter.println("return (this.__view==null?null:this.__view.getId());");
		srcWriter.println("}");
		srcWriter.println("public "+View.class.getCanonicalName()+" getBoundCruxView(){");
		srcWriter.println("return this.__view;");
		srcWriter.println("}");
	}

	/**
	 * @return
	 */
    protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		FastMap.class.getCanonicalName(),
    		BaseEvent.class.getCanonicalName(),
    		GwtEvent.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(),
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		Logger.class.getCanonicalName(),
    		LogConfiguration.class.getCanonicalName(), 
    		Level.class.getCanonicalName()
		};
	    return imports;
    }
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	public String getProxyQualifiedName()
	{
		return controllerClass.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	public String getProxySimpleName()
	{
		return controllerClass.getSimpleSourceName() + CONTROLLER_PROXY_SUFFIX;
	}
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected SourcePrinter getSourcePrinter()
	{
		JPackage pkg = controllerClass.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
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
		composerFactory.addImplementedInterface(ViewAware.class.getCanonicalName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	/**
	 * Generates the signature for the exposed method
	 * 
	 * @param w
	 * @param nameFactory
	 * @param method
	 */
	protected void generateProxyExposedMethodSignature(SourcePrinter w, NameFactory nameFactory, JMethod method)
	{
		// Write the method signature
		JType returnType = method.getReturnType().getErasedType();
		w.print("public ");
		w.print(returnType.getQualifiedSourceName());
		w.print(" ");
		w.print(method.getName()+EXPOSED_METHOD_SUFFIX + "(");
		generateMethodParameters(w, nameFactory, method);
		w.print(")");
		generateMethodTrhowsClause(w, method);
		w.println();
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
     * @param method
     */
    private void generateExposedMethodCall(SourcePrinter sourceWriter, JMethod method)
    {
		sourceWriter.print(method.getName()+"(");
		
		boolean needsComma = false;
		JParameter[] params = method.getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			JParameter param = params[i];

			if (needsComma)
			{
				sourceWriter.print(", ");
			}
			else
			{
				needsComma = true;
			}

			String paramName = param.getName();
			sourceWriter.print(paramName);
		}
		
		sourceWriter.println(");");
    }

    /**
     * @param sourceWriter
     * @param method
     */
    private void generateMethodvalidationCall(SourcePrinter sourceWriter, JMethod method, String validationMethod)
    {
		sourceWriter.print(validationMethod+"(");
		
		JParameter[] params = method.getParameters();
		if (params.length == 1)
		{
			JParameter param = params[0];
			JMethod validate = controllerClass.findMethod(validationMethod, new JType[]{param.getType()});
			if (validate != null)
			{
				sourceWriter.print(param.getName());
			}
		}
		
		sourceWriter.println(");");
    }

	/**
	 * Verify if a method must be included in the list of callable methods in the 
	 * generated invoker class
	 * @param method
	 * @return
	 */
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
	        	JClassType cruxEventType = controllerClass.getOracle().getType(BaseEvent.class.getCanonicalName());
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
	        
	        if (method.getAnnotation(Expose.class) == null)
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
