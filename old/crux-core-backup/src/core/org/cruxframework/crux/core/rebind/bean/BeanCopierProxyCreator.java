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

import org.cruxframework.crux.core.client.bean.BeanCopier;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class BeanCopierProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private JClassType aType;
	private JClassType bType;

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public BeanCopierProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		JClassType beanCopierType = context.getTypeOracle().findType(BeanCopier.class.getCanonicalName());
		JClassType[] parameterTypes = JClassUtils.getActualParameterTypes(baseIntf, beanCopierType);
		aType = parameterTypes[0];
		bType = parameterTypes[1];

		if (JClassUtils.isSimpleType(aType) || JClassUtils.isSimpleType(bType))
		{
			throw new CruxGeneratorException("Can not create a BeanCopier for simple types.");
		}
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public void copyTo("+aType.getParameterizedQualifiedSourceName()+" from, "+bType.getParameterizedQualifiedSourceName()+" to){");
		generateCopyBeanCode(srcWriter, "from", "to", aType, bType);
		srcWriter.println("}");

		srcWriter.println("public void copyFrom("+bType.getParameterizedQualifiedSourceName()+" from, "+aType.getParameterizedQualifiedSourceName()+" to){");
		generateCopyBeanCode(srcWriter, "from", "to", bType, aType);
		srcWriter.println("}");
	}

	protected void generateCopyBeanCode(SourcePrinter srcWriter, String fromVariable, String toVariable, JClassType fromType, JClassType toType)
	{
		if (fromType != null && toType != null)
		{
			PropertyInfo[] fromPropertiesInfo = JClassUtils.extractBeanPropertiesInfo(fromType);
			PropertyInfo[] toPropertiesInfo = JClassUtils.extractBeanPropertiesInfo(toType);

			for (PropertyInfo fromPropertyInfo : fromPropertiesInfo)
			{
				PropertyInfo toPropertyInfo = getEquivalentPropertyInfo(fromPropertyInfo, toPropertiesInfo);
				if (toPropertyInfo != null)
				{
					if (JClassUtils.isSimpleType(fromPropertyInfo.getType()))
					{
						if (fromPropertyInfo.getType().equals(toPropertyInfo.getType()))
						{
							srcWriter.println(toVariable+"."+toPropertyInfo.getWriteMethod().getName()+"("+fromVariable+"."+fromPropertyInfo.getReadMethod().getName()+"());");
						}
						else
						{
							throw new CruxGeneratorException("Can not create a BeanCopier. Incopatibles type for property ["+fromPropertyInfo.getName()+"].");
						}
					}
					else
					{
						JClassType fromPropClass = fromPropertyInfo.getType().isClassOrInterface();
						JClassType toPropClass = toPropertyInfo.getType().isClassOrInterface();
						if (fromPropClass.isAssignableTo(toPropClass))
						{
							srcWriter.println(toVariable+"."+toPropertyInfo.getWriteMethod().getName()+"("+fromVariable+"."+fromPropertyInfo.getReadMethod().getName()+"());");
						}
						else 
						{
							srcWriter.println("if ("+fromVariable+"."+fromPropertyInfo.getReadMethod().getName()+"() != null){");
							srcWriter.println(toVariable+"."+toPropertyInfo.getWriteMethod().getName()+"(GWT.create("+toPropClass.getQualifiedSourceName()+".class));");
							generateCopyBeanCode(srcWriter, fromVariable+"."+fromPropertyInfo.getReadMethod().getName()+"()", 
									toVariable+"."+toPropertyInfo.getReadMethod().getName()+"()", 
									fromPropertyInfo.getType().isClassOrInterface(), 
									toPropertyInfo.getType().isClassOrInterface());
							srcWriter.println("}else{");
							srcWriter.println(toVariable+"."+toPropertyInfo.getWriteMethod().getName()+"(null);");
							srcWriter.println("}");
						}
					}
				}
			}
		}
	}

	protected PropertyInfo getEquivalentPropertyInfo(PropertyInfo aPropertyInfo, PropertyInfo[] bPropertiesInfo)
	{
		for (PropertyInfo bPropertyInfo : bPropertiesInfo)
		{
			if (bPropertyInfo.getName().equals(aPropertyInfo.getName()))
			{
				return bPropertyInfo;
			}
		}

		return null;
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{
				GWT.class.getCanonicalName(),
				aType.getParameterizedQualifiedSourceName(), 
				bType.getParameterizedQualifiedSourceName() 
		};
	}
}
