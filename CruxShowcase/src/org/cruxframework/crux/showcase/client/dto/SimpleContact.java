package org.cruxframework.crux.showcase.client.dto;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.controller.ParameterObject;

@ParameterObject
public class SimpleContact
{
	@Parameter(required=true)
	private String name;
	
	@Parameter(required=true)
	private Long phone;
	
	@Parameter(required=true)
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
