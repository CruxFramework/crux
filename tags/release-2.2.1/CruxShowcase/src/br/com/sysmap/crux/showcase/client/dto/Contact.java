package br.com.sysmap.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Contact implements Serializable {
	
	public static enum Gender{
		FEMALE,
		MALE
	}

	private String name;
	private String phone;
	private Date birthday;
	private Gender gender;
	
	public Contact(){
		
	}
	
	public Contact(String name, String phone, Date birthday, Gender gender)	{
		this.name = name;
		this.phone = phone;
		this.birthday = birthday;
		this.gender = gender;
	}
	
	/**
	 * @return the name
	 */
	public String getName()	{
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
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
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}
}