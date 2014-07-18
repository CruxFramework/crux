package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

public class BeforeCancelRowEditionEvent extends BaseRowEditEvent<BeforeCancelRowEditionHandler, HasBeforeCancelRowEditionHandler>
{

	public BeforeCancelRowEditionEvent(HasBeforeCancelRowEditionHandler source, DataRow row)
	{
		super(source, row);
	}

	private static Type<BeforeCancelRowEditionHandler> TYPE = new Type<BeforeCancelRowEditionHandler>();

	/**
	 * @return
	 */
	public static Type<BeforeCancelRowEditionHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeCancelRowEditionHandler handler)
	{
		handler.onBeforeCancelRowEdition(this);
	}

	@Override
	public Type<BeforeCancelRowEditionHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static BeforeCancelRowEditionEvent fire(HasBeforeCancelRowEditionHandler source, DataRow row)
	{
		BeforeCancelRowEditionEvent event = new BeforeCancelRowEditionEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}
