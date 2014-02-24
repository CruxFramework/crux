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
package org.cruxframework.crux.core.client.screen;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocumentException;
import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.logging.client.LogConfiguration;



/**
 * This class is used to make calls in different documents.
 * @author Thiago da Rosa de Bustamante
 *
 */
@Legacy
@Deprecated
public abstract class ScreenAccessor
{
	private static Logger logger = Logger.getLogger(ScreenAccessor.class.getName());

	/**
	 * Make a call to a cross document method.
	 * 
	 * @param serializedData the call itself. Contains the method and all parameters serialized.
	 * @param target Where the method resides
	 * @return
	 */
	protected String invokeCrossDocument(String serializedData, Target target, String frame, String siblingFrame, JSWindow jsWindow)
	{
		String result;
		String crossDocTarget = null;
		if (target != null)
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				crossDocTarget = target.toString();
				logCrossDocCall(serializedData, crossDocTarget);
			}
			switch (target)
	        {
		        case TOP: result = callTopControllerAccessor(serializedData); break;
		        case ABSOLUTE_TOP: result = callAbsoluteTopControllerAccessor(serializedData); break;
		        case PARENT: result = callParentControllerAccessor(serializedData); break;
		        case OPENER: result = callOpenerControllerAccessor(serializedData); break;
		        default: 
		        	throw new CrossDocumentException(Crux.getMessages().crossDocumentInvalidTarget());
	        }
		}
		else if (!StringUtils.isEmpty(frame))
    	{
			if (LogConfiguration.loggingIsEnabled())
			{
				crossDocTarget = "Frame: "+frame;
				logCrossDocCall(serializedData, crossDocTarget);
			}
			result = callFrameControllerAccessor(frame, serializedData);
    	}
    	else if (!StringUtils.isEmpty(siblingFrame))
    	{
			if (LogConfiguration.loggingIsEnabled())
			{
				crossDocTarget = "SiblingFrame: "+siblingFrame;
				logCrossDocCall(serializedData, crossDocTarget);
			}
			result = callSiblingFrameControllerAccessor(siblingFrame, serializedData);
    	}
    	else if (jsWindow != null)
    	{
			if (LogConfiguration.loggingIsEnabled())
			{
				crossDocTarget = "Window: "+jsWindow;
				logCrossDocCall(serializedData, crossDocTarget);
			}
			result = callWindowControllerAccessor(jsWindow, serializedData);
    	}
    	else
    	{
    		throw new CrossDocumentException(Crux.getMessages().crossDocumentInvalidTarget());
    	}
		
		if (LogConfiguration.loggingIsEnabled())
		{
			logCrossDocCalled(serializedData, crossDocTarget);
		}
		return result;
	}

	/**
	 * @param serializedData
	 * @param target
	 */
	private void logCrossDocCall(String serializedData, String target)
    {
	    String controller = null; 
	    String method = null; 
	    int pipeIndex = serializedData.indexOf('|');
	    if (pipeIndex > 0)
	    {
	    	controller = serializedData.substring(0, pipeIndex);
	    	method = serializedData.substring(pipeIndex+1);
	    	pipeIndex = method.indexOf('|');
	    	if (pipeIndex > 0)
	    	{
	    		method = method.substring(0, pipeIndex);
	    	}
	    	else
	    	{
	    		method = null;
	    	}
	    }
	    logger.log(Level.FINE, Crux.getMessages().screenAccessorCallingCrossDocument(Screen.getId(), controller, method, target));
    }
	
	/**
	 * @param serializedData
	 * @param target
	 */
	private void logCrossDocCalled(String serializedData, String target)
    {
	    String controller = null; 
	    String method = null; 
	    int pipeIndex = serializedData.indexOf('|');
	    if (pipeIndex > 0)
	    {
	    	controller = serializedData.substring(0, pipeIndex);
	    	method = serializedData.substring(pipeIndex+1);
	    	pipeIndex = method.indexOf('|');
	    	if (pipeIndex > 0)
	    	{
	    		method = method.substring(0, pipeIndex);
	    	}
	    	else
	    	{
	    		method = null;
	    	}
	    }
	    logger.log(Level.FINE, Crux.getMessages().screenAccessorCrossDocumentExecuted(Screen.getId(), controller, method, target));
    }

	private native String callTopControllerAccessor(String serializedData)/*-{
		return $wnd.top._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callAbsoluteTopControllerAccessor(String serializedData)/*-{
		var who = $wnd.top;
		var op = $wnd.opener;
		while (op != null)
		{
			who = op.top;
			op = op.opener;
		}
		return who._cruxCrossDocumentAccessor(serializedData);
	}-*/;	
	
	private static native String callOpenerControllerAccessor(String serializedData)/*-{
		return $wnd.opener._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callParentControllerAccessor(String serializedData)/*-{
		return $wnd.parent._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callFrameControllerAccessor(String frame, String serializedData)/*-{
		return $wnd.frames[frame]._cruxCrossDocumentAccessor(serializedData);
	}-*/;		
	
	private static native String callSiblingFrameControllerAccessor(String frame, String serializedData)/*-{
		return $wnd.parent.frames[frame]._cruxCrossDocumentAccessor(serializedData);
	}-*/;		

	private static native String callWindowControllerAccessor(JSWindow jsWindow, String serializedData)/*-{
	return jsWindow._cruxCrossDocumentAccessor(serializedData);
}-*/;		
}
