/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.Empty;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.IndexDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;
import org.cruxframework.crux.core.client.db.annotation.Store;
import org.cruxframework.crux.core.client.db.annotation.Store.Indexed;
import org.cruxframework.crux.core.client.db.annotation.Store.Key;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * This class creates a client proxy for access a database
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public abstract class AbstractDatabaseProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	protected DatabaseDef databaseMetadata;
	protected JClassType integerType;
	protected JClassType doubleType;
	protected JClassType stringType;
	protected JClassType emptyType;
	protected JClassType dateType;

	public AbstractDatabaseProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, false);
		databaseMetadata = baseIntf.getAnnotation(DatabaseDef.class);
		integerType = context.getTypeOracle().findType(Integer.class.getCanonicalName());
		doubleType = context.getTypeOracle().findType(Double.class.getCanonicalName());
		dateType = context.getTypeOracle().findType(Date.class.getCanonicalName());
		stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		emptyType = context.getTypeOracle().findType(Empty.class.getCanonicalName());
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
	    Set<String> added = new HashSet<String>();
	    
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.println("this.name = "+EscapeUtils.quote(databaseMetadata.name())+";");
		srcWriter.println("this.version =  "+databaseMetadata.version()+";");

		if (!DatabaseDef.NoErrorHandler.class.isAssignableFrom(databaseMetadata.defaultErrorHandler()))
		{
			srcWriter.println("this.setDefaultErrorHandler((DatabaseErrorHandler)GWT.create("+databaseMetadata.defaultErrorHandler().getCanonicalName()+".class));");
		}
		
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (added.contains(objectStoreName))
			{
				throw new CruxGeneratorException("Duplicated ObjectStore found on Database["+databaseMetadata.name()+"]. ObjectStore["+objectStoreName+"]");
			}
			added.add(objectStoreName);
        }
		srcWriter.println("}");
	}
		
	protected Set<IndexData> getIndexes(IndexDef[] indexMetadata, JClassType objectStoreTarget, String objectStoreName)
	{
		Set<IndexData> indexesCreated = new HashSet<IndexData>();
		getIndexesFromMetadata(indexMetadata, indexesCreated, objectStoreName);
		getIndexesFromObject(objectStoreTarget, indexesCreated, objectStoreName);
		return indexesCreated;
	}
	
	protected void getIndexesFromObject(JClassType objectStoreTarget, Set<IndexData> indexesCreated, String objectStoreName)
    {
	    if (objectStoreTarget != null)
		{
	    	Store store = objectStoreTarget.getAnnotation(Store.class);
	    	if (store != null)
	    	{
	    		getIndexesFromMetadata(store.indexes(), indexesCreated, objectStoreName);

	    		List<IndexData> indexes = getIndexFromAnnotations(objectStoreTarget, "");
	    		for (IndexData index: indexes)
	    		{
	    			if (indexesCreated.contains(index.keyPath[0]))
	    			{
	    				throw new CruxGeneratorException("Duplicated index declared on ObjectSore ["+objectStoreName+"] Index ["+index.keyPath[0]+"] Datasource ["+databaseMetadata.name()+"]");
	    			}
	    			indexesCreated.add(index);
	    		}
	    	}
		}
    }
	
	protected void getIndexesFromMetadata(IndexDef[] indexMetadata,  Set<IndexData> indexesCreated, String objectStoreName)
	{
		for (IndexDef index : indexMetadata)
		{
			String indexName = getIndexName(index, objectStoreName);
			if (indexesCreated.contains(indexName))
			{
				throw new CruxGeneratorException("Duplicated index declared on ObjectSore ["+objectStoreName+"] Index ["+indexName+"] Datasource ["+databaseMetadata.name()+"]");
			}
			if (index.keyPath() == null || index.keyPath().length == 0)
			{
				throw new CruxGeneratorException("Can not create an index without a key definition. Index ["+indexName+"] ObjectStore["+objectStoreName+"] Datasource ["+databaseMetadata.name()+"].");
			}
			indexesCreated.add(new IndexData(index.keyPath(), index.unique(), index.keyPath().length > 1, indexName)); // && index.multiEntry()
		}
	}
	
	protected List<IndexData> getIndexFromAnnotations(JClassType objectStoreTarget, String prefix)
    {
		List<IndexData> result = new ArrayList<IndexData>();
		List<JMethod> getterMethods = JClassUtils.getGetterMethods(objectStoreTarget);
		for (JMethod method : getterMethods)
        {
	        if (JClassUtils.isSimpleType(method.getReturnType()))
	        {
	        	Indexed indexed = method.getAnnotation(Indexed.class);
	        	if (indexed != null)
	        	{
	        		String property = JClassUtils.getPropertyForGetterOrSetterMethod(method);
	        		result.add(new IndexData(new String[]{prefix+property}, indexed.unique(), false, prefix+property));
	        	}
	        }
	        else
	        {
        		String property = JClassUtils.getPropertyForGetterOrSetterMethod(method);
	        	result.addAll(getIndexFromAnnotations(method.getReturnType().isClassOrInterface(), prefix+property+"."));
	        }
        }
		
	    return result;
    }

	protected String getIndexName(IndexDef indexDef, String objectStoreName)
	{
		if (!StringUtils.isEmpty(indexDef.name()))
		{
			return indexDef.name();
		}
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (String key : indexDef.keyPath())
        {
			if (!first)
			{
				str.append('_');
			}
			first = false;
	        str.append(key);
        }
		
		if (str.length() == 0)
		{
			throw new CruxGeneratorException("Invalid index declared on ObjectSore ["+objectStoreName+"] Datasource ["+databaseMetadata.name()+"]");
		}
		
		return str.toString();
		
	}

	protected boolean isAutoIncrement(JClassType targetObject)
	{
		boolean result = false;
		
		List<JMethod> getterMethods = JClassUtils.getGetterMethods(targetObject);
		for (JMethod method : getterMethods)
        {
	        Key key = method.getAnnotation(Key.class);
			if (key != null)
	        {
				if (result && !key.autoIncrement())
				{
					throw new CruxGeneratorException("Invalid composite key declaration on objectStore ["+targetObject.getQualifiedSourceName()+"]. Can not use autoIncrement only on subset on keyPath set.");
				}
				if (key.autoIncrement() && !method.getReturnType().getQualifiedSourceName().equals("int") && 
					!method.getReturnType().getQualifiedSourceName().equals(Integer.class.getCanonicalName()))
				{
					throw new CruxGeneratorException("Invalid key declaration on objectStore ["+targetObject.getQualifiedSourceName()+"]. Can not use autoIncrement on a non int key.");
				}
	        	result = key.autoIncrement();
	        }
        }
		return result;
	}
	
	protected String[] getKeyPath(ObjectStoreDef objectStoreMetadata, JClassType targetObject)
	{
		if (objectStoreMetadata.keyPath().length > 0)
		{
			return objectStoreMetadata.keyPath(); 
		}
		else if (targetObject != null)
		{
			return getKeyPath(targetObject);
		}
		return new String[]{};
	}
	
	protected String[] getKeyPath(JClassType targetObject)
	{
		List<String> keyPath = new ArrayList<String>();
		List<Key> keys = new ArrayList<Key>();

		List<JMethod> getterMethods = JClassUtils.getGetterMethods(targetObject);
		
		for (JMethod method : getterMethods)
        {
	        Key key = method.getAnnotation(Key.class);
			if (key != null)
	        {
	        	if (!isValidTypeForKey(method.getReturnType()))
	        	{
	        		throw new CruxGeneratorException("Crux databases only support Strings or int as key components");
	        	}
	        	keys.add(key);
        		keyPath.add(JClassUtils.getPropertyForGetterOrSetterMethod(method));
	        }
        }
		for (int i=0; i< keys.size(); i++)
		{
			int orderI = keys.get(i).order();
			if (orderI < 0 && keys.size() > 1)
			{
        		throw new CruxGeneratorException("Crux object stores with composite keys must declare a valid order for its key's components");
			}
			int pos = i;
			for (int j=i+1; j < keys.size(); j++)
			{
				int orderJ = keys.get(j).order();
				if (orderJ < orderI)
				{
					orderI = orderJ;
					pos = j;
				}
			}
			if (pos != i)
			{
				Key key = keys.remove(pos);
				keys.add(i, key);
				String path = keyPath.remove(pos);
				keyPath.add(i, path);
			}
		}
		
		return keyPath.toArray(new String[keyPath.size()]);
	}

	protected boolean isValidTypeForKey(JType jType)
    {
	    return jType.equals(stringType) || 
	    	jType.equals(integerType) || 
	    	jType.equals(JPrimitiveType.INT) ||
	    	jType.equals(doubleType) || 
	    	jType.equals(JPrimitiveType.DOUBLE) ||
	    	jType.equals(dateType);
    }
	
	protected JClassType getObjectStoreTarget(ObjectStoreDef objectStoreMetadata)
	{
		return context.getTypeOracle().findType(objectStoreMetadata.targetClass().getCanonicalName());
	}
	
	protected String getObjectStoreName(ObjectStoreDef objectStoreMetadata, JClassType objectStoreTarget)
	{
		String name = objectStoreMetadata.name();
		if (StringUtils.isEmpty(name))
		{
			try
            {
	            name = objectStoreTarget.getAnnotation(Store.class).value();
            }
            catch (Exception e)
            {
	            throw new CruxGeneratorException("Invalid Database Store. You must inform a name on @ObjectStoreDef, or point to a target Class annotated with @Store.");
            }
		}
		return name;
	}

	public static class IndexData
	{
		public boolean unique;
		public boolean multiEntry;
		public String[] keyPath;
		public final String indexName;
		protected IndexData(String[] keyPath, boolean unique, boolean multiEntry, String indexName)
		{
			this.keyPath = keyPath;
			this.unique = unique;
			this.multiEntry = multiEntry;
			this.indexName = indexName;
		}
	}
}
