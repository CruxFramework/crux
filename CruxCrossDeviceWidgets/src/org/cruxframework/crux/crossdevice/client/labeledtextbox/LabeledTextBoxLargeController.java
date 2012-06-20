package org.cruxframework.crux.crossdevice.client.labeledtextbox;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

/**
*
* @author    Daniel Martins - <code>daniel@cruxframework.org</code>
*
*/
@Controller("labeledTextBoxLargeController")
public class LabeledTextBoxLargeController extends BaseLabeledTextBoxController
{

	@Override
	protected void init()
	{
		super.init();
	}

	@Override
	protected void applyWidgetDependentStyleNames()
	{
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.large.toString());
	}

}
