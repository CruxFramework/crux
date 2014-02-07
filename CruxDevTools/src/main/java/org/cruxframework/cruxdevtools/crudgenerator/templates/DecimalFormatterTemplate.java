package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;

import com.google.gwt.i18n.client.NumberFormat;

public class DecimalFormatterTemplate extends Template
{
	protected final String packageName;
	protected final String formatterName;
	protected List<String> imports = new ArrayList<String>();

	public DecimalFormatterTemplate(EntityMetadata entity, File outputDir, String packageName, String formatterName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
		this.formatterName = formatterName;

	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), formatterName+".java");
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+DecimalFormatterTemplate.class.getPackage().getName().replace('.', '/')+"/decimal-formatter-template.vm";
	}

	@Override
	protected Map<String, Object> getContextMap()
	{
		imports.add(Formatter.class.getCanonicalName());
		imports.add(InvalidFormatException.class.getCanonicalName());
		imports.add(FormatterName.class.getCanonicalName());
		imports.add(NumberFormat.class.getCanonicalName());

		Map<String, Object> contextMap = new HashMap<String, Object>();

		contextMap.put("imports", imports);
		contextMap.put("packageName", packageName);

		return contextMap;
	}

}
