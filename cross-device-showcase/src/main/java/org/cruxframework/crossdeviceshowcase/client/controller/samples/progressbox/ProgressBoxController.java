package org.cruxframework.crossdeviceshowcase.client.controller.samples.progressbox;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.ProgressBox;

import com.google.gwt.user.client.Timer;

@Controller("progressBoxController")
public class ProgressBoxController 
{
	private ProgressBox progress;
	private int timeLeftToHide = 4;
	private Timer timer;	

	@Expose
	public void showProgress()
	{
		progress = ProgressBox.show("");		
		updateTitle();
		progress.setWidth("240px");
		
		timer = new Timer()
		{
			@Override
			public void run()
			{
				timeLeftToHide--;
				
				if(timeLeftToHide == 0)
				{
					hideProgress();
					timer.cancel();
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
		progress.setMessage("Esta mensagem desaparecerá em " + timeLeftToHide + " segundos");
	}
	
	private void hideProgress()
	{
		progress.hide();
	}
}
