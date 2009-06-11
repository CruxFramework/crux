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
package br.com.sysmap.crux.advanced.client.dialog;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Confirm
{
	public static final String DEFAULT_STYLE_NAME = "crux-Confirm" ;
	private static ConfirmController confirmController = null;
	
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 */
	public static void show(String title, String message, String okCall, String cancelCall)
	{
		show(title, message, okCall, cancelCall, DEFAULT_STYLE_NAME);
	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 * @param styleName
	 */
	public static void show(String title, String message, String okCall, String cancelCall, String styleName)
	{
		if (confirmController == null)
		{
			confirmController = new ConfirmController(); 
		}
		confirmController.showConfirm(new ConfirmData(title, message, okCall, cancelCall, styleName));
	}

}
