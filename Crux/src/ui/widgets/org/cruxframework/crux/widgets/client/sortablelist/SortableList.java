/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.sortablelist;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability;
import org.cruxframework.crux.widgets.client.util.draganddrop.MoveCapability.Movable;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * @author Samuel Almeida Cardoso
 * @param <Widget> the SortableList type object.
 */
public class SortableList extends Composite implements ISortableList<Widget> 
{
	
	public class MoveableWidget extends Widget implements Movable<Label> 
	{
		private FlowPanel wrapper;
		private Label moveHandle;
		
		public MoveableWidget(Widget widget) 
		{
			wrapper = new FlowPanel();
			moveHandle = new Label();
			moveHandle.setStyleName("dialogTopBarDragHandle");
			
			wrapper.add(moveHandle);
			wrapper.add(widget);
		}
		
		@Override
		public void setPosition(int x, int y) {
			// TODO Auto-generated method stub
		}

		@Override
		public int getAbsoluteLeft() {
			return 0;
		}

		@Override
		public int getAbsoluteTop() {
			return 0;
		}

		@Override
		public Widget asWidget() {
			return wrapper;
		}
		
		@Override
		public Label getHandle(DragAndDropFeature feature) {
			return moveHandle;
		}
	}
	
	private boolean enabled;
	
	private List<MoveableWidget> items;
	private BeanRenderer<Widget> beanRenderer;

	protected FlowPanel sortableList;
	
	protected CaptionPanel toSelectColumnFieldset;
	
	protected CellList<Widget> availableCellList;
	
	protected Button upButton;
	protected Button downButton;

	public SortableList()
	{
		this(new ArrayList<Widget>());
	}
	
	public SortableList(List<Widget> items)
	{
		sortableList = new FlowPanel();
		
		//First Column
		FlowPanel toSelectColumn = new FlowPanel();
		toSelectColumn.setStyleName("toSelectColumn");

		toSelectColumnFieldset = new CaptionPanel();
		toSelectColumnFieldset.setStyleName("toSelectColumnFieldset");
		
		availableCellList = new CellList<Widget>(new BeanCell());
		availableCellList.setSelectionModel(new SingleSelectionModel<Widget>());
		
		//Middle Column
		FlowPanel buttonsColumn = new FlowPanel();
		buttonsColumn.setStyleName("buttonsColumn");
		
		upButton = new Button();
		upButton.setText("U");
		downButton = new Button();
		downButton.setText("D");
		
		buttonsColumn.add(upButton);
		buttonsColumn.add(downButton);
		
		toSelectColumn.add(toSelectColumnFieldset);
		
		toSelectColumnFieldset.add(availableCellList);
		
		sortableList.add(toSelectColumn);
		sortableList.add(buttonsColumn);
		
		//sortableList.add(toSelectColumnFieldset);
		
		//Initializing Component
		setItems(items);
		bindHandlers();
		initWidget(sortableList);
		setStyleName("crux-SortableList");
	}

	private void updateAvailableList() 
	{
		this.availableCellList.setRowCount(getItems().size());
		this.availableCellList.setRowData(0, getItems());
	}

	private class BeanCell extends AbstractCell<Widget> 
	{
		@Override
		public void render(Context context, Widget data, SafeHtmlBuilder sb) 
		{
			if (data != null) {
				if (beanRenderer == null) 
				{
					setBeanRenderer(new ToStringBeanRenderer<Widget>());
				}

				//String value = beanRenderer.render(data);
				sb.appendHtmlConstant(data.getElement().toString());
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
		this.upButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent events) 
			{
				handleUpSelected();
			}
		});

		this.downButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				handleDownSelected();
			}
		});
	}

	private void handleUpSelected() 
	{
		@SuppressWarnings("unchecked")
		SingleSelectionModel<Widget> availableSelectionModel = (SingleSelectionModel<Widget>) this.availableCellList.getSelectionModel();
		Widget selectedObject = availableSelectionModel.getSelectedObject();
		int index = getItems().indexOf(selectedObject);
		
		if(index == 0)
		{
			return;
		}
		
		Widget previows = getItems().get(index-1);
		
		getItems().set(index-1, selectedObject);
		getItems().set(index, previows);
		
		updateAvailableList();
	}

	private void handleDownSelected() 
	{
		@SuppressWarnings("unchecked")
		SingleSelectionModel<Widget> availableSelectionModel = (SingleSelectionModel<Widget>) this.availableCellList.getSelectionModel();
		Widget selectedObject = availableSelectionModel.getSelectedObject();
		int index = getItems().indexOf(selectedObject);
		
		if(index == getItems().size()-1)
		{
			return;
		}
		
		Widget next = getItems().get(index+1);
		
		getItems().set(index+1, selectedObject);
		getItems().set(index, next);
		
		updateAvailableList();
	}

	public static interface BeanRenderer<B> 
	{
		public String render(B bean);
	}

	@Override
	public void setBeanRenderer(BeanRenderer<Widget> beanRenderer) 
	{
		this.beanRenderer = beanRenderer;
	}

	@Override
	public void setItems(List<Widget> items) 
	{
		if(items == null || items.isEmpty())
		{
			return;
		}
		
		if(this.items == null)
		{
			this.items = new ArrayList<MoveableWidget>();
		}
		
		for(Widget widget : items)
		{
			MoveableWidget e = new MoveableWidget(widget);
			MoveCapability.addMoveCapability(e);
			this.items.add(e);
		}

		updateAvailableList();
	}

	@Override
	public List<Widget> getItems() 
	{
		if (items == null) 
		{
			items = new ArrayList<MoveableWidget>();
		}
		
		ArrayList<Widget> listWidgets = new ArrayList<Widget>();
		
		for(MoveableWidget moveableWidget : items)
		{
			listWidgets.add(moveableWidget.asWidget());
		}
		
		return listWidgets;
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
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);
		
		if(enabled)
		{
			sortableList.addStyleName("enabled");			
		} else
		{
			sortableList.removeStyleName("enabled");
		}
	}

	@Override
	public void addItem(Widget widget) 
	{
		getItems().add(new MoveableWidget(widget));	
	}

	@Override
	public void removeItem(Widget widget) {
		getItems().remove(widget);
	}

	@Override
	public void removeItem(int index) {
		// TODO Auto-generated method stub
		
	}

	public void setHeader(String headerFieldset) 
	{
		this.toSelectColumnFieldset.setCaptionText(headerFieldset);
	}
}
