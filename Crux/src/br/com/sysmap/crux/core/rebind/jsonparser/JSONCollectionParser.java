package br.com.sysmap.crux.core.rebind.jsonparser;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.gwt.user.rebind.SourceWriter;

public class JSONCollectionParser extends JSONComplexTypeParser
{
	/**
	 * Generate the code for test if serialisation must be done. Avoid NullPointerExceptions
	 * @param sourceWriter
	 */
	@Override
	protected void generateTestForNullChecking(SourceWriter sourceWriter)
	{
		sourceWriter.print("(jsonValue != null && jsonValue.isObject() != null && " +
				"jsonValue.isObject().get(\"list\") != null && " +
				"jsonValue.isObject().get(\"list\").isArray() != null)");
	}
	
	/**
	 * Generate code for populate the collection content.
	 * @param parameterType
	 * @param sourceWriter
	 * @param listName
	 */
	@Override
	protected void generatePopulation(Type parameterType, SourceWriter sourceWriter, String listName)
	{
		sourceWriter.print("com.google.gwt.json.client.JSONArray jA"+listName+"_l = jsonValue.isObject().get(\"list\").isArray();");
		
		sourceWriter.print("for (int i"+listName+"_l=0; i"+listName+"_l < jA"+listName+"_l.size(); i"+listName+"_l++)");
		sourceWriter.print("{");
		sourceWriter.print("jsonValue=jA"+listName+"_l.get(i"+listName+"_l);");
		
		if (parameterType instanceof ParameterizedType)
		{
			Type parameterArgType = ((ParameterizedType)parameterType).getActualTypeArguments()[0];
			
			if (parameterArgType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType)parameterArgType;
				JSONParser.getInstance().generateDeserialisationForParameterizedTypes(parameterizedType, sourceWriter, null);
			}
			else if (parameterArgType instanceof GenericArrayType)
			{
				JSONArrayParser.getInstance().generateArrayDeclaration((GenericArrayType)parameterArgType, sourceWriter);
			}
			else if (parameterArgType instanceof TypeVariable)
			{
				Class<?> parameterArgClass = JSONParser.getInstance().getClassForTypeariable((TypeVariable<?>)parameterArgType); 
				sourceWriter.print(parameterArgClass.getName());
			}
			else
			{
				Class<?> param = (Class<?>) parameterArgType;
				if (param.isArray())
				{
					JSONArrayParser.getInstance().generateArrayDeclaration(param, sourceWriter);
				}
				else
				{
					sourceWriter.print(param.getName());
				}
			}
			sourceWriter.print(" "+listName+"_l=");
			JSONParser.getInstance().generateParameterDeserialisationForType(parameterArgType, sourceWriter, listName+"_l");
		}
		else
		{
			sourceWriter.print("Object "+listName+"_l=");
			JSONParser.getInstance().generateParameterDeserialisationForType(Object.class, sourceWriter, listName+"_l");
		}
		sourceWriter.print(";");
		sourceWriter.print(listName+".add("+listName+"_l);");
		sourceWriter.print("}");
	} 

}
