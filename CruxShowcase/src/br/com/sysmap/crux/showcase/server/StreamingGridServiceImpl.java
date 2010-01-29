package br.com.sysmap.crux.showcase.server;

import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.StreamingGridService;

public class StreamingGridServiceImpl implements StreamingGridService {
	
	public Contact[] fetchContacts(int first, int last) {
		
		int count = SimpleGridServiceImpl.CONTACTS.length;
		last = last < count ? last : count - 1; 
		int requestedRecordCount = last - first + 1;
		
		if(requestedRecordCount > 0) {
			
			Contact[] result = new Contact[requestedRecordCount];			
			
			for(int i = first, j = 0; i <= last && i < count; i++, j++){
				Contact contact = SimpleGridServiceImpl.CONTACTS[i];
				result[j] = contact;
			}
			
			return result;
		}
		else {
			
			return new Contact[0];
		}
	}
}