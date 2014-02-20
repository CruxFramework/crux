package org.cruxframework.cruxdevtools.crudgenerator.dto;

/**
 *
 * @author daniel.martins
 *
 */
public class AcceptableValuesDTO
{
	private String label;

	private Object value;

	public AcceptableValuesDTO(String label, Object value)
	{
		this.label = label;
		this.value = value;
	}


	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

}
