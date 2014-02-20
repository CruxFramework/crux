package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.cruxdevtools.crudgenerator.GenerationException;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.IdentifierMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class ServiceInterfaceTemplate extends Template
{
	protected final String packageName;
	protected Set<String> imports = new HashSet<String>();
	protected final String dtoClassName;

	public ServiceInterfaceTemplate(EntityMetadata entity, File outputDir, String packageName, String dtoClassName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
		this.dtoClassName = dtoClassName;
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+ServiceInterfaceTemplate.class.getPackage().getName().replace('.', '/')+"/service-interface-template.vm";
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".java");
	}

	@Override
	protected Map<String, Object> getContextMap()
	{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
        context.put("imports", imports);
		String simpleJavaType = dtoClassName.substring(dtoClassName.lastIndexOf(".") + 1);
		context.put("entity", simpleJavaType);
		context.put("key", getIdentifierType());

        imports.add(dtoClassName);
		return context;
	}

	public String getTypeName()
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"Service";
	}

	protected String getIdentifierType()
	{
		IdentifierMetadata identifier = getEntity().getIdentifier();

		List<String> fieldNames = identifier.getFieldNames();
		if (fieldNames.size() == 1)
		{
			FieldMetadata fieldMetadata = getEntity().getField(fieldNames.get(0));
			String javaType = TemplateUtils.getJavaType(fieldMetadata);
			String simpleJavaType = javaType.substring(javaType.lastIndexOf(".") + 1);

			if(javaType.contains("."))
			{
				imports.add(javaType);
			}

			return simpleJavaType;
		}
		else if (fieldNames.size() > 1)
		{
			String simpleJavaType = dtoClassName.substring(dtoClassName.lastIndexOf(".") + 1);
			return simpleJavaType+".PrimaryKey";
		}
		else
		{
			throw new GenerationException("Invalid Identifier declaration for entity "+getEntity().getName());
		}
	}
}