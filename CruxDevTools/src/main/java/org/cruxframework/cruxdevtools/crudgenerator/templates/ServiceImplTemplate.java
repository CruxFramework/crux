package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.Map;

import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class ServiceImplTemplate extends ServiceInterfaceTemplate 
{
	private final String remoteServiceInterface;

	public ServiceImplTemplate(EntityMetadata entity, File outputDir, String packageName, String dtoClassName, String remoteServiceInterface) 
	{
		super(entity, outputDir, packageName, dtoClassName);
		this.remoteServiceInterface = remoteServiceInterface;
	}

	@Override
	protected String getTemplateFile() 
	{
		return "/"+ServiceImplTemplate.class.getPackage().getName().replace('.', '/')+"/service-impl-template.vm"; 
	}

	@Override
	protected Map<String, Object> getContextMap() 
	{
		Map<String, Object> context = super.getContextMap();
		
		String simpleRemoteInterfaceClassName = remoteServiceInterface.substring(remoteServiceInterface.lastIndexOf(".") + 1);
		context.put("remoteInterface", simpleRemoteInterfaceClassName);
        
        imports.add(remoteServiceInterface);
		return context;
	}
	
	@Override
	public String getTypeName() 
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"ServiceImpl";
	}
}
