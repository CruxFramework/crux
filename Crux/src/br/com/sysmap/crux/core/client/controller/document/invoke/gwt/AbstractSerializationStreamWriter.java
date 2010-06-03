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
package br.com.sysmap.crux.core.client.controller.document.invoke.gwt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for the client and server serialization streams. This class
 * handles the basic serialization and deserialization formatting for primitive
 * types since these are common between the client and the server. It also
 * handles Object- and String-tracking for building graph references.
 * 
 * This is a modified version of GWT AbstractSerializationStreamWriter class
 */
public abstract class AbstractSerializationStreamWriter extends AbstractSerializationStream implements SerializationStreamWriter
{

	// Keep synchronized with LongLib
	private static final double TWO_PWR_16_DBL = 0x10000;

	// Keep synchronized with LongLib
	private static final double TWO_PWR_32_DBL = TWO_PWR_16_DBL * TWO_PWR_16_DBL;

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

	private int objectCount;

	private Map<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();

	private Map<String, Integer> stringMap = new HashMap<String, Integer>();

	private List<String> stringTable = new ArrayList<String>();

	public void prepareToWrite()
	{
		objectCount = 0;
		objectMap.clear();
		stringMap.clear();
		stringTable.clear();
	}

	@Override
	public abstract String toString();

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

	/**
	 * Asymmetric implementation; see subclasses.
	 */
	public abstract void writeLong(long value) throws SerializationException;

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
	 * Append a token to the underlying output buffer.
	 * 
	 * @param token
	 *            the token to append
	 */
	protected abstract void append(String token);

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

	/**
	 * Compute and return the type signature for an object.
	 * 
	 * @param instance
	 *            the instance to inspect
	 * @return the type signature of the instance
	 */
	protected abstract String getObjectTypeSignature(Object instance) throws SerializationException;

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

	/**
	 * Serialize an object into the stream.
	 * 
	 * @param instance
	 *            the object to serialize
	 * @param typeSignature
	 *            the type signature of the object
	 * @throws SerializationException
	 */
	protected abstract void serialize(Object instance, String typeSignature) throws SerializationException;

}
