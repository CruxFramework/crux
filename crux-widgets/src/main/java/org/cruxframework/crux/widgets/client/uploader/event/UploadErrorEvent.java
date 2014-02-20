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
public class UploadErrorEvent extends AbstractFileEvent<UploadErrorHandler>
{
	private static Type<UploadErrorHandler> TYPE = new Type<UploadErrorHandler>();
	
	protected UploadErrorEvent(Blob file, String fileName)
	{
		super(file, fileName);
	}

	@Override
	protected void dispatch(UploadErrorHandler handler)
	{
		handler.onUploadError(this);
	}

	@Override
	public Type<UploadErrorHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<UploadErrorHandler> getType()
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
	public static UploadErrorEvent fire(HasUploadErrorHandlers source, Blob file, String fileName)
	{
		UploadErrorEvent event = new UploadErrorEvent(file, fileName);
		source.fireEvent(event);
		return event;
	}	
}
