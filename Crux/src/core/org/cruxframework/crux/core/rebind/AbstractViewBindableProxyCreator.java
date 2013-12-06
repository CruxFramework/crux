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
package org.cruxframework.crux.core.rebind;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.BindView;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.client.screen.views.ViewBindable;
import org.cruxframework.crux.core.client.utils.EscapeUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractViewBindableProxyCreator extends AbstractWrapperProxyCreator
{
	private JClassType viewBindableType;
	private JClassType viewAwareType;

	public AbstractViewBindableProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    this(logger, context, baseIntf, true);
    }

	public AbstractViewBindableProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf, boolean cacheable)
    {
	    super(logger, context, baseIntf, cacheable);
	    viewBindableType = context.getTypeOracle().findType(ViewBindable.class.getCanonicalName());
	    viewAwareType = context.getTypeOracle().findType(ViewAware.class.getCanonicalName());
    }

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		super.generateProxyFields(srcWriter);
		srcWriter.println("private String __view;");
	}
	
	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		generateViewIdentificationBlock(srcWriter, baseIntf);
		srcWriter.println("}");
	}

	private boolean generateViewIdentificationBlock(SourcePrinter srcWriter, JClassType type)
    {
	    BindRootView bindRootView = type.getAnnotation(BindRootView.class);
	    BindView bindView = type.getAnnotation(BindView.class);

	    boolean ret = false;
	    if (bindRootView != null && bindView != null)
	    {
	    	throw new CruxGeneratorException("ViewBindable class ["+baseIntf.getQualifiedSourceName()+"] can be annotated with BindView or with BindRootView, but not with both...");
	    }
	    if (bindRootView != null)
	    {
	    	srcWriter.println("this.__view = "+Screen.class.getCanonicalName()+".getRootView().getId();");
	    	ret = true;
	    }
	    else if (bindView != null)
	    {
	    	srcWriter.println("this.__view = "+EscapeUtils.quote(bindView.value())+";");
	    	ret = true;
	    }
	    else
	    {
	    	JClassType[] interfaces = type.getImplementedInterfaces();
	    	if (interfaces != null)
	    	{
	    		for (JClassType intf : interfaces)
                {
	                ret = generateViewIdentificationBlock(srcWriter, intf);
	                if (ret)
	                {
	                	break;
	                }
                }
	    	}
	    }
	    return ret;
    }
	
	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		super.generateProxyMethods(srcWriter);
	    generateViewBindableMethods(srcWriter);
    }
	
	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter, JClassType clazz) throws CruxGeneratorException
    {
    	JMethod[] methods = clazz.getOverridableMethods();
    	for (JMethod method : methods)
    	{
    		if (method.getEnclosingType().equals(viewBindableType) || method.getEnclosingType().equals(viewAwareType))
    		{
    			continue;
    		}
    		generateWrapperMethod(method, srcWriter);
    	}
    }
    
	protected void generateViewBindableMethods(SourcePrinter sourceWriter)
    {
		sourceWriter.println("public String getBoundCruxViewId(){");
		sourceWriter.println("return this.__view;");
		sourceWriter.println("}");
		sourceWriter.println();
		sourceWriter.println("public "+View.class.getCanonicalName()+" getBoundCruxView(){");
		sourceWriter.println("return (this.__view!=null?"+View.class.getCanonicalName()+".getView(this.__view):null);");
		sourceWriter.println("}");
		sourceWriter.println();
		sourceWriter.println("public void bindCruxView(String view){");
		sourceWriter.println("this.__view = view;");
		sourceWriter.println("}");
		sourceWriter.println();
    }
	
	protected void generateViewGetterMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public IsWidget _getFromView(String widgetName){");
		srcWriter.println(View.class.getCanonicalName()+" __view = "+View.class.getCanonicalName()+".getView(this.__view);");
		srcWriter.println("assert (__view != null):"+EscapeUtils.quote("No view loaded with desired identifier.")+";");
		srcWriter.println("IsWidget ret = __view.getWidget(widgetName);");
		srcWriter.println("if (ret == null){");
		srcWriter.println("String widgetNameFirstUpper;");
		srcWriter.println("if (widgetName.length() > 1){"); 
		srcWriter.println("widgetNameFirstUpper = Character.toUpperCase(widgetName.charAt(0)) + widgetName.substring(1);");
		srcWriter.println("}");
		srcWriter.println("else{"); 
		srcWriter.println("widgetNameFirstUpper = \"\"+Character.toUpperCase(widgetName.charAt(0));");
		srcWriter.println("}");
		srcWriter.println("ret = __view.getWidget(widgetNameFirstUpper);");
		srcWriter.println("}");
		srcWriter.println("return ret;");
		srcWriter.println("}");
	}
}
