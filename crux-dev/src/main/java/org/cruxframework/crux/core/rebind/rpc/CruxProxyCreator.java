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
package org.cruxframework.crux.core.rebind.rpc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.shared.rpc.st.CruxSynchronizerTokenService;
import org.cruxframework.crux.core.shared.rpc.st.CruxSynchronizerTokenServiceAsync;
import org.cruxframework.crux.core.shared.rpc.st.SensitiveMethodAlreadyBeingProcessedException;
import org.cruxframework.crux.core.shared.rpc.st.UseSynchronizerToken;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.ProxyCreator;

/**
 * This class overrides the GWT Proxy Creator to add a wrapper class around the original generated class. 
 * 
 * <p>
 * The wrapper has two goals:<br>
 *  - Point all requests that does not inform an endPoint to the Crux FrontController Servlet.<br>
 *  - Handle security issues like SynchronizationToken for sensitive methods.  
 * </p>
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxProxyCreator extends ProxyCreator
{
	private static final String WRAPPER_SUFFIX = "_Wrapper";
	private boolean hasSyncTokenMethod = false;
	private TreeLogger logger;
	
	
	/**
	 * @param type
	 */
	public CruxProxyCreator(JClassType type)
	{
		super(type);
		this.hasSyncTokenMethod = hasSyncTokenMethod(type);
	}
	
	/**
	 * @see com.google.gwt.user.rebind.rpc.ProxyCreator#create(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext)
	 */
	@Override
	public RebindResult create(TreeLogger logger, GeneratorContext context)
			throws UnableToCompleteException
	{
		this.logger = logger;
		RebindResult result = super.create(logger, context);
		
		String asyncServiceTypeName = result.getResultTypeName();
		
		return createAsyncWrapper(context, asyncServiceTypeName);
	}

	/**
	 * @param fullClassName
	 * @return
	 */
	protected String[] getPackageAndClassName(String fullClassName) 
	{
		String className = fullClassName;
		String packageName = "";
		int index = -1;
		if ((index = className.lastIndexOf('.')) >= 0) 
		{
			packageName = className.substring(0, index);
			className = className.substring(index + 1, className.length());
		}
		return new String[] {packageName, className};
	}
	
	/**
	 * @see com.google.gwt.user.rebind.rpc.ProxyCreator#getRemoteServiceRelativePath()
	 */
	@Override
	protected String getRemoteServiceRelativePath()
	{
		String ret =  super.getRemoteServiceRelativePath();
		if (ret == null)
		{
			ret = "\"crux.rpc\"";
		}
		
		return ret;
	}

	private boolean checkAlreadyGenerated(GeneratorContext context, String className)
	{
		return context.getTypeOracle().findType(className) != null;
	}

	/**
	 * @param logger
	 * @param context
	 * @param asyncServiceTypeName
	 * @return
	 * @throws UnableToCompleteException 
	 */
	private RebindResult createAsyncWrapper(GeneratorContext context, String asyncServiceTypeName) throws UnableToCompleteException
	{
		JClassType serviceAsync = context.getTypeOracle().findType(serviceIntf.getQualifiedSourceName() + "Async");
		String asyncWrapperName = getProxyWrapperQualifiedName();

		if (checkAlreadyGenerated(context, asyncWrapperName))
		{
			return new RebindResult(RebindMode.USE_EXISTING, asyncWrapperName);
		}

		SourceWriter srcWriter = getSourceWriter(logger, context, asyncServiceTypeName, asyncWrapperName);
	    if (srcWriter == null) 
		{
			return new RebindResult(RebindMode.USE_EXISTING, asyncWrapperName);
		}

		generateWrapperProxyFields(srcWriter, asyncServiceTypeName);

		generateWrapperProxyContructor(srcWriter);

		generateProxyWrapperMethods(srcWriter, serviceAsync);
		
		if (this.hasSyncTokenMethod)
		{
			generateProxyWrapperStartMethod(srcWriter);

			generateProxyWrapperEndMethod(srcWriter);
			
			generateProxyWrapperUpdateTokenMethod(srcWriter);
			
			generateSetServiceEntryPointMethod(srcWriter);
		}
		
	    srcWriter.commit(logger);
	    
	    return new RebindResult(RebindMode.USE_ALL_NEW_WITH_NO_CACHING, asyncWrapperName);
	}

	/**
	 * 
	 * @param parameter
	 * @param methodDescVar 
	 * @param blocksScreen 
	 */
	private void generateAsyncCallbackForSyncTokenMethod(SourceWriter srcWriter, JParameter parameter, String methodDescVar, boolean blocksScreen)
	{
		JParameterizedType parameterizedType = parameter.getType().isParameterized();
		String typeSourceName = parameterizedType.getParameterizedQualifiedSourceName();
		JClassType[] typeArgs = parameterizedType.getTypeArgs();
		
		String typeParameterSourceName = typeArgs[0].getParameterizedQualifiedSourceName();
		
		srcWriter.println("new "+typeSourceName+"(){");
		srcWriter.indent();
		
		srcWriter.println("public void onSuccess("+typeParameterSourceName+" result){");
		srcWriter.indent();
		srcWriter.println("try{");
		srcWriter.println(parameter.getName()+".onSuccess(result);");
		srcWriter.println("}finally{");
		srcWriter.println("__endMethodCall("+methodDescVar+", "+blocksScreen+");");
		srcWriter.println("}");
		srcWriter.outdent();
		srcWriter.println("}");

		srcWriter.println("public void onFailure(Throwable caught){");
		srcWriter.indent();
		srcWriter.println("try{");
		srcWriter.println(parameter.getName()+".onFailure(caught);");
		srcWriter.println("}finally{");
		srcWriter.println("__endMethodCall("+methodDescVar+", "+blocksScreen+");");
		srcWriter.println("}");
		srcWriter.outdent();
		srcWriter.println("}");

		srcWriter.outdent();
		srcWriter.print("}");
	}
	
	/**
	 * @param srcWriter
	 * @param asyncMethod
	 * @param parameters
	 * @param methodDescVar
	 */
	private void generateProxyMethodCall(SourceWriter srcWriter, JMethod asyncMethod,
			List<JParameter> parameters, String methodDescVar, boolean blocksScreen)
	{
		
		srcWriter.print(getProxyWrapperQualifiedName()+".super."+asyncMethod.getName() + "(");
		boolean needsComma = false;
		for (int i = 0; i < parameters.size(); ++i)
		{
			JParameter parameter = parameters.get(i); 
			if (needsComma) 
			{
				srcWriter.print(", ");
			} 
			needsComma = true;
			if (i < (parameters.size()-1))
			{
				srcWriter.print(parameter.getName());
			}
			else
			{
				generateAsyncCallbackForSyncTokenMethod(srcWriter, parameter, methodDescVar, blocksScreen);
			}
		}
		srcWriter.println(");");
	}	
	
	/**
	 * @param srcWriter
	 */
	private void generateProxyWrapperEndMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("private void __endMethodCall(String methodDesc, boolean unblocksScreen){");
		srcWriter.indent();
		
		srcWriter.println("Boolean isProcessing = __syncProcessingMethods.remove(methodDesc);");
		srcWriter.println("if (isProcessing != null && !isProcessing){");
		srcWriter.indent();
		
		srcWriter.println("if (unblocksScreen) Screen.unblockToUser();");
		srcWriter.println("setServiceEntryPoint(__baseEntrypoint);");

		srcWriter.outdent();
		srcWriter.println("}");
		
		srcWriter.outdent();
		srcWriter.println("}");
	}

	/**
	 * @param srcWriter
	 * @param asyncMethod
	 * @throws UnableToCompleteException 
	 */
	private void generateProxyWrapperMethod(SourceWriter srcWriter, JMethod asyncMethod) throws UnableToCompleteException
	{
		try
		{
			JMethod syncMethod = getSyncMethodFromAsync(asyncMethod);
			
			if (syncMethod.getAnnotation(UseSynchronizerToken.class) != null)
			{
				JType asyncReturnType = asyncMethod.getReturnType().getErasedType();
				List<JParameter> parameters = generateProxyWrapperMethodDeclaration(srcWriter, asyncMethod, asyncReturnType);

				generateProxyWrapperMethodCall(srcWriter, syncMethod, asyncMethod, asyncReturnType, parameters);

				srcWriter.outdent();
				srcWriter.println("}");
			}
		}
		catch (NotFoundException e)
		{
			logger.log(TreeLogger.ERROR, "No method found on service interface that matches the async method ["+asyncMethod.getName()+"].");
		}
	}

	/**
	 * @param srcWriter
	 * @param asyncMethod
	 * @param asyncReturnType
	 * @param parameters
	 * @throws UnableToCompleteException 
	 */
	private void generateProxyWrapperMethodCall(SourceWriter srcWriter, JMethod syncMethod,
			JMethod asyncMethod, JType asyncReturnType, List<JParameter> parameters) throws UnableToCompleteException
	{
		if (asyncReturnType != JPrimitiveType.VOID) 
		{
			logger.log(TreeLogger.ERROR, "UseSynchronizer Token only can be used with void return type on Async interface.");
			throw new UnableToCompleteException();
		}
		UseSynchronizerToken synchronizerTokenAnnot = syncMethod.getAnnotation(UseSynchronizerToken.class);
		boolean blocksScreen = synchronizerTokenAnnot.blocksUserInteraction();
		JParameter parameter = parameters.get(parameters.size()-1);
		
		srcWriter.println("final String methodDesc = \""+JClassUtils.getMethodDescription(syncMethod)+"\";");
		srcWriter.println("if (__startMethodCall(methodDesc, "+blocksScreen+")){");
		srcWriter.indent();

		srcWriter.println("__syncTokenService.getSynchronizerToken(methodDesc,");
		srcWriter.println("new AsyncCallback<String>(){");
		srcWriter.indent();
		
		srcWriter.println("public void onSuccess(String result){");
		srcWriter.indent();
		srcWriter.println("__updateMethodToken(methodDesc, result);");
		generateProxyMethodCall(srcWriter, asyncMethod, parameters, "methodDesc", blocksScreen);
		srcWriter.outdent();
		srcWriter.println("}");

		srcWriter.println("public void onFailure(Throwable caught){");
		srcWriter.indent();
		srcWriter.println("try{");
		srcWriter.println(parameter.getName()+".onFailure(caught);");
		srcWriter.println("}finally{");
		srcWriter.println("__endMethodCall(methodDesc, "+blocksScreen+");");
		srcWriter.println("}");
		srcWriter.outdent();
		srcWriter.println("}");

		srcWriter.outdent();
		srcWriter.println("});");
		
		srcWriter.outdent();
		srcWriter.println("}");
		if (synchronizerTokenAnnot.notifyCallsWhenProcessing())
		{
			srcWriter.println("else{");
			srcWriter.indent();

			String sensitiveErrMsg = Crux.class.getName() + ".getMessages().methodIsAlreadyBeingProcessed()";
			srcWriter.println(Crux.class.getName()+".getErrorHandler().handleError("
					+ sensitiveErrMsg 
					+ ", new " + SensitiveMethodAlreadyBeingProcessedException.class.getName() + "(" + sensitiveErrMsg + ")" +
			");");
			
			srcWriter.outdent();
			srcWriter.println("}");
		}
	}

	/**
	 * @param srcWriter
	 * @param asyncMethod
	 * @param asyncReturnType
	 * @return
	 */
	private List<JParameter> generateProxyWrapperMethodDeclaration(SourceWriter srcWriter, 
			                                JMethod asyncMethod, JType asyncReturnType)
	{
		srcWriter.println();
		srcWriter.print("public ");
		srcWriter.print(asyncReturnType.getQualifiedSourceName());
		srcWriter.print(" ");
		srcWriter.print(asyncMethod.getName() + "(");

		boolean needsComma = false;
		List<JParameter> parameters = new ArrayList<JParameter>();
		JParameter[] asyncParams = asyncMethod.getParameters();
		for (int i = 0; i < asyncParams.length; ++i) 
		{
			JParameter param = asyncParams[i];

			if (needsComma) 
			{
				srcWriter.print(", ");
			} 
			else 
			{
				needsComma = true;
			}

			JType paramType = param.getType();
			if (i == (asyncParams.length-1))
			{
				srcWriter.print("final ");
			}
			srcWriter.print(paramType.getQualifiedSourceName());
			srcWriter.print(" ");

			String paramName = param.getName();
			parameters.add(param);
			srcWriter.print(paramName);
		}

		srcWriter.println(") {");
		srcWriter.indent();
		return parameters;
	}

	/**
	 * @param srcWriter
	 * @param serviceAsync
	 * @throws UnableToCompleteException 
	 */
	private void generateProxyWrapperMethods(SourceWriter srcWriter, JClassType serviceAsync) throws UnableToCompleteException 
	{
		JMethod[] asyncMethods = serviceAsync.getOverridableMethods();
		for (JMethod asyncMethod : asyncMethods) 
		{
			JClassType enclosingType = asyncMethod.getEnclosingType();
			JParameterizedType isParameterizedType = enclosingType.isParameterized();
			if (isParameterizedType != null) 
			{
				JMethod[] methods = isParameterizedType.getMethods();
				for (int i = 0; i < methods.length; ++i) 
				{
					if (methods[i] == asyncMethod) 
					{
						asyncMethod = isParameterizedType.getBaseType().getMethods()[i];
					}
				}
			}

			generateProxyWrapperMethod(srcWriter, asyncMethod);
		}
	}

	/**
	 * @param srcWriter
	 */
	private void generateProxyWrapperStartMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("private boolean __startMethodCall(String methodDesc, boolean blocksScreen){");
		srcWriter.indent();
		
		srcWriter.println("boolean ret = !__syncProcessingMethods.containsKey(methodDesc);");
		srcWriter.println("if (ret){");
		srcWriter.indent();
		srcWriter.println("__syncProcessingMethods.put(methodDesc, true);");
		srcWriter.println("if (blocksScreen) Screen.blockToUser();");
		srcWriter.outdent();
		srcWriter.println("}");
		
		srcWriter.println("return ret;");
		
		srcWriter.outdent();
		srcWriter.println("}");
	}

	/**
	 * @param srcWriter
	 */
	private void generateProxyWrapperUpdateTokenMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("private void __updateMethodToken(String methodDesc, String token){");
		srcWriter.indent();
		
		srcWriter.println("__syncProcessingMethods.put(methodDesc, false);");
		
		srcWriter.println("if (this.__hasParameters){");	
		srcWriter.indent();
		srcWriter.println("super.setServiceEntryPoint(__baseEntrypoint + \"&"+CruxSynchronizerTokenService.CRUX_SYNC_TOKEN_PARAM+"=\" + token);");
		srcWriter.outdent();
		srcWriter.println("}else{");	
		srcWriter.indent();
		srcWriter.println("super.setServiceEntryPoint(__baseEntrypoint + \"?"+CruxSynchronizerTokenService.CRUX_SYNC_TOKEN_PARAM+"=\" + token);");
		srcWriter.outdent();
		srcWriter.println("}");	
		
		srcWriter.outdent();
		srcWriter.println("}");
	}
	
	/**
	 * @param srcWriter
	 */
	private void generateSetServiceEntryPointMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("public void setServiceEntryPoint(String entryPoint){");
		srcWriter.indent();
		
		srcWriter.println("__baseEntrypoint = entryPoint;");
		srcWriter.println("super.setServiceEntryPoint(entryPoint);");
		
		srcWriter.outdent();
		srcWriter.println("}");
	}
	
	/**
	 * @param srcWriter
	 */
	private void generateWrapperProxyContructor(SourceWriter srcWriter) 
	{
		srcWriter.println("public " + getProxyWrapperSimpleName() + "() {");
		srcWriter.indent();

		srcWriter.println("super();");
		srcWriter.println("this.__hasParameters = (getServiceEntryPoint()!=null?getServiceEntryPoint().indexOf('?')>0:false);");
		if (this.hasSyncTokenMethod)
		{
			srcWriter.println("this.__baseEntrypoint = getServiceEntryPoint();");	
			srcWriter.println("this.__syncTokenService = (CruxSynchronizerTokenServiceAsync)GWT.create(CruxSynchronizerTokenService.class);");
		}
		srcWriter.println("String locale = Screen.getLocale();");
		srcWriter.println("if (locale != null && locale.trim().length() > 0){");
		srcWriter.indent();
		srcWriter.println("if (this.__hasParameters){");	
		srcWriter.indent();
		srcWriter.println("setServiceEntryPoint(getServiceEntryPoint() + \"&locale=\" + locale);");
		srcWriter.outdent();
		srcWriter.println("}else{");	
		srcWriter.indent();
		srcWriter.println("setServiceEntryPoint(getServiceEntryPoint() + \"?locale=\" + locale);");
		srcWriter.println("this.__hasParameters = true;");	
		srcWriter.outdent();
		srcWriter.println("}");	
		srcWriter.outdent();
		srcWriter.println("}");
		
		srcWriter.outdent();
		srcWriter.println("}");
	}
	
	
	/**
	 * @param srcWriter
	 * @param asyncServiceInterfaceName 
	 */
	private void generateWrapperProxyFields(SourceWriter srcWriter, String asyncServiceInterfaceName)
	{
		srcWriter.println("private boolean __hasParameters = false;");
		if (this.hasSyncTokenMethod)
		{
			srcWriter.println("private Map<String, Boolean> __syncProcessingMethods = new HashMap<String, Boolean>();");
			srcWriter.println("private CruxSynchronizerTokenServiceAsync __syncTokenService;");
			srcWriter.println("private String __baseEntrypoint;");
		}
	}
	
	/**
	 * @return
	 */
	private String getProxyWrapperQualifiedName()
	{
		return serviceIntf.getQualifiedSourceName()+ WRAPPER_SUFFIX;
	}

	/**
	 * @return
	 */
	private String getProxyWrapperSimpleName()
	{
		return serviceIntf.getSimpleSourceName()+WRAPPER_SUFFIX;
	}
	
	/**
	 * @param logger
	 * @param ctx
	 * @param asyncServiceName 
	 * @return
	 */
	private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, String asyncServiceName, String asyncWrapperName) 
	{
		String name[] = getPackageAndClassName(asyncWrapperName);
		String packageName = name[0];
		String className = name[1];
		PrintWriter printWriter = ctx.tryCreate(logger, packageName, className);
		if (printWriter == null) 
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
				packageName, className);

		composerFactory.addImport(Screen.class.getName());
		composerFactory.addImport(Map.class.getName());
		composerFactory.addImport(HashMap.class.getName());
		composerFactory.addImport(AsyncCallback.class.getName());
		if (this.hasSyncTokenMethod)
		{
			composerFactory.addImport(CruxSynchronizerTokenService.class.getName());
			composerFactory.addImport(CruxSynchronizerTokenServiceAsync.class.getName());
			composerFactory.addImport(GWT.class.getName());
		}
		
		composerFactory.setSuperclass(asyncServiceName);
		return composerFactory.createSourceWriter(ctx, printWriter);
	}	

	/**
	 * @param asyncMethod
	 * @return
	 * @throws NotFoundException 
	 */
	private JMethod getSyncMethodFromAsync(JMethod asyncMethod) throws NotFoundException
	{
		JParameter[] parameters = asyncMethod.getParameters();
		List<JType> syncParamTypes = new ArrayList<JType>();
		if (parameters != null && parameters.length > 1)
		{
			for (int i=0; i<parameters.length-1; i++)
			{
				JParameter jParameter = parameters[i];
				syncParamTypes.add(jParameter.getType());
			}
		}
		return serviceIntf.getMethod(asyncMethod.getName(), syncParamTypes.toArray(new JType[syncParamTypes.size()]));
	}
	
	/**
	 * @param type
	 * @return
	 */
	private boolean hasSyncTokenMethod(JClassType type)
	{
		JMethod[] methods = type.getOverridableMethods();
		
		for (JMethod jMethod : methods)
		{
			if (jMethod.getAnnotation(UseSynchronizerToken.class) != null)
			{
				return true;
			}
		}
		
		return false;
	}
}
