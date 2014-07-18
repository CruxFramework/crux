package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

public class CancelRowEditionEvent extends BaseRowEditEvent<CancelRowEditionHandler, HasCancelRowEditionHandler>
{

	public CancelRowEditionEvent(HasCancelRowEditionHandler source, DataRow row)
	{
		super(source, row);
	}

	private static Type<CancelRowEditionHandler> TYPE = new Type<CancelRowEditionHandler>();

	/**
	 * @return
	 */
	public static Type<CancelRowEditionHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(CancelRowEditionHandler handler)
	{
		handler.onCancelRowEditionHandler(this);
	}

	@Override
	public Type<CancelRowEditionHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static CancelRowEditionEvent fire(HasCancelRowEditionHandler source, DataRow row)
	{
		CancelRowEditionEvent event = new CancelRowEditionEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}
