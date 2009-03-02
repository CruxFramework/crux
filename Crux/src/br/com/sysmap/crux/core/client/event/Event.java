package br.com.sysmap.crux.core.client.event;

/**
 * Crux event abstraction. 
 * @author Thiago
 */
public class Event 
{
	private String type = EventFactory.TYPE_CLIENT;
	private String id;
	private String evtCall;
	private String evtCallback;
	private boolean sync;
	
	public Event(String id, String type, String evtCall, String evtCallback, boolean sync) {
		this.id = id;
		this.type = type;
		this.evtCall = evtCall;
		this.evtCallback = evtCallback;
		this.sync = sync;
	}
	
	public String getType() 
	{
		return type;
	}

	public String getId() {
		return id;
	}

	public String getEvtCall() {
		return evtCall;
	}

	public String getEvtCallback() {
		return evtCallback;
	}

	public boolean isSync() {
		return sync;
	}
}
