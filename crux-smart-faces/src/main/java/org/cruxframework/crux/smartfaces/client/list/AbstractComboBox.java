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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataFilter;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider.FilterRegistration;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.dialog.PopupPanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wesley.diniz
 * @param <V>
 *            Value type
 * @param <T>
 *            Data object type
 * 
 *            <p>
 *            <b>Example of usage:</b>
 *            </p>
 *            <p>
 *            &lt;faces:comboBox id="comboBox1" dataObject="person"
 *            pageSize="10" autoLoadData="true"&gt;<br>
 *            &emsp;&lt;faces:dataProvider  dataObject="person" autoLoadData="true" onLoad="homeController.loadComboData" /&gt;<br>
 *            &emsp;&lt;faces:optionsRenderer valuePath="name"
 *            labelPath="name"&gt;<br>
 *            &emsp;&lt;faces:displayWidget&gt; <br>
 *            &emsp;&lt;faces:button bindPath="name" id="buttons"
 *            text="name"&gt;&lt;/faces:button&gt;<br>
 *            &emsp;&lt;/faces:displayWidget&gt;<br>
 *            &emsp;&lt;/faces:optionsRenderer&gt;<br>
 *            &lt;/faces:comboBox&gt;
 *            </p>
 */
public abstract class AbstractComboBox<V, T> extends Composite implements HasValue<V>, Pageable<PagedDataProvider<T>>, HasAllFocusHandlers, HasEnabled, HasSelectHandlers, HasAllMouseHandlers
{
	private static final String BODY_PANEL = "faces-ComboBox-bodyPanel";
	private static final String COMBO_BOX_BUTTON = "faces-ComboBox-Button";
	private static final String COMBO_BOX_COMBO_ITEM_LIST = "faces-comboBox-comboItemList";
	private static final String COMBO_BOX_POPUP = "faces-ComboBox-Popup";
	private static final String COMBO_BOX_SCROLL_PANEL = "faces-comboBox-scrollPanel";
	private static final String COMBO_BOX_TEXT = "faces-ComboBox-Text";
	public static final String DEFAULT_STYLE_NAME = "faces-ComboBox";
	public static final String LABEL_ITEM = "faces-comboBox-LabelItem";

	private final FlowPanel bodyPanel = new FlowPanel();
	private final Button button = new Button();
	private Array<FilterRegistration<T>> filters = CollectionFactory.createArray();
	private boolean isFilterable = true;
	private ComboBoxOptionList<V, T> optionsList;
	protected OptionsRenderer<V, T> optionsRenderer = null;
	private PopupPanel popup;
	private ScrollPanel scrollPanel;
	private final TextBox textBox = new TextBox();
	private V value;

	/**
	 * @param optionsRenderer
	 */
	public AbstractComboBox(OptionsRenderer<V, T> optionsRenderer)
	{
		initWidget(bodyPanel);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		this.optionsRenderer = optionsRenderer;
		createVisualComponents(optionsRenderer);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addBlurHandler(handler);
	}
	
	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
	{
		return addMouseDownHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return addMouseMoveHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return addMouseOverHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return addMouseUpHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
	{
		return addMouseWheelHandler(handler);
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addSelectHandler(handler);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler)
	{
		return addValueChangeHandler(handler);
	}
	
	private void createPopup()
	{
		popup = new PopupPanel();
		popup.addStyleName(COMBO_BOX_POPUP);
		popup.setAutoHideEnabled(true);
		popup.add(scrollPanel);
		popup.showRelativeTo(textBox);
	}
	
	
	private void createVisualComponents(OptionsRenderer<V, T> optionsRenderer)
	{
		this.optionsRenderer = optionsRenderer;
		bodyPanel.add(textBox);
		bodyPanel.setWidth("100%");
		bodyPanel.setStyleName(BODY_PANEL);
		bodyPanel.add(button);

		textBox.setStyleName(COMBO_BOX_TEXT);
		textBox.addClickHandler(new ClickHandler(){

			@Override	
			public void onClick(ClickEvent event)
			{
				createPopup();
			}
		});
		
		textBox.addBlurHandler(new BlurHandler(){
			
			@Override
			public void onBlur(BlurEvent event)
			{
				if(isFilterable)
				{
					filters.clear();
				}
			}
		});
		
		textBox.setReadOnly(!isFilterable);
		
		optionsList = new ComboBoxOptionList<V, T>(optionsRenderer, this);
		optionsList.setStyleName(COMBO_BOX_COMBO_ITEM_LIST);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(COMBO_BOX_SCROLL_PANEL);
		scrollPanel.add(optionsList);
		
		button.setStyleName(COMBO_BOX_BUTTON);
		button.addSelectHandler(new SelectHandler(){
			@Override
			public void onSelect(SelectEvent event)
			{
				createPopup();
			}
		});

		addHandler(new SelectComboItemHandler<V>(){
			@Override
			public void onSelectItem(SelectComboItemEvent<V> event)
			{
				selectItem(event.label,event.value);
			}
		}, SelectComboItemEvent.getType());
		setStyleName(DEFAULT_STYLE_NAME);
		
		if(isFilterable)
		{
			addFilterEvents();
		}
	}

	@Override
	public PagedDataProvider<T> getDataProvider()
	{
		return optionsList.getDataProvider();
	}

	@Override
	public int getPageCount()
	{
		return optionsList.getPageCount();
	}

	@Override
	public int getPageSize()
	{
		return optionsList.getPageSize();
	}

	public int getSelectedIndex()
	{
		//TODO
		return -1;
	}

	public V getValue()
	{
		return value;
	}

	@Override
	public void goToPage(int page)
	{
		optionsList.goToPage(page);
	}

	@Override
	public boolean isDataLoaded()
	{
		return optionsList.isDataLoaded();
	}

	@Override
	public boolean isEnabled()
	{
		return button.isEnabled();
	}

	public boolean isFilterable()
	{
		return isFilterable;
	}

	@Override
	public void nextPage()
	{
		optionsList.nextPage();
	}

	@Override
	public void previousPage()
	{
		optionsList.previousPage();
	}
	
	public void refresh()
	{
		optionsList.reset();
	}

	protected void selectItem(String label, V value)
	{
		textBox.setText(label);
		this.value = value;
		if(popup != null)
		{
			popup.hide();
		}
	}

	@Override
	public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		optionsList.setDataProvider(dataProvider, autoLoadData);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		button.setEnabled(enabled);
		textBox.setEnabled(enabled);
	}

