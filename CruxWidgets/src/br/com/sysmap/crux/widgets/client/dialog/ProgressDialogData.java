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
package br.com.sysmap.crux.widgets.client.dialog;

import java.io.Serializable;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
 */
public class ProgressDialogData implements Serializable
{
    private static final long serialVersionUID = -2904511069640049713L;

    private String styleName;
	private String message;
	private boolean animationEnabled;

	public ProgressDialogData()
	{
	}
	
	public ProgressDialogData(String message, String styleName, boolean animationEnabled)
	{
		this.styleName = styleName;
		this.message = message;
		this.animationEnabled = animationEnabled;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}
}
