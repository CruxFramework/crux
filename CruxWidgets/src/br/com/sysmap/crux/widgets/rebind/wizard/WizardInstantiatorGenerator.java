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
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractRegisteredElementsGenerator;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;
import br.com.sysmap.crux.widgets.client.wizard.WizardInstantiator;
import br.com.sysmap.crux.widgets.client.wizard.WizardPage;
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
public class WizardInstantiatorGenerator extends AbstractRegisteredElementsGenerator
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
		composer.addImport(Wizard.class.getCanonicalName());
		composer.addImport(WizardPage.class.getCanonicalName());

		composer.addImplementedInterface(WizardInstantiator.class.getCanonicalName());

		SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);

		generateCreateWizardMethod(logger, sourceWriter, context);
		generateCreateWizardPageMethod(logger, sourceWriter, context);

		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param dataSerializerClassNames
	 * @param context 
	 */
	private void generateCreateWizardMethod(TreeLogger logger, SourceWriter sourceWriter, GeneratorContext context)
	{
		Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
		boolean first = true;
		sourceWriter.println("public Wizard<?> createWizard(String id, String wizardDataId){");
		sourceWriter.indent();

		while (wizardDatas.hasNext())
		{
			String wizardData = wizardDatas.next();
			Class<?> wizardDataClass = WizardDataObjects.getWizardData(wizardData);
			if (wizardDataClass!= null)
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				sourceWriter.println("if (\""+wizardData+"\".equals(wizardDataId)){");
				sourceWriter.indent();
				sourceWriter.println("return new Wizard<"+getClassSourceName(wizardDataClass)+">(id, wizardDataId);");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
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
	private void generateCreateWizardPageMethod(TreeLogger logger, SourceWriter sourceWriter, GeneratorContext context)
	{
		Iterator<String> wizardDatas = WizardDataObjects.iterateWizardDatas();
		boolean first = true;
		sourceWriter.println("public WizardPage<?> createWizardPage(String wizardId, String wizardDataId){");
		sourceWriter.indent();

		while (wizardDatas.hasNext())
		{
			String wizardData = wizardDatas.next();
			Class<?> wizardDataClass = WizardDataObjects.getWizardData(wizardData);
			if (wizardDataClass!= null)
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				sourceWriter.println("if (\""+wizardData+"\".equals(wizardDataId)){");
				sourceWriter.indent();
				sourceWriter.println("return new WizardPage<"+getClassSourceName(wizardDataClass)+">(wizardId, wizardDataId);");
				sourceWriter.outdent();
				sourceWriter.println("}");
			}
		}		
		sourceWriter.println("return null;");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
}
