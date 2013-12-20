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
package org.cruxframework.crux.core.rebind.ioc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.ioc.Inject.Scope;
import org.cruxframework.crux.core.client.ioc.IocContainer;
import org.cruxframework.crux.core.client.ioc.IocProvider;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.ViewBindable;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.ioc.IoCException;
import org.cruxframework.crux.core.ioc.IocConfig;
import org.cruxframework.crux.core.ioc.IocConfigImpl;
import org.cruxframework.crux.core.ioc.IocContainerManager;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainerRebind extends AbstractProxyCreator
{ 
	private final View view;
	private Map<String, IocConfig<?>> configurations;
	private JClassType viewBindableType;
	private JClassType remoteServiceType;
	private Device device;

	public IocContainerRebind(TreeLogger logger, GeneratorContext context, View view, String device)
    {
	    super(logger, context, false);
		this.view = view;
		viewBindableType = context.getTypeOracle().findType(ViewBindable.class.getCanonicalName());
		remoteServiceType = context.getTypeOracle().findType(RemoteService.class.getCanonicalName());
		this.device = Device.valueOf(device);
		configurations = IocContainerManager.getConfigurationsForView(view, this.device);
    }

	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		Iterator<String> classes = configurations.keySet().iterator();
		while (classes.hasNext())
		{
			String className = classes.next();
			generateContainerInstatiationMethod(srcWriter, className);
		}
    }

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(View view){");
		srcWriter.println("super(view);");
		srcWriter.println("}");
	}
	
	/**
	 * 
	 * @param srcWriter
	 * @param className
	 */
	private void generateContainerInstatiationMethod(SourcePrinter srcWriter, String className)
	{

		try
		{
			srcWriter.println("public  "+className+" get"+className.replace('.', '_')+"("+Scope.class.getCanonicalName()+" scope, String subscope){");
			JClassType type = JClassUtils.getType(context.getTypeOracle(), className);

			IocConfigImpl<?> iocConfig = (IocConfigImpl<?>) configurations.get(className);
			Class<?> providerClass = iocConfig.getProviderClass();
			if (providerClass != null)
			{
				srcWriter.println(className+" result = _getScope(scope).getValue(GWT.create("+providerClass.getCanonicalName()+".class), "+EscapeUtils.quote(className)+", subscope, ");
				generateFieldsPopulationCallback(srcWriter, type);
				srcWriter.println(");");
			}
			else if (iocConfig.getToClass() != null)
			{
				srcWriter.println(className+" result = _getScope(scope).getValue(new "+IocProvider.class.getCanonicalName()+"<"+className+">(){");
				srcWriter.println("public "+className+" get(){");
				srcWriter.println("return GWT.create("+iocConfig.getToClass().getCanonicalName()+".class);");
				srcWriter.println("}");
				srcWriter.println("}, "+EscapeUtils.quote(className)+", subscope, ");
				generateFieldsPopulationCallback(srcWriter, type);
				srcWriter.println(");");
			}
			else
			{
				srcWriter.println(className+" result = _getScope(scope).getValue(new "+IocProvider.class.getCanonicalName()+"<"+className+">(){");
				srcWriter.println("public "+className+" get(){");
				srcWriter.println("return GWT.create("+getInstantiationClass(className)+".class);");
				srcWriter.println("}");
				srcWriter.println("}, "+EscapeUtils.quote(className)+", subscope, ");
				generateFieldsPopulationCallback(srcWriter, type);
				srcWriter.println(");");
			}

			if (type.isAssignableTo(viewBindableType))
			{
				srcWriter.println("if (scope != "+Scope.class.getCanonicalName()+ "."+Scope.DOCUMENT.name()+" && result.getBoundCruxViewId() == null){");
				srcWriter.println("result.bindCruxView(this.getBoundCruxViewId());");
				srcWriter.println("}");
			}
			srcWriter.println("return result;");
			srcWriter.println("}");
		}
		catch (NotFoundException e)
		{
			throw new IoCException("IoC Error Class ["+className+"] not found.", e);
		}
		
    }

	/**
	 * 
	 * @param className
	 * @return
	 */
	private String getInstantiationClass(String className)
    {
		if (className.endsWith("Async"))
		{
			String serviceInterface = className.substring(0, className.length() - 5);
			JClassType type = context.getTypeOracle().findType(serviceInterface);
			if (type != null && type.isAssignableTo(remoteServiceType))
			{
				return type.getQualifiedSourceName();
			}
		}
		
	    return className;
    }

	/**
	 * 
	 * @param srcWriter
	 * @param className
	 */
	private void generateFieldsPopulationCallback(SourcePrinter srcWriter, JClassType type) 
    {
		String className = type.getQualifiedSourceName();	
		srcWriter.println("new IocScope.CreateCallback<"+className+">(){");
		srcWriter.println("public void onCreate("+className+" newObject){");
		injectFieldsAndMethods(srcWriter, type, "newObject", new HashSet<String>(), getProxySimpleName()+".this", configurations);
		srcWriter.println("}");
		srcWriter.println("}");
    }

	/**
	 * 
	 * @param srcWriter
	 * @param type
	 * @param parentVariable
	 * @param iocContainerVariable
	 * @param view
	 * @param device
	 */
	public static void injectFieldsAndMethods(SourcePrinter srcWriter, JClassType type, String parentVariable, String iocContainerVariable, 
			View view, Device device)
	{
		Map<String, IocConfig<?>> configurations = IocContainerManager.getConfigurationsForView(view, device);
		injectFieldsAndMethods(srcWriter, type, parentVariable, new HashSet<String>(), iocContainerVariable, configurations);
	}
	
	/**
	 * 
	 * @param srcWriter
	 * @param type
	 * @param parentVariable
	 * @param added
	 * @param iocContainerVariable
	 * @param configurations
	 */
	private static void injectFieldsAndMethods(SourcePrinter srcWriter, JClassType type, String parentVariable, Set<String> added, String iocContainerVariable, 
			Map<String, IocConfig<?>> configurations)
	{
        injectFields(srcWriter, type, parentVariable, added, iocContainerVariable, configurations);
        injectMethods(srcWriter, type, parentVariable, added, iocContainerVariable, configurations);
        if (type.getSuperclass() != null)
        {
        	injectFieldsAndMethods(srcWriter, type.getSuperclass(), parentVariable, added, iocContainerVariable, configurations);
        }
	}

	/**
	 * 
	 * @param srcWriter
	 * @param type
	 * @param parentVariable
	 * @param added
	 * @param iocContainerVariable
	 * @param configurations
	 */
	private static void injectFields(SourcePrinter srcWriter, JClassType type, String parentVariable, Set<String> added, String iocContainerVariable, Map<String, IocConfig<?>> configurations)
    {
	    for (JField field : type.getFields()) 
        {
        	String fieldName = field.getName();
			if (!added.contains(fieldName))
        	{
				added.add(fieldName);
				JType fieldType = field.getType();
				if ((fieldType.isPrimitive()== null))
				{
					String injectionExpression = getFieldInjectionExpression(field, iocContainerVariable, configurations);
					if (injectionExpression != null)
					{
						if (JClassUtils.isPropertyVisibleToWrite(type, field, false))
						{
							if (JClassUtils.hasSetMethod(field, type))
							{
								String setterMethodName = "set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
                                JMethod method = type.findMethod(setterMethodName, new JType[]{field.getType()});
                                if (method.getAnnotation(Inject.class) == null) // Annotated methods are handled apart
                                {
                                	srcWriter.println(fieldType.getQualifiedSourceName()+" field_"+fieldName+" = "+ injectionExpression+";");
                                	srcWriter.println(parentVariable+"."+setterMethodName+"(field_"+ fieldName+");");
                                }
							}
							else
							{
								srcWriter.println(parentVariable+"."+fieldName+" = "+ injectionExpression+";");
							}
						}
						else
						{
							throw new IoCException("IoC Error Field ["+field.getName()+"] from class ["+type.getQualifiedSourceName()+"] is not a writeable property.");
						}
					}
				}
        	}
        }
    }
	
	/**
	 * 
	 * @param srcWriter
	 * @param type
	 * @param parentVariable
	 * @param added
	 * @param iocContainerVariable
	 * @param configurations
	 */
	private static void injectMethods(SourcePrinter srcWriter, JClassType type, String parentVariable, Set<String> added, String iocContainerVariable, Map<String, IocConfig<?>> configurations)
    {
	    for (JMethod method : type.getMethods()) 
        {
        	Inject inject = method.getAnnotation(Inject.class);
        	if (inject != null && !method.isStatic())
        	{
		    	String methodName = method.getName();
				if (!added.contains(methodName+"()"))
	        	{
					added.add(methodName+"()");
					JParameter[] parameters = method.getParameters();
					List<String> params = new ArrayList<String>();
					for (JParameter parameter : parameters)
	                {
						JType parameterType = parameter.getType();
						if ((parameterType.isPrimitive()!= null))
						{
							throw new IoCException("IoC Error Method ["+methodName+"] from class ["+type.getQualifiedSourceName()+"] declares an invalid parameter. Primitive types are not allowed here");
						}
						String variableName = "parameter_"+methodName+"_"+parameter.getName();
						params.add(variableName);
						String injectionExpression = getParameterInjectionExpression(parameter, iocContainerVariable, configurations);
						srcWriter.println(parameterType.getQualifiedSourceName()+" "+variableName+" = "+ injectionExpression+";");
	                }
					srcWriter.print(parentVariable+"."+methodName+"(");
					boolean first = true;
					for (String param : params)
                    {
						if (!first)
						{
							srcWriter.print(", ");
						}
						first = false;
						srcWriter.print(param);
	                    
                    }
					srcWriter.println(");");
					
	        	}
        	}
        }
    }

	@Override
    public String getProxyQualifiedName()
    {
	    return IocProvider.class.getPackage().getName()+"."+getProxySimpleName();
    }

	@Override
	public String getProxySimpleName()
	{
		String className = view.getId()+"_"+device.toString(); 
		className = className.replaceAll("[\\W]", "_");
		return "IocContainer_"+className;
	}

	@Override
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = IocProvider.class.getPackage().getName();
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
		
		composerFactory.setSuperclass(IocContainer.class.getCanonicalName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
    }
	
	/**
	 * @return
	 */
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    	org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName(),	
    		GWT.class.getCanonicalName()
		};
	    return imports;
    }

	private static String getFieldInjectionExpression(JField field, String iocContainerVariable, Map<String, IocConfig<?>> configurations)
    {
		Inject inject = field.getAnnotation(Inject.class);
		if (inject != null)
		{
			JType fieldType = field.getType();
			if (!field.isStatic())
			{
				if (fieldType.isClassOrInterface() != null)
				{
					String fieldTypeName = fieldType.getQualifiedSourceName();
					IocConfigImpl<?> iocConfig = (IocConfigImpl<?>) configurations.get(fieldTypeName);
					if (iocConfig != null)
					{
						return iocContainerVariable+".get"+fieldTypeName.replace('.', '_')+
						"("+Scope.class.getCanonicalName()+"."+inject.scope().name()+", "+EscapeUtils.quote(inject.subscope())+")";
					}
					else
					{
						return "GWT.create("+fieldTypeName+".class)";
					}
				}
				else
				{
					throw new IoCException("Error injecting field ["+field.getName()+"] from type ["+field.getEnclosingType().getQualifiedSourceName()+"]. Primitive fields can not be handled by ioc container.");
				}
			}
			else
			{
				throw new IoCException("Error injecting field ["+field.getName()+"] from type ["+field.getEnclosingType().getQualifiedSourceName()+"]. Static fields can not be handled by ioc container.");
			}
		}
	    return null;
    }	

	private static String getParameterInjectionExpression(JParameter parameter, String iocContainerVariable, Map<String, IocConfig<?>> configurations)
    {
		JType parameterType = parameter.getType();
		if (parameterType.isClassOrInterface() != null)
		{
			String fieldTypeName = parameterType.getQualifiedSourceName();
			IocConfigImpl<?> iocConfig = (IocConfigImpl<?>) configurations.get(fieldTypeName);
			if (iocConfig != null)
			{
				Inject inject = getInjectAnnotation(parameter);
				return iocContainerVariable+".get"+fieldTypeName.replace('.', '_')+
				"("+Scope.class.getCanonicalName()+"."+inject.scope().name()+", "+EscapeUtils.quote(inject.subscope())+")";
			}
			else
			{
				return "GWT.create("+fieldTypeName+".class)";
			}
		}
		else
		{
			throw new IoCException("Error injecting parameter ["+parameter.getName()+"] from method ["+parameter.getEnclosingMethod().getReadableDeclaration()+"]. Primitive fields can not be handled by ioc container.");
		}
    }

	private static Inject getInjectAnnotation(JParameter parameter)
    {
		Inject result = parameter.getAnnotation(Inject.class);
		if (result == null)
		{
			result = parameter.getEnclosingMethod().getAnnotation(Inject.class);
		}
		return result;
    }	
}
