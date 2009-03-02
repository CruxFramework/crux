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
