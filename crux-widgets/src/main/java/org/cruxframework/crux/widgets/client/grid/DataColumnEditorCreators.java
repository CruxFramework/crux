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

