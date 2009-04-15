/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind.jsonparser;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.gwt.user.rebind.SourceWriter;

/**
 * Helper Class used by Generators to deserialise objects returned by server before they
 * are passed to callback handlers.
 * 
 * @author Thiago Bustamante
 *
 */
public class JSONArrayParser 
{
	/**
	 * Singleton instance
	 */
	private static JSONArrayParser instance = new JSONArrayParser();
	
	/**
	 * Private constructor
	 */
	private JSONArrayParser() 
	{
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static JSONArrayParser getInstance()
	{
		return instance;
	}
	
	/**
	 * Generate array variable declaration
	 * @param param
	 * @param sourceWriter
	 */
	void generateArrayDeclaration(Class<?> param, SourceWriter sourceWriter) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		
		sourceWriter.print(compType.getName());
		for (int i=0; i<numDim; i++)
		{
			sourceWriter.print("[]");	
		}
	}
	
	/**
	 * Generate array variable declaration
	 * @param param
	 * @param sourceWriter
	 */
	void generateArrayDeclaration(GenericArrayType param, SourceWriter sourceWriter) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		
		sourceWriter.print(compType.getName());
		for (int i=0; i<numDim; i++)
		{
			sourceWriter.print("[]");	
		}
	}
	
	/**
	 * Deserialise arrays arguments
	 * 
	 * @param param
	 * @param sourceWriter
	 */
	void generateDeserialisationForArray(Class<?> param, SourceWriter sourceWriter, String resultVariable) 
	{
		generateArrayInstantiation(param, sourceWriter, resultVariable);
		generateArrayPopulation(param, sourceWriter, resultVariable);
	}

	/**
	 * Deserialise arrays arguments
	 * 
	 * @param param
	 * @param sourceWriter
	 */
	void generateDeserialisationForArray(GenericArrayType param, SourceWriter sourceWriter, String resultVariable) 
	{
		generateArrayInstantiation(param, sourceWriter, resultVariable);
		generateArrayPopulation(param, sourceWriter, resultVariable);
	}
	
	/**
	 * Generate the code for array instantiation
	 * 
	 * @param param
	 * @param sourceWriter
	 * @param resultVariable
	 */
	private void generateArrayInstantiation(Class<?> param, SourceWriter sourceWriter, String resultVariable) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		generateArrayInstantiation(compType, sourceWriter, numDim, resultVariable);
	}
	
	/**
	 * Generate the code for array instantiation
	 * 
	 * @param param
	 * @param sourceWriter
	 * @param resultVariable
	 */
	private void generateArrayInstantiation(GenericArrayType param, SourceWriter sourceWriter, String resultVariable) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		generateArrayInstantiation(compType, sourceWriter, numDim, resultVariable);
	}

	/**
	 * Generate the code for array instantiation.
	 * 
	 * @param compType
	 * @param sourceWriter
	 * @param numDim
	 * @param resultVariable
	 */
	private void generateArrayInstantiation(Class<?> compType, SourceWriter sourceWriter, int numDim, String resultVariable) 
	{
		sourceWriter.print("null;");
		
		sourceWriter.print("if(jsonValue != null && jsonValue.isArray() != null){");

		sourceWriter.print("java.util.List<Integer> dimensions"+resultVariable+" = new java.util.ArrayList<Integer>();");
		sourceWriter.print("com.google.gwt.json.client.JSONArray jarr"+resultVariable+";");
		sourceWriter.print("JSONValue jval"+resultVariable+" = jsonValue;");		
		sourceWriter.print("while ((jarr"+resultVariable+" = jval"+resultVariable+".isArray()) != null)");
		sourceWriter.print("{");
			sourceWriter.print("dimensions"+resultVariable+".add(jarr"+resultVariable+".size());");
			sourceWriter.print("if(jarr"+resultVariable+".size() > 0)");
				sourceWriter.print("jval"+resultVariable+" = jarr"+resultVariable+".get(0);");
			sourceWriter.print("else ");
				sourceWriter.print("break;");
		sourceWriter.print("}");
		
		sourceWriter.print(resultVariable+"=");
		sourceWriter.print("new "+compType.getName());
		for (int i=0; i<numDim; i++)
		{
			sourceWriter.print("[dimensions"+resultVariable+".get("+i+")]");	
		}

		sourceWriter.print(";");
		sourceWriter.print("}");
	}

	/**
	 * Generate code for array population
	 * @param param
	 * @param sourceWriter
	 * @param resultVariable
	 */
	private void generateArrayPopulation(Class<?> param, SourceWriter sourceWriter, String resultVariable) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		generateArrayPopulation(compType, sourceWriter, numDim, resultVariable);
	}
	
	/**
	 * Generate code for array population
	 * @param param
	 * @param sourceWriter
	 * @param resultVariable
	 */
	private void generateArrayPopulation(GenericArrayType param, SourceWriter sourceWriter, String resultVariable) 
	{
		int numDim = getNumDimensionsForArray(param);
		Class<?> compType = getArrayBaseType(param);
		generateArrayPopulation(compType, sourceWriter, numDim, resultVariable);
	}
	
	/**
	 * Generate code for array population
	 * @param compType
	 * @param sourceWriter
	 * @param numDim
	 * @param resultVariable
	 */
	private void generateArrayPopulation(Class<?> compType, SourceWriter sourceWriter, int numDim, String resultVariable) 
	{
		sourceWriter.print("if(jsonValue != null && jsonValue.isArray() != null){");
		for (int i = 0; i < numDim; i++) 
		{
			if (i == 0)
			{
				sourceWriter.print("com.google.gwt.json.client.JSONArray jarr"+resultVariable+i+" = jsonValue.isArray();");
			}
			else
			{
				sourceWriter.print("com.google.gwt.json.client.JSONArray jarr"+resultVariable+i+";");
			}
		}
		for (int i = 0; i < numDim; i++) 
		{
			sourceWriter.print("for (int a"+resultVariable+i+"=0; a"+resultVariable+i+" < jarr"+resultVariable+i+".size(); a"+resultVariable+i+"++){");	
			if (i < numDim-1)
			{
				sourceWriter.print("jarr"+resultVariable+(i+1)+"=jarr"+resultVariable+i+".get(a"+resultVariable+i+").isArray();");	
			}
			else
			{
				sourceWriter.print("jsonValue = jarr"+resultVariable+i+".get(a"+resultVariable+i+");");
			}
		}
		
		sourceWriter.print(compType.getName());
		sourceWriter.print(" "+resultVariable+"_a=");
		JSONParser.getInstance().generateParameterDeserialisationForType(compType, sourceWriter, resultVariable+"_a");
		sourceWriter.print(";");

		sourceWriter.print(resultVariable);	
		for (int i = 0; i < numDim; i++) 
		{
			sourceWriter.print("[a"+resultVariable+i+"]");	
		}
		sourceWriter.print("="+resultVariable+"_a;");	
		

		for (int i = 0; i < numDim; i++) 
		{
			sourceWriter.print("}");	
		}
		sourceWriter.print("}");	
	}

	/**
	 * Return the number of dimensions for an array
	 * @param param
	 * @return
	 */
	private int getNumDimensionsForArray(Class<?> param)
	{
		int numDim = 0;
		if (param.isArray())
		{
			String arrayDesc = param.getName();
			for (int i=0; i<arrayDesc.length(); i++)
			{
				if (arrayDesc.charAt(i) == '[')
				{
					numDim++;
				}
				else
				{
					break;
				}
			}
		}
		return numDim;
	}
	
	/**
	 * Return the number of dimensions for an array
	 * @param param
	 * @return
	 */
	private int getNumDimensionsForArray(GenericArrayType param)
	{
		int numDim = 1;
		Type type;
		while ((type = param.getGenericComponentType()) instanceof GenericArrayType)
		{
			numDim++;
			param = (GenericArrayType)type;
		}
		
		return numDim;
	}
	
	
	/**
	 * Return the array's base type name
	 * @param param
	 * @return
	 */
	private Class<?> getArrayBaseType(Class<?> param)
	{
		while (param.isArray())
		{
			param = param.getComponentType();
		}
		
		return param;
	}
	
	/**
	 * Return the array's base type name
	 * @param param
	 * @return
	 */
	private Class<?> getArrayBaseType(GenericArrayType param)
	{
		Type type;
		while ((type = param.getGenericComponentType()) instanceof GenericArrayType)
		{
			param = (GenericArrayType)type;
		}
		if (type instanceof TypeVariable)
		{
			type = JSONParser.getInstance().getClassForTypeariable((TypeVariable<?>)type); 
		}
		return (Class<?>) type;
	}

}
