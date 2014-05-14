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
package org.cruxframework.crux.widgets.client.uploader.event;

import org.cruxframework.crux.core.client.file.Blob;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AddFileEvent extends AbstractFileEvent<AddFileHandler>
{
	private static Type<AddFileHandler> TYPE = new Type<AddFileHandler>();
	
	private boolean canceled = false;

	protected AddFileEvent(Blob file, String fileName)
	{
		super(file, fileName);
	}

	@Override
	protected void dispatch(AddFileHandler handler)
	{
		handler.onAddFile(this);
	}

	@Override
	public Type<AddFileHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<AddFileHandler> getType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCanceled()
	{
		return canceled;
	}
	
	/**
	 * 
	 */
	public void cancel()
	{
		canceled = true;
	}

	/**
	 * @param <I>
	 * @param source
	 * @param file 
	 * @param fileName 
	 * @return
	 */
	public static AddFileEvent fire(HasAddFileHandlers source, Blob file, String fileName)
	{
		AddFileEvent event = new AddFileEvent(file, fileName);
		source.fireEvent(event);
		return event;
	}	
}
