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
package br.com.sysmap.crux.advanced.client.maskedtextbox;

import br.com.sysmap.crux.core.client.formatter.Formatter;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class MaskedTextBox extends TextBox
{
	private static int currentId = 0;
	private Formatter formatter;
	
	/**
	 * 
	 * @param element
	 * @param formatter
	 * @return
	 */
	public static MaskedTextBox wrap(Element element, Formatter formatter) 
	{
		return new MaskedTextBox(TextBox.wrap(element), formatter);
	}

	/**
	 * 
	 */
	public MaskedTextBox()
	{
		this(null);
	}
	
	/**
	 * Constructor
	 * @param formatter
	 */
	public MaskedTextBox(Formatter formatter)
	{
		this(new TextBox(), formatter);
	}
	
	/**
	 * 
	 * @param textBox
	 * @param formatter
	 */
	protected MaskedTextBox(TextBox textBox, Formatter formatter)
	{
		String id = textBox.getElement().getId();
		if (id == null || id.length() == 0)
		{
			textBox.getElement().setId(generateNewId());
		}
		setFormatter(formatter);
	}

	/**
	 * 
	 * @return
	 */
	public Formatter getFormatter()
	{
		return formatter;
	}

	/**
	 * Sets a formatter for widget.
	 * @param formatter
	 */
	public void setFormatter(Formatter formatter)
	{
		setFormatter(formatter, true);
	}
	
	/**
	 * Sets a formatter for widget.
	 * @param formatter
	 * @param applyMask
	 */
	public void setFormatter(Formatter formatter, boolean applyMask)
	{
		this.formatter = formatter;
		if (this.formatter != null && applyMask)
		{
			DeferredCommand.addCommand(new Command() 
			{
				public void execute()
				{
					MaskedTextBox.this.formatter.applyMask(MaskedTextBox.this);
				}
			});
		}
	}

	/**
	 * 
	 * @return
	 */
	public Object getUnformattedValue()
	{
		if (this.formatter != null)
		{
			return this.formatter.unformat(getValue());
			
		}
		else
		{
			return getValue();
		}
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setFormattedValue(Object value)
	{
		if (this.formatter != null)
		{
			super.setValue(this.formatter.format(value));
		}
		else
		{
			super.setValue(value!= null?value.toString():"");
		}
	}
	
	@Override
	public void setValue(String value)
	{
		super.setValue(value);
		//TODO: refresh na mascara
	}
	
	/**
	 * Creates a sequential id
	 * @return
	 */
	protected static String generateNewId() 
	{
		return "_mask_" + (++currentId );
	}
}
