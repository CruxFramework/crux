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
package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DispatchData 
{
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private String evtCall;
	private String idSender;
	
	public String getEvtCall() {
		return evtCall;
	}

	public void setEvtCall(String evtCall) {
		this.evtCall = evtCall;
	}
	
	public String getIdSender() {
		return idSender;
	}

	public void setIdSender(String idSender) {
		this.idSender = idSender;
	}

	public void addParameter(String key, Object parameter)
	{
		parameters.put(key, parameter);
	}

	public Object getParameter(String key)
	{
		return parameters.get(key);
	}
	
	public boolean containsParameter(String key)
	{
		return parameters.containsKey(key);
	}
	
	public Set<String> getParameters()
	{
		return parameters.keySet();
	}
}
