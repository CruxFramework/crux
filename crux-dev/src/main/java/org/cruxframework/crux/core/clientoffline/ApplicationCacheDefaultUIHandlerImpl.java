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
package org.cruxframework.crux.core.clientoffline;

import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ApplicationCacheDefaultUIHandlerImpl implements ApplicationCacheUIHandler
{
    private OfflineMessages messages = GWT.create(OfflineMessages.class);
	private DialogBox progress;
	private static Label contentLabel;

    /**
     * Shows the progress dialog.
     */
	@Override
    public void showMessage(String message) 
    {
    	if (progress == null)
    	{
    		progress = createAlertBox(message);
    	}
    	contentLabel.setText(message);
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
			contentLabel = null;
        }
    }

	@Override
    public void confirmReloadPage()
    {
    	if (Window.confirm(messages.requestUpdate()))
    	{
    		Screen.reload();
    	}
    }
	
	private static DialogBox createAlertBox(final String content) 
	{
        final DialogBox box = new DialogBox();
        contentLabel = new Label(content);
        box.add(contentLabel);
        box.center();
        return box;
    }
}
