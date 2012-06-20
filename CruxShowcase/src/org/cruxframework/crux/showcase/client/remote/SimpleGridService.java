package org.cruxframework.crux.showcase.client.remote;

import java.util.ArrayList;

import org.cruxframework.crux.showcase.client.dto.Contact;


import com.google.gwt.user.client.rpc.RemoteService;

public interface SimpleGridService extends RemoteService {
	
	public ArrayList<Contact> getContactList();
}
