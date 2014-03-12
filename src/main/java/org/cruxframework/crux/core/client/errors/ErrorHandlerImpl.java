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
package org.cruxframework.crux.core.client.errors;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.remote.ErrorHandlerService;
import org.cruxframework.crux.core.client.remote.ErrorHandlerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ErrorHandlerImpl implements ErrorHandler, ValidationErrorHandler
{
	private static Logger logger = Logger.getLogger(Crux.class.getName());
	
	/**
	 * Test Deferred Binding Client LOG feature
	 */
	//TODO: improve this calling Crux.properties
	private static boolean dialogLogErrorEnabled = false;
	//Call the service that will read the system error msg
	private static ErrorHandlerServiceAsync errorHandlerService = GWT.create(ErrorHandlerService.class);
	
	//In order to make it work, insert this in a client pom project:
	/*
	<!-- Run GWT instrumentation -->
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>aspectj-maven-plugin</artifactId>
        <version>1.5</version>
        <configuration>
            <weaveDependencies>
                <weaveDependency>
                     <groupId>org.cruxframework</groupId>
                     <artifactId>crux-dev</artifactId>
                </weaveDependency>
                <weaveDependency>
                    <groupId>com.google.gwt</groupId>
                    <artifactId>gwt-dev</artifactId>
                </weaveDependency>
            </weaveDependencies>
            <complianceLevel>1.6</complianceLevel>
            <source>1.6</source>
            <target>1.6</target>
            <Xlint>ignore</Xlint>
        </configuration>
        <executions>
            <execution>
                <phase>process-sources</phase>
                <goals>
                    <goal>compile</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
	*/
	/**
	 * END: Test Deferred Binding Client LOG feature
	 */
	
	/**
	 * 
	 */
	public void handleError(String errorMessage)
	{
		handleError(errorMessage, null);
	}

	/**
	 * 
	 */
	public void handleError(Throwable t)
	{
	    handleError(t, false);
	}

	/**
	 * 
	 */
	public void handleError(String errorMessage, Throwable t)
	{
		if (t != null)
		{
			//TODO: create a new errorHandler and a new entry point. This is here today only for tests! 
			if(dialogLogErrorEnabled)
			{
				errorHandlerService.getError(new AsyncCallback<ArrayList<Throwable>>() 
				{
					@Override
					public void onSuccess(ArrayList<Throwable> result) 
					{
						Label label = new Label();
						label.setText(result.toString());
						Document.get().getBody().appendChild(label.getElement());	
					}
					
					@Override
					public void onFailure(Throwable exception) 
					{
						Window.alert(exception.getMessage());
					}
				});
			} else
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, errorMessage==null?"":errorMessage, t);
				}
				GWT.log(errorMessage, t);
				Window.alert(errorMessage);
			}
		}
	}

	/**
	 * 
	 */
	public void handleValidationError(String errorMessage)
	{
		Window.alert(errorMessage);
	}

	@Override
    public void handleError(Throwable t, boolean uncaught)
    {
		handleError(t.getMessage(), t);
    }

	@Override
	//TODO: when calling this handler, threat required widgets when they implement HasData.
	public Widget handleValidationError(Widget widget, String errorMessage) 
	{
		widget.addStyleName("error");
		DialogBox errorMsgDialog = new DialogBox();
		errorMsgDialog.setStyleName("errorMsg");
		errorMsgDialog.showRelativeTo(widget);
		return errorMsgDialog;
	}
}
