/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.input;

import java.text.ParseException;

import org.cruxframework.crux.core.client.event.paste.HasPasteHandlers;
import org.cruxframework.crux.core.client.event.paste.PasteEvent;
import org.cruxframework.crux.core.client.event.paste.PasteEventSourceRegisterFactory;
import org.cruxframework.crux.core.client.event.paste.PasteHandler;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ValueBox;

/**
 * A numeric box 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class NumberBox extends Composite implements HasEnabled, Focusable, HasValue<Number>, HasName, 
										HasDirectionEstimator, HasDirection,  HasClickHandlers, 
										HasDoubleClickHandlers, HasAllDragAndDropHandlers, 
										HasAllFocusHandlers, HasAllGestureHandlers,
									    HasAllMouseHandlers, HasAllTouchHandlers
{
	private Box box;

	private NumberRenderer renderer;

	private FormatterOptions formatterOptions;

	private boolean valueChangeHandlerInitialized;

	private Number maxValue;
	
	private Number minValue;
	
	private String localeGroupSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().groupingSeparator();
	
	private String localeDecimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();

	private static final String DEFAULT_STYLE_NAME = "faces-NumberBox";

	public NumberBox()
	{
		this(new FormatterOptions());
	}

	public NumberBox(FormatterOptions formatterOptions)
	{
		renderer = new NumberRenderer(this);
		box = new Box(renderer);

		EventsHandler eventsHandler = new EventsHandler(this);
		box.addKeyDownHandler(eventsHandler);
		box.addKeyPressHandler(eventsHandler);
		box.addKeyUpHandler(eventsHandler);
		box.addBlurHandler(eventsHandler);
		box.addPasteHandler(eventsHandler);

		initWidget(box);
		setFormatterOptions(formatterOptions);
		setStyleName(DEFAULT_STYLE_NAME);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
	{
		return addDomHandler(handler, DoubleClickEvent.getType());
	}

	@Override
	public HandlerRegistration addDragEndHandler(DragEndHandler handler)
	{
		return addBitlessDomHandler(handler, DragEndEvent.getType());
	}

	@Override
	public HandlerRegistration addDragEnterHandler(DragEnterHandler handler)
	{
		return addBitlessDomHandler(handler, DragEnterEvent.getType());
	}

	@Override
	public HandlerRegistration addDragHandler(DragHandler handler)
	{
		return addBitlessDomHandler(handler, DragEvent.getType());
	}

	@Override
	public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler)
	{
		return addBitlessDomHandler(handler, DragLeaveEvent.getType());
	}

	@Override
	public HandlerRegistration addDragOverHandler(DragOverHandler handler)
	{
		return addBitlessDomHandler(handler, DragOverEvent.getType());
	}

	@Override
	public HandlerRegistration addDragStartHandler(DragStartHandler handler)
	{
		return addBitlessDomHandler(handler, DragStartEvent.getType());
	}

	@Override
	public HandlerRegistration addDropHandler(DropHandler handler)
	{
		return addBitlessDomHandler(handler, DropEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler)
	{
		return addDomHandler(handler, GestureChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addGestureEndHandler(GestureEndHandler handler)
	{
		return addDomHandler(handler, GestureEndEvent.getType());
	}

	@Override
	public HandlerRegistration addGestureStartHandler(GestureStartHandler handler)
	{
		return addDomHandler(handler, GestureStartEvent.getType());
	}

	@Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
    {
	    return addDomHandler(handler, MouseDownEvent.getType());
    }

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
	{
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler)
	{
		return addDomHandler(handler, TouchCancelEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Number> handler)
	{
		// Initialization code
		if (!valueChangeHandlerInitialized)
		{
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler()
			{
				public void onChange(ChangeEvent event)
				{
					ValueChangeEvent.fire(NumberBox.this, getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
    public Direction getDirection()
    {
	    return box.getDirection();
    }

	@Override
    public DirectionEstimator getDirectionEstimator()
    {
	    return box.getDirectionEstimator();
    }

	public Number getMaxValue()
	{
		return maxValue;
	}

	public Number getMinValue()
	{
		return minValue;
	}

	@Override
    public String getName()
    {
	    return box.getName();
    }

	@Override
	public int getTabIndex()
	{
		return box.getTabIndex();
	}

	@Override
	public Number getValue()
	{
		return box.getValue();
	}

	@Override
	public boolean isEnabled()
	{
		return box.isEnabled();
	}

	@Override
	public void setAccessKey(char key)
	{
		box.setAccessKey(key);
	}

	@Override
    public void setDirection(Direction direction)
    {
		box.setDirection(direction);
    }

	@Override
    public void setDirectionEstimator(boolean enabled)
    {
		box.setDirectionEstimator(enabled);
    }

	@Override
    public void setDirectionEstimator(DirectionEstimator directionEstimator)
    {
		box.setDirectionEstimator(directionEstimator);
    }

	@Override
	public void setEnabled(boolean enabled)
	{
		box.setEnabled(enabled);
	}
	
	@Override
	public void setFocus(boolean focused)
	{
		box.setFocus(focused);
	}

	public void setFormatterOptions(FormatterOptions formatterOptions)
	{
		StringBuilder pattern = new StringBuilder("0");

		if (formatterOptions.showGroupSeparators)
		{
			for (int i = 0; i < formatterOptions.groupSize - 1; i++)
			{
				pattern.insert(0, "#");
			}
			pattern.insert(0, "#,");
			if (StringUtils.isEmpty(formatterOptions.groupSeparator))
			{
				formatterOptions.groupSeparator = localeGroupSeparator;
			}

		}
		if (formatterOptions.fractionDigits > 0)
		{
			pattern.append(".");
			for (int i = 0; i < formatterOptions.fractionDigits; i++)
			{
				pattern.append("0");
			}

			if (StringUtils.isEmpty(formatterOptions.decimalSeparator))
			{
				formatterOptions.decimalSeparator = localeDecimalSeparator;
			}

		}
		assert (!StringUtils.unsafeEquals(formatterOptions.decimalSeparator, formatterOptions.groupSeparator)) : 
				"Invalid options. Decimal separator can not be equals to group separator.";

		NumberFormat numberFormat = NumberFormat.getFormat(pattern.toString());
		renderer.setNumberFormat(numberFormat);
		this.formatterOptions = formatterOptions;
	}

	public void setMaxValue(Number maxValue)
	{
		this.maxValue = maxValue;
		Number value = getValue();
		if (value!= null && maxValue != null && value.doubleValue() > maxValue.doubleValue())
		{
			setValue(maxValue);
		}
	}

	public void setMinValue(Number minValue)
	{
		this.minValue = minValue;
		Number value = getValue();
		if (value!= null && minValue != null && value.doubleValue() < minValue.doubleValue())
		{
			setValue(minValue);
		}
	}

	@Override
    public void setName(String name)
    {
		box.setName(name);
    }

	@Override
	public void setTabIndex(int index)
	{
		box.setTabIndex(index);
	}

	@Override
	public void setValue(Number value)
	{
		setValue(value, false);
	}

	@Override
	public void setValue(Number value, boolean fireEvents)
	{
		setValue(value, fireEvents, true);
	}

	private void setValue(Number value, boolean fireEvents, boolean ensureValueConstraints)
    {
	    if (ensureValueConstraints)
	    {
	    	if (value != null && maxValue != null && value.doubleValue() > maxValue.doubleValue())
	    	{
	    		value = maxValue;
	    	}
	    	if (value != null && minValue != null && value.doubleValue() < minValue.doubleValue())
	    	{
	    		value = minValue;
	    	}
	    }
		
		box.setValue(value, fireEvents);
    }

	public static class FormatterOptions
	{
		public static final int DEFAULT_FRACTION_DIGITS = 0;
		public static final boolean DEFAULT_ALLOW_NEGATIVES = true;
		public static final boolean DEFAULT_SHOW_GROUP_SEPARATOR = true;
		public static final int DEFAULT_GROUP_SIZE = 3;

		private int fractionDigits = DEFAULT_FRACTION_DIGITS;
		private boolean allowNegatives = DEFAULT_ALLOW_NEGATIVES;
		private boolean showGroupSeparators = DEFAULT_SHOW_GROUP_SEPARATOR;
		private int groupSize = DEFAULT_GROUP_SIZE;
		private String groupSeparator = null;
		private String decimalSeparator = null;

		public FormatterOptions()
		{
		}

		public String getDecimalSeparator()
		{
			return decimalSeparator;
		}

		public int getFractionDigits()
		{
			return fractionDigits;
		}

		public String getGroupSeparator()
		{
			return groupSeparator;
		}

		public int getGroupSize()
		{
			return groupSize;
		}

		public boolean isAllowNegatives()
		{
			return allowNegatives;
		}

		public boolean isShowGroupSeparators()
		{
			return showGroupSeparators;
		}

		public void setAllowNegatives(boolean allowNegatives)
		{
			this.allowNegatives = allowNegatives;
		}

		public void setDecimalSeparator(String decimalSeparator)
		{
			this.decimalSeparator = decimalSeparator;
		}

		public void setFractionDigits(int fractionDigits)
		{
			this.fractionDigits = fractionDigits;
		}

		public void setGroupSeparator(String groupSeparator)
		{
			this.groupSeparator = groupSeparator;
		}

		public void setGroupSize(int groupSize)
		{
			this.groupSize = groupSize;
		}

		public void setShowGroupSeparators(boolean showGroupSeparators)
		{
			this.showGroupSeparators = showGroupSeparators;
		}
	}

	static class Box extends ValueBox<Number> implements HasPasteHandlers
	{
		public Box(NumberRenderer renderer)
		{
			super(Document.get().createTextInputElement(), renderer, renderer);
			getElement().setAttribute("inputmode", "numeric");
			PasteEventSourceRegisterFactory.getRegister().registerPasteEventSource(this, getElement());
		}
		
		@Override
	    public HandlerRegistration addPasteHandler(PasteHandler handler)
	    {
			return addHandler(handler, PasteEvent.getType());
	    }
	}

	static class EventsHandler implements KeyDownHandler, KeyPressHandler, KeyUpHandler, BlurHandler, PasteHandler
	{
		private static final int DASH = 189;
		private NumberBox numberBox;
		private boolean isControlChar;
		private String text;
		private boolean ignored;

		public EventsHandler(NumberBox numberBox)
		{
			this.numberBox = numberBox;
		}

		@Override
		public void onBlur(BlurEvent event)
		{
			if (StringUtils.unsafeEquals("-", numberBox.box.getText()))
			{
				numberBox.setValue(null);
			}
			else
			{
				Number value = numberBox.getValue();
		    	if (value != null && 
		    		  ((numberBox.maxValue != null && value.doubleValue() > numberBox.maxValue.doubleValue())
		    		|| (numberBox.minValue != null && value.doubleValue() < numberBox.minValue.doubleValue())))
		    	{
					numberBox.setValue(null);
		    	}
			}
		}
		
		@Override
		public void onKeyDown(KeyDownEvent event)
		{
			ignored = false;
			int keyCode = event.getNativeKeyCode();

			boolean isNumber = (keyCode >= KeyCodes.KEY_ZERO && keyCode <= KeyCodes.KEY_NINE) || 
							   (keyCode >= KeyCodes.KEY_NUM_ZERO && keyCode <= KeyCodes.KEY_NUM_NINE);

			boolean isMinus = (keyCode == KeyCodes.KEY_NUM_MINUS || keyCode == DASH);

			isControlChar = keyCode <= KeyCodes.KEY_DELETE || event.isControlKeyDown() || event.isAltKeyDown() || event.isMetaKeyDown();

			if (isMinus && numberBox.formatterOptions.allowNegatives)
			{
				Number numVal = numberBox.box.getValue();
				double value = (numVal != null ? numVal.doubleValue() : 0);
				if (value < 0)
				{
					ignored = true;
				}
				else if (value == 0)
				{
					numberBox.box.setText("-");
					ignored = true;
				}
			}
			else if (keyCode == KeyCodes.KEY_DELETE || keyCode == KeyCodes.KEY_BACKSPACE)
			{
				Number numVal = numberBox.box.getValue();
				double value = (numVal != null ? numVal.doubleValue() : 0);
				if (value == 0)
				{
					numberBox.setValue(null);
					ignored = true;
				}
			}
			else if (!isNumber && !isControlChar)
			{
				ignored = true;
			}

			if (ignored)
			{
				event.preventDefault();
			}
		}

		@Override
		public void onKeyPress(KeyPressEvent event)
		{
			if (!isControlChar)
			{
				String newText = processKey(numberBox.box.getText(), event.getCharCode());
				updateTextValue(newText);
				event.preventDefault();
			}
			text = numberBox.box.getText();
		}

		@Override
		public void onKeyUp(KeyUpEvent event)
		{
			if (isControlChar && !ignored)
			{
				String newText = numberBox.box.getText();
				if (!StringUtils.unsafeEquals(newText, text))
				{
					updateTextValue(newText);
					text = numberBox.box.getText();
				}
			}
		}

		@Override
		public void onPaste(PasteEvent event)
		{
			String newText = numberBox.box.getText();
			if (StringUtils.isEmpty(newText))
			{
				numberBox.setValue(null);
			}
			else
			{
				if (updateTextValue(newText))
				{
					text = numberBox.box.getText();
				}
				else
				{
					updateTextValue(text);
				}
			}
			ignored = true;
		}

		private String processKey(String text, char charCode)
		{
			StringBuilder result = new StringBuilder(text);

			int cursorPos = numberBox.box.getCursorPos();

			if (numberBox.box.getSelectionLength() > 0)
			{
				result.delete(cursorPos, cursorPos + numberBox.box.getSelectionLength());
			}
			if (charCode == '-')
			{
				result.insert(0, charCode);
			}
			else
			{
				result.insert(cursorPos, charCode);
			}
			return result.toString();
		}

		private boolean updateTextValue(String text)
		{
			boolean ret = false;
			if (numberBox.formatterOptions.showGroupSeparators)
			{
				text = text.replace(numberBox.formatterOptions.groupSeparator, "");
			}
			if (numberBox.formatterOptions.fractionDigits > 0)
			{
				text = text.replace(numberBox.formatterOptions.decimalSeparator, "");
			}

			try
			{
				double number = Long.parseLong(text);

				if (numberBox.formatterOptions.fractionDigits > 0)
				{
					number = number / Math.pow(10.0, numberBox.formatterOptions.fractionDigits);
				}

				if (numberBox.maxValue == null || number <= numberBox.maxValue.doubleValue())
				{
					numberBox.setValue(number, false, false);
					ret = true;
				}
			}
			catch (NumberFormatException e)
			{
				ret = numberBox.getValue() == null;
				numberBox.setValue(null);
			}
			return ret;
		}
	}

	static class NumberRenderer extends AbstractRenderer<Number> implements Parser<Number>
	{
		private static final String SWAP_DECIMAL_SEPARATOR = "_|_";
		private NumberFormat format;
		private NumberBox numberBox;

		public NumberRenderer(NumberBox numberBox)
		{
			this.numberBox = numberBox;
		}

		@Override
		public Number parse(CharSequence text) throws ParseException
		{
			if (text.length() == 0)
			{
				return null;
			}

			try
			{
				String toParse = text.toString();
				toParse = changeText(toParse, false);
				return format.parse(toParse);
			}
			catch (NumberFormatException e)
			{
				throw new ParseException(e.getMessage(), 0);
			}
		}

		public String render(Number object)
		{
			if (object == null)
			{
				return "";
			}
			String result = format.format(object);
			return changeText(result, true);
		}

		public void setNumberFormat(NumberFormat format)
		{
			this.format = format;
		}

		private String changeText(String text, boolean toString)
		{
			boolean needsDecimalReplacement = (numberBox.formatterOptions.fractionDigits > 0 && 
												!StringUtils.unsafeEquals(numberBox.formatterOptions.decimalSeparator, numberBox.localeDecimalSeparator));
			boolean needsSeparatorReplacement = (numberBox.formatterOptions.showGroupSeparators && 
												!StringUtils.unsafeEquals(numberBox.formatterOptions.groupSeparator, numberBox.localeGroupSeparator));
			if (needsDecimalReplacement)
			{
				if (toString)
				{
					text = text.replace(".", SWAP_DECIMAL_SEPARATOR);
				}
				else
				{
					text = text.replace(numberBox.formatterOptions.decimalSeparator, SWAP_DECIMAL_SEPARATOR);
				}
			}

			if (needsSeparatorReplacement)
			{
				if (toString)
				{
					text = text.replace(",", numberBox.formatterOptions.groupSeparator);
				}
				else
				{
					text = text.replace(numberBox.formatterOptions.groupSeparator, ",");
				}
			}

			if (needsDecimalReplacement)
			{
				if (toString)
				{
					text = text.replace(SWAP_DECIMAL_SEPARATOR, numberBox.formatterOptions.decimalSeparator);
				}
				else
				{
					text = text.replace(SWAP_DECIMAL_SEPARATOR, ".");
				}
			}
			return text;
		}
	}
}