	public void setFilterable(boolean isFilterable)
	{
		this.isFilterable = isFilterable;
	}

	@Override
	public void setPager(Pager pager)
	{
		bodyPanel.add(pager);
		optionsList.setPager(pager);
	}

	@Override
	public void setPageSize(int pageSize)
	{
		optionsList.setPageSize(pageSize);
	}

	public void setSelectedIndex(int index)
	{
		//TODO
	}

	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneCombobox());
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneCombobox());
		}
	}
	
	protected abstract void setValueByObject(T obj);

	
	@Override
	public void setWidth(String width)
	{
 		super.setWidth(width);
		scrollPanel.setWidth(width);

		width = width.substring(0,width.indexOf("px"));
		int widthInt = Integer.parseInt(width);
		int widthTextBox = widthInt-23;
		textBox.setWidth(widthTextBox+"px");
	}
	
	//TODO Finish filter functions
	private void addFilterEvents()
	{
		@SuppressWarnings("unchecked")
		final FilterableProvider<T> provider = (FilterableProvider<T>) getDataProvider();
		
		if(provider != null)
		{
			textBox.addKeyPressHandler(new KeyPressHandler(){

				@Override
				public void onKeyPress(KeyPressEvent event)
				{
					Window.alert("aaa");
					char keyCode = event.getCharCode();
					final String text = textBox.getText();
					
					final DataFilter<T> newFilter = new DataFilter<T>(){
						
						@Override
						public boolean accept(T dataObject)
						{
							return String.valueOf(optionsRenderer.getValue(dataObject)).startsWith(text);
						}
					};
					
					if(keyCode == KeyCodes.KEY_BACKSPACE || keyCode == KeyCodes.KEY_DELETE)
					{
						FilterRegistration<T> registration = null;
						
						if(filters.size() > 0)
						{
							registration = filters.get(filters.size()-1);
						}
	
						registration.replace(newFilter,false);
					}else
					{
						filters.add(provider.addFilter(newFilter));
					}
				}
			});
		}
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 * @param <T>
	 */
	private static class ComboBoxOptionList<V, T> extends WidgetList<T>
	{
		private final AbstractComboBox<V, T> comboBoxParent;

		public ComboBoxOptionList(OptionsRenderer<V, T> optionsRenderer, AbstractComboBox<V, T> comboBoxParent)
		{
			super(optionsRenderer);
			this.comboBoxParent = comboBoxParent;
		}

		@Override
		protected AbstractPageable.Renderer<T> getRenderer()
		{
			return new Renderer<T>(){

				@SuppressWarnings("unchecked")
				@Override
				public void render(T value)
				{
					IsWidget widget = widgetFactory.createWidget(dataProvider.get());
					ComboBoxOptionPanel<V> panel = new ComboBoxOptionPanel<V>(comboBoxParent);
					panel.setValue(((OptionsRenderer<V, T>) widgetFactory).getValue(dataProvider.get()));
					panel.setLabel(((OptionsRenderer<V, T>) widgetFactory).getLabel(dataProvider.get()));
					panel.add(widget);
					
					contentPanel.add(panel);
				}
			};
		}
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	private static class ComboBoxOptionPanel<V> extends Composite
	{
		private static final String COMBO_BOX_OPTION_PANEL = "faces-comboBoxOptionPanel";
		private SelectablePanel bodyPanel = new SelectablePanel();
		private String label;
		private V value;

		ComboBoxOptionPanel(final Widget parent)
		{
			initWidget(bodyPanel);
			bodyPanel.setStyleName(COMBO_BOX_OPTION_PANEL);
			bodyPanel.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent event)
				{
					SelectComboItemEvent<V> ev = new SelectComboItemEvent<V>();
					ev.value = getValue();
					ev.label = getLabel();
					parent.fireEvent(ev);
				}
			});
		}

		void add(IsWidget w)
		{
			bodyPanel.add(w);
		}

		String getLabel()
		{
			return label;
		}

		V getValue()
		{
			return value;
		}

		void setLabel(String label)
		{
			this.label = label;
		}

		void setValue(V value)
		{
			this.value = value;
		}
	}

	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 * @param <T>
	 */
	public static interface OptionsRenderer<V, T> extends WidgetFactory<T>
	{
		String getLabel(T record);

		V getValue(T record);
	}

	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	@SuppressWarnings({"rawtypes","unused"})
	private static class SelectComboItemEvent<V> extends GwtEvent<SelectComboItemHandler>
	{
		private static final Type<SelectComboItemHandler> TYPE = new Type<SelectComboItemHandler>();
		private String label;
		private V value;

		static Type<SelectComboItemHandler> getType()
		{
			return TYPE;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void dispatch(SelectComboItemHandler handler)
		{
			handler.onSelectItem(this);
		}

		@Override
		public Type<SelectComboItemHandler> getAssociatedType()
		{
			return TYPE;
		}

		V getValue()
		{
			return value;
		}
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	private static interface SelectComboItemHandler<V> extends EventHandler
	{
		void onSelectItem(SelectComboItemEvent<V> event);
	}
}
