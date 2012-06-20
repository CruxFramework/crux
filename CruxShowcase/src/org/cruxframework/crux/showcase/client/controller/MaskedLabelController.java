package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.widgets.client.maskedlabel.MaskedLabel;

@Controller("maskedLabelController")
public class MaskedLabelController 
{
	@Create
	protected MaskedLabelScreen screen;
	
	@Expose
	@SuppressWarnings("deprecation")
	public void onLoad(){
		screen.getBirthDateLabel().setUnformattedValue(new Date(60,12,23));
		screen.getHeightLabel().setUnformattedValue(6.1);
		screen.getWeightLabel().setUnformattedValue(191);
	}

	public static interface MaskedLabelScreen extends ScreenWrapper	{
		MaskedLabel getBirthDateLabel();
		MaskedLabel getHeightLabel();
		MaskedLabel getWeightLabel();
	}
}