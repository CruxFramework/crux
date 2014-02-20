/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.textbox;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.maskedtextbox.IntFilterFormatter;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.text.client.IntegerParser;
import com.google.gwt.text.client.IntegerRenderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ValueBoxBase;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
//TODO refatorar isso. Deveriamos ter no futuro boxs usando html5 apenas
public class NumberTextBox extends Composite implements HasValue<Integer>, HasEnabled, HasAllFocusHandlers, HasName 
{
	private Impl impl;

	static interface Impl extends IsWidget, HasValue<Integer>, HasEnabled, HasAllFocusHandlers, HasName 
	{
		int getMaxLength();
		void setMaxLength(int length);
	}
	
	static class DesktopImpl extends Composite implements Impl
	{
		public static class ValueChangeWrapEvent extends ValueChangeEvent<Integer>
		{
			protected ValueChangeWrapEvent(Integer value, DesktopImpl source)
            {
	            super(value);
	            setSource(source);
            }
		}

		private MaskedTextBox box;

		public DesktopImpl()
        {
			box = new MaskedTextBox(new IntFilterFormatter());
			initWidget(box);
        }

		@Override
        public void setValue(Integer value)
        {
			setValue(value, false);
        }

		@Override
        public void setValue(Integer value, boolean fireEvents)
        {
	        box.setUnformattedValue(value, fireEvents);
        }

        public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Integer> handler)
        {
	        return box.addValueChangeHandler(new ValueChangeHandler<String>()
			{
				@Override
                public void onValueChange(ValueChangeEvent<String> event)
                {
					handler.onValueChange(new ValueChangeWrapEvent(Integer.parseInt(event.getValue()), DesktopImpl.this));
                }
			});
        }

		@Override
        public Integer getValue()
        {
	        return (Integer) box.getUnformattedValue();
        }

		@Override
        public boolean isEnabled()
        {
	        return box.isEnabled();
        }

		@Override
        public void setEnabled(boolean enabled)
        {
			box.setEnabled(enabled);
        }

		@Override
        public HandlerRegistration addFocusHandler(FocusHandler handler)
        {
	        return box.addFocusHandler(handler);
        }

		@Override
        public HandlerRegistration addBlurHandler(BlurHandler handler)
        {
	        return box.addBlurHandler(handler);
        }

		@Override
        public void setName(String name)
        {
			box.setName(name);
        }

		@Override
        public String getName()
        {
	        return box.getName();
        }

		@Override
        public int getMaxLength()
        {
	        return box.getMaxLength();
        }

		@Override
        public void setMaxLength(int length)
        {
			box.setMaxLength(length);
        }
	}
	
	static class DevicesImpl extends Composite implements Impl
	{
		static class HTML5Input extends ValueBoxBase<Integer>
		{
			private static InputElement baseElement = null;
			
			protected HTML5Input()
	        {
		        super(createElement(), IntegerRenderer.instance(), IntegerParser.instance());
		        setDirectionEstimator(false);
		        if (LocaleInfo.getCurrentLocale().isRTL()) 
		        {
		          setDirection(Direction.LTR);
		        }
	        }

			private static InputElement createElement()
            {
				initBaseElement();
	            return baseElement.cloneNode(false).cast();
            }

			private static void initBaseElement()
            {
	            if (baseElement == null)
				{
					baseElement = createInputElement();
				}
            }
			
			private static boolean isSupported()
			{
				initBaseElement();
				return !StringUtils.unsafeEquals(baseElement.getType(), "text");
			}

			private static native InputElement createInputElement() /*-{
			    var e = $doc.createElement("INPUT");
			    e.type = "number";
			    return e;
			}-*/;

			public int getMaxLength()
            {
			    return getInputElement().getMaxLength();
            }

			public void setMaxLength(int length)
            {
				getInputElement().setMaxLength(length);
            }
			
			private InputElement getInputElement() 
			{
				return getElement().cast();
			}
		}

		ValueBoxBase<Integer> impl;
		
		public DevicesImpl()
        {
			if (HTML5Input.isSupported())
			{
				impl = new HTML5Input();
			}
			else
			{
				impl = new IntegerBox();
			}
			initWidget(impl);
        }
		
		@Override
        public Integer getValue()
        {
	        return impl.getValue();
        }

		@Override
        public void setValue(Integer value)
        {
			impl.setValue(value);
        }

		@Override
        public void setValue(Integer value, boolean fireEvents)
        {
			impl.setValue(value, fireEvents);
        }

		@Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler)
        {
	        return impl.addValueChangeHandler(handler);
        }

		@Override
        public boolean isEnabled()
        {
	        return impl.isEnabled();
        }

		@Override
        public void setEnabled(boolean enabled)
        {
			impl.setEnabled(enabled);
        }

		@Override
        public HandlerRegistration addFocusHandler(FocusHandler handler)
        {
	        return impl.addFocusHandler(handler);
        }

		@Override
        public HandlerRegistration addBlurHandler(BlurHandler handler)
        {
	        return impl.addBlurHandler(handler);
        }

		@Override
        public void setName(String name)
        {
			impl.setName(name);
        }

		@Override
        public String getName()
        {
	        return impl.getName();
        }

		@Override
        public int getMaxLength()
        {
			if (HTML5Input.isSupported())
			{
				return ((HTML5Input)impl).getMaxLength();
			}
			else
			{
				return ((IntegerBox)impl).getMaxLength();
			}
        }

		@Override
        public void setMaxLength(int length)
        {
			if (HTML5Input.isSupported())
			{
				((HTML5Input)impl).setMaxLength(length);
			}
			else
			{
				((IntegerBox)impl).setMaxLength(length);
			}
        }
	}
	
	public NumberTextBox()
    {
		impl = GWT.create(Impl.class);
		initWidget(impl.asWidget());
		setStyleName("crux-NumberTextBox");
    }

	@Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler)
    {
	    return impl.addValueChangeHandler(handler);
    }

	@Override
    public Integer getValue()
    {
	    return impl.getValue();
    }

	@Override
    public void setValue(Integer value)
    {
		setValue(value, false);
    }

	@Override
    public void setValue(Integer value, boolean fireEvents)
    {
		impl.setValue(value, fireEvents);
    }

	@Override
    public boolean isEnabled()
    {
	    return impl.isEnabled();
    }

	@Override
    public void setEnabled(boolean enabled)
    {
		impl.setEnabled(enabled);
    }

	@Override
    public HandlerRegistration addFocusHandler(FocusHandler handler)
    {
	    return impl.addFocusHandler(handler);
    }

	@Override
    public HandlerRegistration addBlurHandler(BlurHandler handler)
    {
	    return impl.addBlurHandler(handler);
    }

	@Override
    public void setName(String name)
    {
		impl.setName(name);
    }

	@Override
    public String getName()
    {
	    return impl.getName();
    }
	
	public int getMaxLength()
	{
		return impl.getMaxLength();
	}
	
	public void setMaxLength(int length)
	{
		impl.setMaxLength(length);
	}

}
