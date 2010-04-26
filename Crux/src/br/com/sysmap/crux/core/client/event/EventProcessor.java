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
package br.com.sysmap.crux.core.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Abstraction for a event processor. Each processor process events according with the 
 * properties in the TagEvent Object.
 * @author Thiago
 *
 */
public abstract class EventProcessor 
{
	boolean _hasReturn = false;
	Object _returnValue = null;
	Throwable _exception = null;
	String _validationMessage = null;

	abstract void processEvent(GwtEvent<?> sourceEvent);
	
	abstract void processEvent(CruxEvent<?> sourceEvent, boolean fromOutOfModule);

	public Throwable exception() 
	{
		return _exception;
	}

	public boolean hasException() 
	{
		return _exception != null;
	}

	public boolean hasReturn() 
	{
		return _hasReturn;
	}

	public Object returnValue() 
	{
		return _returnValue;
	}

	public String validationMessage() 
	{
		return _validationMessage;
	}

	public void setHasReturn(boolean hasReturn)
    {
    	_hasReturn = hasReturn;
    }

	public void setReturnValue(Object returnValue)
    {
    	_returnValue = returnValue;
    }

	public void setException(Throwable exception)
    {
    	_exception = exception;
    }

	public void setValidationMessage(String validationMessage)
    {
    	_validationMessage = validationMessage;
    }
}
