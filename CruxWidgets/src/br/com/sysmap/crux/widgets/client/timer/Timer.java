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
package br.com.sysmap.crux.widgets.client.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.timeout.HasTimeoutHandlers;
import br.com.sysmap.crux.widgets.client.event.timeout.TimeoutEvent;
import br.com.sysmap.crux.widgets.client.event.timeout.TimeoutHandler;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * A time counter capable of firing scheduled events.
 * @author Gesse S. F. Dafe
 */
public class Timer extends Composite implements HasTimeoutHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-Timer";
	public static final String DEFAULT_PATTERN = "HH:mm:ss";

	private final Label widget;
	private long initial;
	private long creationTime;
	private boolean regressive;
	private boolean allowChanges;
	private String pattern;
	private DateTimeFormat formatter;
	
	private boolean running = false;
	private long startTime = 0;
	private List<TimeouTask> events = new ArrayList<TimeouTask>();	
	
	private TimeProcessor timeProcessor;
	
	
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 */
	public Timer(long initial, boolean regressive, String pattern, boolean start)
	{	
		this.initial = initial;
		this.creationTime = initial;
		this.regressive = regressive;
		this.pattern = pattern;
		
		if(StringUtils.isEmpty(this.pattern))
		{
			this.formatter = DateTimeFormat.getFormat(DEFAULT_PATTERN);
		}
		else
		{
			this.formatter = DateTimeFormat.getFormat(this.pattern);
		}
		
		this.timeProcessor = new TimeProcessor(this);
		this.allowChanges = !start;
		
		widget = new Label();
		widget.setStyleName(DEFAULT_STYLE_NAME);
		widget.setText(format(initial));
		
		initWidget(widget);
		
		if(start)
		{
			start();
		}
		
		Screen.addCloseHandler
		(
			new CloseHandler<Window>()
			{
				public void onClose(CloseEvent<Window> event)
				{
					stop();
				}
			}
		);
	}	

	/**
	 * @param amount
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String format(long amount)
	{
		Date when = new Date(0, 0, 1, 0, 0, 0);
				
		long secs = amount % 60;
		long mins = ((amount - secs) / 60) % 60;
		long hours = (amount - mins * 60 - secs) / (60 * 60);

		when.setHours((int) hours);
		when.setMinutes((int) mins);
		when.setSeconds((int) secs);
		
		return formatter.format(when);
	}
	
	/**
	 * 
	 */
	public void start()
	{
		if(!this.running)
		{
			this.startTime  = (new Date()).getTime();
			this.running = true;
			timeProcessor.scheduleRepeating(500);
		}
	}
	
	/**
	 * 
	 */
	public void stop()
	{
		if(this.running)
		{
			this.running = false;
			this.timeProcessor.cancel();
			this.initial += ((new Date()).getTime() - this.startTime) / 1000;
			this.update();
		}
	}
	
	/**
	 * 
	 */
	public void reset()
	{
		stop();
		this.initial = creationTime;
		start();
	}
	
	/**
	 * 
	 */
	public void clear()
	{
		clear(false);
	}
	
	/**
	 * 
	 */
	public void clear(boolean reprocessTimeoutEvents)
	{
		stop();
		
		if(reprocessTimeoutEvents)
		{
			resetTimeoutEvents();
		}
		
		this.initial = creationTime;		
		this.allowChanges = true;
		
		update();
	}
	
	/**
	 * 
	 */
	void update()
	{
		long diffTime = this.initial;

		if(this.running)
		{
			long delta = ((new Date()).getTime() - this.startTime) / 1000;

			if(this.regressive)
			{
				diffTime = diffTime - delta;
				diffTime = diffTime < 0 ? 0 : diffTime;
			}
			else
			{
				diffTime = diffTime + delta;
			}		
		}
			
		this.widget.setText(this.format(diffTime));

		if(this.running)
		{
			this.checkForTimeoutEvents(diffTime);		
		}
	}

	/**
	 * @param currentCount
	 */
	private void checkForTimeoutEvents(long currentCount)
	{
		if(this.events != null)
		{
			for (TimeouTask task  : this.events)
			{
				if(!task.isProcessed())
				{
					if((this.regressive && task.getTime() >= currentCount) || (!this.regressive && task.getTime() <= currentCount))
					{
						task.setProcessed(true);
						task.process(this);
					}
				}
			}
		}		
	}

	/**
	 * @param currentCount
	 */
	private void resetTimeoutEvents()
	{
		if(this.events != null)
		{
			for (TimeouTask task  : this.events)
			{
				task.setProcessed(false);
			}
		}		
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.event.timeout.HasTimeoutHandlers#addTimeoutHandler(br.com.sysmap.crux.widgets.client.event.timeout.TimeoutHandler)
	 */
	public HandlerRegistration addTimeoutHandler(TimeoutHandler handler)
	{
		TimeouTask task = new TimeouTask(handler);
		this.events.add(task);
		return addHandler(handler, TimeoutEvent.getType());
	}

	/**
	 * @param initial the initial to set
	 */
	public void setInitial(long initial)
	{
		if(!allowChanges)
		{
			throw new IllegalStateException(WidgetMsgFactory.getMessages().startedTimerCannotBeModified());
		}
		else
		{
			this.initial = initial;
		}
	}

	/**
	 * @param regressive the regressive to set
	 */
	public void setRegressive(boolean regressive)
	{
		if(!allowChanges)
		{
			throw new IllegalStateException(WidgetMsgFactory.getMessages().startedTimerCannotBeModified());
		}
		else
		{
			this.regressive = regressive;
		}
	}

	/**
	 * @return the pattern
	 */
	public String getPattern()
	{
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
		
		if(!StringUtils.isEmpty(this.pattern))
		{
			this.formatter = DateTimeFormat.getFormat(this.pattern);
		}
		else
		{
			this.formatter = DateTimeFormat.getFormat(DEFAULT_PATTERN);
		}
	}
}

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
class TimeProcessor extends com.google.gwt.user.client.Timer
{
	private Timer timer;
	
	public TimeProcessor(Timer timer)
	{
		this.timer = timer;
	}

	@Override
	public void run()
	{
		this.timer.update();		
	}	
}

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
class TimeouTask
{
	private boolean processed;
	TimeoutHandler handler;
	
	/**
	 * @param handler
	 */
	public TimeouTask(TimeoutHandler handler)
	{
		this.handler = handler;
	}

	/**
	 * @param timer
	 */
	public void process(Timer timer)
	{
		handler.onTimeout(new TimeoutEvent(timer));
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed()
	{
		return processed;
	}
	
	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(boolean processed)
	{
		this.processed = processed;
	}
	
	/**
	 * @return the time
	 */
	public long getTime()
	{
		return handler.getScheduledTime();
	}
}