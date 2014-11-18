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

import org.cruxframework.crux.core.client.bean.BeanContentValidator;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class BeanContentValidatorProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private JClassType aType;

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public BeanContentValidatorProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		JClassType beanComparatorType = context.getTypeOracle().findType(BeanContentValidator.class.getCanonicalName());
		JClassType[] parameterTypes = JClassUtils.getActualParameterTypes(baseIntf, beanComparatorType);
		aType = parameterTypes[0];

		if (JClassUtils.isSimpleType(aType))
		{
			throw new CruxGeneratorException("Can not create a BeanContentValidator for simple types.");
		}
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public boolean isEmpty("+aType.getParameterizedQualifiedSourceName()+" a){");
		generateValidateBeanCode(srcWriter, "a", aType, true);
		srcWriter.println("return true;");
		srcWriter.println("}");

		srcWriter.println("public boolean isFilled("+aType.getParameterizedQualifiedSourceName()+" a){");
		generateValidateBeanCode(srcWriter, "a", aType, false);
		srcWriter.println("return true;");
		srcWriter.println("}");
	}

	protected void generateValidateBeanCode(SourcePrinter srcWriter, String objVariable, JClassType type, boolean empty)
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
						if (propertyInfo.getType().getQualifiedSourceName().equals(String.class.getCanonicalName()))
						{
							srcWriter.println("if ("+(empty?"!":"")+"StringUtils.isEmpty("+objVariable+"."+propertyInfo.getReadMethod().getName()+"())){");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
						else if (primitiveType == null)
						{
							srcWriter.println("if ("+objVariable+"."+propertyInfo.getReadMethod().getName()+"() "+(empty?"!=":"==")+" null){");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
					}
					else
					{
						srcWriter.println("if ("+objVariable+"."+propertyInfo.getReadMethod().getName()+"() != null){");
						generateValidateBeanCode(srcWriter, objVariable+"."+propertyInfo.getReadMethod().getName()+"()", 
								propertyInfo.getType().isClassOrInterface(), empty);
						srcWriter.println("}");
						if (!empty)
						{
							srcWriter.println("else {");
							srcWriter.println("return false;");
							srcWriter.println("}");
						}
					}
				}
			}
		}
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{
				aType.getParameterizedQualifiedSourceName(),
				StringUtils.class.getCanonicalName()
		};
	}
}
