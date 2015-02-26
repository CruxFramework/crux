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
package org.cruxframework.crux.widgets.client.colorpicker;


import java.util.Arrays;
import java.util.List;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.WidgetMessages;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.util.ColorUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@PartialSupport
/**
 * @author Thiago da Rosa de Bustamante
 */
public class ColorPickerDialog extends DialogBox implements HasCloseHandlers<PopupPanel>, IsWidget, OrientationChangeHandler
{
	private SaturationLightnessPicker slPicker;
	private HuePicker huePicker;
	private String color;

	protected WidgetMessages messages = WidgetMsgFactory.getMessages();
	private Widget dialogArea;
	private Button okButton;
	private Button cancelButton;
	private SimplePanel previewPanel;
	private VerticalPanel panel;
	private TextBox manualPicker;
	
	private SelectHandler buttonSelectHandler = new SelectHandler()
	{
		public void onSelect(SelectEvent event)
		{
			Object button = event.getSource();
			if (button != null && okButton != null && okButton.toString().equals(button.toString()))
			{
				color = slPicker.getColor();
				
				if(!StringUtils.isEmpty(color))
				{
					if(!color.startsWith("#"))
					{
						manualPicker.setText("#" + color);
					} else
					{
						manualPicker.setText(color);
					}
				}
			}

			close(button != null && cancelButton != null && cancelButton.toString().equals(button.toString()));
		}
	};

	private ColorPickerDialog()
	{
		panel = new VerticalPanel();
		dialogArea = createDialogArea();
		panel.add(dialogArea);
		panel.add(getManualTextBoxColorPicker());
		panel.add(createControlBar());
		setStyleName("crux-ColorPickerDialog");
		setWidget(panel);
		handleOrientationChangeHandlers();
	}

	private void handleOrientationChangeHandlers() 
	{
		this.addAttachHandler(new Handler()
		{
			private HandlerRegistration orientationHandlerRegistration;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					try
					{
						orientationHandlerRegistration = Screen.addOrientationChangeHandler(ColorPickerDialog.this);	
					} catch (Exception e)
					{
						orientationHandlerRegistration = null;
					}
				}
				else if (orientationHandlerRegistration != null)
				{
					orientationHandlerRegistration.removeHandler();
					orientationHandlerRegistration = null;
				}
			}
		});
	}
	
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler)
	{
	    return super.addCloseHandler(handler);
	}
	
	protected void close(boolean canceled)
	{
		hide();
	}

	protected HorizontalPanel createControlBar()
	{
		HorizontalPanel panel = new HorizontalPanel();
		FlowPanel buttonBar = createButtonBar();
		panel.add(buttonBar);
		previewPanel = new SimplePanel();
		previewPanel.addStyleName("previewPanel");
		panel.add(previewPanel);
		panel.setCellHorizontalAlignment(previewPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.setCellVerticalAlignment(buttonBar, HasVerticalAlignment.ALIGN_BOTTOM);
		panel.setWidth("100%");
		return panel;
	}
	
	protected TextBox getManualTextBoxColorPicker()
	{
		final TextBox innerManualPicker = new TextBox();
		innerManualPicker.addStyleName("manualPicker");
		innerManualPicker.addBlurHandler(new BlurHandler() 
		{
			@Override
			public void onBlur(BlurEvent event) 
			{
				if(!StringUtils.isEmpty(innerManualPicker.getText()))
				{
					slPicker.setColor(innerManualPicker.getText().replace("#", ""));	
				}
			}
		});
		this.manualPicker = innerManualPicker;
		return manualPicker;
	}
	
	protected FlowPanel createButtonBar()
	{
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName("PickerButtons"); 
		List<? extends Widget> buttons = createButtonsForButtonBar();
		for (Widget button : buttons)
		{
			buttonsPanel.add(button);
		}
		return buttonsPanel;
	}

	protected List<? extends Widget> createButtonsForButtonBar()
	{
		okButton = createButton(messages.okLabel());
		cancelButton = createButton(messages.cancelLabel()); 
		return Arrays.asList(okButton, cancelButton);
	}

	protected Button createButton(String text)
	{
		return new Button(text, buttonSelectHandler);
	}

	@Override
	public void show()
	{
		//if it's a touch device, then we should wait for virtual keyboard to get closed.
		//Otherwise the dialog message will not be properly centered in screen.  
		if(Screen.getCurrentDevice().getInput().equals(DeviceAdaptive.Input.touch))
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
			{
				@Override
				public boolean execute() 
				{
					doShow();
					return false;
				}
			}, 1000);
		} else 
		{
			doShow();
		}
	}
	
	private void doShow()
	{
	    super.show();
	}
	
	protected HorizontalPanel createDialogArea()
	{
		setText(messages.colorPickerDialogSelectColor());

		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(5);
		// the pickers
		slPicker = new SaturationLightnessPicker();
		
		slPicker.addValueChangeHandler(new ValueChangeHandler<String>()
		{
			@Override
            public void onValueChange(ValueChangeEvent<String> event)
            {
				previewPanel.getElement().getStyle().setBackgroundColor("#"+event.getValue());
				manualPicker.setText("#"+event.getValue());
            }
		});
		panel.add(slPicker);
		huePicker = new HuePicker();
		panel.add(huePicker);

		// bind saturation/lightness picker and hue picker together
		huePicker.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				slPicker.setHue(event.getValue());
			}
		});

		return panel;
	}

	public void setColor(String color)
	{
		this.color = color;
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		huePicker.setHue(hsl[0]);
		slPicker.setColor(color);
		previewPanel.getElement().getStyle().setBackgroundColor(color);
	}

	public String getColor()
	{
		return color;
	}

	public static boolean isSupported() 
	{
		return Canvas.isSupported();
	}

	public static ColorPickerDialog createIfSupported() 
	{
		if (isSupported())
		{
			return new ColorPickerDialog();
		}
		return null;
	}
	
	@Override
	public void onOrientationChange() 
	{
		center();
	}
}