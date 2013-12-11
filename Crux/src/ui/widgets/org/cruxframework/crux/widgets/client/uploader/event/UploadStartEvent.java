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
public class UploadStartEvent extends AbstractFileEvent<UploadStartHandler>
{
	private static Type<UploadStartHandler> TYPE = new Type<UploadStartHandler>();
	
	private boolean canceled = false;
	
	protected UploadStartEvent(Blob file, String fileName)
	{
		super(file, fileName);
	}

	@Override
	protected void dispatch(UploadStartHandler handler)
	{
		handler.onUploadStart(this);
	}

	@Override
	public Type<UploadStartHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<UploadStartHandler> getType()
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
	 * @param fileName 
	 * @param file 
	 * @return
	 */
	public static UploadStartEvent fire(HasUploadStartHandlers source, Blob file, String fileName)
	{
		UploadStartEvent event = new UploadStartEvent(file, fileName);
		source.fireEvent(event);
		return event;
	}	
}
