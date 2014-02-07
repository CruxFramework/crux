package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.IdentifierMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class CruxXmlTemplate extends Template
{
	protected final String packageName;
	protected Map<String, FieldMetadata> fields = new LinkedHashMap<String, FieldMetadata>();

	public CruxXmlTemplate(EntityMetadata entity, File outputDir, String packageName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".crux.xml");
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+CruxXmlTemplate.class.getPackage().getName().replace('.', '/')+"/crux-xml-template.vm";
	}

	@Override
	protected Map<String, Object> getContextMap()
	{
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("entity", getTypeName());
		context.put("textUtils", new TextUtils());
		processFields(context);

		return context;
	}

	public String getTypeName()
	{
		return Character.toLowerCase(getEntity().getName().charAt(0)) + getEntity().getName().substring(1);
	}

	protected void processFields(Map<String, Object> context)
	{
		for (String fieldName : getEntity().getFieldNames())
		{
			FieldMetadata field = getEntity().getField(fieldName);
			IdentifierMetadata identifier = getEntity().getIdentifier();

			if(!identifier.getFieldNames().contains(fieldName) || (identifier.getFieldNames().contains(fieldName) && identifier.isVisible()))
			{
				fields.put(fieldName, field);

				if((identifier.getFieldNames().contains(fieldName) && !identifier.isVisible()))
				{
					field.setDisabled(true);
				}
			}
		}

		context.put("fields", fields);
	}
}
