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
package br.com.sysmap.crux.core.client.screen;

import br.com.sysmap.crux.core.client.controller.document.invoke.CrossDocumentException;
import br.com.sysmap.crux.core.client.controller.document.invoke.Target;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ScreenAccessor
{
	/**
	 * Make a call to a cross document method.
	 * 
	 * @param serializedData the call itself. Contains the method and all parameters serialized.
	 * @param target Where the method resides
	 * @return
	 */
	protected String invokeCrossDocument(String serializedData, Target target)
	{
		switch (target)
        {
	        case TOP: return invokeCrossDocumentOnTop(serializedData);
	        default: throw new CrossDocumentException("Invalid Target");//TODO - Thiago: message here.
        }
	}
	
	/**
	 * @param serializedData
	 * @return
	 */
	protected String invokeCrossDocumentOnTop(String serializedData)
	{
		return callTopControllerAccessor(serializedData);
	}
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	private native String callTopControllerAccessor(String serializedData)/*-{
		return $wnd.top._cruxCrossDocumentAccessor(serializedData);
	}-*/;	
}
