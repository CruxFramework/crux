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
package br.com.sysmap.crux.core.server.screen;

import br.com.sysmap.crux.core.utils.RegexpPatterns;


public class EventFactory  
{
	public static Event getEvent(String evtId, String evt)
	{
		if (evt != null && evt.trim().length() > 0)
		{
			String[] evtProps = RegexpPatterns.REGEXP_PIPE.split(evt);
			if (evtProps.length == 0) return null;

			String callback = null;
			String type = br.com.sysmap.crux.core.client.event.EventFactory.TYPE_CLIENT;
			String call = evtProps[0];
			boolean sync = false;
			
			if (evtProps.length>1 && evtProps[1].length() > 0)
			{
				type = evtProps[1];
			}
			if (evtProps.length>2 && evtProps[2].length() > 0)
			{
				callback = evtProps[2];
			}
			if (evtProps.length>3 && evtProps[3].length() > 0)
			{
				sync = br.com.sysmap.crux.core.client.event.EventFactory.SYNC_TYPE_SYNCHRONOUS.equals(evtProps[3]);
			}

			return new Event(evtId, type, call, callback, sync);
		}
		
		return null;
	}
}
