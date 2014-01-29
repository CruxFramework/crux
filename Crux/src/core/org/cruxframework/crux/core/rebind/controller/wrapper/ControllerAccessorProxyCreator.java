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
package org.cruxframework.crux.core.rebind.controller.wrapper;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.Target;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractViewBindableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ControllerAccessorProxyCreator extends AbstractViewBindableProxyCreator
{
	public ControllerAccessorProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter)
	{
		JType returnType = method.getReturnType();
		
		JClassType returnTypeClass = returnType.isClassOrInterface();
		if (returnTypeClass != null)
		{
			String controllerName = null;
			Target target = method.getAnnotation(Target.class);
			if (target != null) 
			{
				controllerName = target.value();
				String controller = ClientControllers.getController(controllerName, Device.valueOf(getDeviceFeatures()));
				JClassType controllerType = context.getTypeOracle().findType(controller);
				if (controllerType == null)
				{
					throw new CruxGeneratorException("Error generating method ["+method.getName()+"] from ControllerAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"]. Can not identify the controller type.");
				}
				if (!returnTypeClass.isAssignableFrom(controllerType))
				{
					throw new CruxGeneratorException("Error generating method ["+method.getName()+"] from ControllerAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"]. Controller type is not compatible with method return type.");
				}
			}
			else
			{
				Controller controller = returnTypeClass.getAnnotation(Controller.class);
				controllerName = controller.value();
			}
			if (controllerName != null)
			{
				if(method.getParameters().length == 0)
				{
						generateWrapperMethod(sourceWriter, returnTypeClass, controllerName, method.getName());
				}
				else
				{
					throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must have no parameters.");
				}
			}
			else
			{
				throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a class annotated with @Controller.");
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a class annotated with @Controller.");
		}
	}

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param controllerName
	 * @param methodName
	 */
	private void generateWrapperMethod(SourcePrinter sourceWriter, JClassType returnType, String controllerName, String methodName)
    {
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + methodName+"(){");
		sourceWriter.println(View.class.getCanonicalName()+" __view = "+View.class.getCanonicalName()+".getView(this.__view);");
		sourceWriter.println("assert(__view != null):"+EscapeUtils.quote("View was not loaded. Ensure that desired view is loaded by the application (through useView declaration).")+";");
		sourceWriter.println("return __view.getController("+EscapeUtils.quote(controllerName)+");");
		sourceWriter.println("}");
    }
}
