package org.cruxframework.crux.smartfaces.client.disposal.topmenudisposallayout;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.smartfaces.client.disposal.DisposalLayout;


@Templates({
	@Template(name="topMenuDisposalLayoutLarge", device=Device.all),
	@Template(name="topMenuDisposalLayoutSmall", device=Device.smallDisplayArrows),
	@Template(name="topMenuDisposalLayoutSmall", device=Device.smallDisplayTouch)
})
public interface TopMenuDisposalLayout extends DisposalLayout, DeviceAdaptive
{
	
}


