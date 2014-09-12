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

import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.dialog.PopupPanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wesley.diniz
 *
 * @param <V> Value type
 * @param <T> Data object type
 */
public abstract class AbstractComboBox<V, T> extends Composite implements HasValue<V>, Pageable<PagedDataProvider<T>>
{
	private static final String FACES_COMBO_BOX_POPUP = "faces-ComboBox-Popup";
	private static final String FACES_COMBO_BOX_BUTTON = "faces-ComboBox-Button";
	public static final String DEFAULT_STYLE_NAME = "faces-ComboBox";
	private static final String BODY_PANEL_STYLE_NAME = "faces-ComboBox-bodyPanel";
	
	private PopupPanel popup;
	private final Button button = new Button();
	private ComboBoxOptionList<V,T> optionsList;
	private final TextBox textBox = new TextBox();
	private final FlowPanel bodyPanel = new FlowPanel();
	private V value;
	
	public AbstractComboBox(OptionsRenderer<V, T> optionsRenderer)
	{
		initWidget(bodyPanel);
		createVisualComponents(optionsRenderer);
	} 
	
	private void createVisualComponents(OptionsRenderer<V, T> optionsRenderer)
	{
		//define styles
		this.bodyPanel.add(textBox);
		this.bodyPanel.setStyleName(BODY_PANEL_STYLE_NAME);
		this.bodyPanel.add(button);
		
		this.textBox.setStyleName("faces-textbox");
		
		this.textBox.addClickHandler(new ClickHandler(){
			
			@Override
			public void onClick(ClickEvent event)
			{
				createPopup();
			}
		});
		
		this.optionsList = new ComboBoxOptionList<V,T>(optionsRenderer, this);
		
		this.button.setStyleName(FACES_COMBO_BOX_BUTTON);
		this.button.addSelectHandler(new SelectHandler(){
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
				//TODO Change to show only the display field
				textBox.setText(String.valueOf("id:" + event.value + " - Label: " +event.displayField));
				//textBox.setText(event.getDisplayField());
				value = event.getValue();
				popup.hide();
			}
		}, SelectComboItemEvent.getType());
		setStyleName(DEFAULT_STYLE_NAME);
	}

	private void createPopup()
	{ 
		popup = new PopupPanel();
		popup.addStyleName(FACES_COMBO_BOX_POPUP);
		popup.setAutoHideEnabled(true);
		popup.add(optionsList);
		popup.showRelativeTo(textBox);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler)
	{
		return addValueChangeHandler(handler);
	}

	@Override
	public int getPageSize()
	{
		return optionsList.getPageSize();
	}

	@Override
	public void setPageSize(int pageSize)
	{
		optionsList.setPageSize(pageSize);
	}	
	
	@Override
	public PagedDataProvider<T> getDataProvider()
	{
		return optionsList.getDataProvider();
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

	@Override
	public int getPageCount()
	{
		return optionsList.getPageCount();
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
	public void setPager(Pager pager)
	{
		bodyPanel.add(pager);
		optionsList.setPager(pager);
	}
	
	@Override
	public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		optionsList.setDataProvider(dataProvider, autoLoadData);  
	}
	
	public V getValue()
	{
		return this.value;
	}
	
	/**
	 * @author wesley.diniz
	 *
	 * @param <V>
	 * @param <T>
	 */
	public static interface OptionsRenderer<V, T> extends WidgetFactory<T>
	{
		V getValueField(T record);
		String getDisplayField(T record);
	}
	
	/**
	 * @author wesley.diniz
	 *
	 * @param <V>
	 */
	private static class ComboBoxOptionPanel<V> extends Composite
	{
		private SelectablePanel bodyPanel = new SelectablePanel();
		private V value;
		private String displayField;

		ComboBoxOptionPanel(final Widget parent)
		{
			initWidget(bodyPanel);
			bodyPanel.setStyleName("faces-comboBoxOptionPanel");
			
//	TODO		bodyPanel.addDomHandler(new MouseOverHandler(){
//				@Override
//				public void onMouseOver(MouseOverEvent event)
//				{
//				}
//			},MouseOverEvent.getType());
//			
			bodyPanel.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent event)
				{
					SelectComboItemEvent<V> ev = new SelectComboItemEvent<V>();
					ev.value = getValue();
					ev.displayField = getDisplayField();
					parent.fireEvent(ev);
				}
			});
			
			bodyPanel.addDomHandler(new MouseOutHandler(){
				
				@Override
				public void onMouseOut(MouseOutEvent event)
				{
				}
			}, MouseOutEvent.getType());
		}

		void add(IsWidget w)
		{
			this.bodyPanel.add(w);
		}
		
		V getValue()
		{
			return value;
		}

		void setValue(V value)
		{
			this.value = value;
		}

		String getDisplayField()
		{
			return displayField;
		}

		void setDisplayField(String displayField)
		{
			this.displayField = displayField;
		}
	}
	
    /**
     * @author wesley.diniz
     *
     * @param <V>
     * @param <T>
     */
    private static class ComboBoxOptionList<V,T> extends WidgetList<T>
	{
    	private final AbstractComboBox<V,T> comboBoxParent;
    	
		public ComboBoxOptionList(OptionsRenderer<V,T> optionsRenderer, AbstractComboBox<V,T> comboBoxParent)
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
					IsWidget widget = widgetFactory.createWidget(dataProvider.getBoundObject());
					ComboBoxOptionPanel<V> panel = new ComboBoxOptionPanel<V>(comboBoxParent);
					panel.setValue(((OptionsRenderer<V,T>)widgetFactory).getValueField(dataProvider.getBoundObject()));
					panel.setDisplayField(((OptionsRenderer<V,T>)widgetFactory).getDisplayField(dataProvider.getBoundObject()));
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
    @SuppressWarnings("rawtypes")
	private static class SelectComboItemEvent<V> extends GwtEvent<SelectComboItemHandler>
    {
    	private static final Type<SelectComboItemHandler> TYPE = new Type<SelectComboItemHandler>();
    	private V value;
    	private String displayField;
    	
		@Override
		public Type<SelectComboItemHandler> getAssociatedType()
		{
			return TYPE;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void dispatch(SelectComboItemHandler handler)
		{
			handler.onSelectItem(this);
		}

		static Type<SelectComboItemHandler> getType()
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
