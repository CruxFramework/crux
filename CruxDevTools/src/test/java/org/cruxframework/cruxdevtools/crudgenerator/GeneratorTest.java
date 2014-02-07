package org.cruxframework.cruxdevtools.crudgenerator;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.cruxframework.cruxdevtools.crudgenerator.dto.AcceptableValuesDTO;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.GUI;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.Type;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.IdentifierMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.templates.CruxDefaultProjectLayout;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTest
{

	private static final String PACKAGE_NAME = "br.com.triggo.test";

	private static final String ENTITY_NAME = "Car";

	private File outputFolder;

	@Before
	public void setUp() throws Exception
	{
		outputFolder = new File("../Car/src");

		deleteFiles(outputFolder);
	}

	private void deleteFiles(File file)
	{
		if (file.exists())
		{
			if(file.isDirectory())
			{
				File[] files = file.listFiles();

				if(files != null)
				{
					for (File child : files)
					{
						deleteFiles(child);
					}
				}
			}
			else
			{
				if(deletableFiles(file))
				{
					file.delete();
				}
			}
		}
	}

	private boolean deletableFiles(File file)
	{
		if(  (ENTITY_NAME+"Controller.java").equals(file.getName())
		  || "Crux.properties".equals(file.getName())
		  || "CruxModuleConfig.properties".equals(file.getName())
		  || (ENTITY_NAME+".gwt.xml").equals(file.getName())
		  || (ENTITY_NAME+".module.xml").equals(file.getName()))
		{
			return false;
		}

		return true;
	}

	private EntityMetadata loadEntity()
	{
		IdentifierMetadata identifier = new IdentifierMetadata();
		EntityMetadata target = new EntityMetadata(ENTITY_NAME, identifier);

		FieldMetadata mark = new FieldMetadata();
		mark.setName("mark");
		mark.setFieldType(Type.String);
		mark.setFieldGUI(GUI.ListBox);
		mark.setLabel("Mark");
		mark.setRequired(true);
		mark.setMaxLenght(10);

		ArrayList<AcceptableValuesDTO> markAcceptableValues = new ArrayList<AcceptableValuesDTO>();
		markAcceptableValues.add(new AcceptableValuesDTO("Chevrolet", "Chevrolet"));
		markAcceptableValues.add(new AcceptableValuesDTO("Dodge", "Dodge"));
		markAcceptableValues.add(new AcceptableValuesDTO("Ford", "Ford"));
		markAcceptableValues.add(new AcceptableValuesDTO("Toyota", "Toyota"));
		mark.setAcceptableValues(markAcceptableValues);


		FieldMetadata model = new FieldMetadata();
		model.setName("model");
		model.setFieldType(Type.String);
		model.setFieldGUI(GUI.ListBox);
		model.setLabel("Model");

		FieldMetadata year = new FieldMetadata();
		year.setName("year");
		year.setFieldType(Type.Integer);
		year.setRequired(true);
		year.setFieldGUI(GUI.Text);
		year.setLabel("Year");

		FieldMetadata fuel = new FieldMetadata();
		fuel.setName("fuel");
		fuel.setFieldType(Type.Boolean);
		fuel.setFieldGUI(GUI.RadioBox);
		fuel.setRequired(true);
		fuel.setLabel("Fuel");

		ArrayList<AcceptableValuesDTO> fuelAcceptableValues = new ArrayList<AcceptableValuesDTO>();
		fuelAcceptableValues.add(new AcceptableValuesDTO("Gas", "G"));
		fuelAcceptableValues.add(new AcceptableValuesDTO("Ethanol", "E"));
		fuel.setAcceptableValues(fuelAcceptableValues);


		FieldMetadata serialNumber = new FieldMetadata();
		serialNumber.setName("serialNumber");
		serialNumber.setLabel("Serial Number");
		serialNumber.setFieldType(Type.Decimal);
		serialNumber.setFieldGUI(GUI.Label);
		serialNumber.setMaxLenght(1000);

		FieldMetadata numberPlate = new FieldMetadata();
		numberPlate.setName("numberPlate");
		numberPlate.setLabel("Number Plate");
		numberPlate.setFieldType(Type.Integer);
		numberPlate.setFieldGUI(GUI.Label);
		numberPlate.setMaxLenght(10);

		FieldMetadata foundationDate = new FieldMetadata();
		foundationDate.setName("foundationDate");
		foundationDate.setLabel("Foundation Date");
		foundationDate.setFieldGUI(GUI.Calendar);
		foundationDate.setFieldType(Type.Date);

		FieldMetadata optional = new FieldMetadata();
		optional.setName("optional");
		optional.setLabel("Optionals");
		optional.setFieldGUI(GUI.CheckBox);
		optional.setFieldType(Type.Boolean);

		ArrayList<AcceptableValuesDTO> optionalAcceptableValues = new ArrayList<AcceptableValuesDTO>();
		optionalAcceptableValues.add(new AcceptableValuesDTO("ABS Brake", "ABS Brake"));
		optionalAcceptableValues.add(new AcceptableValuesDTO("Air Bag", "Air Bag"));
		optionalAcceptableValues.add(new AcceptableValuesDTO("Sunroof", "Sunroof"));
		optional.setAcceptableValues(optionalAcceptableValues);

		FieldMetadata field1 = new FieldMetadata();
		field1.setName("value");
		field1.setRequired(true);
		field1.setLabel("Value");
		field1.setMaxLenght(102);
		field1.setFieldGUI(GUI.Text);
		field1.setFieldType(Type.Decimal);
		field1.setDisabled(true);


		identifier.addField("serialNumber");
		identifier.addField("numberPlate");
		identifier.setVisible(true);

		target.addField(mark);
		target.addField(model);
		target.addField(year);
		target.addField(fuel);
		target.addField(serialNumber);
		target.addField(numberPlate);
		target.addField(optional);
		target.addField(field1);

		return target;
	}

	@Test
	public void testCruxDefaultProjectLayoutGeneration()
	{
		try
		{
			EntityMetadata target = loadEntity();

			Generator generator = new Generator(outputFolder, target, PACKAGE_NAME, new CruxDefaultProjectLayout(PACKAGE_NAME));
			generator.generate();

			chekGeneratedFile("br/com/triggo/test/client/dto/carDTO.java");
			chekGeneratedFile("br/com/triggo/test/client/remote/BaseService.java");
			chekGeneratedFile("br/com/triggo/test/client/remote/BaseServiceAsync.java");
			chekGeneratedFile("br/com/triggo/test/client/remote/CarService.java");
			chekGeneratedFile("br/com/triggo/test/client/remote/CarServiceAsync.java");
			chekGeneratedFile("br/com/triggo/test/client/controller/CarController.java");
			chekGeneratedFile("br/com/triggo/test/client/controller/BaseController.java");
			chekGeneratedFile("br/com/triggo/test/server/service/CarServiceImpl.java");
			chekGeneratedFile("br/com/triggo/test/server/service/BaseServiceImpl.java");
		}
		catch (Exception e)
		{
			fail("Error generating project: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void chekGeneratedFile(String file) throws IOException {
		if (!new File(outputFolder, file).exists())
		{
			fail("File "+file+" was not generated on expected folder ("+outputFolder.getCanonicalPath()+"). ");
		}
	}
}
