/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;

/**
 * Represents a FileUpload component.
 * @author Thiago Bustamante
 */
public class FileUpload extends Component
{
	protected com.google.gwt.user.client.ui.FileUpload fileUploadWidget;
	
	public FileUpload(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.FileUpload());
	}

	public FileUpload(String id, com.google.gwt.user.client.ui.FileUpload widget) 
	{
		super(id, widget);
		this.fileUploadWidget = widget;
	}
	
	/**
	 * Gets the filename selected by the user. This property has no mutator, as
	 * browser security restrictions preclude setting it.
	 * 
	 * @return the widget's filename
	 */
	public String getFilename() 
	{
		return fileUploadWidget.getFilename();
	}

	public String getName() 
	{
		return fileUploadWidget.getName();
	}

	public void setName(String name) 
	{
		fileUploadWidget.setName(name);
	}	
}
