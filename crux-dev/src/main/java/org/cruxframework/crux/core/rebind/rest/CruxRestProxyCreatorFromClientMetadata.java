/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.rest;

import java.lang.annotation.Annotation;

import org.cruxframework.crux.core.client.rest.RestProxy.TargetEndPoint;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.POST;
import org.cruxframework.crux.core.shared.rest.annotation.PUT;
import org.cruxframework.crux.core.shared.rest.annotation.Path;
import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxRestProxyCreatorFromClientMetadata extends CruxRestProxyCreator
{
	public CruxRestProxyCreatorFromClientMetadata(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
    protected String getServiceBasePath(GeneratorContext context)
    {
		String value = baseIntf.getAnnotation(Path.class).value();
		if (value == null)
		{
			value = "";
		}
		else if (!value.startsWith("/"))
		{
			value = "/"+value;
		}
		
		return value;
    }

	@Override
    protected void generateHostPathInitialization(SourcePrinter srcWriter)
    {
		TargetEndPoint targetEndPoint = baseIntf.getAnnotation(TargetEndPoint.class);
	    String basePath = targetEndPoint != null? targetEndPoint.value() : "";
		if (basePath.endsWith("/"))
		{
			basePath = basePath.substring(0, basePath.length()-1);
		}
		
	    srcWriter.println("__hostPath = \""+basePath+"\";");
    }

	@Override
    protected RestMethodInfo getRestMethodInfo(JMethod method) throws InvalidRestMethod
    {
		JParameter[] parameters = method.getParameters();
		Annotation[][] parameterAnnotations = new Annotation[parameters.length-1][];

		for (int i=0; i < parameters.length -1; i++)
        {
			parameterAnnotations[i] = parameters[i].getAnnotations();
        }
		
	    String methodURI = getRestURI(method, parameterAnnotations, method.getAnnotation(Path.class));
	    String httpMethod = HttpMethodHelper.getHttpMethod(method.getAnnotations(), false);
	    StateValidationModel validationModel = getStateValidationModel(method);
	    boolean isReadMethod = method.getAnnotation(GET.class) != null;
	    if (isReadMethod)
	    {
	    	readMethods.add(methodURI);
	    }
	    else if (validationModel != null && !validationModel.equals(StateValidationModel.NO_VALIDATE))
	    {
	    	updateMethods.add(methodURI);
	    }
	    
	    RestMethodInfo methodInfo = new RestMethodInfo(method, parameterAnnotations, methodURI, httpMethod, validationModel, isReadMethod);
	    return methodInfo;
    }
	
	private StateValidationModel getStateValidationModel(JMethod method)
    {
		PUT put = method.getAnnotation(PUT.class);
		if (put != null)
		{
			return put.validatePreviousState();
		}
		POST post = method.getAnnotation(POST.class);
		if (post != null)
		{
			return post.validatePreviousState();
		}

	    return null;
    }
}
