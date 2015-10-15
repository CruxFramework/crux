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
	
	@TypeConverter.Converter("doubleNumber")
	public static class DoubleNumberConverter implements TypeConverter<Double, Number>
	{

		@Override
        public Number to(Double a)
        {
	        return a;
        }

		@Override
        public Double from(Number b)
        {
	        return (b==null?null:b.doubleValue());
        }
	}

	@TypeConverter.Converter("numberDouble")
	public static class NumberDoubleConverter implements TypeConverter<Number, Double>
	{
		@Override
        public Double to(Number a)
        {
	        return (a==null?null:a.doubleValue());
        }

		@Override
        public Number from(Double b)
        {
	        return b;
        }
	}

	@TypeConverter.Converter("integerNumber")
	public static class IntegerNumberConverter implements TypeConverter<Integer, Number>
	{

		@Override
        public Number to(Integer a)
        {
	        return a;
        }

		@Override
        public Integer from(Number b)
        {
	        return (b==null?null:b.intValue());
        }
	}

	@TypeConverter.Converter("numberInteger")
	public static class IntegerDoubleConverter implements TypeConverter<Number, Integer>
	{
		@Override
        public Integer to(Number a)
        {
	        return (a==null?null:a.intValue());
        }

		@Override
        public Number from(Integer b)
        {
	        return b;
        }
	}
	
	@TypeConverter.Converter("longString")
	public static class LongStringConverter implements TypeConverter<Long, String>
	{

		@Override
        public String to(Long a)
        {
	        return (a!=null?a.toString():null);
        }

		@Override
        public Long from(String b)
        {
	        return (!StringUtils.isEmpty(b)?Long.parseLong(b):null);
        }
	}
	
	@TypeConverter.Converter("stringLong")
	public static class StringLongConverter implements TypeConverter<String, Long>
	{

		@Override
        public Long to(String a)
        {
	        return (!StringUtils.isEmpty(a)?Long.parseLong(a):null);
        }

		@Override
        public String from(Long b)
        {
	        return (b!=null?b.toString():null);
        }
	}
}
