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
package org.cruxframework.crux.core.client.dataprovider;

/**
 * A record in a {@link DataProvider}.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class DataProviderRecord<T>
{
	T recordObject;
	AbstractDataProvider<T> dataProvider;
	DataProviderRecordState state = new DataProviderRecordState();
	
	DataProviderRecord(AbstractDataProvider<T> dataSource)
	{
		this.dataProvider = dataSource; 
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

	DataProviderRecordState getCurrentState()
	{
		return new DataProviderRecordState(state.isSelected(), state.isDirty(), state.isCreated(), state.isRemoved(), state.isReadOnly());
	}

	void set(T value)
	{
		T previousValue = recordObject;
		if ((previousValue != null && (value==null || !previousValue.equals(value))) ||
			(previousValue == null && value != null))
		{
			setRecordObject(value);
			setDirty(true);
		}
	}
	
	void setDirty(boolean dirty)
	{
		if (dirty)
		{
			DataProviderRecordState previousState = getCurrentState();
			this.state.setDirty(true);
			if (!previousState.equals(state));
			{
				dataProvider.updateState(this, previousState);
			}
		}
		else
		{
			this.state.setDirty(false);
		}
	}
	
	void setReadOnly(boolean readOnly)
	{
		if (this.state.isReadOnly() != readOnly)
		{
			DataProviderRecordState previousState = getCurrentState();
			this.state.setReadOnly(readOnly);
			dataProvider.updateState(this, previousState);
		}
	}

	void setSelected(boolean selected)
	{
		if (this.state.isSelected() != selected)
		{
			DataProviderRecordState previousState = getCurrentState();
			this.state.setSelected(selected);
			dataProvider.updateState(this, previousState);
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
	
	void setRecordObject(T recordObject)
    {
    	this.recordObject = recordObject;
    }
	
	/**
	 * Represents the state of a {@link DataProvider} record
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class DataProviderRecordState 
	{
		private boolean created;
		private boolean dirty;
		private boolean readOnly;
		private boolean removed;
		private boolean selected;
		
		protected DataProviderRecordState()
		{
			this(false, false, false, false, false);
		}

		protected DataProviderRecordState(boolean selected, boolean dirty, boolean created, boolean removed, boolean readOnly)
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
			if (!(obj instanceof DataProviderRecordState)) return false;
			DataProviderRecordState otherState = (DataProviderRecordState)obj;
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
