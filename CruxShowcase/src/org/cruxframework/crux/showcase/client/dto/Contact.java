package org.cruxframework.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Contact implements Serializable {
	
	public static enum Gender{
		FEMALE,
		MALE
	}

    private int id;
    private String name;
	private String phone;
	private Date birthday;
	private Gender gender;
	private Address	address;
	
	public Contact(int id, String name, String phone, Date birthday, Gender gender, Address address){
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.birthday = birthday;
		this.gender = gender;
		this.address = address;
	}
	
	public Contact(){		
	}
	
	public String getName()	{
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Address getAddress() {
    	return address;
    }

	public void setAddress(Address address) {
    	this.address = address;
    }

	public int getId() {
    	return id;
    }

	public void setId(int id) {
    	this.id = id;
    }
}