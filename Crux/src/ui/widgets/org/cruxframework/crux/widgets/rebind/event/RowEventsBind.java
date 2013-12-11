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
package org.cruxframework.crux.widgets.rebind.event;

import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowSelectEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeRowSelectHandler;
import org.cruxframework.crux.widgets.client.event.row.BeforeShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.BeforeShowRowDetailsHandler;
import org.cruxframework.crux.widgets.client.event.row.LoadRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.LoadRowDetailsHandler;
import org.cruxframework.crux.widgets.client.event.row.RowClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowClickHandler;
import org.cruxframework.crux.widgets.client.event.row.RowDoubleClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowDoubleClickHandler;
import org.cruxframework.crux.widgets.client.event.row.RowRenderEvent;
import org.cruxframework.crux.widgets.client.event.row.RowRenderHandler;
import org.cruxframework.crux.widgets.client.event.row.ShowRowDetailsEvent;
import org.cruxframework.crux.widgets.client.event.row.ShowRowDetailsHandler;


/**
 * All event binders for grid row events
 * @author Gesse S. F. Dafe
 */
public class RowEventsBind
{
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowClickEvtBind extends EvtProcessor
	{
		public RowClickEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowClick";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return RowClickEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowClickHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowDoubleClickEvtBind extends EvtProcessor
	{
		public RowDoubleClickEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowDoubleClick";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return RowDoubleClickEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowDoubleClickHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowRenderEvtBind extends EvtProcessor
	{
		public RowRenderEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowRender";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return RowRenderEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowRenderHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class BeforeRowSelectEvtBind extends EvtProcessor
	{
		public BeforeRowSelectEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onBeforeRowSelect";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return BeforeRowSelectEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return BeforeRowSelectHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class BeforeShowRowDetailsEvtBind extends EvtProcessor
	{
		public BeforeShowRowDetailsEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onBeforeShowRowDetails";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return BeforeShowRowDetailsEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return BeforeShowRowDetailsHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class ShowRowDetailsEvtBind extends EvtProcessor
	{
		public ShowRowDetailsEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onShowRowDetails";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return ShowRowDetailsEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return ShowRowDetailsHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class LoadRowDetailsEvtBind extends EvtProcessor
	{
		public LoadRowDetailsEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onLoadRowDetails";

		/**
		 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
		 */
		public String getEventName()
		{
			return EVENT_NAME;
		}		

		@Override
	    public Class<?> getEventClass()
	    {
		    return LoadRowDetailsEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return LoadRowDetailsHandler.class;
	    }		
	}
}