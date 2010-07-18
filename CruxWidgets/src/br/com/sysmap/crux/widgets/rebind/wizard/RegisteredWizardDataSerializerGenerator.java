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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractRegisteredElementsGenerator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.widgets.client.wizard.RegisteredWizardDataSerializer;
import br.com.sysmap.crux.widgets.client.wizard.WizardDataSerializer;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredWizardDataSerializerGenerator extends AbstractRegisteredElementsGenerator
{
	protected static WidgetGeneratorMessages widgetMessages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractRegisteredElementsGenerator#generateClass(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext, com.google.gwt.core.ext.typeinfo.JClassType, java.util.List)
	 */
	@Override
    protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, List<Screen> screens)
    {
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImport(GWT.class.getCanonicalName());
		composer.addImport(Crux.class.getCanonicalName());
		composer.addImport(WizardDataSerializer.class.getCanonicalName());

		composer.addImplementedInterface(RegisteredWizardDataSerializer.class.getCanonicalName());

		SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);

		Map<String, String> dataSerializerClassNames = new HashMap<String, String>();

		generateSerializerClasses(logger, sourceWriter, dataSerializerClassNames, context);
		generateGetWizardDataSerializerMethod(sourceWriter, dataSerializerClassNames);

		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}


	/**
	 * @param sourceWriter
	 * @param dataSerializerClassNames
	 */
	private void generateGetWizardDataSerializerMethod(SourceWriter sourceWriter, Map<String, String> dataSerializerClassNames)
	{
		
		sourceWriter.println("WizardDataSerializer<?> getWizardDataSerializer(String wizardDataId){");
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
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param dataSerializerClassNames
	 * @param context 
	 */
	private void generateSerializerClasses(TreeLogger logger, SourceWriter sourceWriter,
			Map<String, String> dataSerializerClassNames, GeneratorContext context)
	{
		Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
		while (wizardDatas.hasNext())
		{
			generateSerializerClass(logger, sourceWriter, wizardDatas.next(), dataSerializerClassNames, context);
		}		
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllersAdded
	 * @param context 
	 */
	private void generateSerializerClass(TreeLogger logger, SourceWriter sourceWriter, String wizardData, 
			Map<String, String> wizardDataAdded, GeneratorContext context)
	{
		try
		{
			Class<?> wizardDataClass = WizardDataObjects.getWizardData(wizardData);
			if (!wizardDataAdded.containsKey(wizardData) && wizardDataClass!= null)
			{
				String genClass = "Intf_"+wizardData;
				sourceWriter.println("public static interface "+genClass+" extends WizardDataSerializer<"
						+getClassSourceName(wizardDataClass)+">{}");
				wizardDataAdded.put(wizardData, genClass);
			}
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException(widgetMessages.errorGeneratingRegisteredWizardData(wizardData, e.getLocalizedMessage()), e);
		}
	}
}
