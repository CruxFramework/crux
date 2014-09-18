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
package org.cruxframework.crux.core.rebind.crossdevice;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.PropertyProviderGenerator;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceFeaturesPropertyGenerator implements PropertyProviderGenerator 
{
	private static final List<String> VALID_VALUES = Arrays.asList(new String[]{
			"largeDisplayMouse", "largeDisplayTouch", "largeDisplayArrows", "smallDisplayArrows", "smallDisplayTouch"});

	static void writeDeviceFeaturesPropertyJavaScript(SourceWriter body) 
	{
		body.println("var ua = navigator.userAgent.toLowerCase();"); 
		body.println("var supportsTouch = ('ontouchstart' in window);");
		
		body.println("if ((ua.indexOf('googletv') != -1) || (ua.indexOf('ipad;') != -1)){");
		body.indent();
		body.println("return 'largeDisplay'+(supportsTouch?'Touch':'Arrows');");
		body.outdent();
		body.println("} else if ((ua.indexOf('opera mini') != -1)  || (ua.indexOf('opera mobi') != -1) || (ua.indexOf('mobile') != -1) || "+
	                     "(ua.indexOf('iphone') != -1) || (ua.indexOf('ipod;') != -1)){");
		body.indent();
		body.println("return 'smallDisplay'+(supportsTouch?'Touch':'Arrows');");
		body.outdent();
		body.println("} else if (ua.indexOf('android') != -1){");
		body.indent();
		body.println("return 'largeDisplay'+(supportsTouch?'Touch':'Arrows');");
		body.outdent();
		body.println("} else {");
		body.indent();
		body.println("return 'largeDisplay'+(supportsTouch?'Touch':'Mouse');");
		body.outdent();
		body.println("}");
	}

	public String generate(TreeLogger logger, SortedSet<String> possibleValues, String fallback, SortedSet<ConfigurationProperty> configProperties) throws UnableToCompleteException
	{
		for (String value : possibleValues) 
		{
			if (!VALID_VALUES.contains(value)) 
			{
				throw new CruxGeneratorException("Property device.features can not be assigned to value ["+value+"].");
			}
		}
		StringSourceWriter body = new StringSourceWriter();
		body.println("{");
		body.indent();
		writeDeviceFeaturesPropertyJavaScript(body);
		body.outdent();
		body.println("}");

		return body.toString();
	}
}
