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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import org.cruxframework.crux.core.server.rest.core.EntityTag;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConditionalResponse
{
	private EntityTag etag;
	private long lastModified;
	private int status;

	public ConditionalResponse()
	{
	}

	public ConditionalResponse(EntityTag etag, long lastModified, int status)
	{
		this.etag = etag;
		this.lastModified = lastModified;
		this.status = status;
	}

	public EntityTag getEtag()
	{
		return etag;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public int getStatus()
	{
		return status;
	}

	public void setEtag(EntityTag etag)
	{
		this.etag = etag;
	}

	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}
}
