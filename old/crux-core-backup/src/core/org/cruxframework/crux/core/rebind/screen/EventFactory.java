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
package org.cruxframework.crux.core.rebind.screen;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class EventFactory  
{
	public static Event getEvent(String evtId, String evt)
	{
		if (evtId != null && evtId.trim().length() > 0 && evt != null && evt.trim().length() > 0)
		{
			int dotPos = evt.indexOf('.');
			if (dotPos > 0 && dotPos < evt.length()-1)
			{
				String evtHandler = evt.substring(0, dotPos);
				final String method = evt.substring(dotPos+1);				
				return new Event(evtId, evtHandler, method);
			}
		}
		return null;
	}
}
