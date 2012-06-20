package org.cruxframework.crux.showcase.client.remote;

import org.cruxframework.crux.showcase.client.dto.Contact;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StreamingGridServiceAsync {
	
	public void fetchContacts(int first, int last, AsyncCallback<Contact[]> callback);
}
