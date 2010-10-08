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
package br.com.sysmap.crux.gadget.rebind.rpc;

import java.io.PrintWriter;

import br.com.sysmap.crux.core.rebind.rpc.CruxProxyCreator;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.gadgets.client.gwtrpc.GadgetsGwtRpc;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * This class overrides the Crux Proxy Creator to add a wrapper class around the original generated class. 
 * 
 * <p>
 * The wrapper redirects the original request through a proxy, as described on:<br>
 *  http://code.google.com/p/gwt-google-apis/wiki/GadgetsFAQ#How_can_I_get_GWT_RPC_to_work_in_a_Gadget?
 * </p>
 * 
 * 
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class CruxGadgetProxyCreator extends CruxProxyCreator
{
	private static final String WRAPPER_SUFFIX = "_GadgetWrapper";
	private TreeLogger logger;
	
	
	/**
	 * @param type
	 */
	public CruxGadgetProxyCreator(JClassType type)
	{
		super(type);
	}
	
	/**
	 * @see com.google.gwt.user.rebind.rpc.ProxyCreator#create(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext)
	 */
	@Override
	public String create(TreeLogger logger, GeneratorContext context)
			throws UnableToCompleteException
	{
		this.logger = logger;
		String cruxServiceTypeName = super.create(logger, context);
		
		return createGadgetWrapper(context, cruxServiceTypeName);
	}

	/**
	 * @param logger
	 * @param context
	 * @param cruxServiceTypeName
	 * @return
	 * @throws UnableToCompleteException 
	 */
	private String createGadgetWrapper(GeneratorContext context, String cruxServiceTypeName) throws UnableToCompleteException
	{
		SourceWriter srcWriter = getSourceWriter(logger, context, cruxServiceTypeName);

		String gadgetWrapperName = getGadgetProxyQualifiedName();
		if (srcWriter == null) 
		{
			return gadgetWrapperName;
		}

		generateGadgetProxyContructor(srcWriter);

	    srcWriter.commit(logger);
	    
		return gadgetWrapperName;
	}
	
	/**
	 * @param srcWriter
	 */
	private void generateGadgetProxyContructor(SourceWriter srcWriter)
	{
		srcWriter.println("public " + getGadgetProxySimpleName() + "() {");
		srcWriter.indent();

		srcWriter.println("super();");
		srcWriter.println("GadgetsGwtRpc.redirectThroughProxy((ServiceDefTarget) this);");
		
		srcWriter.outdent();
		srcWriter.println("}");
		
	}

	/**
	 * @param logger
	 * @param ctx
	 * @param cruxServiceTypeName 
	 * @return
	 */
	private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, String cruxServiceTypeName) 
	{
		String name[] = getPackageAndClassName(getGadgetProxyQualifiedName());
		String packageName = name[0];
		String className = name[1];
		PrintWriter printWriter = ctx.tryCreate(logger, packageName, className);
		if (printWriter == null) 
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);
		
		composerFactory.addImport(GadgetsGwtRpc.class.getCanonicalName());
		composerFactory.addImport(ServiceDefTarget.class.getCanonicalName());
		
		composerFactory.setSuperclass(cruxServiceTypeName);

		return composerFactory.createSourceWriter(ctx, printWriter);
	}
	
	/**
	 * @return
	 */
	private String getGadgetProxyQualifiedName()
	{
		return serviceIntf.getQualifiedSourceName()+ WRAPPER_SUFFIX;
	}
	
	/**
	 * @return
	 */
	private String getGadgetProxySimpleName()
	{
		return ClassUtils.getSourceName(serviceIntf)+WRAPPER_SUFFIX;
	}
}
