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
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import org.cruxframework.crux.core.server.rest.annotation.RestService;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@SupportedAnnotationTypes("org.cruxframework.crux.core.server.rest.annotation.RestService")
public class RestServiceProcessor extends CruxAnnotationProcessor
{
	private Properties cruxRestMap;

	public RestServiceProcessor()
	{
		cruxRestMap = new Properties();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		try
		{
			//				cruxRestMap.load(cruxRestFile.openInputStream());
			for (Element elem : roundEnv.getElementsAnnotatedWith(RestService.class))
			{
				RestService restService = elem.getAnnotation(RestService.class);
				if (elem.getKind() == ElementKind.CLASS) 
				{
					String restServiceAlias = restService.value();
					String restServiceClassName = ((TypeElement) elem).getQualifiedName().toString();

					cruxRestMap.put(restServiceAlias, restServiceClassName);
				}
			}

			if (roundEnv.processingOver())
			{
				FileObject cruxRestFile = getResourceFile("META-INF/crux-rest");
				cruxRestMap.store(cruxRestFile.openOutputStream(), "Crux REST Services implementations");
			}
		}
		catch (IOException e)
		{
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}

		return true;
	}
}
