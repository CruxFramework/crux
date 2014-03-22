/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.converter;

import org.cruxframework.crux.core.client.utils.StringUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class TypeConverters
{
	private TypeConverters(){}

	@TypeConverter.Converter("stringInteger")
	public static class StringIntegerConverter implements TypeConverter<String, Integer>
	{
		@Override
        public Integer to(String a)
        {
	        return (!StringUtils.isEmpty(a)?Integer.parseInt(a):null);
        }

		@Override
        public String from(Integer b)
        {
	        return (b!=null?b.toString():null);
        }
	}

	@TypeConverter.Converter("integerString")
	public static class IntegerStringConverter implements TypeConverter<Integer, String>
	{

		@Override
        public String to(Integer a)
        {
	        return (a!=null?a.toString():null);
        }

		@Override
        public Integer from(String b)
        {
	        return (!StringUtils.isEmpty(b)?Integer.parseInt(b):null);
        }
	}
	
	@TypeConverter.Converter("stringDouble")
	public static class StringDoubleConverter implements TypeConverter<String, Double>
	{
		@Override
        public Double to(String a)
        {
	        return (!StringUtils.isEmpty(a)?Double.parseDouble(a):null);
        }

		@Override
        public String from(Double b)
        {
	        return (b!=null?b.toString():null);
        }
	}

	@TypeConverter.Converter("doubleString")
	public static class DoubleStringConverter implements TypeConverter<Double, String>
	{

		@Override
        public String to(Double a)
        {
	        return (a!=null?a.toString():null);
        }

		@Override
        public Double from(String b)
        {
	        return (!StringUtils.isEmpty(b)?Double.parseDouble(b):null);
        }
	}
	
}
