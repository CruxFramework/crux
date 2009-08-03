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

import java.lang.reflect.Method;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ScreenWrapperGenerator extends AbstractInterfaceWrapperGenerator
{
	protected void generateMethodWrapper(TreeLogger logger, Method method, SourceWriter sourceWriter)
	{
		Class<?> returnType = method.getReturnType();
		String name = method.getName();
		if (name.startsWith("get") && Widget.class.isAssignableFrom(returnType) && method.getParameterTypes().length == 0)
		{
			String widgetName = name.substring(3);
			if (widgetName.length() > 0)
			{
				widgetName = Character.toLowerCase(widgetName.charAt(0)) + widgetName.substring(1);
				String classSourceName = getClassSourceName(returnType);
				sourceWriter.println("public "+classSourceName+" " + name+"(){");
				sourceWriter.println("return Screen.get(\""+widgetName+"\", "+classSourceName+".class);");
				sourceWriter.println("}");
			}
		}
	}
}
