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

import org.cruxframework.crux.core.client.collection.FastList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.lang.LongLib;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * For internal use only. Used for cross document call serialization.
 * @see CrossDocument
 */
public final class ClientSerializationStreamReader implements SerializationStreamReader
{
	int index;
	JavaScriptObject results;
	String[] stringTable;
	private FastList<Object> seenArray = new FastList<Object>();
	private Serializer serializer;

	/**
	 * @param serializer
	 */
	public ClientSerializationStreamReader(Serializer serializer)
	{
		this.serializer = serializer;
	}

	private static native JavaScriptObject eval(String encoded) /*-{
		return eval(encoded);
	}-*/;

	public void prepareToRead(String encoded) throws SerializationException
	{
		results = eval(encoded);
		index = -1;
		seenArray.clear();
		stringTable = new String[readTableSize()];

		int resultLength = getLength(results);
		int initTable = resultLength - stringTable.length -1;
		for (int i=0; i<stringTable.length; i++)
		{
			stringTable[i] = readTableString(initTable+i);
		}
	}

	public native boolean readBoolean() /*-{
		return !!this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	public native byte readByte() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	public native char readChar() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	public native double readDouble() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	public native float readFloat() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	public native int readInt() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	private native int readTableSize() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results.length-1];
	}-*/;
	
	private static native int getLength(JavaScriptObject array) /*-{
		return array.length;
	}-*/;	
	
	/**
	 * @see com.google.gwt.user.client.rpc.SerializationStreamReader#readLong()
	 */
	public long readLong()
	{
		String s = readString();
		return LongLib.longFromBase64(s);
	}

	/**
	 * @see com.google.gwt.user.client.rpc.SerializationStreamReader#readObject()
	 */
	public final Object readObject() throws SerializationException
	{
		int token = readInt();

		if (token < 0)
		{
			// Negative means a previous object
			// Transform negative 1-based to 0-based.
			return seenArray.get(-(token + 1));
		}

		// Positive means a new object
		String typeSignature = getString(token);
		if (typeSignature == null)
		{
			// a null string means a null instance
			return null;
		}

		return deserialize(typeSignature);
	}

	public native short readShort() /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[++this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::index];
	}-*/;

	private native String readTableString(int idx) /*-{
		return this.@org.cruxframework.crux.core.client.controller.crossdoc.ClientSerializationStreamReader::results[idx];
	}-*/;
	
	public String readString()
	{
		return getString(readInt());
	}

	protected Object deserialize(String typeSignature) throws SerializationException
	{
		int id = reserveDecodedObjectIndex();
		Object instance = serializer.instantiate(this, typeSignature);
		rememberDecodedObject(id, instance);
		serializer.deserialize(this, instance, typeSignature);
		return instance;
	}

	protected String getString(int index) 
	{
		// index is 1-based
		return index > 0 ? this.stringTable[index - 1] : null;
	};

	protected final void rememberDecodedObject(int index, Object o)
	{
		// index is 1-based
		seenArray.set(index - 1, o);
	}

	protected final int reserveDecodedObjectIndex()
	{
		seenArray.add(null);

		// index is 1-based
		return seenArray.size();
	}
}
