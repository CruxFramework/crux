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
package br.com.sysmap.crux.widgets.rebind.wizard;

import java.util.Iterator;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;
import br.com.sysmap.crux.widgets.client.wizard.WizardInstantiator;
import br.com.sysmap.crux.widgets.client.wizard.WizardPage;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WizardInstantiatorProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	protected static WidgetGeneratorMessages widgetMessages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public WizardInstantiatorProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(WizardInstantiator.class.getCanonicalName()));
    }	
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		GWT.class.getCanonicalName(), 
	    		Crux.class.getCanonicalName(), 
	    		Wizard.class.getCanonicalName(), 
	    		WizardPage.class.getCanonicalName() 
			};
		    return imports;  
    }

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateCreateWizardMethod(srcWriter);
		generateCreateWizardPageMethod(srcWriter);
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @param sourceWriter
	 */
	private void generateCreateWizardMethod(SourceWriter sourceWriter)
	{
		try
        {
	        Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
	        sourceWriter.println("public Wizard<?> createWizard(String id, String wizardDataId){");
	        sourceWriter.indent();

	        sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".isEmpty(wizardDataId)){");
	        sourceWriter.indent();
	        sourceWriter.println("return new Wizard<String>(id, \"string\");");
	        sourceWriter.outdent();
	        sourceWriter.println("}");

	        while (wizardDatas.hasNext())
	        {
	        	String wizardData = wizardDatas.next();
	        	JClassType wizardDataClass = baseIntf.getOracle().getType(WizardDataObjects.getWizardData(wizardData));
	        	if (wizardDataClass!= null)
	        	{
	        		sourceWriter.println("else if (\""+wizardData+"\".equals(wizardDataId)){");
	        		sourceWriter.indent();
	        		sourceWriter.println("return new Wizard<"+wizardDataClass.getParameterizedQualifiedSourceName()+">(id, wizardDataId);");
	        		sourceWriter.outdent();
	        		sourceWriter.println("}");
	        	}
	        }		
	        sourceWriter.println("return new Wizard<java.io.Serializable>(id, wizardDataId);");
	        sourceWriter.outdent();
	        sourceWriter.println("}");
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @param sourceWriter
	 */
	private void generateCreateWizardPageMethod(SourceWriter sourceWriter)
	{
		try
        {
	        Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
	        sourceWriter.println("public WizardPage<?> createWizardPage(String wizardId, String wizardDataId){");
	        sourceWriter.indent();

	        sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".isEmpty(wizardDataId)){");
	        sourceWriter.indent();
	        sourceWriter.println("return new WizardPage<String>(wizardId, \"string\");");
	        sourceWriter.outdent();
	        sourceWriter.println("}");

	        while (wizardDatas.hasNext())
	        {
	        	String wizardData = wizardDatas.next();
	        	JClassType wizardDataClass = baseIntf.getOracle().getType(WizardDataObjects.getWizardData(wizardData));
	        	if (wizardDataClass!= null)
	        	{
	        		sourceWriter.println("else if (\""+wizardData+"\".equals(wizardDataId)){");
	        		sourceWriter.indent();
	        		sourceWriter.println("return new WizardPage<"+wizardDataClass.getParameterizedQualifiedSourceName()+">(wizardId, wizardDataId);");
	        		sourceWriter.outdent();
	        		sourceWriter.println("}");
	        	}
	        }		
	        sourceWriter.println("return new WizardPage<java.io.Serializable>(wizardId, wizardDataId);");
	        sourceWriter.outdent();
	        sourceWriter.println("}");
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
}
