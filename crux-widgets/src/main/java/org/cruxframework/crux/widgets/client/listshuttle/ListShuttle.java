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
package org.cruxframework.crux.widgets.client.listshuttle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * @author Jair Elton
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @param <T> the ListShuttle type object.
 */
//TODO refatorar este componente
public class ListShuttle<T> extends Composite implements IListShuttle<T>
{
	private boolean enabled;
	
	private List<T> availableItems;
	private List<T> selectedItems;
	private BeanRenderer<T> beanRenderer;

	protected FlowPanel listShuttle;
	
	protected CaptionPanel toSelectColumnFieldset;
	protected CaptionPanel selectedColumnFieldset;
	
	protected CellList<T> availableCellList;
	protected CellList<T> selectedCellList;
	
	protected Button addSelectedButton;
	protected Button addAllButton;
	protected Button removeSelectedButton;
	protected Button removeAllButton;

	public ListShuttle()
	{
		this(new ArrayList<T>(), new ArrayList<T>());
	}
	
	public ListShuttle(List<T> availableItems, List<T> selectedItems)
	{
		listShuttle = new FlowPanel();
		
		//First Column
		FlowPanel toSelectColumn = new FlowPanel();
		toSelectColumn.setStyleName("toSelectColumn");

		toSelectColumnFieldset = new CaptionPanel();
		toSelectColumnFieldset.setStyleName("toSelectColumnFieldset");
		
		availableCellList = new CellList<T>(new BeanCell());
		availableCellList.setSelectionModel(new MultiSelectionModel<T>());
		
		//Middle Column
		FlowPanel buttonsColumn = new FlowPanel();
		buttonsColumn.setStyleName("buttonsColumn");
		
		addSelectedButton = new Button();
		addSelectedButton.setText(">");
		addAllButton = new Button();
		addAllButton.setText(">>");
		removeSelectedButton = new Button();
		removeSelectedButton.setText("<");
		removeAllButton = new Button();
		removeAllButton.setText("<<");
		
		//Last Column
		FlowPanel selectedColumn = new FlowPanel();
		selectedColumn.setStyleName("selectedColumn");
		
		selectedColumnFieldset = new CaptionPanel();
		selectedColumnFieldset.setStyleName("selectedColumnFieldset");
		
		selectedCellList = new CellList<T>(new BeanCell());
		selectedCellList.setSelectionModel(new MultiSelectionModel<T>());
		
		//Attaching columns
		toSelectColumnFieldset.add(availableCellList);
		selectedColumnFieldset.add(selectedCellList);
		
		toSelectColumn.add(toSelectColumnFieldset);
		selectedColumn.add(selectedColumnFieldset);
		
		buttonsColumn.add(addSelectedButton);
		buttonsColumn.add(addAllButton);
		buttonsColumn.add(removeSelectedButton);
		buttonsColumn.add(removeAllButton);
		
		listShuttle.add(toSelectColumn);
		listShuttle.add(buttonsColumn);
		listShuttle.add(selectedColumn);
		
		//Initializing Component
		setAvailableItems(availableItems);
		setSelectedItems(selectedItems);
		bindHandlers();
		initWidget(listShuttle);
		setStyleName("crux-ListShuttle");
	}

	private void updateAvailableList() 
	{
		this.availableCellList.setRowCount(getAvailableItems().size());
		this.availableCellList.setRowData(0, getAvailableItems());
	}

	private void updateSelectedList() 
	{
		this.selectedCellList.setRowCount(getSelectedItems().size());
		this.selectedCellList.setRowData(0, getSelectedItems());
	}

	private class BeanCell extends AbstractCell<T> 
	{
		@Override
		public void render(Context context, T data, SafeHtmlBuilder sb) 
		{
			if (data != null) {
				if (beanRenderer == null) 
				{
					setBeanRenderer(new ToStringBeanRenderer<T>());
				}

				String value = beanRenderer.render(data);
				sb.append(SafeHtmlUtils.fromString(value));
			}
		}
	}

	private class ToStringBeanRenderer<B> implements BeanRenderer<B> 
	{
		@Override
		public String render(B bean) 
		{
			return bean == null ? "null" : bean.toString();
		}
	}

