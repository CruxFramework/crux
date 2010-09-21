/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.util;

import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.Timer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FrameUtils
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface FrameStateMonitor
	{
		void registerStateCallback(Element frameElement, FrameStateCallback callback, int timeout);
	}
	
	public static interface FrameAccessor
	{
		JSWindow getFrameWindow(IFrameElement frame);
	}	

	private static FrameStateMonitor stateMonitor = GWT.create(FrameStateMonitor.class);
	private static FrameAccessor accessor = GWT.create(FrameAccessorImpl.class);
	
	/**
	 * 
	 * @param frameElement
	 * @param callback
	 * @param timeout
	 */
	public static void registerStateCallback(final Element frameElement, final FrameStateCallback callback, int timeout)
	{
		stateMonitor.registerStateCallback(frameElement, callback, timeout);
	}
	
	/**
	 * 
	 * @param frameElement
	 * @param callback
	 */
	public static void registerStateCallback(final Element frameElement, final FrameStateCallback callback)
	{
		registerStateCallback(frameElement, callback, 0);
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class FrameStateMonitorOperaImpl extends FrameStateMonitorImpl
	{
		@Override
		protected boolean isLoaded(String frameState)
		{
			return ("loaded".equals(frameState) || "complete".equals(frameState));
		}
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class FrameStateMonitorSafariImpl extends FrameStateMonitorImpl
	{
		@Override
		protected boolean isLoaded(String frameState)
		{
			return ("complete".equals(frameState));
		}
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class FrameStateMonitorImpl implements FrameStateMonitor
	{
		private Map<String, Timer> monitorTimers = new HashMap<String, Timer>();
		private Map<String, Timer> timeoutTimers = new HashMap<String, Timer>();
		private static int framecounter = 0;
		public void registerStateCallback(final Element frameElement, final FrameStateCallback callback, int timeout)
		{
			unregisterStateCallback(frameElement);
			final Timer monitorTimer = new Timer()
			{
				@Override
				public void run()
				{
					String frameState = getFrameState(frameElement);
					if (isLoaded(frameState))
					{
						cancel();
						callback.onComplete();
					}
				}
			};
			monitorTimers.put(frameElement.getId(), monitorTimer);
			monitorTimer.scheduleRepeating(5);
			
			if (timeout > 0)
			{
				final Timer timeoutTimer = new Timer()
				{
					@Override
					public void run()
					{
						monitorTimer.cancel();
						callback.onComplete();
					}
				};
				timeoutTimers.put(frameElement.getId(), timeoutTimer);
				timeoutTimer.schedule(timeout);
			}
		}
		
		private void unregisterStateCallback(Element frameElement)
		{
			String id = frameElement.getId();
			if (StringUtils.isEmpty(id))
			{
				id = generateFrameId();
				frameElement.setId(id);
			}
			Timer monitorTimer = monitorTimers.get(id);
			Timer timeoutTimer = timeoutTimers.get(id);

			if (timeoutTimer != null)
			{
				timeoutTimer.cancel();
				timeoutTimers.remove(id);
			}
			if (monitorTimer != null)
			{
				monitorTimer.cancel();
				monitorTimers.remove(id);
			}
		}

		private String generateFrameId()
		{
			return "__frame"+(framecounter++);
		}

		private native String getFrameState(Element frameElement)/*-{
			var element = frameElement;
			if (element != null && element.contentDocument != null)
			{
				return element.contentDocument.readyState;
			}
			else
			{
				return '';
			}
		}-*/;
		
		protected abstract boolean isLoaded(String frameState);
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class FrameStateMonitorIEImpl implements FrameStateMonitor
	{
		public void registerStateCallback(final Element frameElement, final FrameStateCallback callback, int timeout)
		{
			Timer timer = null;
			if (timeout > 0)
			{
				timer = new Timer()
				{
					@Override
					public void run()
					{
						unregisterStateCallback(frameElement);
						callback.onComplete();
					}
				};
				timer.schedule(timeout);
			}
			registerStateCallback(frameElement, callback, timer);
		}
	
		private native void registerStateCallback(Element frameElement, FrameStateCallback callback, Timer timer)/*-{
			var element = frameElement;
			element.onreadystatechange=function() {
				if (element.readyState=='complete') {
					element.onreadystatechange=null;
					try
					{
						callback.@br.com.sysmap.crux.widgets.client.util.FrameStateCallback::onComplete()();
						if (timer != null)
						{
							timer.@com.google.gwt.user.client.Timer::cancel()();
						}
					}
					catch(e)
					{
					}
				}
			};
		}-*/;
	
		private native void unregisterStateCallback(Element frameElement)/*-{
			var element = frameElement;
			element.onreadystatechange=null;
		}-*/;
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class FrameStateMonitorMozillaImpl implements FrameStateMonitor
	{
		public void registerStateCallback(final Element frameElement, final FrameStateCallback callback, int timeout)
		{
			Timer timer = null;
			if (timeout > 0)
			{
				timer = new Timer()
				{
					@Override
					public void run()
					{
						unregisterStateCallback(frameElement);
						callback.onComplete();
					}
				};
				timer.schedule(timeout);
			}
			registerStateCallback(frameElement, callback, timer);
		}
	
		private native void registerStateCallback(Element frameElement, FrameStateCallback callback, Timer timer)/*-{
			var element = frameElement;
			element.onload=function() {
				element.onload=null;
				try
				{
					callback.@br.com.sysmap.crux.widgets.client.util.FrameStateCallback::onComplete()();
					if (timer != null)
					{
						timer.@com.google.gwt.user.client.Timer::cancel()();
					}
				}
				catch(e)
				{
				}
			};
		}-*/;
	
		private native void unregisterStateCallback(Element frameElement)/*-{
			var element = frameElement;
			element.onload=null;
		}-*/;
	}

	public static class FrameAccessorIE implements FrameAccessor
	{
		public native JSWindow getFrameWindow(IFrameElement elem)/*-{
			try{
				return elem.contentWindow;
			}
			catch(e){
				return null;
			}
		}-*/;
	}
	
	public static class FrameAccessorImpl implements FrameAccessor
	{
		public native JSWindow getFrameWindow(IFrameElement elem)/*-{
			try{
				return elem.contentDocument.defaultView;
			}
			catch(e){
				return null;
			}
		}-*/;
	}
	
	public static JSWindow getFrameWindow(IFrameElement frame)
	{
		return accessor.getFrameWindow(frame);
	}
}