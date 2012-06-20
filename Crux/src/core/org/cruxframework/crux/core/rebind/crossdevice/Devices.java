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
package org.cruxframework.crux.core.rebind.crossdevice;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Devices
{
	private static Map<String, Device[]> DEVICES_MAP = new HashMap<String, Device[]>();
	
	static
	{
		DEVICES_MAP.put(Device.largeDisplayMouse.toString(), new Device[]{Device.largeDisplayMouse, Device.all});
		DEVICES_MAP.put(Device.largeDisplayArrows.toString(), new Device[]{Device.largeDisplayArrows, Device.all});
		DEVICES_MAP.put(Device.largeDisplayTouch.toString(), new Device[]{Device.largeDisplayTouch, Device.all});
		DEVICES_MAP.put(Device.smallDisplayArrows.toString(), new Device[]{Device.smallDisplayArrows, Device.all});
		DEVICES_MAP.put(Device.smallDisplayTouch.toString(), new Device[]{Device.smallDisplayTouch, Device.smallDisplayArrows, Device.all});
	}
	
	/**
	 * Return a list of devices that support the target device. That list is ordered by 
	 * relevance (Eg. {Device.smallDisplayArrows, Device.largeDisplayMouse, Device.all}})
	 * @param device
	 * @return
	 */
	public static Device[] getDevicesForDevice(String device)
	{
		Device[] devices = DEVICES_MAP.get(device);
		if (devices == null)
		{
			return new Device[]{Device.all};
		}
		return devices;
	}
	
	
}
