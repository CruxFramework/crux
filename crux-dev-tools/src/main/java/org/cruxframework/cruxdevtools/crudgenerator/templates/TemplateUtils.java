package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.util.Date;

import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;

public class TemplateUtils 
{
	public static String getJavaType(FieldMetadata fieldMetadata) 
	{
		switch (fieldMetadata.getFieldType()) 
		{
			case String: return "String";
			case Integer: return "Integer";
			case Character: return "Character";
			case Boolean: return "Boolean";
			case Date: return Date.class.getCanonicalName();
			case Decimal: return "Double";
			case Binary: return "OhMyGod!";//TODO tratar o binary
		}
		return null;
	}

}
