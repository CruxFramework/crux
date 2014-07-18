package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

public class BeforeSaveRowEditionEvent extends BaseRowEditEvent<BeforeSaveRowEditionHandler, HasBeforeSaveRowEditionHandler>{

	public BeforeSaveRowEditionEvent(HasBeforeSaveRowEditionHandler source, DataRow row) 
	{
		super(source, row);
	}

	private static Type<BeforeSaveRowEditionHandler> TYPE = new Type<BeforeSaveRowEditionHandler>();

	/**
	 * @return
	 */
	public static Type<BeforeSaveRowEditionHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeSaveRowEditionHandler handler)
	{
		handler.onBeforeSaveRowEdition(this);
	}

	@Override
	public Type<BeforeSaveRowEditionHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static BeforeSaveRowEditionEvent fire(HasBeforeSaveRowEditionHandler source, DataRow row)
	{
		BeforeSaveRowEditionEvent event = new BeforeSaveRowEditionEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}
