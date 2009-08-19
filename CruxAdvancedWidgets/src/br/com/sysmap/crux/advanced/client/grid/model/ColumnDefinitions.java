package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ColumnDefinitions<T extends ColumnDefinition>
{
	private List<T> definitionsInOrder = new ArrayList<T>();
	private Map<String, T> definitionsByKey = new HashMap<String, T>();
	
	public void add(String key, T definition){
		definitionsInOrder.add(definition);
		definitionsByKey.put(key, definition);
		definition.setKey(key);
	}
	
	List<T> getDefinitions(){
		return definitionsInOrder;
	}
	
	T getDefinition(String key){
		return definitionsByKey.get(key);
	}
	
	int getColumnIndex(String key){
		return definitionsInOrder.indexOf(definitionsByKey.get(key));
	}
	
	public Iterator<T> getIterator()
	{
		return new ColumnIterator<T>(definitionsInOrder);
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
			return cursor < defs.size() - 1;
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