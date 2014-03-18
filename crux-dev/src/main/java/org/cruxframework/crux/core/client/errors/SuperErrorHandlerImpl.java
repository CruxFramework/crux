/*
 * Copyright 2014 cruxframework.org.
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Cardoso
 *
	//In order to make it work, insert this in a client pom project:
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
 *
 */
public class SuperErrorHandlerImpl implements ErrorHandler, ValidationErrorHandler
{
	private static Logger logger = Logger.getLogger(Crux.class.getName());
	
	private static ErrorHandlerServiceAsync errorHandlerService = null;
	
	private SuperErrorHandlerResource resources = GWT.create(SuperErrorHandlerResource.class);
	
	private ErrorHandlerServiceAsync getErrorHandlerService()
	{
		if(errorHandlerService == null)
		{
			errorHandlerService = GWT.create(ErrorHandlerService.class);
		}
		return errorHandlerService;
	}
	
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
	public void handleError(final String errorMessage, final Throwable t)
	{
		if (t != null)
		{
			if (!LogConfiguration.loggingIsEnabled())
			{
				GWT.log(errorMessage, t);
				Window.alert(errorMessage);
				return;
			}
			
			getErrorHandlerService().getError(new AsyncCallback<ArrayList<Throwable>>() 
			{
				@Override
				public void onSuccess(ArrayList<Throwable> result) 
				{
					if(result == null || result.isEmpty())
					{
						logger.log(Level.SEVERE, errorMessage==null?"":errorMessage, t);
						GWT.log(errorMessage, t);
						return;
					}
					resources.css().ensureInjected();
					
					for(Throwable throwable : result)
					{
						FlowPanel errorMsgContainer = new FlowPanel();
						errorMsgContainer.setStyleName(resources.css().errorMsgContainer());
						
						FlowPanel errorMsgContainerHeader = new FlowPanel();
						errorMsgContainerHeader.setStyleName(resources.css().errorMsgContainerHeader());
						Label labelHeader = new Label();
						labelHeader.setText(throwable.getMessage());
						errorMsgContainerHeader.add(labelHeader);
						errorMsgContainer.add(errorMsgContainerHeader);
						
						StringBuffer sb = new StringBuffer();
						if(throwable.getStackTrace() != null)
						{
							for(StackTraceElement stackTraceElement : throwable.getStackTrace())
							{
								sb.append(stackTraceElement.toString() + "\n");
							}

							FlowPanel errorMsgContainerBody = new FlowPanel();
							errorMsgContainerBody.setStyleName(resources.css().errorMsgContainerBody());
							Label labelStack = new Label();
							labelStack.setText(sb.toString());
							errorMsgContainerBody.add(labelStack);
							errorMsgContainer.add(errorMsgContainerBody);
						}
						Document.get().getBody().appendChild(errorMsgContainer.getElement());
					}
				}
				
				@Override
				public void onFailure(Throwable exception) 
				{
					Window.alert("CRITICAL ERROR: failed to connect service to get log: " + exception.getMessage());
				}
			});
		} else
		{
			Window.alert(errorMessage);
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
