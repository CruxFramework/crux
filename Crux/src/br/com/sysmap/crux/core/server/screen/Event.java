package br.com.sysmap.crux.core.server.screen;

public class Event extends br.com.sysmap.crux.core.client.event.Event implements Cloneable
{
	public Event(String id, String type, String evtCall, String evtCallback, boolean sync) 
	{
		super(id, type, evtCall, evtCallback, sync);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		return super.clone();
	}
}
