package org.cruxframework.crux.showcase.client.dto;

import java.io.Serializable;

import org.cruxframework.crux.core.client.controller.ValueObject;

@ValueObject
public class Address implements Serializable
{
    private static final long serialVersionUID = -910929623393552232L;
	private String street;
	private String city;
	private String state;
//	private Address addr;
	
	public Address()
    {
	    // TODO Auto-generated constructor stub
    }
	
	public Address(String street)
    {
		this.street = street;
    }
	
	public String getStreet()
    {
    	return street;
    }
	public void setStreet(String street)
    {
    	this.street = street;
    }
	public String getCity()
    {
    	return city;
    }
	public void setCity(String city)
    {
    	this.city = city;
    }
	public String getState()
    {
    	return state;
    }
	public void setState(String state)
    {
    	this.state = state;
    }

/*	public Address getAddr()
    {
    	return addr;
    }

	public void setAddr(Address addr)
    {
    	this.addr = addr;
    }*/
}
