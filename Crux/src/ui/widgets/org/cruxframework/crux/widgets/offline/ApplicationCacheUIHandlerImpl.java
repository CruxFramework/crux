/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.offline;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.clientoffline.ApplicationCacheUIHandler;
import org.cruxframework.crux.core.clientoffline.OfflineMessages;
import org.cruxframework.crux.widgets.client.dialog.ConfirmDialog;
import org.cruxframework.crux.widgets.client.dialog.Progress;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ApplicationCacheUIHandlerImpl implements ApplicationCacheUIHandler
{
    private OfflineMessages messages = GWT.create(OfflineMessages.class);
	private Progress progress;

    /**
     * Shows the progress dialog.
     */
	@Override
    public void showMessage(String message) 
    {
    	if (progress == null)
    	{
    		progress = Progress.show(message);
    	}
    	else
    	{
    		progress.setMessage(message);
    	}
    }

    /**
     * Hides the progress dialog.
     */
	@Override
    public void hideMessage() 
    {
        if (progress != null)
        {
        	progress.hide();
        	progress = null;
        }
    }


	@Override
    public void confirmReloadPage()
    {
    	ConfirmDialog.show(messages.requestUpdateTitle(), messages.requestUpdate(), new OkHandler()
		{
			@Override
			public void onOk(OkEvent event)
			{
	    		Screen.reload();
			}
		}, null);
    }
}