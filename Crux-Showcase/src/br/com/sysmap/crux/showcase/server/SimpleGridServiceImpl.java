package br.com.sysmap.crux.showcase.server;

import java.util.Date;

import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.remote.SimpleGridService;

public class SimpleGridServiceImpl implements SimpleGridService {
	
	private static final String[] NAMES = {
		"Jack", "Rose", "Albert", "Pam", "Diana", "Joe",
		"Suse", "Robert", "David", "Tom", "Betty", "Bill",
		"Rian", "Phill", "Moe", "Lisa", "Herbert", "Daniel",
		"Donald", "Ted", "Nancy", "Ashley", "Carol", "Maggy",
		"Monica", "Sarah", "Cindy", "Justin", "Frank", "Roy"		
	};
	
	private static int COUNT = NAMES.length;
	
	public Contact[] getContactList() {
		
		Contact[] result = new Contact[COUNT];
		
		for(int i = 0; i < COUNT; i++)
		{
			Contact contact = new Contact();
			contact.setBirthday(generateDate(i));
			contact.setPhone(generatePhone(i));
			contact.setName(NAMES[i]);
			result[i] = contact;
		}
		
		return result;
	}

	private String generatePhone(int i) {
		int m = (i % 9) + 1;
		int n = (i % 9);
		return "" +  m + n + m + " - " + n + m + n + m;
	}

	@SuppressWarnings(/*using old constructor only for simplicity*/"deprecation")
	private Date generateDate(int i) {
		return new Date(2009, i % 12, (i % 28) + 1);
	}
}