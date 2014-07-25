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
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class ApplicationCacheHandler implements EntryPoint 
{
	private static Logger logger = Logger.getLogger(ApplicationCacheHandler.class.getName());

    public static final int UNCACHED = 0;
    public static final int IDLE = 1;
    public static final int CHECKING = 2;
    public static final int DOWNLOADING = 3;
    public static final int UPDATEREADY = 4;
    public static final int OBSOLETE = 5;

    public static enum CacheEvent {onCached, onChecking, onDownloading, onNoupdate, onUpdateready, onProgress, onObsolete, onError}
	
    private OfflineMessages messages = GWT.create(OfflineMessages.class);
    private OfflineConstants constants = GWT.create(OfflineConstants.class);
    private ApplicationCacheUIHandler uiHandler = GWT.create(ApplicationCacheUIHandler.class);

    private static boolean updating = false;
	private boolean obsolete = false;
	private static FastList<ApplicationCacheEvent.Handler> cacheEventHandlers = null;

	private Network network;

	public static HandlerRegistration addApplicationCacheHandler(final ApplicationCacheEvent.Handler handler)
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Adding a new applicationCache event handler.");
		}
		if (cacheEventHandlers == null)
		{
			 cacheEventHandlers = new FastList<ApplicationCacheEvent.Handler>();
		}
		cacheEventHandlers.add(handler);
		
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = cacheEventHandlers.indexOf(handler);
				if (index >= 0)
				{
					cacheEventHandlers.remove(index);
				}
			}
		};
	}
	
    /**
     * Initializes and starts the monitoring.
     */
    public void onModuleLoad() 
    {
    	if(!isSupported() || !GWT.isProdMode())
    	{
    		return;
    	}
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Starting application cache handler.");
		}
    	
        hookAllListeners(this);
        scheduleUpdateChecker();
        if (getStatus() == DOWNLOADING)
        {
        	onDownloading();
        }
        
        network = Network.get(); // initializes network monitor...
        // Sometimes android leaves the status indicator spinning and spinning
        // and spinning...
        pollForStatusOnAndroid();
    }

    /**
     * @return The status of the application cache.
     */
    private static native int getStatus()/*-{
        return $wnd.applicationCache.status;
    }-*/;

    /**
     * Asks the application cache to update itself.
     */
    public static void updateCache()
    {
    	try
    	{
        	if (!updating) 
            {
        		updateCacheNative();
            }
    	}
    	catch (Exception e) 
    	{
    		if (LogConfiguration.loggingIsEnabled())
    		{
    			logger.log(Level.SEVERE, "Error updating cache.", e);
    		}
		}
    }
    /**
     * Asks the application cache to update itself.
     */
    public static native void updateCacheNative()/*-{
        $wnd.applicationCache.update();
    }-*/;

    public static native void swapCache()/*-{
        $wnd.applicationCache.swapCache();
    }-*/;
    
    private void fireApplicationCacheEvent(CacheEvent eventType)
    {
    	if (cacheEventHandlers != null)
    	{
    		ApplicationCacheEvent event = new ApplicationCacheEvent(network, eventType);
    		for (int i=0; i< cacheEventHandlers.size(); i++)
    		{
    			cacheEventHandlers.get(i).onCacheEvent(event);
    		}
    	}
    }
    
    private void pollForStatusOnAndroid() 
    {
        if (Screen.isAndroid()) 
        {
            Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                        @Override
                        public boolean execute() 
                        {
                            if (updating) 
                            {
                                // The normal listeners are working correctly
                                return false;
                            }
                            switch (getStatus()) 
                            {
                            	case IDLE:
                            		uiHandler.hideMessage();
                            		return false;
                            	case UPDATEREADY:
                            		requestUpdate(false);
                            		return false;
                            	default:
                            		return true;
                            }
                        }
                    }, 500);
        }
    }

    /**
     * Check for updates to the application cache every 30 minutes
     */
    private void scheduleUpdateChecker() {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() 
            {
                if (obsolete)
                {
                	if (LogConfiguration.loggingIsEnabled())
                	{
                		logger.log(Level.INFO, "Cache was obsolete. Swapping cache.");
                	}
                	swapCache();
                }
                else
                {
                	if (LogConfiguration.loggingIsEnabled())
                	{
                		logger.log(Level.INFO, "Trying to update cache...");
                	}
                	updateCache();
                }
                return true;
            }
        }, constants.updateCheckInterval());
    }

    /**
     * Called when a cached event is triggered
     * 
     * @param event The event.
     */
    protected void onCached() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "Resources cached.");
		}
    	uiHandler.hideMessage();
    	updating = false;
    	fireApplicationCacheEvent(CacheEvent.onCached);
    }

    /**
     * Called when a checking event is triggered
     * 
     * @param event The event.
     */
    protected void onChecking() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.checkingResources());
		}
    	fireApplicationCacheEvent(CacheEvent.onChecking);
    }

    /**
     * Called when a downloading event is triggered
     * 
     * @param event The event.
     */
    protected void onDownloading() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.downloadingResources());
		}
    	updating = true;
    	fireApplicationCacheEvent(CacheEvent.onDownloading);
    }

    /**
     * Called when a noupdate event is triggered
     * 
     * @param event The event.
     */
    protected void onNoUpdate() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "No updates found");
		}
    	uiHandler.hideMessage();
    	updating = false;
    	fireApplicationCacheEvent(CacheEvent.onNoupdate);
    }

    /**
     * Called when a update ready event is triggered
     * 
     * @param event The event.
     */
    protected void onUpdateReady() 
    {
    	uiHandler.hideMessage();
    	updating = false;
    	fireApplicationCacheEvent(CacheEvent.onUpdateready);
		ApplicationCacheHandler.swapCache();
    	Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				requestUpdate(false);
			}
		});
    }

    /**
     * Called when a progress event is triggered
     * 
     * @param event The event.
     */
    protected void onProgress(ProgressEvent event)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.progressStatus(event.getLoaded(), event.getTotal()));
		}
    	uiHandler.showMessage(messages.progressStatus(event.getLoaded(), event.getTotal()));
    	fireApplicationCacheEvent(CacheEvent.onProgress);
    }
    
	/**
     * Called when an error event is triggered.
     * 
     * @param event The error event.
     */
    protected void onError() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.applicationCacheError());
		}
    	uiHandler.hideMessage();
    	updating = false;
    	fireApplicationCacheEvent(CacheEvent.onError);
    }

	/**
     * Called when an error event is triggered.
     * 
     * @param event The error event.
     */
    protected void onObsolete() 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.applicationCacheObsolete());
		}
		this.obsolete = true; 
    	updating = false;
    	uiHandler.showMessage(messages.applicationCacheObsolete());
    	fireApplicationCacheEvent(CacheEvent.onObsolete);
    }

    /**
     * Called when a new version of the application cache
     * has been detected. Asks the user if we should
     * update now unless forced.
     * 
     * @param force true to force reloading the site without asking the user.
     */
    protected void requestUpdate(boolean force) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "New updates available. Requesting permission to update.");
		}
        if (force) 
        {
    		Screen.reload();
        }
        else
        {
        	uiHandler.confirmReloadPage();
        }
    }

    /**
     * 
     * @return
     */
    public static native boolean isSupported()/*-{
	    if($wnd.applicationCache)
		{
			return true;
		}
		return false;
	}-*/;
    
    /**
     * Hooks all listeners to the specified instance.
     * 
     * @param instance
     *            the instance to hook the listeners to.
     */
    protected final native void hookAllListeners(ApplicationCacheHandler instance)/*-{
        $wnd.applicationCache.addEventListener('cached',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onCached()();
            }, false);
        $wnd.applicationCache.addEventListener('checking',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onChecking()();
            }, false);
        $wnd.applicationCache.addEventListener('downloading',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onDownloading()();
            }, false);
        $wnd.applicationCache.addEventListener('noupdate',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onNoUpdate()();
            }, false);
        $wnd.applicationCache.addEventListener('updateready',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onUpdateReady()();
            }, false);
        $wnd.applicationCache.addEventListener('progress',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onProgress(Lorg/cruxframework/crux/core/clientoffline/ProgressEvent;)(event);
            }, false);
        $wnd.applicationCache.addEventListener('obsolete',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onObsolete()();
            }, false);
        $wnd.applicationCache.addEventListener('error',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onError()();
            }, false);
    }-*/;
}