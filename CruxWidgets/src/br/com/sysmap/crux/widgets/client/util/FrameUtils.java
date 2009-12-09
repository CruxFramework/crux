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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class FrameUtils
{
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	public static interface FrameStateMonitor
	{
		void registerStateCallback(Element frameElement, FrameStateCallback callback, int timeout);
	}

	private static FrameStateMonitor stateMonitor = GWT.create(FrameStateMonitor.class);
	
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	static abstract class FrameStateMonitorImpl implements FrameStateMonitor
	{
		public void registerStateCallback(final Element frameElement, final FrameStateCallback callback, int timeout)
		{
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
			monitorTimer.scheduleRepeating(5);
			
			if (timeout > 0)
			{
				Timer timeoutTimer = new Timer()
				{
					@Override
					public void run()
					{
						monitorTimer.cancel();
						callback.onComplete();
					}
				};
				timeoutTimer.schedule(timeout);
			}
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
	 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
}