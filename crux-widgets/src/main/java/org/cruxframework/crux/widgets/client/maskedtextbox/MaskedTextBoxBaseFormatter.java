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
package org.cruxframework.crux.widgets.client.maskedtextbox;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.formatter.MaskedFormatter;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for MaskedTextBox  Formatters
 * @author Thiago da Rosa de Bustamante
 */
public abstract class MaskedTextBoxBaseFormatter implements MaskedFormatter
{
	private FastMap<String> definitions;
	
	public MaskedTextBoxBaseFormatter()
	{
	}
	
	public MaskedTextBoxBaseFormatter(FastMap<String> definitions)
	{
		this.definitions = definitions;
	}
	
	public void applyMask(Widget widget)
	{
		if (widget instanceof MaskedTextBox)
		{
			((MaskedTextBox) widget).setMaskedInput(new MaskedInput((MaskedTextBox) widget, getMask(), getPlaceHolder(), true, definitions));
		}		
	}
	
	public void applyMask(Widget widget, boolean clearIfNotValid)
	{
		if (widget instanceof MaskedTextBox)
		{
			MaskedTextBox masked = (MaskedTextBox) widget;
			
			if(masked.getMaskedInput() != null)
			{
				masked.getMaskedInput().removeMask();
			}
			
			masked.setMaskedInput(new MaskedInput(masked, getMask(), getPlaceHolder(), clearIfNotValid, definitions));
		}		
	}

	public void removeMask(Widget widget)
	{
		if (widget instanceof MaskedTextBox)
		{
			MaskedTextBox maskedTxt = (MaskedTextBox) widget;
			
			MaskedInput maskedInput = maskedTxt.getMaskedInput();
			if (maskedInput != null && maskedInput.getTextBox() != null)
			{
				maskedInput.removeMask();
			}
		}
	}
	
	protected char getPlaceHolder()
	{
		return '_';
	}
}
