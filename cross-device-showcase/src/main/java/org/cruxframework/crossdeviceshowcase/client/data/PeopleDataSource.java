/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client.data;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.datasource.LocalPagedDataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSource;
import org.cruxframework.crux.core.client.datasource.annotation.DataSourceRecordIdentifier;
import org.cruxframework.crux.core.client.db.Cursor;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.Index;
import org.cruxframework.crux.core.client.db.ObjectStore;
import org.cruxframework.crux.core.client.db.Transaction;
import org.cruxframework.crux.core.client.db.Transaction.Mode;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.ioc.Inject.Scope;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DataSource("peopleDataSource")
@DataSourceRecordIdentifier("id")
public class PeopleDataSource extends LocalPagedDataSource<PersonResume> 
{
	@Inject(scope=Scope.DOCUMENT)
	private CompanyDatabase database;

	public void setDatabase(CompanyDatabase database) 
	{
		this.database = database;
	}

	@Override
	public void load() 
	{
		Transaction transaction = database.getTransaction(new String[]{Person.STORE_NAME}, Mode.readOnly);
		ObjectStore<Integer, Person> personStore = transaction.getObjectStore(Person.STORE_NAME);
		Index<Integer, String, Person> nameIndex = personStore.getIndex("name");
		
		final List<PersonResume> data = new ArrayList<PersonResume>();
		
		nameIndex.openKeyCursor(new DatabaseCursorCallback<String, Integer>() {

			@Override
			public void onSuccess(Cursor<String, Integer> cursor) 
			{
				if (cursor != null && cursor.getValue() != null)
				{
					data.add(new PersonResume(cursor.getValue(), cursor.getKey()));
					cursor.continueCursor();
				}
				else
				{
					updateData(data);
				}
			}
		});
	}
}
