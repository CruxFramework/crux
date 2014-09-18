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
package org.cruxframework.crux.smartfaces.client.menu;

import org.cruxframework.crux.smartfaces.client.menu.Type.LargeType;
import org.cruxframework.crux.smartfaces.client.menu.Type.SmallType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface MenuRenderer 
{
	void render(Menu menu, LargeType largeType, SmallType smallType);
	
	class LargeMenuRenderer implements MenuRenderer
	{
		@Override
		public void render(Menu menu, LargeType largeType, SmallType smallType) 
		{
			assert (largeType != null):"Cannot instantiate type :<" + this.getClass().getName() + ">. Menu not switable to this device.";
			menu.addStyleName(largeType.styleName);
		}
	}

	class SmallMenuRenderer implements MenuRenderer
	{
		@Override
		public void render(Menu menu, LargeType largeType, SmallType smallType) 
		{
			assert (smallType != null):"Cannot instantiate type :<" + this.getClass().getName() + ">. Menu not switable to this device.";
			menu.addStyleName(smallType.styleName);
		}
	}
}
