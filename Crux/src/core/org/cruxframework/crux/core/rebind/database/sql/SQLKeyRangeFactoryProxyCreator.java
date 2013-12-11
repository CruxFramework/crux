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
package org.cruxframework.crux.core.rebind.database.sql;

import java.io.PrintWriter;

import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.WSQLKeyRange;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLKeyRangeFactoryProxyCreator extends SQLAbstractKeyValueProxyCreator
{
	private JClassType keyRangeType;
 	private String parentName;

	public SQLKeyRangeFactoryProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, 
			String[] keyPath, String parentName)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.keyRangeType = context.getTypeOracle().findType(KeyRange.class.getCanonicalName());
		this.parentName = parentName;
	}

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		//Do not generate serializer
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetNativeKeyMethod(srcWriter);
		generateOnlyMethod(srcWriter);
		generateLowerBoundOpenMethod(srcWriter);
		generateLowerBoundMethod(srcWriter);
		generateUpperBoundOpenMethod(srcWriter);
		generateUpperBoundMethod(srcWriter);
		generateBoundOpenMethod(srcWriter);
		generateBoundMethod(srcWriter);
	}
	
	protected void generateOnlyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> only("+keyTypeName+" key){");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeKey = getNativeKey(key);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		else
		{
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateLowerBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> lowerBound("+keyTypeName+" key, boolean open){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeKey = getNativeKey(key);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(open);");
			srcWriter.println("keyProperties.push(true);");
		}
		else
		{
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(open);");
			srcWriter.println("keyProperties.push(true);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");

		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateLowerBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> lowerBound("+keyTypeName+" key){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeKey = getNativeKey(key);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		else
		{
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");

		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateUpperBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> upperBound("+keyTypeName+" key, boolean open){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeKey = getNativeKey(key);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(open);");
		}
		else
		{
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(open);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateUpperBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> upperBound("+keyTypeName+" key){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeKey = getNativeKey(key);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println("keyProperties.push(nativeKey);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		else
		{
			srcWriter.println("keyProperties.push((String)null);");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeKey, keyProperties);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");
				
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> bound("+keyTypeName+" startKey, "+keyTypeName+" endKey, boolean startOpen, boolean endOpen){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeStartKey = getNativeKey(startKey);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeEndKey = getNativeKey(endKey);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push(nativeStartKey);");
			srcWriter.println("keyProperties.push(nativeEndKey);");
			srcWriter.println("keyProperties.push(startOpen);");
			srcWriter.println("keyProperties.push(endOpen);");
		}
		else
		{
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeStartKey, keyProperties);");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeEndKey, keyProperties);");
			srcWriter.println("keyProperties.push(startOpen);");
			srcWriter.println("keyProperties.push(endOpen);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> bound("+keyTypeName+" startKey, "+keyTypeName+" endKey){");

		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeStartKey = getNativeKey(startKey);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" nativeEndKey = getNativeKey(endKey);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" keyProperties = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		if (hasCompositeKey())
		{
			srcWriter.println("keyProperties.push(nativeStartKey);");
			srcWriter.println("keyProperties.push(nativeEndKey);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		else
		{
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeStartKey, keyProperties);");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(nativeEndKey, keyProperties);");
			srcWriter.println("keyProperties.push(true);");
			srcWriter.println("keyProperties.push(true);");
		}
		srcWriter.println("return new WSQLKeyRange<"+keyTypeName+">(keyProperties);");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	@Override
	public String getProxyQualifiedName()
	{
		return keyRangeType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		String typeName = parentName.replaceAll("\\W", "_");
		return typeName+"_SQL_KeyRageFactory";
	}

	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = keyRangeType.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}
		composerFactory.addImplementedInterface("KeyRangeFactory<"+getKeyTypeName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				KeyRange.class.getCanonicalName(),
				KeyRangeFactory.class.getCanonicalName(),
				WSQLKeyRange.class.getCanonicalName()
		};
		return imports;
	}
	
}
