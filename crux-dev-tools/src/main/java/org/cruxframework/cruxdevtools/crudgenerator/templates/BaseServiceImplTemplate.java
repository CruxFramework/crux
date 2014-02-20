package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.cruxdevtools.crudgenerator.Template;

public class BaseServiceImplTemplate extends Template
{
	protected final String packageName;
	protected Set<String> imports = new HashSet<String>();

	public BaseServiceImplTemplate(File outputDir, String packageName)
	{
		super(null, outputDir);
		this.packageName = packageName;
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+BaseServiceImplTemplate.class.getPackage().getName().replace('.', '/')+"/base-service-impl-template.vm";
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

		return context;
	}

	public String getTypeName()
	{
		return "BaseServiceImpl";
	}
}
