/*
 * Copyright 2011 cruxframework.org.
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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.ScreenAccessor;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.Serializer;


/**
 * The base class for invocation proxies created to make cross document calls
 * @author Thiago da Rosa de Bustamante
 * @see CrossDocument
 */
public abstract class CrossDocumentProxy extends ScreenAccessor implements TargetDocument
{
	protected Target target;
	protected String frame;
	protected String siblingFrame;
	protected JSWindow jsWindow;

	private final Serializer serializer;


	public CrossDocumentProxy(Serializer serializer)
    {
		this.serializer = serializer;
		setTarget(Target.SELF);
    }
	
	/**
	 * @see org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument#setTargetWindow(org.cruxframework.crux.core.client.screen.JSWindow)
	 */
	public void setTargetWindow(JSWindow jsWindow)
	{
		this.jsWindow = jsWindow;
		this.target = null;
		this.frame = null;
		this.siblingFrame = null;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument#setTarget(org.cruxframework.crux.core.client.controller.crossdoc.Target)
	 */
    public void setTarget(Target target)
	{
		this.target = target;
		this.jsWindow = null;
		this.frame = null;
		this.siblingFrame = null;
	}

	/**
	 * @see org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument#setTargetFrame(java.lang.String)
	 */
    public void setTargetFrame(String frame)
	{
		this.frame = frame;
		this.jsWindow = null;
	    this.target = null;
		this.siblingFrame = null;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument#setTargetSiblingFrame(java.lang.String)
	 */
    public void setTargetSiblingFrame(String frame)
	{
		this.siblingFrame = frame;
		this.jsWindow = null;
		this.frame = null;
	    this.target = null;
	}
	
	/**
	 * Returns a
	 * {@link com.google.gwt.user.client.rpc.SerializationStreamReader
	 * SerializationStreamReader} that is ready for reading.
	 * 
	 * @param encoded
	 *            string that encodes the response of an cross document call
	 * @return {@link com.google.gwt.user.client.rpc.SerializationStreamReader
	 *         SerializationStreamReader} that is ready for reading
	 * @throws SerializationException
	 */
	public SerializationStreamReader createStreamReader(String encoded) throws SerializationException
	{
		ClientSerializationStreamReader clientSerializationStreamReader = new ClientSerializationStreamReader(serializer);
		clientSerializationStreamReader.prepareToRead(encoded);
		return clientSerializationStreamReader;
	}
	
	/**
	 * Returns a
	 * {@link com.google.gwt.user.client.rpc.SerializationStreamWriter
	 * SerializationStreamWriter} that has had
	 * {@link ClientSerializationStreamWriter#prepareToWrite()} called on it.
	 * 
	 * @return {@link com.google.gwt.user.client.rpc.SerializationStreamWriter
	 *         SerializationStreamWriter} that has had
	 *         {@link ClientSerializationStreamWriter#prepareToWrite()} called
	 *         on it and it has already had had the name of the cross document
	 *         interface written as well
	 */
	public SerializationStreamWriter createStreamWriter()
	{
		ClientSerializationStreamWriter clientSerializationStreamWriter = new ClientSerializationStreamWriter(serializer);
		clientSerializationStreamWriter.prepareToWrite();
		return clientSerializationStreamWriter;
	}

	/**
	 * Return <code>true</code> if the encoded response contains a value
	 * returned by the method invocation.
	 * 
	 * @param encodedResponse
	 * @return <code>true</code> if the encoded response contains a value
	 *         returned by the method invocation
	 */
	private boolean isReturnValue(String encodedResponse)
	{
		return encodedResponse.startsWith("//OK");
	}

	/**
	 * Return <code>true</code> if the encoded response contains a checked
	 * exception that was thrown by the method invocation.
	 * 
	 * @param encodedResponse
	 * @return <code>true</code> if the encoded response contains a checked
	 *         exception that was thrown by the method invocation
	 */
	private boolean isThrownException(String encodedResponse)
	{
		return encodedResponse.startsWith("//EX");
	}
	
	/** Make a call to a cross document method 
	 * @param payload the call itself. Contains the method and all parameters serialized.
	 * @return
	 * @throws Throwable 
	 */
	protected Object doInvoke(String payload, CrossDocumentReader reader) throws Throwable
	{
		String serializedRet = invokeCrossDocument(payload, target, frame, siblingFrame, jsWindow);
		Object result = null;

		if (payload == null)
		{
			throw new CrossDocumentException(Crux.getMessages().crossDocumentInvocationError());
		}
		else if (isReturnValue(serializedRet))
		{
			result = reader.read(createStreamReader(serializedRet.substring(4)));//remove //OK
		}
		else if (isThrownException(serializedRet))
		{
			throw (Throwable) createStreamReader(serializedRet.substring(4)).readObject();//remove //EX
		}
		else
		{
			throw new CrossDocumentException(serializedRet);
		}

		return result;
	}
	
	/**
	 * Enumeration used to read specific return types out of a
	 * {@link SerializationStreamReader}.
	 */
	public enum CrossDocumentReader
	{
		BOOLEAN
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readBoolean();
			}
		},

		BYTE
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readByte();
			}
		},

		CHAR
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readChar();
			}
		},

		DOUBLE
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readDouble();
			}
		},

		FLOAT
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readFloat();
			}
		},

		INT
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readInt();
			}
		},

		LONG
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readLong();
			}
		},

		OBJECT
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readObject();
			}
		},

		SHORT
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readShort();
			}
		},

		STRING
		{
			@Override
			public Object read(SerializationStreamReader streamReader) throws SerializationException
			{
				return streamReader.readString();
			}
		},

		VOID
		{
			@Override
			public Object read(SerializationStreamReader streamReader)
			{
				return null;
			}
		};

		public abstract Object read(SerializationStreamReader streamReader) throws SerializationException;
	}
}
