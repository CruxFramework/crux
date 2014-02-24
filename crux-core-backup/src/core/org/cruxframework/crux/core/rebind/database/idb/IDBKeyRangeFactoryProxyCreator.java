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
package org.cruxframework.crux.core.rebind.database.idb;

import java.io.PrintWriter;

import org.cruxframework.crux.core.client.db.IDXKeyRange;
import org.cruxframework.crux.core.client.db.KeyRange;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBKeyRange;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBKeyRangeFactoryProxyCreator extends IDBAbstractKeyValueProxyCreator
{
	private JClassType keyRangeType;
 	private String parentName;

	public IDBKeyRangeFactoryProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, 
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
		generateOnlyMethod(srcWriter);
		generateLowerBoundOpenMethod(srcWriter);
		generateLowerBoundMethod(srcWriter);
		generateUpperBoundOpenMethod(srcWriter);
		generateUpperBoundMethod(srcWriter);
		generateBoundOpenMethod(srcWriter);
		generateBoundMethod(srcWriter);
		if (hasCompositeKey())
		{
			generateGetNativeKeyMethod(srcWriter);
		}
	}
	
	protected void generateOnlyMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> only("+keyTypeName+" key){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.only(getNativeKey(key)));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.only(key));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateLowerBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> lowerBound("+keyTypeName+" key, boolean open){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.lowerBound(getNativeKey(key), open));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.lowerBound(key, open));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateLowerBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> lowerBound("+keyTypeName+" key){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.lowerBound(getNativeKey(key)));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.lowerBound(key));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateUpperBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> upperBound("+keyTypeName+" key, boolean open){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.upperBound(getNativeKey(key), open));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.upperBound(key, open));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateUpperBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> upperBound("+keyTypeName+" key){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.upperBound(getNativeKey(key)));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.upperBound(key));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateBoundOpenMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> bound("+keyTypeName+" startKey, "+keyTypeName+" endKey, boolean startOpen, boolean endOpen){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.bound(getNativeKey(startKey), getNativeKey(endKey), startOpen, endOpen));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.bound(startKey, endKey, startOpen, endOpen));");
		}
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateBoundMethod(SourcePrinter srcWriter)
    {
		String keyTypeName = getKeyTypeName();
		srcWriter.println("public KeyRange<"+keyTypeName+"> bound("+keyTypeName+" startKey, "+keyTypeName+" endKey){");
		if (hasCompositeKey())
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.bound(getNativeKey(startKey), getNativeKey(endKey)));");
		}
		else
		{
			srcWriter.println("return new IDXKeyRange<"+keyTypeName+">(IDBKeyRange.bound(startKey, endKey));");
		}
				
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
		return typeName+"_KeyRageFactory";
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
				IDXKeyRange.class.getCanonicalName(),
				KeyRangeFactory.class.getCanonicalName(),
				IDBKeyRange.class.getCanonicalName()
		};
		return imports;
	}
	
}
