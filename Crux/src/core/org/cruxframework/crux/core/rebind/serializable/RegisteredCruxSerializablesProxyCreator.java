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
package org.cruxframework.crux.core.rebind.serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.CruxSerializable;
import org.cruxframework.crux.core.client.screen.RegisteredCruxSerializables;
import org.cruxframework.crux.core.client.serializer.BooleanSerializer;
import org.cruxframework.crux.core.client.serializer.ByteSerializer;
import org.cruxframework.crux.core.client.serializer.CharacterSerializer;
import org.cruxframework.crux.core.client.serializer.DateSerializer;
import org.cruxframework.crux.core.client.serializer.DoubleSerializer;
import org.cruxframework.crux.core.client.serializer.FloatSerializer;
import org.cruxframework.crux.core.client.serializer.IntegerSerializer;
import org.cruxframework.crux.core.client.serializer.LongSerializer;
import org.cruxframework.crux.core.client.serializer.SQLDateSerializer;
import org.cruxframework.crux.core.client.serializer.ShortSerializer;
import org.cruxframework.crux.core.client.serializer.StringBufferSerializer;
import org.cruxframework.crux.core.client.serializer.StringBuilderSerializer;
import org.cruxframework.crux.core.client.serializer.StringSerializer;
import org.cruxframework.crux.core.client.serializer.TimestampSerializer;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Screen;


import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
public class RegisteredCruxSerializablesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, Boolean> serializables = new HashMap<String, Boolean>();

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredCruxSerializablesProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredCruxSerializables.class.getCanonicalName()), false);
    }	
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public "+getProxySimpleName()+"(){ ");
		srcWriter.indent();
		generateDefaultSerializersBlock(srcWriter);
		
		List<Screen> screens = getScreens();
		
		for (Screen screen : screens)
		{
			Iterator<String> iterator = screen.iterateSerializers();
			while (iterator.hasNext())
			{
				String serializer = iterator.next();
				generateSerialisersBlock(srcWriter, serializer);
			}
		}
		srcWriter.outdent();
		srcWriter.println("}");
    }


	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<CruxSerializable> serializers = new FastMap<CruxSerializable>();");
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateGetCruxSerializableMethod(srcWriter);
		generateRegisterCruxSerializableMethod(srcWriter);
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		CruxSerializable.class.getCanonicalName(), 
	    		FastMap.class.getCanonicalName()
			};
	    return imports;
    }

	/**
	 * @param sourceWriter
	 */
	private void generateDefaultSerializersBlock(SourceWriter sourceWriter)
	{
		sourceWriter.println("serializers.put(\"java.lang.Boolean\", new " + BooleanSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Byte\", new " + ByteSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Character\", new " + CharacterSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.util.Date\", new " + DateSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Double\", new " + DoubleSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Float\", new " + FloatSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Integer\", new " + IntegerSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Long\", new " + LongSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Short\", new " + ShortSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.sql.Date\", new " + SQLDateSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.StringBuffer\", new " + StringBufferSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.StringBuilder\", new " + StringBuilderSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.String\", new " + StringSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Timestamp\", new " + TimestampSerializer.class.getName() + "());");
	}

	/**
	 * @param srcWriter
	 */
	private void generateGetCruxSerializableMethod(SourceWriter srcWriter)
    {
	    srcWriter.println("public CruxSerializable getCruxSerializable(String type){");
		srcWriter.indent();
		srcWriter.println("return serializers.get(type);");
		srcWriter.outdent();
		srcWriter.println("}");
    }

	/**
	 * @param srcWriter
	 */
	private void generateRegisterCruxSerializableMethod(SourceWriter srcWriter)
    {
	    srcWriter.println("public void registerCruxSerializable(String type, CruxSerializable moduleShareable){");
		srcWriter.indent();
		srcWriter.println("serializers.put(type, moduleShareable);");
		srcWriter.outdent();
		srcWriter.println("}");
    } 
		
	/**
	 * @param sourceWriter
	 * @param serializer
	 */
	private void generateSerialisersBlock(SourceWriter sourceWriter, String serializer)
	{
		try
        {
	        if (!serializables.containsKey(serializer) && Serializers.getCruxSerializable(serializer)!= null)
	        {
	        	JClassType serializerClass = baseIntf.getOracle().getType(Serializers.getCruxSerializable(serializer));
	        	sourceWriter.println("serializers.put(\""+serializerClass.getQualifiedSourceName()+"\", new " + serializerClass.getParameterizedQualifiedSourceName() + "());");
	        	serializables.put(serializer, true);
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
}
