package br.com.sysmap.crux.showcase.client.remote;

import br.com.sysmap.crux.showcase.client.dto.Contact;

import com.google.gwt.user.client.rpc.RemoteService;

public interface StreamingGridService extends RemoteService {
	
	public Contact[] fetchContacts(int first, int last);
}
