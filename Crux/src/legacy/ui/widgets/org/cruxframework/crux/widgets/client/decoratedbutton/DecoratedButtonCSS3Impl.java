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
package org.cruxframework.crux.widgets.client.decoratedbutton;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.widgets.client.util.CSS3Utils;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Button;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public class DecoratedButtonCSS3Impl extends Button implements DecoratedButtonIntf
{
	public DecoratedButtonCSS3Impl()
    {
		super();
		getStyleElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

	/*
	 * @see org.cruxframework.crux.widgets.client.decoratedbutton.DecoratedButtonIntf#getSpecificStyleName(java.lang.String)
	 */
	public String getSpecificStyleName(String style)
	{
		return CSS3Utils.getCSS3StyleName(style);
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		if(enabled)
		{
			removeStyleDependentName("disabled");
		}
		else
		{
			addStyleDependentName("disabled");
		}
		
		super.setEnabled(enabled);
	}
}