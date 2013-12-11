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
public class UploadCompleteEvent extends AbstractFileEvent<UploadCompleteHandler>
{
	private static Type<UploadCompleteHandler> TYPE = new Type<UploadCompleteHandler>();
	
	protected UploadCompleteEvent(Blob file, String fileName)
	{
		super(file, fileName);
	}

	@Override
	protected void dispatch(UploadCompleteHandler handler)
	{
		handler.onUploadComplete(this);
	}

	@Override
	public Type<UploadCompleteHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<UploadCompleteHandler> getType()
	{
		return TYPE;
	}
	
	/**
	 * @param <I>
	 * @param source
	 * @param file 
	 * @param fileName 
	 * @return
	 */
	public static UploadCompleteEvent fire(HasUploadCompleteHandlers source, Blob file, String fileName)
	{
		UploadCompleteEvent event = new UploadCompleteEvent(file, fileName);
		source.fireEvent(event);
		return event;
	}	
}
