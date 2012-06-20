package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

@MessageName("myMessages")
public interface MyMessages extends Messages
{
	@DefaultMessage("This form contains internationalized labels. Current locale: default (en_US)")
	String currentLocaleTitle();

	@DefaultMessage("Name:")
	String name();

	@DefaultMessage("Phone: ")
	String phone();
	
	@DefaultMessage("Date of Birth: ")
	String birthDate();

	@DefaultMessage("Change Locale")
	String changeLocaleButton();
}

