package org.cruxframework.crux.showcase.server;

import java.util.ArrayList;
import java.util.Date;

import org.cruxframework.crux.showcase.client.dto.Address;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.dto.Contact.Gender;
import org.cruxframework.crux.showcase.client.remote.SimpleGridService;


/**
 * Service Implementation
 */
public class SimpleGridServiceImpl implements SimpleGridService {
	
	static final Contact[] CONTACTS={
		new Contact(1,	"Jack",		generatePhone(0),	generateDate(0),	Gender.MALE,	new Address("138 Flowers St.")),
		new Contact(2,	"Rose",		generatePhone(1),	generateDate(1),	Gender.FEMALE,	new Address("122 Cedar St.")),
		new Contact(3,	"Lisa",		generatePhone(2),	generateDate(2),	Gender.FEMALE,	new Address("450 Maple St.")),
		new Contact(4,	"Diana",	generatePhone(3),	generateDate(3),	Gender.FEMALE,	new Address("791 Washington Ave.")),
		new Contact(5,	"Albert",	generatePhone(4),	generateDate(4),	Gender.MALE,	new Address("140 White Hill St.")),
		new Contact(6,	"Joe",		generatePhone(5),	generateDate(5),	Gender.MALE,	new Address("880 Park Road")),		
		new Contact(7,	"Suse",		generatePhone(6),	generateDate(6),	Gender.FEMALE,	new Address("111 42nd St.")),
		new Contact(8,	"David",	generatePhone(7),	generateDate(7),	Gender.MALE,	new Address("662 Boulevard Ring")),
		new Contact(9,	"Robert",	generatePhone(8),	generateDate(8),	Gender.MALE,	new Address("181 Gasoline Alley")),
		new Contact(10,	"Betty",	generatePhone(9),	generateDate(9),	Gender.FEMALE,	new Address("13 Victoria Road")),
		new Contact(11,	"Tom",		generatePhone(10),	generateDate(10),	Gender.MALE,	new Address("997 Windsor Road")),		
		new Contact(12,	"Rian",		generatePhone(11),	generateDate(11),	Gender.MALE,	new Address("1414 Summer Field St.")),
		new Contact(13,	"Ashley",	generatePhone(12),	generateDate(12),	Gender.FEMALE,	new Address("207 Buffalo Ave")),
		new Contact(14,	"Bill",		generatePhone(13),	generateDate(13),	Gender.MALE,	new Address("40 Church St.")),
		new Contact(15,	"Eddye",	generatePhone(14),	generateDate(14),	Gender.MALE,	new Address("1010 Manchester Road")),
		new Contact(16,	"Paul",		generatePhone(15),	generateDate(15),	Gender.MALE,	new Address("51 Mill Lane"))
	};
	
	/**
	 * @see org.cruxframework.crux.showcase.client.remote.SimpleGridService#getContactList()
	 */
	public ArrayList<Contact> getContactList() {
		ArrayList<Contact> result = new ArrayList<Contact>();
		for (Contact contact : CONTACTS) {
			result.add(contact);
		}
		return result;
	}

	private static String generatePhone(int i) {
		int m = (i % 9) + 1;
		int n = (i % 9);
		return "(" + m + n + m + ") " +  n + m + n + "-" + m + n + m + n;
	}

	@SuppressWarnings("deprecation")
	private static Date generateDate(int i) {
		return new Date(109, i % 12, (i % 28) + 1);
	}
}