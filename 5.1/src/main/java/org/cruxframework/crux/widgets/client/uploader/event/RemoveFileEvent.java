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
public class RemoveFileEvent extends AbstractFileEvent<RemoveFileHandler>
{
	private static Type<RemoveFileHandler> TYPE = new Type<RemoveFileHandler>();
	
	private FileRemoveAction fileRemoveAction;
	
	public static interface FileRemoveAction
	{
		public void removeFile();
	}
	
	protected RemoveFileEvent(Blob file, String fileName, FileRemoveAction fileRemoveAction)
	{
		super(file, fileName);
		this.fileRemoveAction = fileRemoveAction;
	}

	@Override
	protected void dispatch(RemoveFileHandler handler)
	{
		handler.onRemoveFile(this);
	}

	@Override
	public Type<RemoveFileHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<RemoveFileHandler> getType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 */
	public void doRemoveFile()
	{
		if(fileRemoveAction != null)
		{
			fileRemoveAction.removeFile();
		}
	}

	/**
	 * @param <I>
	 * @param source
	 * @param fileName 
	 * @param removedFile 
	 * @return
	 */
	public static RemoveFileEvent fire(HasRemoveFileHandlers source, Blob removedFile, String fileName, FileRemoveAction fileRemoveAction)
	{
		RemoveFileEvent event = new RemoveFileEvent(removedFile, fileName, fileRemoveAction);
		source.fireEvent(event);
		return event;
	}	
}
