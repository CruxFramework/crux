package org.cruxframework.crux.showcase.client.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.errors.ErrorHandler;
import org.cruxframework.crux.core.client.errors.ValidationErrorHandler;
import org.cruxframework.crux.core.client.rpc.st.SensitiveMethodAlreadyBeingProcessedException;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.dialog.ProgressDialog;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class ShowcaseErrorHandler implements ErrorHandler, ValidationErrorHandler
{
	private static Logger logger = Logger.getLogger(Crux.class.getName());
	private static boolean showingQuickInfo = false;
	
	public void handleError(String errorMessage)
	{
		handleError(errorMessage, null);
	}

	public void handleError(Throwable t)
	{
		handleError(t.getMessage(), t);
	}

	public void handleError(String errorMessage, Throwable t)
	{
		if (t != null)
		{
			if(t instanceof SensitiveMethodAlreadyBeingProcessedException)
			{
				showQuickInfo(t.getMessage());
				return;
			}
			else if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.SEVERE, errorMessage==null?"":errorMessage, t);
			}			
		}
		if (errorMessage != null)
		{
			MessageBox.show("Oops...", errorMessage, null);
		}
	}

	private void showQuickInfo(String message)
	{
		if(!showingQuickInfo)
		{
			showingQuickInfo = true;
			ProgressDialog.show(message);
						
			new Timer()
			{			
				@Override
				public void run()
				{
					ProgressDialog.hide();
					showingQuickInfo = false;
				}
			}.schedule(3000);
		}
	}

	public void handleValidationError(String errorMessage)
	{
		MessageBox.show("Oops...", errorMessage, null);
	}
}
