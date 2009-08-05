package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.i18n.Name;

import com.google.gwt.i18n.client.Messages;

@Name("myMessages")
public interface MyMessages extends Messages
{
	@DefaultMessage("I18N on page Declaratively")
	String message1();

	@DefaultMessage("Change Locale")
	String myButton();
}

