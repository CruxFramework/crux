package org.cruxframework.cruxdevtools.crudgenerator.metadata;

import java.util.ArrayList;
import java.util.List;

public class IdentifierMetadata
{
	private List<String> fields = new ArrayList<String>();
	private boolean visible;


	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public List<String> getFieldNames()
	{
		return fields;
	}

	public void addField(String field)
	{
		this.fields.add(field);
	}

	public void addField(FieldMetadata field)
	{
		this.fields.add(field.getName());
	}
}
