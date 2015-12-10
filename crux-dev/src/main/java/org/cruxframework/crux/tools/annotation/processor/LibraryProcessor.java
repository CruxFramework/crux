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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import org.cruxframework.crux.core.annotation.processor.CruxAnnotationProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@SupportedAnnotationTypes("org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory")
@SupportedOptions(
	{
		CruxAnnotationProcessor.CRUX_RUN_APT,
		CruxAnnotationProcessor.CRUX_APT_INCREMENTAL
	})
public class LibraryProcessor extends CruxAnnotationProcessor
{
	private static final String CRUX_WIDGETS_FACTORY_MAP_FILE = "META-INF/crux-widgets-factory";
	private static final String CRUX_WIDGETS_TYPE_MAP_FILE = "META-INF/crux-widgets-type";
	private boolean previousSourcesLoaded = false;
	private Properties widgetFactoryMap;
	private Properties widgetTypeMap;

	public LibraryProcessor()
	{
		widgetFactoryMap = new Properties();
		widgetTypeMap = new Properties();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		if(!runAPT())
		{
			return true;
		}
		
		try
		{
			boolean incremental = isIncremental();
			if (incremental && !previousSourcesLoaded)
			{
				loadPreviousMapFiles();
			}
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

					removePreviousEntryForCurrentClass(incremental, widgetFactoryClassName);
					
					widgetFactoryMap.put(widgetType, widgetFactoryClassName);
					widgetTypeMap.put(targetWidget, widgetType);
				}
			}

			if (roundEnv.processingOver())
			{
				FileObject cruxWidgetFactoryFile = createResourceFile(CRUX_WIDGETS_FACTORY_MAP_FILE);
				OutputStream outputStream = cruxWidgetFactoryFile.openOutputStream();
				widgetFactoryMap.store(outputStream, "Crux Widget Factories mapping");
				outputStream.close();

				FileObject cruxWidgetTypeFile = createResourceFile(CRUX_WIDGETS_TYPE_MAP_FILE);
				outputStream = cruxWidgetTypeFile.openOutputStream();
				widgetTypeMap.store(outputStream, "Crux Widget Types mapping");
				outputStream.close();
			}
		}
		catch (Exception e)
		{
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}

		return true;
	}

	private void loadPreviousMapFiles() throws IOException
    {
	    InputStream inputStream = getResourceFileStream(CRUX_WIDGETS_FACTORY_MAP_FILE);
	    if (inputStream != null)
	    {
	    	widgetFactoryMap.load(inputStream);
	    	inputStream.close();
	    }

	    inputStream = getResourceFileStream(CRUX_WIDGETS_TYPE_MAP_FILE);
	    if (inputStream != null)
	    {
	    	widgetTypeMap.load(inputStream);
	    	inputStream.close();
	    }
	    previousSourcesLoaded = true;
    }

	private void removePreviousEntryForCurrentClass(boolean incremental, String widgetFactoryClassName)
    {
	    if (incremental && widgetFactoryMap.containsValue(widgetFactoryClassName))
	    {
	    	for (Entry<Object, Object> entry: widgetFactoryMap.entrySet())
	    	{
	    		if (entry.getValue().equals(widgetFactoryClassName))
	    		{
	    			Object previousWidgetType = entry.getKey();
	    			widgetFactoryMap.remove(previousWidgetType);
	    			for (Entry<Object, Object> entryTypes: widgetTypeMap.entrySet())
	    			{
	    				if (entryTypes.getValue().equals(previousWidgetType))
	    				{
	    					widgetTypeMap.remove(entryTypes.getKey());
	    					break;
	    				}
	    			}
	    			break;
	    		}
	    	}
	    }
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
