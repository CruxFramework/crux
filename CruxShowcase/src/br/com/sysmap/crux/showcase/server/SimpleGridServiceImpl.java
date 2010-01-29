package br.com.sysmap.crux.showcase.server;

import java.util.Date;

import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.dto.Contact.Gender;
import br.com.sysmap.crux.showcase.client.remote.SimpleGridService;

public class SimpleGridServiceImpl implements SimpleGridService {
	
	static final Contact[] CONTACTS = {
		new Contact("Jack",		generatePhone(0),	generateDate(0),	Gender.MALE),
		new Contact("Rose",		generatePhone(1),	generateDate(1),	Gender.FEMALE),
		new Contact("Lisa",		generatePhone(2),	generateDate(2),	Gender.FEMALE),
		new Contact("Diana",	generatePhone(3),	generateDate(3),	Gender.FEMALE),
		new Contact("Albert",	generatePhone(4),	generateDate(4),	Gender.MALE),
		new Contact("Joe",		generatePhone(5),	generateDate(5),	Gender.MALE),		
		new Contact("Suse",		generatePhone(6),	generateDate(6),	Gender.FEMALE),
		new Contact("David",	generatePhone(7),	generateDate(7),	Gender.MALE),
		new Contact("Robert",	generatePhone(8),	generateDate(8),	Gender.MALE),
		new Contact("Betty",	generatePhone(9),	generateDate(9),	Gender.FEMALE),
		new Contact("Tom",		generatePhone(10),	generateDate(10),	Gender.MALE),		
		new Contact("Rian",		generatePhone(11),	generateDate(11),	Gender.MALE),
		new Contact("Ashley",	generatePhone(12),	generateDate(12),	Gender.FEMALE),
		new Contact("Bill",		generatePhone(13),	generateDate(13),	Gender.MALE),
		new Contact("Eddye",	generatePhone(14),	generateDate(14),	Gender.MALE),
		new Contact("Paul",		generatePhone(15),	generateDate(15),	Gender.MALE)
	};
	
	public Contact[] getContactList() {
		return CONTACTS;
	}

	static String generatePhone(int i) {
		int m = (i % 9) + 1;
		int n = (i % 9);
		return "" +  m + n + m + " - " + n + m + n + m;
	}

	@SuppressWarnings(/*using old constructor only for simplicity*/"deprecation")
	static Date generateDate(int i) {
		return new Date(2009, i % 12, (i % 28) + 1);
	}
}