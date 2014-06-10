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
package org.cruxframework.crux.core.client.datasource;

import org.cruxframework.crux.core.client.Legacy;



/**
 * A record in a datasource.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 * @deprecated Use DataProvider instead.
 */
@Deprecated
@Legacy
public class DataSourceRecord<T>
{
	protected Object identifier;
	protected T recordObject;

	private DataSource<T> dataSource;
	private DataSourceRecordState state = new DataSourceRecordState();
	
	public DataSourceRecord(DataSource<T> dataSource,Object identifier)
	{
		this.identifier = identifier;
		this.dataSource = dataSource; 
	}

	public DataSourceRecordState getCurrentState()
	{
		return new DataSourceRecordState(state.isSelected(), state.isDirty(), state.isCreated(), state.isRemoved(), state.isReadOnly());
	}

	public Object getIdentifier()
	{
		return identifier;
	}
	
	public T getRecordObject()
    {
    	return recordObject;
    }

	public boolean isCreated()
	{
		return this.state.isCreated();
	}
	
	public boolean isDirty()
	{
		return this.state.isDirty();
	}

	public boolean isReadOnly()
	{
		return state.isReadOnly();
	}
	
	public boolean isRemoved()
	{
		return this.state.isRemoved();
	}

	public boolean isSelected()
	{
		return state.isSelected();
	}

	public void set(T value)
	{
		T previousValue = recordObject;
		if ((previousValue != null && (value==null || !previousValue.equals(value))) ||
			(previousValue == null && value != null))
		{
			DataSourceRecordState previousState = getCurrentState();
			setRecordObject(value);
			this.state.setDirty(true);
			if (!previousState.equals(state));
			{
				dataSource.updateState(this, previousState);
			}
		}
	}
	
	public void setDirty()
	{
		DataSourceRecordState previousState = getCurrentState();
		this.state.setDirty(true);
		if (!previousState.equals(state));
		{
			dataSource.updateState(this, previousState);
		}
	}
	
	public void setReadOnly(boolean readOnly)
	{
		if (this.state.isReadOnly() != readOnly)
		{
			DataSourceRecordState previousState = getCurrentState();
			this.state.setReadOnly(readOnly);
			dataSource.updateState(this, previousState);
		}
	}

	public void setRecordObject(T recordObject)
    {
    	this.recordObject = recordObject;
    }

	public void setSelected(boolean selected)
	{
		if (this.state.isSelected() != selected)
		{
			DataSourceRecordState previousState = getCurrentState();
			this.state.setSelected(selected);
			dataSource.updateState(this, previousState);
		}
	}

	void setCreated(boolean created)
	{
		this.state.setCreated(created);
	}
	
	void setRemoved(boolean removed)
	{
		this.state.setRemoved(removed);
	}
	
	public static class DataSourceRecordState 
	{
		private boolean created;
		private boolean dirty;
		private boolean readOnly;
		private boolean removed;
		private boolean selected;
		
		protected DataSourceRecordState()
		{
			this(false, false, false, false, false);
		}

		protected DataSourceRecordState(boolean selected, boolean dirty, boolean created, boolean removed, boolean readOnly)
		{
			this.selected = selected;
			this.dirty = dirty;
			this.created = created;
			this.removed = removed;
			this.readOnly = readOnly; 
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == null) return false;
			if (!(obj instanceof DataSourceRecordState)) return false;
			DataSourceRecordState otherState = (DataSourceRecordState)obj;
			return (this.selected == otherState.selected) && (this.dirty == otherState.dirty) && 
			       (this.created == otherState.created) && (this.removed == otherState.removed) &&
			       (this.readOnly == otherState.readOnly);
		}
		public boolean isCreated()
		{
			return created;
		}
		public boolean isDirty()
		{
			return dirty;
		}
		public boolean isRemoved()
		{
			return removed;
		}
		public boolean isSelected()
		{
			return selected;
		}
		public void setCreated(boolean created)
		{
			this.created = created;
		}
		public void setDirty(boolean dirty)
		{
			this.dirty = dirty;
		}
		public void setRemoved(boolean removed)
		{
			this.removed = removed;
		}
		public void setSelected(boolean selected)
		{
			this.selected = selected;
		}
		protected boolean isReadOnly()
		{
			return readOnly;
		}
		
		protected void setReadOnly(boolean readOnly)
		{
			this.readOnly = readOnly;
		}
	}
}
