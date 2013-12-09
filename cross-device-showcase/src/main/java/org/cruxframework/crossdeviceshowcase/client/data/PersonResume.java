/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client.data;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PersonResume 
{
	private Integer id;
	private String name;
	
	public PersonResume(Integer id, String name) 
	{
		this.id = id;
		this.name = name;
	}
	
	public Integer getId() 
	{
		return id;
	}
	public String getName() 
	{
		return name;
	}
}
