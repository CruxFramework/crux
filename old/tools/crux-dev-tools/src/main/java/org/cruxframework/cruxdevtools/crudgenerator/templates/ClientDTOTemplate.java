package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.controller.ParameterObject;
import org.cruxframework.cruxdevtools.crudgenerator.GenerationException;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.dto.AcceptableValuesDTO;
import org.cruxframework.cruxdevtools.crudgenerator.exception.CrudGeneratorException;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.GUI;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.Type;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.IdentifierMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class ClientDTOTemplate extends Template
{
	private final String packageName;
	private Set<String> imports = new HashSet<String>();
	private Map<String, String> fields = new LinkedHashMap<String, String>();
	private Map<String, String> identifierFields = new LinkedHashMap<String, String>();
	private boolean compositeIdentifier = false;

	public ClientDTOTemplate(EntityMetadata entity, File outputDir, String packageName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+ClientDTOTemplate.class.getPackage().getName().replace('.', '/')+"/client-dto-template.vm";
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".java");
	}

	@Override
	protected Map<String, Object> getContextMap() throws CrudGeneratorException
	{
		createFields();

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
        context.put("imports", imports);
        context.put("fields", fields);
        context.put("identifierFields", identifierFields);
        context.put("compositeIdentifier", compositeIdentifier);

        //imports.add(ValueObject.class.getCanonicalName());
        imports.add(Serializable.class.getCanonicalName());
        imports.add(Parameter.class.getCanonicalName());
        imports.add(ParameterObject.class.getCanonicalName());

		return context;
	}

	public String getTypeName()
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"DTO";
	}

	private void createFields() throws CrudGeneratorException
	{
		IdentifierMetadata identifier = getEntity().getIdentifier();

		for (String fieldName : getEntity().getFieldNames())
		{
			FieldMetadata fieldMetadata = getEntity().getField(fieldName);
			String javaType = TemplateUtils.getJavaType(fieldMetadata);
			String simpleJavaType = javaType.substring(javaType.lastIndexOf(".") + 1);

			if(javaType.contains("."))
			{
				imports.add(javaType);
			}

			if (identifier.getFieldNames().contains(fieldMetadata.getName()))
			{
				identifierFields.put(TextUtils.toJavaIdentifier(fieldMetadata.getName(), false), simpleJavaType);
			}
			else
			{
				if( (fieldMetadata.getFieldGUI().equals(GUI.CheckBox) || fieldMetadata.getFieldGUI().equals(GUI.RadioBox)))
				{
					if(fieldMetadata.getFieldType().equals(Type.Boolean))
					{
						for(AcceptableValuesDTO values : fieldMetadata.getAcceptableValues())
						{
							fields.put(TextUtils.toJavaIdentifier(values.getLabel(),false), simpleJavaType);
						}
					}
					else
					{
						throw new CrudGeneratorException("Incompatible types of GUI and Types");
					}
				}
				else
				{
					fields.put(TextUtils.toJavaIdentifier(fieldMetadata.getName(), false), simpleJavaType);
				}
			}
		}
		if (identifierFields.size() > 1)
		{
			this.compositeIdentifier = true;
		}
		else if (identifierFields.size() == 1)
		{
			String key = identifierFields.keySet().iterator().next();
			fields.put(key, identifierFields.get(key));
		}
		else
		{
			throw new GenerationException("Invalid Identifier declaration for entity "+getEntity().getName());
		}
	}
}