	private void bindHandlers() 
	{
		this.addSelectedButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent events) 
			{
				handleAddSelected();
			}
		});

		this.addAllButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				handleAddAll();
			}
		});

		this.removeSelectedButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				handleRemoveSelected();
			}
		});

		this.removeAllButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				handleRemoveAll();
			}
		});
	}

	private void handleAddSelected() 
	{
		@SuppressWarnings("unchecked")
		MultiSelectionModel<T> availableSelectionModel = (MultiSelectionModel<T>) this.availableCellList.getSelectionModel();

		Set<T> selectedObjects = availableSelectionModel.getSelectedSet();

		for(T selectedObject : selectedObjects)
		{
			if (!getSelectedItems().contains(selectedObject)) 
			{
				getSelectedItems().add(selectedObject);
				updateSelectedList();
			}

			if (getAvailableItems().remove(selectedObject)) 
			{
				updateAvailableList();
			}	
		}
	}

	private void handleRemoveSelected() 
	{
		@SuppressWarnings("unchecked")
		MultiSelectionModel<T> removeSelectionModel = (MultiSelectionModel<T>) this.selectedCellList.getSelectionModel();

		Set<T> selectedObjects = removeSelectionModel.getSelectedSet();

		for(T selectedObject : selectedObjects)
		{
			if (!getAvailableItems().contains(selectedObject)) 
			{
				getAvailableItems().add(selectedObject);
				updateAvailableList();
			}

			if (getSelectedItems().remove(selectedObject)) 
			{
				updateSelectedList();
			}	
		}
	}

	private void handleRemoveAll() 
	{
		for (T item : getSelectedItems()) 
		{
			if (!getAvailableItems().contains(item)) 
			{
				getAvailableItems().add(item);
			}
		}

		getSelectedItems().clear();

		updateAvailableList();
		updateSelectedList();
	}

	private void handleAddAll() 
	{
		for (T item : getAvailableItems()) 
		{
			if (!getSelectedItems().contains(item)) 
			{
				getSelectedItems().add(item);
			}
		}

		getAvailableItems().removeAll(getSelectedItems());

		updateAvailableList();
		updateSelectedList();
	}

	public static interface BeanRenderer<B> 
	{
		public String render(B bean);
	}

	@Override
	public void setAvailableHeader(String availableHeader) 
	{
		this.toSelectColumnFieldset.setCaptionText(availableHeader);
	}

	@Override
	public void setSelectedHeader(String selectedHeader) 
	{
		this.selectedColumnFieldset.setCaptionText(selectedHeader);
	}

	@Override
	public void setBeanRenderer(BeanRenderer<T> beanRenderer) 
	{
		this.beanRenderer = beanRenderer;
	}

	@Override
	public void setAvailableItems(List<T> availableItems) 
	{
		this.availableItems = availableItems;

		if (getSelectedItems() != null && !getSelectedItems().isEmpty()) 
		{
			getAvailableItems().remove(getSelectedItems());
		}

		updateAvailableList();
	}

	@Override
	public List<T> getAvailableItems() 
	{
		if (availableItems == null) 
		{
			availableItems = new ArrayList<T>();
		}
		return availableItems;
	}

	@Override
	public void setSelectedItems(List<T> selectedItems) 
	{
		this.selectedItems = selectedItems;
		updateSelectedList();

		if (getAvailableItems() != null && !getAvailableItems().isEmpty()) 
		{
			getAvailableItems().removeAll(getSelectedItems());
			updateAvailableList();
		}
	}

	@Override
	public List<T> getSelectedItems() 
	{
		if (selectedItems == null) 
		{
			selectedItems = new ArrayList<T>();
		}
		return selectedItems;
	}

	@Override
	public boolean isEnabled() 
	{
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
		addSelectedButton.setEnabled(enabled);
		addAllButton.setEnabled(enabled);
		removeSelectedButton.setEnabled(enabled);
		removeAllButton.setEnabled(enabled);
		
		if(enabled)
		{
			listShuttle.addStyleName("enabled");			
		} else
		{
			listShuttle.removeStyleName("enabled");
		}
	}
}
