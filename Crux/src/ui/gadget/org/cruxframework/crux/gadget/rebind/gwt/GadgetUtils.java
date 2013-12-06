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
package org.cruxframework.crux.gadget.rebind.gwt;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.cruxframework.crux.gadget.client.features.impl.PreferenceGeneratorName;
import org.cruxframework.crux.gadget.client.meta.LanguageDirection;
import org.cruxframework.crux.gadget.client.meta.GadgetInfo.GadgetLocale;
import org.cruxframework.crux.gadget.client.meta.GadgetInfo.UseLongManifestName;
import org.cruxframework.crux.gadget.client.meta.GadgetInfo.UserPreferences;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetUtils
{
	public static boolean useLongManifestName(TreeLogger logger, Class<?> extendsGadget)
	{
		UseLongManifestName annotation = extendsGadget.getAnnotation(UseLongManifestName.class);
		if (annotation == null)
		{
			logger.log(TreeLogger.WARN, "Gadget class " + extendsGadget.getName() + " is missing @UseLongManifestName annotation.  " + 
					"Using short names will become the default in the future.");
			return false;
		}
		return annotation.value();
	}
	
	/**
	 * Add the key-value pairs of an Annotation to an Element. Enumerated
	 * properties will be ignored. Specific properties can be excluded by name.
	 */
	public static void writeAnnotationToElement(TreeLogger logger, Annotation a, Element elt, String... excludeNames) throws UnableToCompleteException
	{
		List<String> excludeList = Arrays.asList(excludeNames);
		for (Method m : a.annotationType().getDeclaredMethods())
		{
			try
			{
				String name = m.getName();
				Object value = m.invoke(a);
				if (value instanceof Enum)
				{
					continue;
				}
				else if (excludeList.contains(name))
				{
					continue;
				}
				else if (!value.equals(m.getDefaultValue()))
				{
					elt.setAttribute(name, value.toString());
				}
			}
			catch (IllegalAccessException e)
			{
				logger.log(TreeLogger.ERROR, "Could not decode annotation", e);
				throw new UnableToCompleteException();
			}
			catch (InvocationTargetException e)
			{
				logger.log(TreeLogger.ERROR, "Could not decode annotation", e);
				throw new UnableToCompleteException();
			}
		}
	}

	/**
	 * @param logger
	 * @param d
	 * @param parent
	 * @param locales
	 * @throws UnableToCompleteException
	 */
	public static void writeLocalesToElement(TreeLogger logger, Document d, Element parent, GadgetLocale[] locales) throws UnableToCompleteException
	{
		for (GadgetLocale locale : locales)
		{
			Element localeElement = (Element) parent.appendChild(d.createElement("Locale"));
			String country = locale.country();
			String lang = locale.lang();
			if (country.length() == 0 && lang.length() == 0)
			{
				logger.log(TreeLogger.ERROR, "All @GadgetLocale specifictions must include either lang() or country().");
				throw new UnableToCompleteException();
			}
			writeAnnotationToElement(logger, locale, localeElement, "language_direction");
			LanguageDirection direction = locale.language_direction();
			if (direction != null && direction != LanguageDirection.UNSPECIFIED)
			{
				localeElement.setAttribute("language_direction", direction.getValue());
			}
		}
	}
	
	/**
	 * Returns the subtype of UserPreferences accepted by a Gadget.
	 */
	public static Class<?> getUserPrefsType(TreeLogger logger, Class<?> extendsGadget)
	{
		UserPreferences annotation = extendsGadget.getAnnotation(UserPreferences.class);
		String gadgetClassName = extendsGadget.getName();
		return getUserPrefsType(logger, annotation, gadgetClassName);
	}

	/**
	 * Returns the subtype of UserPreferences accepted by a Gadget.
	 */
	public static Class<?> getUserPrefsType(TreeLogger logger, JClassType extendsGadget)
	{
		UserPreferences annotation = extendsGadget.getAnnotation(UserPreferences.class);
		String gadgetClassName = extendsGadget.getName();
		return getUserPrefsType(logger, annotation, gadgetClassName);
	}

	private static Class<?> getUserPrefsType(TreeLogger logger, UserPreferences annotation, String gadgetClassName)
    {
	    if (annotation == null)
		{
			logger.log(TreeLogger.INFO, "Gadget class " + gadgetClassName + " is missing @UserPreferences annotation.  " + 
					"Using default value.");
			return org.cruxframework.crux.gadget.client.features.UserPreferences.class;
		}
		Class<? extends org.cruxframework.crux.gadget.client.features.UserPreferences> value = annotation.value();
		return value;
    }	
	
	/**
	 * Return an instance of a PreferenceGenerator that can be used for a
	 * subtype of Preference.
	 */
	public static PreferenceGenerator getPreferenceGenerator(TreeLogger logger, JClassType extendsPreferenceType) throws UnableToCompleteException
	{

		PreferenceGeneratorName generator = extendsPreferenceType.getAnnotation(PreferenceGeneratorName.class);
		String preferenceTypeName = extendsPreferenceType.getQualifiedSourceName();

		return getPreferenceGenerator(logger, generator, preferenceTypeName);
	}

	/**
	 * Return an instance of a PreferenceGenerator that can be used for a
	 * subtype of Preference.
	 */
	public static PreferenceGenerator getPreferenceGenerator(TreeLogger logger, Class<?> extendsPreferenceType) throws UnableToCompleteException
	{

		PreferenceGeneratorName generator = extendsPreferenceType.getAnnotation(PreferenceGeneratorName.class);
		String preferenceTypeName = extendsPreferenceType.getCanonicalName();

		return getPreferenceGenerator(logger, generator, preferenceTypeName);
	}
	
	private static PreferenceGenerator getPreferenceGenerator(TreeLogger logger, PreferenceGeneratorName generator, String preferenceTypeName) throws UnableToCompleteException
    {
	    if (generator == null)
		{
			logger.log(TreeLogger.ERROR, "No PreferenceGenerator defined for type " + preferenceTypeName, null);
			throw new UnableToCompleteException();
		}

		try
		{
			String typeName = generator.value();

			Class<? extends PreferenceGenerator> clazz = Class.forName(typeName).asSubclass(PreferenceGenerator.class);
			return clazz.newInstance();
		}
		catch (ClassCastException e)
		{
			logger.log(TreeLogger.ERROR, "Not a PreferenceGenerator", e);
			throw new UnableToCompleteException();
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Unable to create PreferenceGenerator", e);
			throw new UnableToCompleteException();
		}
    }
}
