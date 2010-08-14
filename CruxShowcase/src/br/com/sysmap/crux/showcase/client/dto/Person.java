package br.com.sysmap.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

import br.com.sysmap.crux.core.client.controller.ValueObject;
import br.com.sysmap.crux.widgets.client.wizard.WizardData;

@ValueObject
@WizardData("person")
public class Person implements Serializable
{
    private static final long serialVersionUID = -4139032953687869658L;
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