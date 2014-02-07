package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.cruxdevtools.crudgenerator.CrudLayout;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;

public class CruxDefaultProjectLayout implements CrudLayout
{
	private final String basePackageName;


	public CruxDefaultProjectLayout(String basePackageName)
	{
		this.basePackageName = basePackageName;
	}

	public List<Template> getTemplates(EntityMetadata entity, File outputDir)
	{
		List<Template> result = new ArrayList<Template>();

		String dtoPackageName = basePackageName+".client.dto";
		String remotePackageName = basePackageName+".client.remote";
		String screenPackageName = basePackageName+".client.screen";

		//Create the ClientDTO
		ClientDTOTemplate clientDTOTemplate = new ClientDTOTemplate(entity, outputDir, dtoPackageName);
		result.add(clientDTOTemplate);

		//Create the Service Interface
		String dtoClassName = dtoPackageName+"."+clientDTOTemplate.getTypeName();
		ServiceInterfaceTemplate serviceInterfaceTemplate = new ServiceInterfaceTemplate(entity, outputDir, remotePackageName, dtoClassName);
		result.add(serviceInterfaceTemplate);

		//Create the Service Async Interface
		String remoteServiceInterface = remotePackageName +"."+serviceInterfaceTemplate.getTypeName();
		ServiceInterfaceAsyncTemplate serviceInterfaceAsyncTemplate = new ServiceInterfaceAsyncTemplate(entity, outputDir, remotePackageName, dtoClassName);
		result.add(serviceInterfaceAsyncTemplate);

		//Create the Service Implementation
		ServiceImplTemplate serviceImplTemplate = new ServiceImplTemplate(entity, outputDir, basePackageName+".server.service", dtoClassName, remoteServiceInterface);
		result.add(serviceImplTemplate);

		//Create the Base Service Interface
		BaseServiceInterfaceTemplate baseServiceInterfaceTemplate = new BaseServiceInterfaceTemplate(outputDir, basePackageName+".client.remote");
		result.add(baseServiceInterfaceTemplate);

		//Create the Base Service Async Interface
		BaseServiceInterfaceAsyncTemplate baseServiceInterfaceAsyncTemplate = new BaseServiceInterfaceAsyncTemplate(outputDir, basePackageName+".client.remote");
		result.add(baseServiceInterfaceAsyncTemplate);

		//Create the Base Service Implementation
		BaseServiceImplTemplate baseServiceImplTemplate = new BaseServiceImplTemplate(outputDir, basePackageName+".server.service");
		result.add(baseServiceImplTemplate);

		//Create the Screen Interface
		ScreenInterfaceTemplate screenInterfaceTemplate = new ScreenInterfaceTemplate(entity, outputDir, screenPackageName );
		result.add(screenInterfaceTemplate);

		//Create the Abstract Controller
		String screenClassName = screenPackageName + "." + screenInterfaceTemplate.getTypeName();
		AbstractControllerTemplate abstractControllerTemplate = new AbstractControllerTemplate(entity, outputDir, basePackageName+".client.controller", dtoClassName, remoteServiceInterface, screenClassName);
		result.add(abstractControllerTemplate);

		//Create the Base Controller
		BaseControllerTemplate baseControllerTemplate = new BaseControllerTemplate(outputDir, basePackageName+".client.controller");
		result.add(baseControllerTemplate);

		//Create the Controller Template
		if(!new File(outputDir + "/" + basePackageName.replace(".", "/") + "/client/controller/" + entity.getName()+"Controller.java").exists())
		{
			ControllerTemplate controllerTemplate = new ControllerTemplate(entity, outputDir, basePackageName+".client.controller");
			result.add(controllerTemplate);
		}

		//Create the Crux Xml
		CruxXmlTemplate cruxXmlTemplate = new CruxXmlTemplate(entity, outputDir, basePackageName+".public");
		result.add(cruxXmlTemplate);

		//Create the Boolean Formatter
		BooleanFormatterTemplate booleanFormatterTemplate = new BooleanFormatterTemplate(entity, outputDir, basePackageName+".client.formatter","BooleanFormatter");
		result.add(booleanFormatterTemplate);

		//Create the Date Formatter
		DateFormatterTemplate dateFormatterTemplate = new DateFormatterTemplate(entity, outputDir, basePackageName+".client.formatter","DateFormatter");
		result.add(dateFormatterTemplate);

		//Create the Decimal Formatter
		DecimalFormatterTemplate decimalFormatterTemplate = new DecimalFormatterTemplate(entity, outputDir, basePackageName+".client.formatter","DecimalFormatter");
		result.add(decimalFormatterTemplate);

		//Create the Number Formatter
		NumberFormatterTemplate numberFormatterTemplate = new NumberFormatterTemplate(entity, outputDir, basePackageName+".client.formatter","NumberFormatter");
		result.add(numberFormatterTemplate);

		return result;
	}
}
