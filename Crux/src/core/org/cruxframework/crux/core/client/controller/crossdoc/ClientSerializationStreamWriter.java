/*
 * Copyright 2008 Google Inc.
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
package org.cruxframework.crux.core.client.controller.crossdoc;

import java.util.IdentityHashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.utils.EscapeUtils;

import com.google.gwt.lang.LongLib;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * For internal use only. Used for cross document serialization.
 * This is a modified version of the GWT ClientSerializationStreamWriter
 * @see CrossDocument
 */
public final class ClientSerializationStreamWriter implements SerializationStreamWriter
{
    public static final int MAXIMUM_ARRAY_LENGTH = 1 << 15;
    private static final String POSTLUDE = "])";
    private static final String PRELUDE = "].concat([";

    private int count = 0;
    private StringBuffer encodeBuffer;
	private boolean needsComma = false;
	private int objectCount;
	private Map<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();
	private final Serializer serializer;
	private boolean streamOpen = false;
	private FastMap<Integer> stringMap = new FastMap<Integer>();
	private FastList<String> stringTable = new FastList<String>();
	private int total = 0;

	/**
	 * Constructs a <code>ClientSerializationStreamWriter</code>
	 * 
	 * @param serializer
	 *            the {@link Serializer} to use policy
	 */
	public ClientSerializationStreamWriter(Serializer serializer)
	{
		this.serializer = serializer;
	}

	public void append(String token)
	{
		assert (token != null);

		total++;
		if (count++ == MAXIMUM_ARRAY_LENGTH)
		{
			if (total == MAXIMUM_ARRAY_LENGTH + 1)
			{
				encodeBuffer.append(PRELUDE);
			}
			else
			{
				encodeBuffer.append("],[");
			}
			count = 0;
			needsComma = false;
		}

		if (needsComma)
		{
			encodeBuffer.append(",");
		}
		else
		{
			needsComma = true;
		}

		encodeBuffer.append(token);
	}

	/**
	 * Call this method before attempting to append any tokens. This method
	 * implementation <b>must</b> be called by any overridden version.
	 */
	public void prepareToWrite()
	{
		objectCount = 0;
		objectMap.clear();
		stringMap.clear();
		stringTable.clear();
		encodeBuffer = new StringBuffer();
		streamOpen = true;
	}

	@Override
	public String toString()
	{
		if (!streamOpen)
		{
			throw new IllegalStateException(Crux.getMessages().crossDocumentSerializationErrorStreamClosed());
		}
		
		writeStringTable();
		streamOpen = false;

		if (total > MAXIMUM_ARRAY_LENGTH)
		{
			return "[" + encodeBuffer.toString() + POSTLUDE;
		}
		else
		{
			return "[" + encodeBuffer.toString() + "]";
		}
	}

	public void writeBoolean(boolean fieldValue)
	{
		append(fieldValue ? "1" : "0");
	}

	public void writeByte(byte fieldValue)
	{
		append(String.valueOf(fieldValue));
	}

	public void writeChar(char ch)
	{
		// just use an int, it's more foolproof
		append(String.valueOf((int) ch));
	}

	public void writeDouble(double fieldValue)
	{
		append(String.valueOf(fieldValue));
	}

	public void writeFloat(float fieldValue)
	{
		writeDouble(fieldValue);
	}

	public void writeInt(int fieldValue)
	{
		append(String.valueOf(fieldValue));
	}

	public void writeLong(long fieldValue)
	{
		writeString(LongLib.toBase64(fieldValue));
	}

	public void writeObject(Object instance) throws SerializationException
	{
		if (instance == null)
		{
			// write a null string
			writeString(null);
			return;
		}

		int objIndex = getIndexForObject(instance);
		if (objIndex >= 0)
		{
			// We've already encoded this object, make a backref
			// Transform 0-based to negative 1-based
			writeInt(-(objIndex + 1));
			return;
		}

		saveIndexForObject(instance);

		// Serialize the type signature
		String typeSignature = getObjectTypeSignature(instance);
		writeString(typeSignature);
		// Now serialize the rest of the object
		serialize(instance, typeSignature);
	}

	public void writeShort(short value)
	{
		append(String.valueOf(value));
	}

	public void writeString(String value)
	{
		writeInt(addString(value));
	}

	/**
	 * Add a string to the string table and return its index.
	 * 
	 * @param string
	 *            the string to add
	 * @return the index to the string
	 */
	protected int addString(String string)
	{
		if (string == null)
		{
			return 0;
		}
		Integer o = stringMap.get(string);
		if (o != null)
		{
			return o;
		}
		stringTable.add(string);
		// index is 1-based
		int index = stringTable.size();
		stringMap.put(string, index);
		return index;
	}

	/**
	 * Get the index for an object that may have previously been saved via
	 * {@link #saveIndexForObject(Object)}.
	 * 
	 * @param instance
	 *            the object to save
	 * @return the index associated with this object, or -1 if this object
	 *         hasn't been seen before
	 */
	protected int getIndexForObject(Object instance)
	{
		return objectMap.containsKey(instance) ? objectMap.get(instance) : -1;
	}

	protected String getObjectTypeSignature(Object o)
	{
		Class<?> clazz = o.getClass();

		if (o instanceof Enum<?>)
		{
			Enum<?> e = (Enum<?>) o;
			clazz = e.getDeclaringClass();
		}

		return serializer.getSerializationSignature(clazz);
	}

	/**
	 * Gets the string table.
	 */
	protected FastList<String> getStringTable()
	{
		return stringTable;
	}

	/**
	 * Remember this object as having been seen before.
	 * 
	 * @param instance
	 *            the object to remember
	 */
	protected void saveIndexForObject(Object instance)
	{
		objectMap.put(instance, objectCount++);
	}

	protected void serialize(Object instance, String typeSignature) throws SerializationException
	{
		serializer.serialize(this, instance, typeSignature);
	}

	private void writeStringTable()
	{
		FastList<String> stringTable = getStringTable();
		for (int i=0; i< stringTable.size(); i++)
		{
			append(EscapeUtils.quote(stringTable.get(i), false));
		}
		append(String.valueOf(stringTable.size()));
	}
}
