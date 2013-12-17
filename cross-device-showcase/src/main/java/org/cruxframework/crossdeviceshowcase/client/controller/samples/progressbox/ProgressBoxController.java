package org.cruxframework.crossdeviceshowcase.client.controller.samples.progressbox;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.ProgressBox;

import com.google.gwt.user.client.Timer;

@Controller("progressBoxController")
public class ProgressBoxController 
{
	private static int DURATION = 4;
	private ProgressBox progress;
	private int timeLeftToHide = DURATION;
	private Timer timer;	

	@Expose
	public void showProgress()
	{
		progress = ProgressBox.show("");		
		updateTitle();
		progress.setWidth("300px");
		
		timer = new Timer()
		{
			@Override
			public void run()
			{
				timeLeftToHide--;
				
				if(timeLeftToHide == 0)
				{
					hideProgress();
				}
				else
				{
					updateTitle();
				}
			}
		};
		
		timer.scheduleRepeating(1000);
	}

	private void updateTitle()
	{
		progress.setMessage("Aguarde " + timeLeftToHide + " segundos...");
	}
	
	private void hideProgress()
	{
		progress.hide();
		timer.cancel();
		timeLeftToHide = DURATION;
	}
}
