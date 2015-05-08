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
package org.cruxframework.crux.core.rebind.context.scanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.JClassScanner;

import com.google.gwt.core.ext.typeinfo.JClassType;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public class DataSourceScanner 
{
	private Map<String, Map<String, String>> dataSourcesCanonicalNames;
	private boolean initialized = false;
	private JClassScanner jClassScanner;

	public DataSourceScanner(JClassScanner jClassScanner)
    {
		this.jClassScanner = jClassScanner;
    }

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getDataSource(String name, Device device) throws ResourceNotFoundException
	{
		initializeDataSources();
		Map<String, String> map = dataSourcesCanonicalNames.get(name);
		String result = map.get(device.toString());
		if (result == null && !device.equals(Device.all))
		{
			result = map.get(Device.all.toString());
		}
		if (result == null)
		{
        	throw new ResourceNotFoundException("Can not found requested dataSource ["+name+"], for device ["+device+"].");
		}
		return result;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Class<?> getDataSourceClass(String name, Device device) throws ResourceNotFoundException
	{
		initializeDataSources();
		try
        {
			Map<String, String> map = dataSourcesCanonicalNames.get(name);
			String result = map.get(device.toString());
			if (result == null && !device.equals(Device.all))
			{
				result = map.get(Device.all.toString());
			}
	        return Class.forName(result);
        }
        catch (Exception e)
        {
        	throw new ResourceNotFoundException("Can not found requested dataSource ["+name+"], for device ["+device+"].");
        }
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasDataSource(String name)
	{
		initializeDataSources();
		return (name != null && dataSourcesCanonicalNames.containsKey(name));
	}
	
	/**
	 * @return
	 */
	public Iterator<String> iterateDataSources()
	{
		initializeDataSources();
		return dataSourcesCanonicalNames.keySet().iterator();
	}
	
	/**
	 * 
	 */
	protected void initializeDataSources()
	{
		if (!initialized)
		{
			try
			{
				dataSourcesCanonicalNames = new HashMap<String, Map<String, String>>();
				JClassType[] dataSourceTypes;
				dataSourceTypes = jClassScanner.searchClassesByInterface(DataSource.class.getCanonicalName());
				if (dataSourceTypes != null)
				{
					for (JClassType dataSourceClass : dataSourceTypes) 
					{
						org.cruxframework.crux.core.client.datasource.annotation.DataSource annot = 
								dataSourceClass.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.DataSource.class);
						if (annot != null)
						{
							Device[] devices = annot.supportedDevices();
							String resourceKey = annot.value();
							if (devices == null || devices.length ==0)
							{
								addResource(dataSourceClass, resourceKey, Device.all);
							}
							else
							{
								for (Device device : devices)
								{
									addResource(dataSourceClass, resourceKey, device);
								}
							}
						}
						else
						{
							String simpleName = dataSourceClass.getSimpleSourceName();
							if (simpleName.length() >1)
							{
								simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
							}
							else
							{
								simpleName = simpleName.toLowerCase();
							}
							addResource(dataSourceClass, simpleName, Device.all);
						}
					}
				}
				initialized = true;
			}
			catch (Exception e)
			{
		    	throw new CruxGeneratorException("Error initializing DataSource scanner.", e);
			}
		}
	}
	
	/**
	 * 
	 * @param datasourceClass
	 * @param datasourceKey
	 * @param device
	 */
	private void addResource(JClassType datasourceClass, String datasourceKey, Device device)
    {
		if (!datasourceClass.isAbstract())
		{
			if (!dataSourcesCanonicalNames.containsKey(datasourceKey))
			{
				dataSourcesCanonicalNames.put(datasourceKey, new HashMap<String, String>());
			}
			Map<String, String> canonicallCassNamesByDevice = dataSourcesCanonicalNames.get(datasourceKey);
			
			String deviceKey = device.toString();
			if (dataSourcesCanonicalNames.containsKey(deviceKey))
			{
				throw new CruxGeneratorException("Duplicated alis for Datasource: ["+datasourceKey+"].");
			}
			canonicallCassNamesByDevice.put(deviceKey, datasourceClass.getQualifiedSourceName());
		}
    }
}
