package br.com.sysmap.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Contact implements Serializable {
	
	private String name;
	private String phone;
	private Date birthday;
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the phone
	 */
	public String getPhone()
	{
		return phone;
	}
	
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday()
	{
		return birthday;
	}

	/**
	 * @param birthDay the birthday to set
	 */
	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}
}