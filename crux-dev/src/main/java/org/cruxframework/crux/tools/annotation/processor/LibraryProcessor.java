/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.tools.annotation.processor;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@SupportedAnnotationTypes("org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory")
public class LibraryProcessor extends CruxAnnotationProcessor
{
	private Properties cruxWidgetFactoryMap;
	private Properties cruxWidgetTypeMap;

	public LibraryProcessor()
	{
		cruxWidgetFactoryMap = new Properties();
		cruxWidgetTypeMap = new Properties();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		try
		{
			for (Element elem : roundEnv.getElementsAnnotatedWith(DeclarativeFactory.class))
			{
				DeclarativeFactory factoryAnnotation = elem.getAnnotation(DeclarativeFactory.class);
				if (elem.getKind() == ElementKind.CLASS)
				{
					String id = factoryAnnotation.id();
					String library = factoryAnnotation.library();
					String widgetType = library + "_" + id;
					String targetWidget = getTargetWidgetType(factoryAnnotation).toString();
					String widgetFactoryClassName = ((TypeElement) elem).getQualifiedName().toString();

					cruxWidgetFactoryMap.put(widgetType, widgetFactoryClassName);
					cruxWidgetTypeMap.put(targetWidget, widgetType);
				}
			}

			if (roundEnv.processingOver())
			{
				FileObject cruxWidgetFactoryFile = getResourceFile("META-INF/crux-widgets-factory");
				cruxWidgetFactoryMap.store(cruxWidgetFactoryFile.openOutputStream(), "Crux Widget Factories mapping");

				FileObject cruxWidgetTypeFile = getResourceFile("META-INF/crux-widgets-type");
				cruxWidgetTypeMap.store(cruxWidgetTypeFile.openOutputStream(), "Crux Widget Types mapping");
			}
		}
		catch (IOException e)
		{
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}

		return true;
	}

	/**
	 * Hacking to access the TypeMirror from an annotation 
	 * @param factoryAnnotation
	 * @return
	 */
	private static TypeMirror getTargetWidgetType(DeclarativeFactory factoryAnnotation)
	{
		try
		{
			factoryAnnotation.targetWidget(); // this should throw
		}
		catch (MirroredTypeException mte)
		{
			return mte.getTypeMirror();
		}
		return null; // can this ever happen ??
	}
}
