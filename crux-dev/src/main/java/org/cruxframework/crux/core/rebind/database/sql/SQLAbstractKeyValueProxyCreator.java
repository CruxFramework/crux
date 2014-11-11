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

import java.util.Date;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.db.WSQLAbstractObjectStore.EncodeCallback;
import org.cruxframework.crux.core.client.db.WSQLKeyRange;
import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.FileUtils;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.database.AbstractKeyValueProxyCreator;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONParser;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SQLAbstractKeyValueProxyCreator extends AbstractKeyValueProxyCreator
{
	private JClassType blobType;

	public SQLAbstractKeyValueProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.blobType = context.getTypeOracle().findType(Blob.class.getCanonicalName());
	}

	@Override
	protected void generateGetNativeKeyMethod(SourcePrinter srcWriter)
	{
	    if (hasCompositeKey())
	    {
	    	super.generateGetNativeKeyMethod(srcWriter);
	    }
	    else
	    {
			srcWriter.println("private "+JsArrayMixed.class.getCanonicalName()+" getNativeKey("+getKeyTypeName()+" key){");
			srcWriter.println(JsArrayMixed.class.getCanonicalName()+ " result = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		    srcWriter.println("getNativeKey(key, result);");
		    srcWriter.println("return result;");
		    srcWriter.println("}");
		    srcWriter.println();

		    srcWriter.println("private void getNativeKey("+getKeyTypeName()+" key, "+JsArrayMixed.class.getCanonicalName()+" result){");
	    	srcWriter.println("if (key == null){");
	    	srcWriter.println("result.push((String)null);");
	    	srcWriter.println("return;");
	    	srcWriter.println("}");

		    JType jType = getPropertyType(keyPath[0]);
		    if (jType.equals(stringType))
		    {
		    	srcWriter.println("result.push((String)key);");
		    }
		    else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
		    {
		    	srcWriter.println("result.push((int)key);");
		    }
		    else if (jType.equals(doubleType) || (jType.equals(JPrimitiveType.DOUBLE)))
		    {
		    	srcWriter.println("result.push((double)key);");
		    }
		    else if (jType.equals(dateType))
		    {
		    	srcWriter.println("result.push((double)(("+Date.class.getCanonicalName()+")key).getTime());");
		    }
		    else
		    {
		    	throw new CruxGeneratorException("Invalid key type for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
		    }
		    srcWriter.println("}");
	    }
	}

	protected JType getPropertyType(String property)
    {
	    JType jType = JClassUtils.getTypeForProperty(property, targetObjectType);
	    if (jType == null)
	    {
	    	throw new CruxGeneratorException("Invalid property for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
	    }
	    return jType;
    }
	
	protected void generateGetKeyPathMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected Array<String> getKeyPath(){");
		srcWriter.println("Array<String> keyPath = "+CollectionFactory.class.getCanonicalName()+".createArray();");
	    for(String path: keyPath)
		{
			srcWriter.println("keyPath.add("+EscapeUtils.quote(path)+");");
		}
		srcWriter.println("return keyPath;");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateGetIndexedColumnNamesMethod(SourcePrinter srcWriter, Set<String> indexColumns)
	{
		srcWriter.println("protected Array<String> getIndexedColumnNames(){");

		srcWriter.println("Array<String> indexColumnNames = "+CollectionFactory.class.getCanonicalName()+".createArray();");
		for (String col : indexColumns)
		{
			for(String path: keyPath)
			{
				if (!path.equals(col))
				{					
					srcWriter.println("indexColumnNames.add("+EscapeUtils.quote(col)+");");
				}
			}
		}
		srcWriter.println("return indexColumnNames;");
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateGetKeyRangeFactoryMethod(SourcePrinter srcWriter, String parentName)
    {
		srcWriter.println("public KeyRangeFactory<"+getKeyTypeName()+"> getKeyRangeFactory(){");
		String keyRangeFatoryClassName = new SQLKeyRangeFactoryProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, parentName).create();
		srcWriter.println("return (KeyRangeFactory<"+getKeyTypeName()+">) new "+keyRangeFatoryClassName+"();");
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateDecodeObjectMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected "+getTargetObjectClassName()+" decodeObject(String encodedObject){");
		srcWriter.println("if ("+StringUtils.class.getCanonicalName()+".isEmpty(encodedObject)){");
		srcWriter.println("return null;");
		srcWriter.println("}");
		if (targetObjectType.isAssignableTo(blobType))
		{
			srcWriter.println("return "+FileUtils.class.getCanonicalName()+".fromDataURI(encodedObject).cast();");
		}
		else
		{
			srcWriter.println("return "+serializerVariable+".decode("+JSONParser.class.getCanonicalName()+".parseStrict(encodedObject).isObject());");
		}
		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateEncodeObjectMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected void encodeObject("+getTargetObjectClassName()+" object, "+EncodeCallback.class.getCanonicalName()+" callback){");

		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		if (targetObjectType.isAssignableTo(blobType))
		{
			throw new CruxGeneratorException("Blobs and files can not be inserted into object stores. Use FieStore instead.");
		}
		else if (isEmptyType())
		{
			srcWriter.println("callback.onEncode("+JsUtils.class.getCanonicalName()+".toJSONValue(object));");
		}
		else
		{
			srcWriter.println("callback.onEncode("+serializerVariable+".encode(object).isObject());");
		}
		srcWriter.println("}");
		srcWriter.println();
	}

	protected void generateAddKeyRangeToQueryMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public void addKeyRangeToQuery(final KeyRange<"+getKeyTypeName()+"> range, "+
				StringBuilder.class.getCanonicalName()+" sql, "+JsArrayMixed.class.getCanonicalName()+" args){");
		srcWriter.println(JavaScriptObject.class.getCanonicalName()+" nativeKeyRange = "+WSQLKeyRange.class.getCanonicalName()+".getNativeKeyRange(range);");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" lower = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		srcWriter.println(JsArrayMixed.class.getCanonicalName()+" upper = "+JsArrayMixed.class.getCanonicalName()+".createArray().cast();");
		srcWriter.println(JsUtils.class.getCanonicalName()+".readPropertyValue(nativeKeyRange, \"lower\", lower, false);");
		srcWriter.println(JsUtils.class.getCanonicalName()+".readPropertyValue(nativeKeyRange, \"upper\", upper, false);");

		srcWriter.println("boolean hasLower = false;");
		srcWriter.println("if (lower.length() > 0){");
		srcWriter.println("hasLower = true;");

		if (keyPath.length == 1)
		{
			srcWriter.println("sql.append("+EscapeUtils.quote("\""+keyPath[0]+"\"")+" +(range.isLowerOpen()?\">\":\">=\")+\" ?\");");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(lower, args);");
		}
		else
		{
			boolean first = true;
			for(int i = 0; i < keyPath.length; i++)
			{
				if (!first)
				{
					srcWriter.println("sql.append(\" AND \");");
				}
				srcWriter.println("sql.append("+EscapeUtils.quote("\""+keyPath[i]+"\"")+" +(range.isLowerOpen()?\">\":\">=\")+\" ?\");");
				first = false;
			}
			
			srcWriter.println(JsArrayMixed.class.getCanonicalName()+" lowerArrayColumn = lower.getObject(0).cast();");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(lowerArrayColumn, args);");
		}
		srcWriter.println("}");

		srcWriter.println("if (upper.length() > 0){");

		if (keyPath.length == 1)
		{
			srcWriter.println("if (hasLower){");
			srcWriter.println("sql.append(\" AND \");");
			srcWriter.println("}");
			srcWriter.println("sql.append("+EscapeUtils.quote("\""+keyPath[0]+"\"")+" +(range.isUpperOpen()?\"<\":\"<=\")+\" ?\");");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(upper, args);");
		}
		else
		{
			boolean first = true;
			for(int i = 0; i < keyPath.length; i++)
			{
				if (!first)
				{
					srcWriter.println("sql.append(\" AND \");");
				}
				else
				{
					srcWriter.println("if (hasLower){");
					srcWriter.println("sql.append(\" AND \");");
					srcWriter.println("}");
				}
				
				srcWriter.println("sql.append("+EscapeUtils.quote("\""+keyPath[i]+"\"")+" +(range.isUpperOpen()?\"<\":\"<=\")+\" ?\");");
				first = false;
			}

			srcWriter.println(JsArrayMixed.class.getCanonicalName()+" upperArrayColumn = upper.getObject(0).cast();");
			srcWriter.println(JsUtils.class.getCanonicalName()+".copyValues(upperArrayColumn, args);");
		}
		srcWriter.println("}");

		srcWriter.println("}");
		srcWriter.println();
	}
	
	protected void generateAddKeyToQueryMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected void addKeyToQuery(final "+getKeyTypeName()+" key, "+
				StringBuilder.class.getCanonicalName()+" sql, "+JsArrayMixed.class.getCanonicalName()+" args){");
		
		boolean first = true;
		for(String path: keyPath)
		{
			if (!first)
			{
				srcWriter.print("sql.append(\" AND \");");
			}
			srcWriter.println("sql.append("+EscapeUtils.quote("\""+path+"\" = ?")+");");
			first = false;
		}
		srcWriter.println("getNativeKey(key, args);");
		
		srcWriter.println("}");
		srcWriter.println();
	}
}
