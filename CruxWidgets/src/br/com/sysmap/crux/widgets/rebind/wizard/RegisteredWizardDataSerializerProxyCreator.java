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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.widgets.client.wizard.RegisteredWizardDataSerializer;
import br.com.sysmap.crux.widgets.client.wizard.WizardDataSerializer;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredWizardDataSerializerProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	protected static WidgetGeneratorMessages widgetMessages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);
	private Map<String, String> dataSerializerClassNames = new HashMap<String, String>();
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredWizardDataSerializerProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredWizardDataSerializer.class.getCanonicalName()));
    }	
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		GWT.class.getCanonicalName(), 
	    		Crux.class.getCanonicalName(), 
	    		WizardDataSerializer.class.getCanonicalName() 
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
		generateGetWizardDataSerializerMethod(srcWriter);
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateSerializerClasses(srcWriter);
    }	

	/**
	 * @param sourceWriter
	 * @param dataSerializerClassNames
	 */
	private void generateGetWizardDataSerializerMethod(SourceWriter sourceWriter)
	{
		
		sourceWriter.println("public WizardDataSerializer<?> getWizardDataSerializer(String wizardDataId){");
		sourceWriter.indent();
		boolean first = true;
		for (String serializer : dataSerializerClassNames.keySet()) 
		{
			if (!first)
			{
				sourceWriter.print("else ");
			}
			sourceWriter.println("if (\""+serializer+"\".equals(wizardDataId)){");
			sourceWriter.indent();

			sourceWriter.println("return (WizardDataSerializer<?>)GWT.create("+dataSerializerClassNames.get(serializer)+".class);");

			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		sourceWriter.println("return null;");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param dataSerializerClassNames
	 * @param context 
	 */
	private void generateSerializerClasses(SourceWriter sourceWriter)
	{
		generateDefaultSerializerClass(sourceWriter);

		Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
		while (wizardDatas.hasNext())
		{
			generateSerializerClass(sourceWriter, wizardDatas.next());
		}		
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllersAdded
	 * @param context 
	 */
	private void generateDefaultSerializerClass(SourceWriter sourceWriter)
	{
		try
		{
			if (!dataSerializerClassNames.containsKey("string"))
			{
				String genClass = "Intf_string";
				sourceWriter.println("public static interface "+genClass+" extends WizardDataSerializer<String>{}");
				dataSerializerClassNames.put("string", genClass);
			}
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException(widgetMessages.errorGeneratingRegisteredWizardData("string", e.getLocalizedMessage()), e);
		}
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllersAdded
	 * @param context 
	 */
	private void generateSerializerClass(SourceWriter sourceWriter, String wizardData)
	{
		try
		{
			JClassType wizardDataClass = baseIntf.getOracle().findType(WizardDataObjects.getWizardData(wizardData));
			if (!dataSerializerClassNames.containsKey(wizardData) && wizardDataClass!= null)
			{
				String genClass = "Intf_"+wizardData;
				sourceWriter.println("public static interface "+genClass+" extends WizardDataSerializer<"
						+wizardDataClass.getParameterizedQualifiedSourceName()+">{}");
				dataSerializerClassNames.put(wizardData, genClass);
			}
		}
		catch (Exception e) 
		{
			throw new CruxGeneratorException(widgetMessages.errorGeneratingRegisteredWizardData(wizardData, e.getLocalizedMessage()), e);
		}
	}
}
