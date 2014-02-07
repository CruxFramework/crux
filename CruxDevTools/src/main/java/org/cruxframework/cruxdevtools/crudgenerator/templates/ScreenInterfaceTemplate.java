package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.widgets.client.maskedlabel.MaskedLabel;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.dto.AcceptableValuesDTO;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.GUI;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.Type;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 *
 * @author daniel.martins
 *
 */

public class ScreenInterfaceTemplate extends Template
{

	protected Set<String> imports = new HashSet<String>();
	private Map<String, Object> fields = new LinkedHashMap<String, Object>();
	protected final String packageName;

	public ScreenInterfaceTemplate(EntityMetadata entity, File outputDir, String packageName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+ScreenInterfaceTemplate.class.getPackage().getName().replace('.', '/')+"/screen-interface-template.vm";
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+ packageName.replace('.', '/'), getTypeName()+".java");
	}


	@Override
	protected Map<String, Object> getContextMap()
	{
		createScreenFields();


		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
        context.put("imports", imports);
        context.put("screenName", getTypeName());
        context.put("fields", fields);

        //imports.add(ScreenWrapper.class.getCanonicalName());

		return context;
	}

	private void createScreenFields()
	{
		for(String fieldName : getEntity().getFieldNames())
		{
			FieldMetadata field = getEntity().getField(fieldName);

			if(field.getAcceptableValues() != null && !ListBox.class.getSimpleName().equals(getFieldType(field)))
			{
				for(AcceptableValuesDTO item : field.getAcceptableValues())
				{
					fields.put(TextUtils.toJavaIdentifier(item.getLabel(),true), getFieldType(field));
				}
			}
			else
			{
				fields.put(TextUtils.toJavaIdentifier(fieldName,true), getFieldType(field));
			}
		}
	}


	private String getFieldType(FieldMetadata field)
	{
		if(field.getFieldGUI() == GUI.Calendar)
		{
			imports.add(DateBox.class.getCanonicalName());

			return TextUtils.toJavaIdentifier(DateBox.class.getSimpleName(),true);
		}
		else if (field.getFieldGUI() == GUI.Text)
		{
			if(field.getFormatter() != null || field.getFieldType() != Type.String)
			{
				imports.add(MaskedTextBox.class.getCanonicalName());

				return TextUtils.toJavaIdentifier(MaskedTextBox.class.getSimpleName(),true);
			}
			else
			{
				imports.add(TextBox.class.getCanonicalName());

				return TextUtils.toJavaIdentifier(TextBox.class.getSimpleName(),true);
			}
		}
		else if (field.getFieldGUI() == GUI.CheckBox)
		{
			imports.add(CheckBox.class.getCanonicalName());

			return TextUtils.toJavaIdentifier(CheckBox.class.getSimpleName(),true);
		}
		else if (field.getFieldGUI() == GUI.ListBox)
		{
			imports.add(ListBox.class.getCanonicalName());

			return TextUtils.toJavaIdentifier(ListBox.class.getSimpleName(),true);
		}
		else if (field.getFieldGUI() == GUI.LongText)
		{
			imports.add(TextBox.class.getCanonicalName());

			return TextUtils.toJavaIdentifier(TextBox.class.getSimpleName(),true);
		}
		else if (field.getFieldGUI() == GUI.RadioBox)
		{
			imports.add(RadioButton.class.getCanonicalName());

			return TextUtils.toJavaIdentifier(RadioButton.class.getSimpleName(),true);
		}
		else if (field.getFieldGUI() == GUI.Label)
		{
			if(field.getFieldType() == Type.String )
			{
				imports.add(Label.class.getCanonicalName());

				return TextUtils.toJavaIdentifier(Label.class.getSimpleName(),true);
			}
			else
			{
				imports.add(MaskedLabel.class.getCanonicalName());

				return TextUtils.toJavaIdentifier(MaskedLabel.class.getSimpleName(),true);
			}
		}
		else
		{
			imports.add(Object.class.getSimpleName());

			return TextUtils.toJavaIdentifier(Object.class.getName(),true);
		}
	}

	public String getTypeName()
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true) + "Screen";
	}


}
