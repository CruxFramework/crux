package org.cruxframework.crux.showcase.client.remote;

import org.cruxframework.crux.showcase.client.dto.Contact;

import com.google.gwt.user.client.rpc.RemoteService;

public interface StreamingGridService extends RemoteService {
	
	public Contact[] fetchContacts(int first, int last);
}
