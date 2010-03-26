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
package br.com.sysmap.crux.core.rebind.rpc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.rpc.st.CruxSynchronizerTokenService;
import br.com.sysmap.crux.core.client.rpc.st.CruxSynchronizerTokenServiceAsync;
import br.com.sysmap.crux.core.client.rpc.st.UseSynchronizerToken;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
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
 * @author Thiago da Rosa de Bustamante - <code>thiago@sysmap.com.br</code>
 *
 */
public class CruxProxyCreator extends ProxyCreator
{
	private static final String WRAPPER_SUFFIX = "_Wrapper";
	private static final String WAITING_TOKEN = "waitingToken";
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
	public String create(TreeLogger logger, GeneratorContext context)
			throws UnableToCompleteException
	{
		this.logger = logger;
		String asyncServiceTypeName = super.create(logger, context);
		
		return createAsyncWrapper(context, asyncServiceTypeName);
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
	
	/**
	 * @param logger
	 * @param context
	 * @param asyncServiceTypeName
	 * @return
	 * @throws UnableToCompleteException 
	 */
	private String createAsyncWrapper(GeneratorContext context, String asyncServiceTypeName) throws UnableToCompleteException
	{
		JClassType serviceAsync = context.getTypeOracle().findType(serviceIntf.getQualifiedSourceName() + "Async");
		SourceWriter srcWriter = getSourceWriter(logger, context, asyncServiceTypeName);

		String asyncWrapperName = getProxyWrapperQualifiedName();
		if (srcWriter == null) 
		{
			return asyncWrapperName;
		}

		generateWrapperProxyFields(srcWriter, asyncServiceTypeName);

		generateWrapperProxyContructor(srcWriter);

		generateProxyWrapperMethods(srcWriter, serviceAsync);
		
		if (this.hasSyncTokenMethod)
		{
			generateProxyWrapperStartMethod(srcWriter);

			generateProxyWrapperEndMethod(srcWriter);
			
			generateProxyWrapperUpdateTokenMethod(srcWriter);
		}
		
	    srcWriter.commit(logger);
	    
		return asyncWrapperName;
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
			srcWriter.println("private Map<String, String> __syncProcessingMethods = new HashMap<String, String>();");
			srcWriter.println("private CruxSynchronizerTokenServiceAsync __syncTokenService;");
		}
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
	 * @param asyncMethod
	 * @throws UnableToCompleteException 
	 */
	private void generateProxyWrapperMethod(SourceWriter srcWriter, JMethod asyncMethod) throws UnableToCompleteException
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
			paramType = paramType.getErasedType();
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
		
		srcWriter.println("final String methodDesc = \""+ClassUtils.getMethodDescription(syncMethod)+"\";");
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

			srcWriter.println(Crux.class.getName()+".getErrorHandler().handleError("+Crux.class.getName()+".getMessages().methodIsAlreadyBeingProcessed());");
			
			srcWriter.outdent();
			srcWriter.println("}");
		}
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
	 */
	private void generateProxyWrapperStartMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("private boolean __startMethodCall(String methodDesc, boolean blocksScreen){");
		srcWriter.indent();
		
		srcWriter.println("boolean ret = !__syncProcessingMethods.containsKey(methodDesc);");
		srcWriter.println("if (ret){");
		srcWriter.indent();
		srcWriter.println("__syncProcessingMethods.put(methodDesc, \""+WAITING_TOKEN+"\");");
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
	private void generateProxyWrapperEndMethod(SourceWriter srcWriter)
	{
		srcWriter.println();
		srcWriter.println("private void __endMethodCall(String methodDesc, boolean unblocksScreen){");
		srcWriter.indent();
		
		srcWriter.println("if (unblocksScreen) Screen.unblockToUser();");
		srcWriter.println("String previousEntryPoint = __syncProcessingMethods.remove(methodDesc);");
		srcWriter.println("if (previousEntryPoint != null && !previousEntryPoint.equals(\""+WAITING_TOKEN+"\")){");
		srcWriter.indent();
		
		srcWriter.println("setServiceEntryPoint(previousEntryPoint);");

		srcWriter.outdent();
		srcWriter.println("}");
		
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
		
		srcWriter.println("__syncProcessingMethods.put(methodDesc, getServiceEntryPoint());");
		
		srcWriter.println("if (this.__hasParameters){");	
		srcWriter.indent();
		srcWriter.println("setServiceEntryPoint(getServiceEntryPoint() + \"&"+CruxSynchronizerTokenService.CRUX_SYNC_TOKEN_PARAM+"=\" + token);");
		srcWriter.outdent();
		srcWriter.println("}else{");	
		srcWriter.indent();
		srcWriter.println("setServiceEntryPoint(getServiceEntryPoint() + \"?"+CruxSynchronizerTokenService.CRUX_SYNC_TOKEN_PARAM+"=\" + token);");
		srcWriter.outdent();
		srcWriter.println("}");	
		
		srcWriter.outdent();
		srcWriter.println("}");
	}
	
	/**
	 * @param asyncMethod
	 * @return
	 * @throws UnableToCompleteException 
	 */
	private JMethod getSyncMethodFromAsync(JMethod asyncMethod) throws UnableToCompleteException
	{
		JParameter[] parameters = asyncMethod.getParameters();
		List<JType> syncParamTypes = new ArrayList<JType>();
		if (parameters != null && parameters.length > 1)
		{
			for (int i=0; i<parameters.length-1; i++)
			{
				JParameter jParameter = parameters[i];
				syncParamTypes.add(jParameter.getType().getErasedType());
			}
		}
		try
		{
			return serviceIntf.getMethod(asyncMethod.getName(), syncParamTypes.toArray(new JType[syncParamTypes.size()]));
		}
		catch (NotFoundException e)
		{
			throw new UnableToCompleteException();
		}
	}
	
	/**
	 * @return
	 */
	private String getProxyWrapperSimpleName()
	{
		return serviceIntf.getSimpleSourceName()+WRAPPER_SUFFIX;
	}

	/**
	 * @return
	 */
	private String getProxyWrapperQualifiedName()
	{
		return serviceIntf.getQualifiedSourceName()+ WRAPPER_SUFFIX;
	}
	
	/**
	 * @param logger
	 * @param ctx
	 * @param asyncServiceName 
	 * @return
	 */
	private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, String asyncServiceName) 
	{
		String name[] = getPackageAndClassName(getProxyWrapperQualifiedName());
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
	 * @param fullClassName
	 * @return
	 */
	private String[] getPackageAndClassName(String fullClassName) 
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
	 * @param type
	 * @return
	 */
	private boolean hasSyncTokenMethod(JClassType type)
	{
		JMethod[] methods = type.getMethods();
		
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
