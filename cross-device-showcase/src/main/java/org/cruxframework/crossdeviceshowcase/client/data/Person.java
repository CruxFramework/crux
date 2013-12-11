/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client.data;

import java.util.Date;

import org.cruxframework.crux.core.client.db.annotation.Store;
import org.cruxframework.crux.core.client.db.annotation.Store.Indexed;
import org.cruxframework.crux.core.client.db.annotation.Store.Key;
import org.cruxframework.crux.core.client.dto.DataObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Store(Person.STORE_NAME)
@DataObject(Person.STORE_NAME)
public class Person 
{
	public static final String STORE_NAME = "Person"; 
	
	private Integer id;
	private String name;
	private Date dateOfBirth;
	private Address address;
	
	@Key(autoIncrement=true)
	public Integer getId() 
	{
		return id;
	}
	
	public void setId(Integer id) 
	{
		this.id = id;
	}

	@Indexed
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public Date getDateOfBirth() 
	{
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) 
	{
		this.dateOfBirth = dateOfBirth;
	}
	
	public Address getAddress() 
	{
		return address;
	}
	
	public void setAddress(Address address) 
	{
		this.address = address;
	}
}
