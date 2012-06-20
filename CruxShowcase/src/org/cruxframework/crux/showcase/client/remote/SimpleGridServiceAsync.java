package org.cruxframework.crux.showcase.client.remote;

import java.util.ArrayList;

import org.cruxframework.crux.showcase.client.dto.Contact;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SimpleGridServiceAsync {
	
	public void getContactList(AsyncCallback<ArrayList<Contact>> callback);
}
