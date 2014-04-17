package org.cruxframework.cruxdevtools.crudgenerator.metadata;

import java.util.List;

import org.cruxframework.cruxdevtools.crudgenerator.dto.AcceptableValuesDTO;

public class FieldMetadata
{

	public static enum Type{String, Character, Integer, Decimal, Date, Boolean, Binary}
	public static enum GUI{Label, Text, LongText, Calendar, ListBox, CheckBox, RadioBox}

	private int lenght;
	private int maxLenght;
	private String name;
	private String label;
	private boolean required;
	private boolean disabled;
	private List<AcceptableValuesDTO> acceptableValues;
	private String formatter;
	private Type fieldType;
	private GUI fieldGUI;


	public int getLenght()
	{
		return lenght;
	}
	public void setLenght(int lenght)
	{
		this.lenght = lenght;
	}
	public int getMaxLenght()
	{
		return maxLenght;
	}
	public void setMaxLenght(int maxLenght)
	{
		this.maxLenght = maxLenght;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getLabel()
	{
		return label;
	}
	public void setLabel(String label)
	{
		this.label = label;
	}
	public boolean isRequired()
	{
		return required;
	}
	public void setRequired(boolean required)
	{
		this.required = required;
	}

	public List<AcceptableValuesDTO> getAcceptableValues()
	{
		return acceptableValues;
	}
	public void setAcceptableValues(List<AcceptableValuesDTO> acceptableValues)
	{
		this.acceptableValues = acceptableValues;
	}
	public String getFormatter()
	{
		return formatter;
	}
	public void setFormatter(String formatter)
	{
		this.formatter = formatter;
	}
	public Type getFieldType()
	{
		return fieldType;
	}
	public void setFieldType(Type fieldType)
	{
		this.fieldType = fieldType;
	}
	public GUI getFieldGUI()
	{
		return fieldGUI;
	}
	public void setFieldGUI(GUI fieldGUI)
	{
		this.fieldGUI = fieldGUI;
	}
	public boolean isDisabled()
	{
		return disabled;
	}
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

}
