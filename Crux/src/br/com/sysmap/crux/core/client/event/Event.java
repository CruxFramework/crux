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
