package org.cruxframework.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

import org.cruxframework.crux.core.client.controller.ValueObject;
import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.dto.DataObjectIdentifier;

@ValueObject
@DataObject("person")
public class Person implements Serializable
{
    private static final long serialVersionUID = -4139032953687869658L;

    @DataObjectIdentifier
    private String name;
	private String phone;
	private Date dateOfBirth;
	private Address address;

	public Person(){
	}
	
	public Person(String name, String phone, Date dateOfBirth){
		this.name = name;
		this.phone = phone;
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getPhone(){
		return phone;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public Date getDateOfBirth(){
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth){
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