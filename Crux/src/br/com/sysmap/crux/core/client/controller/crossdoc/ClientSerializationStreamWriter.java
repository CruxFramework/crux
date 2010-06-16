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
package br.com.sysmap.crux.core.client.controller.crossdoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.UnsafeNativeLong;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * For internal use only. Used for cross document serialization.
 * This is a modified version of the GWT ClientSerializationStreamWriter
 */
public final class ClientSerializationStreamWriter implements SerializationStreamWriter
{
    public static final int MAXIMUM_ARRAY_LENGTH = 1 << 15;
    private static final String POSTLUDE = "])";
    private static final String PRELUDE = "].concat([";

    // Keep synchronized with LongLib
	private static final double TWO_PWR_16_DBL = 0x10000;
    // Keep synchronized with LongLib
	private static final double TWO_PWR_32_DBL = TWO_PWR_16_DBL * TWO_PWR_16_DBL;
    private int count = 0;
    private StringBuffer encodeBuffer;
	private boolean needsComma = false;
	private int objectCount;
	private Map<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();
	private final Serializer serializer;
	private boolean streamOpen = false;
	private Map<String, Integer> stringMap = new HashMap<String, Integer>();
	private List<String> stringTable = new ArrayList<String>();
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

	/**
	 * Make an instance equivalent to stringing highBits next to lowBits, where
	 * highBits and lowBits are assumed to be in 32-bit twos-complement
	 * notation. As a result, for any double[] l, the following identity holds:
	 * 
	 * <blockquote> <code>l == makeFromBits(l.highBits(), l.lowBits())</code>
	 * </blockquote>
	 */
	// Keep synchronized with LongLib
	public static double[] makeLongComponents(int highBits, int lowBits)
	{
		double high = highBits * TWO_PWR_32_DBL;
		double low = lowBits;
		if (lowBits < 0)
		{
			low += TWO_PWR_32_DBL;
		}
		return new double[] { low, high };
	}

	@UnsafeNativeLong
	// Keep synchronized with LongLib
	private static native double[] makeLongComponents0(long value) /*-{
		return value;
	}-*/;
	

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
			throw new IllegalStateException("WriterStream is not open");//TODO - Thiago - messages
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
		/*
		 * Client code represents longs internally as an array of two Numbers.
		 * In order to make serialization of longs faster, we'll send the
		 * component parts so that the value can be directly reconstituted on
		 * the server.
		 */
		double[] parts;
		if (GWT.isScript())
		{
			parts = makeLongComponents0(fieldValue);
		}
		else
		{
			parts = makeLongComponents((int) (fieldValue >> 32), (int) fieldValue);
		}
		assert parts.length == 2;
		writeDouble(parts[0]);
		writeDouble(parts[1]);
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
	protected List<String> getStringTable()
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
		List<String> stringTable = getStringTable();
		for (String s : stringTable)
		{
			append(EscapeUtils.quote(s, false));
		}
		append(String.valueOf(stringTable.size()));
	}
}
