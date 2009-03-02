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
	private Screen screen;
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

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
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
		screen = null;
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
