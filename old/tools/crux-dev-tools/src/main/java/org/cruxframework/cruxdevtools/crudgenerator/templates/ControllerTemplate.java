package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

public class ControllerTemplate extends Template
{
	protected final String packageName;
	protected Set<String> imports = new HashSet<String>();

	public ControllerTemplate(EntityMetadata entity, File outputDir, String packageName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
	}


	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".java");
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+AbstractControllerTemplate.class.getPackage().getName().replace('.', '/')+"/controller-template.vm";
	}


	@Override
	protected Map<String, Object> getContextMap()
	{
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
		context.put("superClassName", TextUtils.toJavaIdentifier(getEntity().getName(), true) + "AbstractController" );

		imports.add(Controller.class.getCanonicalName());

		context.put("imports", imports);

		return context;
	}

	public String getTypeName()
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"Controller";
	}


}
