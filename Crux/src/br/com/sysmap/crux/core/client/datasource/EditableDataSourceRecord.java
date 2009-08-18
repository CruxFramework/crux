/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.datasource;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class EditableDataSourceRecord extends DataSourceRecord
{
	private EditableDataSourceRecordState state = new EditableDataSourceRecordState();
	private EditableDataSource dataSource;
	
	public EditableDataSourceRecord(EditableDataSource dataSource,Object identifier)
	{
		super(identifier);
		this.dataSource = dataSource; 
	}

	public void set(int column, Object value)
	{
		Object previousValue = columns.get(column);
		if ((previousValue != null && !previousValue.equals(value)) ||
			(previousValue == null && value != null))
		{
			EditableDataSourceRecordState previousState = getCurrentState();
			columns.set(column, value);
			this.state.setDirty(true);
			if (!previousState.equals(state));
			{
				dataSource.updateState(this, previousState);
			}
		}
	}
	
	public boolean isSelected()
	{
		return state.isSelected();
	}

	public void setSelected(boolean selected)
	{
		if (this.state.isSelected() != selected)
		{
			EditableDataSourceRecordState previousState = getCurrentState();
			this.state.setSelected(selected);
			dataSource.updateState(this, previousState);
		}
	}

	public boolean isDirty()
	{
		return this.state.isDirty();
	}

	public boolean isCreated()
	{
		return this.state.isCreated();
	}

	void setCreated(boolean created)
	{
		this.state.setCreated(created);
	}

	public boolean isRemoved()
	{
		return this.state.isRemoved();
	}

	void setRemoved(boolean removed)
	{
		this.state.setRemoved(removed);
	}
	
	public EditableDataSourceRecordState getCurrentState()
	{
		return new EditableDataSourceRecordState(state.isSelected(), state.isDirty(), state.isCreated(), state.isRemoved());
	}
	
	public static class EditableDataSourceRecordState 
	{
		private boolean selected;
		private boolean dirty;
		private boolean created;
		private boolean removed;
		
		public EditableDataSourceRecordState()
		{
			this(false, false, false, false);
		}

		public EditableDataSourceRecordState(boolean selected, boolean dirty, boolean created, boolean removed)
		{
			this.selected = selected;
			this.dirty = dirty;
			this.created = created;
			this.removed = removed;
		}

		public boolean isSelected()
		{
			return selected;
		}
		public void setSelected(boolean selected)
		{
			this.selected = selected;
		}
		public boolean isDirty()
		{
			return dirty;
		}
		public void setDirty(boolean dirty)
		{
			this.dirty = dirty;
		}
		public boolean isCreated()
		{
			return created;
		}
		public void setCreated(boolean created)
		{
			this.created = created;
		}
		public boolean isRemoved()
		{
			return removed;
		}
		public void setRemoved(boolean removed)
		{
			this.removed = removed;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == null) return false;
			if (!(obj instanceof EditableDataSourceRecordState)) return false;
			EditableDataSourceRecordState otherState = (EditableDataSourceRecordState)obj;
			return (this.selected == otherState.selected) && (this.dirty == otherState.dirty) && 
			       (this.created == otherState.created) && (this.removed == otherState.removed);
		}
	}
}
