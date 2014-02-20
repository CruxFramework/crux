package org.cruxframework.cruxdevtools.crudgenerator;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.cruxframework.cruxdevtools.crudgenerator.exception.CrudGeneratorException;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;

public abstract class Template
{
	private final EntityMetadata entity;
	private final File outputDir;

	public Template(EntityMetadata entity, File outputDir)
	{
		this.entity = entity;
		this.outputDir = outputDir;
	}

	protected EntityMetadata getEntity()
	{
		return entity;
	}

	protected File getOutputDir()
	{
		return outputDir;
	}

	protected abstract String getTemplateFile();
	protected abstract File getOutputFile();
	protected abstract Map<String, Object> getContextMap() throws CrudGeneratorException;

	public void run()
	{
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();

			org.apache.velocity.Template t = ve.getTemplate(getTemplateFile());

			Map<String, Object> contextMap = getContextMap();
			VelocityContext context = new VelocityContext();
			for (String key : contextMap.keySet())
			{
				context.put(key, contextMap.get(key));
			}

			FileWriter writer = null;
			try
			{
				File outputFile = getOutputFile();
				outputFile.getParentFile().mkdirs();
				writer = new FileWriter(outputFile);
				t.merge(context, writer);
			}
			finally
			{
				writer.close();
			}
		}
		catch (Exception e)
		{
			throw new GenerationException(e);
		}
	}
}
