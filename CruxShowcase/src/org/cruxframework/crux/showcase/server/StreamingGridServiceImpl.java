package org.cruxframework.crux.showcase.server;

import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.remote.StreamingGridService;

/**
 * Service Implementation
 */
public class StreamingGridServiceImpl implements StreamingGridService {
	
	/**
	 * @see org.cruxframework.crux.showcase.client.remote.StreamingGridService#fetchContacts(int, int)
	 */
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