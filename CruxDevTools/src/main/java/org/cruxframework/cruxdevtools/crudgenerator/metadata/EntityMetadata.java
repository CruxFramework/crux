package org.cruxframework.cruxdevtools.crudgenerator.metadata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author thiago
 *
 */
public class EntityMetadata
{
	private final String name;
	private final IdentifierMetadata identifier;
	private Map<String, FieldMetadata> fields = new LinkedHashMap<String, FieldMetadata>();
	private Map<String, RelationshipMetadata> relationships = new HashMap<String, RelationshipMetadata>();

	public EntityMetadata(String name, IdentifierMetadata identifier)
	{
		this.name = name;
		this.identifier = identifier;
	}

	public String getName()
	{
		return name;
	}

	public IdentifierMetadata getIdentifier()
	{
		return identifier;
	}

	public Set<String> getFieldNames()
	{
		return fields.keySet();
	}

	public void addField(FieldMetadata field)
	{
		this.fields.put(field.getName(), field);
	}

	public FieldMetadata getField(String name)
	{
		return this.fields.get(name);
	}

	public Set<String> getRelationshipNames()
	{
		return relationships.keySet();
	}

	public void addRelationship(RelationshipMetadata relationship)
	{
		this.relationships.put(relationship.getName(), relationship);
	}

	public RelationshipMetadata getRelationship(String name)
	{
		return this.relationships.get(name);
	}

}
