package org.cruxframework.cruxdevtools.crudgenerator;

import java.io.File;
import java.util.List;

import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;

public interface CrudLayout 
{
	List<Template> getTemplates(EntityMetadata entity, File outputDir);
}
