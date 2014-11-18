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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.clientoffline.NetworkEvent.Handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * A Network detection tool.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Network implements HasNetworkHandlers
{
	private static Logger logger = Logger.getLogger(Network.class.getName());
	private static Network instance;
	private Impl impl;

	public static abstract class Impl implements HasNetworkHandlers
	{
		protected OfflineMessages messages = GWT.create(OfflineMessages.class);
		protected FastList<Handler> handlers = new FastList<NetworkEvent.Handler>();
		
        public HandlerRegistration addNetworkHandler(final Handler handler)
        {
        	handlers.add(handler);
	        return new HandlerRegistration()
			{
				@Override
				public void removeHandler()
				{
					int index = handlers.indexOf(handler);
					if (index >= 0)
					{
						handlers.remove(index);
					}
				}
			};
        }
        
        /**
         * Check if this browser support Crux offline working.
         * @return
         */
        public boolean isSupported()
        {
        	return true;
        }
        
        protected void fireOnLineEvent()
        {
        	fireEvent(new NetworkEvent(true));
        }
        
        protected void fireOffLineEvent()
        {
        	fireEvent(new NetworkEvent(false));
        }

        public void fireEvent(NetworkEvent event)
        {
        	for (int i = 0; i < handlers.size(); i++)
        	{
        		handlers.get(i).onNetworkChanged(event);
        	}
        }
	}
	
	public static class SafariImpl extends Impl
	{
		public SafariImpl()
        {
			createNetworkEventListeners(this);
        }
		
		@Override
        public native boolean isOnLine()/*-{
	        return navigator.onLine;
        }-*/;

		private native void createNetworkEventListeners(SafariImpl instance)/*-{
	        $wnd.addEventListener('offline',
	                function(event) {
	                    instance.@org.cruxframework.crux.core.clientoffline.Network.SafariImpl::fireOffLineEvent()();
	                }, false);
	        $wnd.addEventListener('online',
	                function(event) {
	                    instance.@org.cruxframework.crux.core.clientoffline.Network.SafariImpl::fireOnLineEvent()();
	                }, false);
		}-*/;
	}
	
	public static class UnsupportedImpl extends Impl
	{
		@Override
        public boolean isOnLine()
		{
	        throw new UnsupportedOperationException();
        }

		@Override
        public HandlerRegistration addNetworkHandler(Handler handler)
        {
	        throw new UnsupportedOperationException();
        };
        
        @Override
        public boolean isSupported()
        {
            return false;
        }
	}

	public static class CacheManifestImpl extends Impl
	{
		private boolean isOnLine = getInitialState();
		
		public CacheManifestImpl()
        {
			ApplicationCacheHandler.addApplicationCacheHandler(new ApplicationCacheEvent.Handler()
			{
				@Override
				public void onCacheEvent(ApplicationCacheEvent event)
				{
					switch (event.getEventType())
                    {
                    	case onDownloading:
                    	case onUpdateready:
                    	case onNoupdate:
                    		onCacheHitEvent();
	                    break;
                    	case onError:
                    		onCacheFailedEvent();
                    	default:
                    		//ignores
                    }
				}
			});
			ApplicationCacheHandler.updateCache();
        }
		
		private native boolean getInitialState()/*-{
			if (navigator.onLine){
				return true;
			}
	        return false;
        }-*/;

		@Override
        public boolean isOnLine()
		{
	        return isOnLine;
        }

		private void onCacheHitEvent()
		{
    		if (LogConfiguration.loggingIsEnabled())
    		{
    			logger.log(Level.INFO, messages.networkOnLine());
    			logger.log(Level.FINE, "onCacheHitEvent: old status: "+isOnLine);
    		}
			boolean oldStatus = isOnLine;
			isOnLine = true;
			if (!oldStatus)
			{
				fireOnLineEvent();
			}
		}
		
		private void onCacheFailedEvent()
		{
    		if (LogConfiguration.loggingIsEnabled())
    		{
    			logger.log(Level.INFO, messages.networkOffLine());
    			logger.log(Level.FINE, "onCacheFailedEvent: old status: "+isOnLine);
    		}
			boolean oldStatus = isOnLine;
			isOnLine = false;
			if (oldStatus)
			{
				fireOffLineEvent();
			}
		}
	}

	/**
	 * Singleton constructor
	 */
	private Network()
	{
		impl = GWT.create(Impl.class);
	}
	
	/**
	 * Retrieve the Network instance
	 * @return
	 */
	public static Network get()
	{
		if (instance == null)
		{
    		if (LogConfiguration.loggingIsEnabled())
    		{
    			logger.log(Level.FINE, "Creating new Network monitor");
    		}
			instance = new Network();
		}
		return instance;
	}
	
	/**
	 * Returns true if the application has network connection
	 */
	@Override
	public boolean isOnLine()
	{
		return impl.isOnLine();
	}

	/**
	 * Add a network events handler
	 */
	@Override
    public HandlerRegistration addNetworkHandler(Handler handler)
    {
	    return impl.addNetworkHandler(handler);
    }
	
    /**
     * Check if this browser support Crux offLine working.
     * @return true if offLine is supported.
     */
	public boolean isSupported()
	{
		return impl.isSupported();
	}
}
