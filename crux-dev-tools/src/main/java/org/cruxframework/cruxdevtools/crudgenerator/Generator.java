package org.cruxframework.cruxdevtools.crudgenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;

public class Generator
{
	private final List<Template> templates;
	private final boolean overrideExistingFiles;
	private File outputFolder;
	private String packageName;

	public Generator(File outputFolder, EntityMetadata entity, String packageName, CrudLayout crudLayout)
	{
		this(outputFolder, entity, packageName, crudLayout, true);
	}

	public Generator(File outputFolder, EntityMetadata entity, String packageName, CrudLayout crudLayout, boolean overrideExistingFiles)
	{
		this.overrideExistingFiles = overrideExistingFiles;
		this.templates = crudLayout.getTemplates(entity, outputFolder);
		this.outputFolder = outputFolder;
		this.packageName = packageName;
	}

	/**
	 * @throws IOException
	 *
	 */
	public void generate() throws IOException
	{
		for (Template template : templates)
		{
			if(overrideExistingFiles || !template.getOutputFile().exists())
			{
				template.run();
			}
		}

		createStaticFiles();
	}

	private void createStaticFiles() throws IOException
	{
		FileUtils.copyFilesFromDir(new File("./styles"), new File(outputFolder + "/" + packageName.replace(".", "/") + "/public/"));
	}
}
