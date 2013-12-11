/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ShowcaseMessages extends Messages
{
	@DefaultMessage("O seu navegador não suporta a criação de bancos de dados locais.")
	String databaseNotSupportedError();
}
