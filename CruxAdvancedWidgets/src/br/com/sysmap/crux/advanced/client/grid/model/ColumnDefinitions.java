package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ColumnDefinitions
{
	private List<ColumnDefinition> definitionsInOrder = new ArrayList<ColumnDefinition>();
	private Map<String, ColumnDefinition> definitionsByKey = new HashMap<String, ColumnDefinition>();
	
	public void add(String key, ColumnDefinition definition){
		definitionsInOrder.add(definition);
		definitionsByKey.put(key, definition);
		definition.setKey(key);
	}
	
	List<ColumnDefinition> getDefinitions(){
		return definitionsInOrder;
	}
	
	ColumnDefinition getDefinition(String key){
		return definitionsByKey.get(key);
	}
	
	int getColumnIndex(String key){
		return definitionsInOrder.indexOf(definitionsByKey.get(key));
	}
	
	public Iterator<ColumnDefinition> getIterator()
	{
		return new ColumnIterator<ColumnDefinition>(definitionsInOrder);
	}
		
	public static class ColumnIterator<T extends ColumnDefinition> implements Iterator<T>
	{
		int cursor = 0;
		private List<T> defs;
	
		ColumnIterator(List<T> definitions)
		{
			this.defs = definitions;
		}
		
		public boolean hasNext()
		{
			return cursor < defs.size();
		}

		public T next()
		{
			if(hasNext())
			{
				return (defs.get(cursor++));
			}
			else
			{
				return null;
			}
		}

		public void remove()
		{
			throw new RuntimeException(); // TODO - msg
		}		
	}
}