/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.showcase.client.dto;

import java.util.Date;

import br.com.sysmap.crux.core.client.controller.ValueObject;
import br.com.sysmap.crux.core.client.screen.CruxSerializable;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.screen.serializable.annotation.SerializableName;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@ValueObject
@SerializableName("person")
public class Person implements CruxSerializable
{
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

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#deserialize(java.lang.String)
	 */
	public Object deserialize(String serializedData){
		if (serializedData != null && serializedData.length() > 0){
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			String dateAttr = data.getAttribute("dateOfBirth");
			Person person = new Person(data.getAttribute("name"), 
								 data.getAttribute("phone"), 
								 StringUtils.isEmpty(dateAttr)?null:new Date(Long.parseLong(dateAttr)));
			person.setAddress(new Address());
			person.getAddress().setStreet(data.getAttribute("address.street"));
			person.getAddress().setCity(data.getAttribute("address.city"));
			person.getAddress().setState(data.getAttribute("address.state"));
			
			return person;
		}
		return null;
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#newArray(int)
	 */
	public Object[] newArray(int size){
	    return new Person[size];
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#serialize()
	 */
	public String serialize(){
		Document document = XMLParser.createDocument();

		Element data = document.createElement("data");
		document.appendChild(data);
		
		data.setAttribute("name", name);
		data.setAttribute("phone", phone);
		if (dateOfBirth!= null){
			data.setAttribute("dateOfBirth", Long.toString(dateOfBirth.getTime()));
		}

		if (this.address != null)
		{
			data.setAttribute("address.street", address.getStreet());
			data.setAttribute("address.city", address.getCity());
			data.setAttribute("address.state", address.getState());
		}
		
		return document.toString();
    }
}

