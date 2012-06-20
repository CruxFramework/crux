package org.cruxframework.crux.crossdevice.client.labeledtextbox;

import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
*
* @author    Daniel Martins - <code>daniel@cruxframework.org</code>
*
*/
public abstract class BaseLabeledTextBoxController extends DeviceAdaptiveController implements LabeledTextBox
{

	@Create
	protected LabeledTextBoxHandler labeledTextBoxHandler;

	FlowPanel flowPanel;
	Label label;
	TextBox textBox;


	@Override
	protected void init()
	{
		flowPanel = getChildWidget("labeledTextBox");
		label = getChildWidget("label");
		textBox = getChildWidget("textBox");
	}

	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("xdev-LabeledTextBox");
		applyWidgetDependentStyleNames();
	}

	@Override
	public void setWidth(String width)
	{
		this.flowPanel.setWidth(width);
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.flowPanel.setVisible(visible);
	}

	@Override
	public boolean isVisible()
	{
		return this.flowPanel.isVisible();
	}

	@Override
	public void setStyleName(String style)
	{
		this.flowPanel.setStyleName(style);
	}

	@Override
	public String getStyleName()
	{
		return this.flowPanel.getStyleName();
	}

	@Override
	public void setTitle(String title)
	{
		this.textBox.setTitle(title);
	}

	@Override
	public String getTitle()
	{
		return this.textBox.getTitle();
	}

	@Override
	public void setHeight(String height)
	{
		this.flowPanel.setHeight(height);
	}

	@Override
	public void setValue(String value)
	{
		this.textBox.setValue(value);
	}

	@Override
	public String getValue()
	{
		return labeledTextBoxHandler.getValue(this.textBox);
	}

	@Override
	public void setMaxLength(int maxLength)
	{
		this.textBox.setMaxLength(maxLength);
	}

	@Override
	public int getMaxLength()
	{
		return this.textBox.getMaxLength();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.textBox.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled()
	{
		return this.textBox.isEnabled();
	}

	@Override
	public void setText(String text)
	{
		this.label.setText(text);
	}

	@Override
	public String getText()
	{
		return this.label.getText();
	}

	@Override
	public void setValue(String value, boolean fireEvents)
	{
		this.textBox.setValue(value, fireEvents);
	}

	@Override
	public void setName(String name)
	{
		this.textBox.setName(name);
	}

	@Override
	public String getName()
	{
		return this.textBox.getName();
	}

	@Override
	public void setReadOnly(boolean readOnly)
	{
		this.textBox.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly()
	{
		return this.textBox.isReadOnly();
	}

	@Override
	public void setPlaceholder(String placeholder)
	{
		labeledTextBoxHandler.setPlaceholder(this.textBox, placeholder);
	}

	@Override
	public String getPlaceholder()
	{
		return labeledTextBoxHandler.getPlaceholder(this.textBox);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
	{
		return this.textBox.addValueChangeHandler(handler);
	}

	@Override
	public int getTabIndex()
	{
		return this.textBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key)
	{
		this.textBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused)
	{
		this.textBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index)
	{
		this.textBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return this.textBox.addFocusHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return this.textBox.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
	{
		return this.textBox.addMouseDownHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return this.addMouseUpHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return this.textBox.addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return this.textBox.addMouseOverHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return this.textBox.addMouseMoveHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
	{
		return this.textBox.addMouseWheelHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler)
	{
		return this.textBox.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
	{
		return this.textBox.addKeyDownHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
	{
		return this.textBox.addKeyPressHandler(handler);
	}


	public interface LabeledTextBoxHandler
	{
		void setPlaceholder(final TextBox textBox, String placeholder);
		String getValue(final TextBox textBox);
		String getPlaceholder(final TextBox textBox);
	}

	static class HTMLPlaceHolder implements LabeledTextBoxHandler
	{
		public void setPlaceholder(final TextBox textBox, String placeholder)
		{
			textBox.getElement().setAttribute("placeholder", placeholder);
		}

		@Override
		public String getPlaceholder(final TextBox textBox)
		{
			return textBox.getElement().getAttribute("placeholder");
		}

		@Override
		public String getValue(TextBox textBox)
		{
			return textBox.getValue();
		}
	}


	static class JSPlaceHolder implements LabeledTextBoxHandler
	{
		String placeholder;

		@Override
		public void setPlaceholder(final TextBox textBox, final String placeholder)
		{
			this.placeholder = placeholder;

			textBox.setValue(placeholder);
			StyleUtils.addStyleDependentName(textBox.getElement(), "placeholder");

			textBox.addBlurHandler(new BlurHandler()
			{

				@Override
				public void onBlur(BlurEvent event)
				{
					TextBox textBox = (TextBox) event.getSource();

					if( textBox.getValue() == null || "".equals(textBox.getValue()) )
					{
						textBox.setValue(placeholder);
						StyleUtils.addStyleDependentName(textBox.getElement(), "placeholder");
					}
				}
			});

			textBox.addFocusHandler(new FocusHandler()
			{

				@Override
				public void onFocus(FocusEvent event)
				{
					TextBox textBox = (TextBox) event.getSource();

					StyleUtils.removeStyleDependentName(textBox.getElement(), "placeholder");

					if( textBox.getValue() != null && textBox.getValue().equals(placeholder) )
					{
						textBox.setValue(null);
					}
				}
			});
		}


		@Override
		public String getPlaceholder(final TextBox textBox)
		{
			return placeholder;
		}


		@Override
		public String getValue(TextBox textBox)
		{
			return textBox.getValue() != null && !textBox.getValue().equals(placeholder) ? textBox.getValue() : null;
		}
	}
}
