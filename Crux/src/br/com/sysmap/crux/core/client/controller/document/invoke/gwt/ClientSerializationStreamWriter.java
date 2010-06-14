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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.UnsafeNativeLong;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.List;

/**
 * For internal use only. Used for cross document serialization.
 * This is a modified version of the GWT ClientSerializationStreamWriter
 */
public final class ClientSerializationStreamWriter extends AbstractSerializationStreamWriter
{
    public static final int MAXIMUM_ARRAY_LENGTH = 1 << 15;
    private static final String POSTLUDE = "])";
    private static final String PRELUDE = "].concat([";

    private int count = 0;
    private boolean needsComma = false;
    private int total = 0;
    private boolean streamOpen = false;
	
    /**
	 * Used by JSNI, see {@link #quoteString(String)}.
	 */
	@SuppressWarnings("unused")
	private static JavaScriptObject regex = getQuotingRegex();

	/**
	 * Quote characters in a user-supplied string to make sure they are safe to
	 * send to the server.
	 * 
	 * @param str
	 *            string to quote
	 * @return quoted string
	 */
	public static native String quoteString(String str) /*-{
	                                                    var regex = @br.com.sysmap.crux.core.client.controller.document.invoke.gwt.ClientSerializationStreamWriter::regex;
	                                                    var idx = 0;
	                                                    var out = "";
	                                                    var result;
	                                                    while ((result = regex.exec(str)) != null) {
	                                                    out += str.substring(idx, result.index);
	                                                    idx = result.index + 1;
	                                                    var ch = result[0].charCodeAt(0);
	                                                    if (ch == 0) {
	                                                    out += "\\0";
	                                                    } else if (ch == 92) { // backslash
	                                                    out += "\\\\";
	                                                    } else if (ch == 124) { // vertical bar
	                                                    // 124 = "|" = AbstractSerializationStream.SEPARATOR_CHAR
	                                                    out += "\\!";
	                                                    } else {
	                                                    var hex = ch.toString(16);
	                                                    out += "\\u0000".substring(0, 6 - hex.length) + hex;
	                                                    }
	                                                    }
	                                                    return out + str.substring(idx);
	                                                    }-*/;
	
	@Override
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
	 * Create the RegExp instance used for quoting dangerous characters in user
	 * payload strings.
	 * 
	 * Note that {@link AbstractSerializationStream#SEPARATOR_CHAR} is used
	 * in this expression, which must be updated if the separator character is
	 * changed.
	 * 
	 * For Android WebKit, we quote many more characters to keep them from being
	 * mangled.
	 * 
	 * @return RegExp object
	 */
	private static native JavaScriptObject getQuotingRegex() /*-{
	                                                         // "|" = AbstractSerializationStream.SEPARATOR_CHAR
	                                                         var ua = navigator.userAgent.toLowerCase();
	                                                         if (ua.indexOf("android") != -1) {
	                                                         // initial version of Android WebKit has a double-encoding bug for UTF8,
	                                                         // so we have to encode every non-ASCII character.
	                                                         // TODO(jat): revisit when this bug is fixed in Android
	                                                         return /[\u0000\|\\\u0080-\uFFFF]/g;
	                                                         } else if (ua.indexOf("webkit") != -1) {
	                                                         // other WebKit-based browsers need some additional quoting due to combining
	                                                         // forms and normalization (one codepoint being replaced with another).
	                                                         // Verified with Safari 4.0.1 (5530.18)
	                                                         return /[\u0000\|\\\u0300-\u03ff\u0590-\u05FF\u0600-\u06ff\u0730-\u074A\u07eb-\u07f3\u0940-\u0963\u0980-\u09ff\u0a00-\u0a7f\u0b00-\u0b7f\u0e00-\u0e7f\u0f00-\u0fff\u1900-\u194f\u1a00-\u1a1f\u1b00-\u1b7f\u1dc0-\u1dff\u1f00-\u1fff\u2000-\u206f\u20d0-\u20ff\u2100-\u214f\u2300-\u23ff\u2a00-\u2aff\u3000-\u303f\uD800-\uFFFF]/g;
	                                                         } else {
	                                                         return /[\u0000\|\\\uD800-\uFFFF]/g;
	                                                         }
	                                                         }-*/;

	@UnsafeNativeLong
	// Keep synchronized with LongLib
	private static native double[] makeLongComponents0(long value) /*-{
	                                                               return value;
	                                                               }-*/;

	private StringBuffer encodeBuffer;

	private final Serializer serializer;

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
	 * Call this method before attempting to append any tokens. This method
	 * implementation <b>must</b> be called by any overridden version.
	 */
	@Override
	public void prepareToWrite()
	{
		super.prepareToWrite();
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

	@Override
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

	@Override
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

	@Override
	protected void serialize(Object instance, String typeSignature) throws SerializationException
	{
		serializer.serialize(this, instance, typeSignature);
	}

	private void writeStringTable()
	{
		List<String> stringTable = getStringTable();
		for (String s : stringTable)
		{
			append(quoteString(s));
		}
		append(String.valueOf(stringTable.size()));
	}
}
