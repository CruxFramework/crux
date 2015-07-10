/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen;

import org.cruxframework.crux.core.rebind.dataprovider.DataProviderType;
import org.json.JSONObject;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class DataProvider
{
	private int hashValue = 0;
	private String id;
	private JSONObject metadata;
	private DataProviderType type;
	
	public DataProvider(JSONObject metadata, String id, DataProviderType type)
    {
		this.metadata = metadata;
		this.id = id;
		this.type = type;
    }

	@Override
	public boolean equals(Object obj) 
	{
    	if (obj == null) return false;
    	if (!(obj instanceof DataProvider)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((DataProvider)obj).getId();
    	return (compId1 == null?compId2==null:compId1.equals(compId2));
    }

	public String getId() 
	{
		return id;
	}

	public JSONObject getMetadata()
	{
		return metadata;
	}

	public DataProviderType getType()
	{
		return type;
	}
	
	@Override
	public int hashCode()
    {
        if (this.hashValue == 0)
        {
            int result = 17;
            String compStr = this.getId();
            int idComp = compStr == null ? 0 : compStr.hashCode();
            result = result * 37 + idComp;
            this.hashValue = result;
        }
        return this.hashValue;
    }
}
