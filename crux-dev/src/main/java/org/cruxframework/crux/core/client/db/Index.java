/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.client.db;

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.IndexDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;
import org.cruxframework.crux.core.client.db.annotation.Store;
import org.cruxframework.crux.core.client.db.annotation.Store.Indexed;
import org.cruxframework.crux.core.client.db.annotation.Store.Key;

/**
 * <p>Represents an index into this database. Indexes increases searching performance considerably.</p>
 * 
 * <p>
 * You can define an index for an object store, using three different strategies:
 * </p>
 * <p>
 * 1) Through {@code @}{@link IndexDef} annotation os yor Database interface.
 * <pre>
 * {@code @}{@link DatabaseDef}(name="CruxCompanyDatabase", version=1 
 *    objectStores={{@code @}{@link ObjectStoreDef}(targetClass=Person.class, 
 *                   indexes={{@code @}{@link IndexDef}(name="myIndex", keyPath={"name"}, unique=true)})
 *                 }
 *    )
 * public interface CompanyDatabase extends Database{
 * }
 * </pre>
 * </p>
 * <p>
 * 2) Through {@code @}{@link IndexDef} annotation on your Store object.
 * <pre>
 * {@code @}{@link Store}(value=Person.STORE_NAME, indexes={{@code @}{@link IndexDef}(name="myIndex", keyPath={"name", "age"}, unique=true)})
 * public class Person {
 *    public static final String STORE_NAME = "Person"; 
 *    private Integer id;
 *    private String name;
 *    private int age;
 *    
 *    {@code @}{@link Key}
 *    public Integer getId(){return id;}
 *    public void setId(Integer id){this.id = id;}
 *    public String getName(){return name;}
 *    public void setName(String name){this.name = name;}
 *    public int getAge(){return age;}
 *    public void setAge(int age){this.age = age;}
 * }
 * </pre>
 * </p>
 * <p>
 * 3) Through {@code @}{@link Indexed} annotation on the property of your Store object.
 * <pre>
 * {@code @}{@link Store}(Person.STORE_NAME))
 * public class Person {
 *    public static final String STORE_NAME = "Person"; 
 *    private Integer id;
 *    private String name;
 *    private int age;
 *    
 *    {@code @}{@link Key}
 *    public Integer getId(){return id;}
 *    public void setId(Integer id){this.id = id;}
 *    {@code @}{@link Indexed}(unique=true)
 *    public String getName(){return name;}
 *    public void setName(String name){this.name = name;}
 *    public int getAge(){return age;}
 *    public void setAge(int age){this.age = age;}
 * }
 * </pre>
 * </p>
 * <p>
 * Once defined, you can access the index to perform faster search operations:
 * </p>
 * <pre>
 * Transaction transaction = database.getTransaction(new String[]{Person.STORE_NAME}, Mode.readOnly);
 * ObjectStore{@code <Integer, Person>} personStore = transaction.getObjectStore(Person.STORE_NAME);
 * Index{@code <Integer, String, Person>} nameIndex = personStore.getIndex("name");
 * nameIndex.get(35, new DatabaseRetrieveCallback{@code <Person>}() {
 *    {@code @}Override
 *    public void onSuccess(Person person){
 *      // ...
 *    }
 * });
 * </pre>
 *  
 * @param <K> object key type
 * @param <I> index key type. The type of the indexed column
 * @param <V> object type
 * @author Thiago da Rosa de Bustamante
 */
public abstract class Index<K, I, V> extends DBObject 
{
	/**
	 * Constructor
	 * @param db database
	 */
	protected Index(AbstractDatabase db)
    {
		super(db);
    }

	/**
	 * Retrieve the index name
	 * @return index name
	 */
	public abstract String getName();

	/**
	 * Retrieve true if each key inside the index must be unique.
	 * If an unique index exists for an object store, any add or put operation that violates
	 * this index constraints will fail.
	 * @return true if index is unique
	 */
	public abstract boolean isUnique();
	
	/**
	 * This flag affects how the index behaves when the result of evaluating the index's key path yields an Array. 
	 * If the multiEntry flag is false, then a single record whose key is an Array is added to the index. 
	 * If the multiEntry flag is true, then the one record is added to the index for each item in the Array. 
	 * @return true if this is a multi entry index.
	 */
	public abstract boolean isMultiEntry();
	
	/**
	 * Return the number of items referenced by the index.
	 * @param callback called when the operation complete
	 */
	public abstract void count(DatabaseCountCallback callback);

	/**
	 * Return the number of items referenced by the index in the given range.
	 * @param keyRange an object specifying the indexed property range to count
	 * @param callback called when the operation complete
	 */
	public abstract void count(KeyRange<I> range, DatabaseCountCallback callback);

	/**
	 * Retrieve the object associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key index key
	 * @param callback called when the operation complete
	 */
	public abstract void get(I key, DatabaseRetrieveCallback<V> callback);
	
	/**
	 * Retrieve the object in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange an object specifying the indexed property range. The first item from the range will be retrieved.
	 * @param callback called when the operation complete
	 */
	public abstract void get(KeyRange<I> keyRange, DatabaseRetrieveCallback<V> callback);
	
	/**
	 * Retrieve the object key associated with the given key from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param key index key
	 * @param callback called when the operation complete
	 */
	public abstract void getKey(I key, DatabaseRetrieveCallback<K> callback);
	
	/**
	 * Retrieve the object key in the given keyRange from the index. To read the object, 
	 * use the method onSuccess from {@link DatabaseRetrieveCallback}.
	 * @param keyRange an object specifying the indexed property range. The first item from the range will be retrieved.
	 * @param callback called when the operation complete
	 */
	public abstract void getKey(KeyRange<I> keyRange, DatabaseRetrieveCallback<K> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param callback called when the operation complete
	 */
	public abstract void openCursor(DatabaseCursorCallback<I, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange an object specifying the indexed property range, used to filter the query.
	 * @param callback called when the operation complete
	 */
	public abstract void openCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, V> callback);
	
	/**
	 * Open a cursor to iterate over the object store.
	 * @param keyRange an object specifying the indexed property range, used to filter the query.
	 * @param direction specifies the cursor iteration direction
	 * @param callback called when the operation complete
	 */
	public abstract void openCursor(KeyRange<I> keyRange, CursorDirection direction, DatabaseCursorCallback<I, V> callback);

	/**
	 * Open a cursor to iterate over the objects keys into the store.
	 * @param callback called when the operation complete
	 */
	public abstract void openKeyCursor(DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Open a cursor to iterate over the objects keys into the store.
	 * @param keyRange an object specifying the indexed property range, used to filter the query.
	 * @param callback called when the operation complete
	 */
	public abstract void openKeyCursor(KeyRange<I> keyRange, DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Open a cursor to iterate over the objects keys into the store.
	 * @param keyRange an object specifying the indexed property range, used to filter the query.
	 * @param direction specifies the cursor iteration direction
	 * @param callback called when the operation complete
	 */
	public abstract void openKeyCursor(KeyRange<I> keyRange, CursorDirection direction, DatabaseCursorCallback<I, K> callback);
	
	/**
	 * Creates a factory for {@link KeyRange} objects used by this index.
	 * @return the factory
	 */	
	public abstract KeyRangeFactory<I> getKeyRangeFactory();
}
