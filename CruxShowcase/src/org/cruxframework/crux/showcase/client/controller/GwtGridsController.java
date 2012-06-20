package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.remote.SimpleGridServiceAsync;


import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

@Controller("gwtGridsController")
public class GwtGridsController {

	@Create
	protected SimpleGridServiceAsync service;

	@Expose
	public void onLoadList(){
		List<String> values = new ArrayList<String>();
		
		for (int i = 0; i < 15; i++)
        {
			values.add("valor "+i);
        }
		
		Screen.get("myList", CellList.class).setRowData(values);
	}
	
	@Expose
	public void onSelectionChange(SelectionChangeEvent event)
	{
		SingleSelectionModel<String> selection = (SingleSelectionModel<String>) event.getSource();
		Window.alert(selection.getSelectedObject());
	}
	
	@Expose
	public ListDataProvider<Contact> onLoadTable(){
		final ListDataProvider<Contact> provider = new ListDataProvider<Contact>();
		
		service.getContactList(new AsyncCallbackAdapter<ArrayList<Contact>>(this)
		{
            @Override
			public void onComplete(ArrayList<Contact> result)
			{
            	provider.getList().addAll(result);
			}
		});
		
		return provider;
	}
	
	@Expose
	public FieldUpdater<Contact, String> getFieldUpdater()
	{
		return new FieldUpdater<Contact, String>()
		{
			public void update(int index, Contact object, String value)
            {
				Window.alert("Old Value: ["+object.getName()+"] New Value: ["+value+"]");
				object.setName(value);
            }
		};
	}
	
}