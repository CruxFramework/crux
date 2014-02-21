/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.bean;

import org.cruxframework.crux.core.client.bean.BeanComparator;
import org.cruxframework.crux.core.client.utils.ObjectUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class BeanComparatorProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private JClassType aType;

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public BeanComparatorProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		JClassType beanComparatorType = context.getTypeOracle().findType(BeanComparator.class.getCanonicalName());
		JClassType[] parameterTypes = JClassUtils.getActualParameterTypes(baseIntf, beanComparatorType);
		aType = parameterTypes[0];

		if (JClassUtils.isSimpleType(aType))
		{
			throw new CruxGeneratorException("Can not create a BeanComparator for simple types.");
		}
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public boolean equals("+aType.getParameterizedQualifiedSourceName()+" a1, "+aType.getParameterizedQualifiedSourceName()+" a2){");
		generateCompareBeanCode(srcWriter, "a1", "a2", aType);
		srcWriter.println("return true;");
		srcWriter.println("}");
	}

	protected void generateCompareBeanCode(SourcePrinter srcWriter, String objVariable, String otherVariable, JClassType type)
	{
		if (type != null)
		{
			PropertyInfo[] propertiesInfo = JClassUtils.extractBeanPropertiesInfo(type);

			if (propertiesInfo != null && propertiesInfo.length > 0)
			{
				for (PropertyInfo propertyInfo : propertiesInfo)
				{
					if (JClassUtils.isSimpleType(propertyInfo.getType()))
					{
						JPrimitiveType primitiveType = propertyInfo.getType().isPrimitive();
						JEnumType enumType = propertyInfo.getType().isEnum();
						JClassType classType = propertyInfo.getType().isClassOrInterface();
						if (primitiveType != null || enumType != null)
						{
							srcWriter.println("if ("+objVariable+"."+propertyInfo.getReadMethod().getName()+"() != "+otherVariable+"."+propertyInfo.getReadMethod().getName()+"()){");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
						else if (classType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
						{
							srcWriter.println("if (!StringUtils.equals("+objVariable+"."+propertyInfo.getReadMethod().getName()+"(), "+otherVariable+"."+propertyInfo.getReadMethod().getName()+"())){");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
						else
						{
							srcWriter.println("if (!ObjectUtils.isEqual("+objVariable+"."+propertyInfo.getReadMethod().getName()+"(), "+otherVariable+"."+propertyInfo.getReadMethod().getName()+"())){");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
					}
					else
					{
						srcWriter.println("if ("+objVariable+"."+propertyInfo.getReadMethod().getName()+"() != null){");
						srcWriter.println("if ("+otherVariable+"."+propertyInfo.getReadMethod().getName()+"() == null){");
						srcWriter.println("return false;");
						srcWriter.println("}");

						generateCompareBeanCode(srcWriter, objVariable+"."+propertyInfo.getReadMethod().getName()+"()", 
								otherVariable+"."+propertyInfo.getReadMethod().getName()+"()", 
								propertyInfo.getType().isClassOrInterface());

						srcWriter.println("}else if ("+otherVariable+"."+propertyInfo.getReadMethod().getName()+"() != null){");
						srcWriter.println("return false;");
						srcWriter.println("}");
					}
				}
			}
			else
			{
				srcWriter.println("if (!ObjectUtils.isEqual("+objVariable+","+otherVariable+")){");
				srcWriter.println("return false;");
				srcWriter.println("}");
			}
		}
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{
				StringUtils.class.getCanonicalName(),
				ObjectUtils.class.getCanonicalName(),
				aType.getParameterizedQualifiedSourceName() 
		};
	}
}
