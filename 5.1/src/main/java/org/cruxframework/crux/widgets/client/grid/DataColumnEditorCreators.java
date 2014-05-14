package org.cruxframework.crux.widgets.client.grid;

import org.cruxframework.crux.core.client.formatter.HasFormatter;

import com.google.gwt.user.client.ui.HasValue;

public class DataColumnEditorCreators
{
	static abstract class DataColumnEditorCreator<T>
	{
		public abstract T createEditorWidget(DataColumnDefinition column);
	}
	
	public static abstract class HasValueDataColumnEditorCreator<V> extends DataColumnEditorCreator<HasValue<V>>
	{
		@Override
		public abstract HasValue<V> createEditorWidget(DataColumnDefinition column);
	}
	
	public static abstract class HasFormatterDataColumnEditorCreator extends DataColumnEditorCreator<HasFormatter>
	{
		@Override
		public abstract HasFormatter createEditorWidget(DataColumnDefinition column);
	}
	
	static abstract class GenericDataColumnEditorCreator extends DataColumnEditorCreator<Object>
	{
		@Override
		public abstract Object createEditorWidget(DataColumnDefinition column);
	}
}

