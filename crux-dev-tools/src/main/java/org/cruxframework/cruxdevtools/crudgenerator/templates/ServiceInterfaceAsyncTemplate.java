package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;

import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class ServiceInterfaceAsyncTemplate extends ServiceInterfaceTemplate 
{
	public ServiceInterfaceAsyncTemplate(EntityMetadata entity, File outputDir, String packageName, String dtoClassName) 
	{
		super(entity, outputDir, packageName, dtoClassName);
	}

	@Override
	protected String getTemplateFile() 
	{
		return "/"+ServiceInterfaceAsyncTemplate.class.getPackage().getName().replace('.', '/')+"/service-interface-async-template.vm"; 
	}
	
	@Override
	public String getTypeName() 
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"ServiceAsync";
	}
}
