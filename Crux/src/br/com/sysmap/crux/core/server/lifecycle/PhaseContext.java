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
package br.com.sysmap.crux.core.server.lifecycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;
import br.com.sysmap.crux.core.server.screen.Screen;


public class PhaseContext 
{
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseException phaseException;
	private Object cycleResult;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private DispatchData dispatchData;
	private boolean interruptCycle = false;
	private Object dto;
	private String interruptMessage;
	
	public void setAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}
	
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public boolean containsAttribute(String key)
	{
		return attributes.containsKey(key);
	}
	
	public Iterator<String> iteratorAtributesKey()
	{
		return attributes.keySet().iterator();
	}
	
	public Iterator<Object> iteratorAtributesValue()
	{
		return attributes.values().iterator();
	}

	public PhaseException getPhaseException() {
		return phaseException;
	}

	void setPhaseException(PhaseException phaseException) {
		this.phaseException = phaseException;
	}

	public Object getCycleResult() {
		return cycleResult;
	}

	public void setCycleResult(Object cycleResult) {
		this.cycleResult = cycleResult;
	}
	
	public DispatchData getDispatchData() {
		return dispatchData;
	}

	public void setDispatchData(DispatchData dispatchData) {
		this.dispatchData = dispatchData;
	}

	public boolean isInterruptCycle() {
		return interruptCycle;
	}

	public void interruptCycle(String interruptMessage) {
		this.interruptCycle = true;
		this.interruptMessage = interruptMessage;
	}

	public String getInterruptMessage() {
		return interruptMessage;
	}

	public Object getDto() {
		return dto;
	}

	public void setDto(Object dto) {
		this.dto = dto;
	}

	public void clear()
	{
		attributes.clear();
		phaseException = null;
		cycleResult = null;
		dispatchData = null;
		interruptCycle = false;
		dto = null;
		request = null;
		response = null;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
	public PhaseContext(HttpServletRequest request, HttpServletResponse response) 
	{
		this.request = request;
		this.response = response;
	}
}
