package org.cruxframework.crux.showcase.client.controller;

import java.io.Serializable;
import java.util.Date;

import org.cruxframework.crux.core.client.controller.ValueObject;

@ValueObject
@SuppressWarnings("serial")
public class Person implements Serializable
{
	private String name;
	private Long phone;
	private Date dateOfBirth;
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Long getPhone(){
		return phone;
	}

	public void setPhone(Long phone){
		this.phone = phone;
	}

	public Date getDateOfBirth(){
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}
}
