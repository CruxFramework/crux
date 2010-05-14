package br.com.sysmap.crux.showcase.client.dto;

import br.com.sysmap.crux.core.client.controller.ValueObject;

@ValueObject
public class Address
{
	private String street;
	private String city;
	private String state;
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
}
