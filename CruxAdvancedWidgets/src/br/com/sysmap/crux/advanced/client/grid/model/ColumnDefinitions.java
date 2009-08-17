package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.ArrayList;
import java.util.HashMap;
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
}