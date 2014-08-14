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
package org.cruxframework.crux.tools.codeserver.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.websocket.SocketCloseEvent;
import org.cruxframework.crux.core.client.websocket.SocketCloseHandler;
import org.cruxframework.crux.core.client.websocket.SocketErrorEvent;
import org.cruxframework.crux.core.client.websocket.SocketErrorHandler;
import org.cruxframework.crux.core.client.websocket.SocketMessageEvent;
import org.cruxframework.crux.core.client.websocket.SocketMessageHandler;
import org.cruxframework.crux.core.client.websocket.SocketOpenEvent;
import org.cruxframework.crux.core.client.websocket.SocketOpenHandler;
import org.cruxframework.crux.core.client.websocket.WebSocket;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CodeServerNotifier implements EntryPoint 
{
	public static final int DEFAULT_COMPILER_NOTIFIER_PORT = 9877;
	private static Logger logger = Logger.getLogger(CodeServerNotifier.class.getName());

	private DialogBox dialogBox;
	private Label label;
	
	@Override
	public void onModuleLoad() 
	{
		dialogBox = new DialogBox();
		label = new Label();
		dialogBox.add(label);
		//TODO take the URL from user, as a parameter... if not provided, use the expression below as default
		String url = "ws://" + Window.Location.getHostName() + ":" + DEFAULT_COMPILER_NOTIFIER_PORT;
		WebSocket socket = WebSocket.createIfSupported(url);
		
		if(socket == null)
		{
			logger.info("Browser do not support Websocket.");
			return;
		}
		
		socket.addCloseHandler(new SocketCloseHandler() 
		{
			@Override
			public void onClose(SocketCloseEvent event) 
			{
				String message = "Compilation Notifier Socket was closed. You will not be notified about compilations on server until you reload your page";
				Crux.getErrorHandler().handleError(message);
			}
		});
		
		socket.addOpenHandler(new SocketOpenHandler() 
		{
			@Override
			public void onOpen(SocketOpenEvent event) 
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Connected to Compilation Notifier service.");
				}
			}
		});
		socket.addErrorHandler(new SocketErrorHandler() 
		{
			@Override
			public void onError(SocketErrorEvent event) 
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Unexpected error on Compilation Notifier service socket.");
				}
			}
		});
		
		socket.addMessageHandler(new SocketMessageHandler() 
		{
			@Override
			public void onMessage(SocketMessageEvent event) 
			{
				try
				{
					String message = event.getMessage();
					CompilationMessage compilationMessage = JsonUtils.safeEval(message);
					
					switch (compilationMessage.getOperation()) 
					{
						case START:
							Screen.blockToUser();
							label.setText("Compiling module "+compilationMessage.getModule()+"...");
							dialogBox.center();
						break;
						
						case END:
							Screen.unblockToUser();
							dialogBox.hide();
							label.setText(null);
							if (compilationMessage.getStatus())
							{
								Window.Location.reload();
							}
							else
							{
								Crux.getErrorHandler().handleError("Error compiling module "+compilationMessage.getModule()+".");
							}
						break;
					}
				}
				catch(Exception e)
				{
					Crux.getErrorHandler().handleError("Error parsing message from Compilation Notifier service", e);
				}
			}
		});
	}
}
