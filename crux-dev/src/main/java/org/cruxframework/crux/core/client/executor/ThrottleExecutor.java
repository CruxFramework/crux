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
package org.cruxframework.crux.core.client.executor;

import java.util.Date;

import com.google.gwt.user.client.Timer;


/**
 * Throttles a chain of incoming tasks in order to
 * execute only some of them, ensuring that the
 * max number of tasks executed per second is fixed.
 * 
 * @author Gesse Dafe
 */
public abstract class ThrottleExecutor
{
	private int ratio;
	private long lastExec = new Date().getTime();

	/**
	 * Creates a executor capable of controlling the 
	 * interval between the invocations to its 
	 * action.
	 *  
	 * @param ratio the interval between the invocations, in milliseconds
	 */
	public ThrottleExecutor(int ratio) 
	{
		this.ratio = ratio;
	}
	
	/**
	 * Controls the ratio
	 */
	private Timer timer = new Timer()
	{
		public void run() 
		{
			throttle();
		}
	};
	
	/**
	 * Throttles the invocations on the executor
	 */
	public void throttle() 
	{
		long now = new Date().getTime();
		
		long delta = now - lastExec;
		
		if(delta < ratio)
		{
			if(timer != null)
			{
				timer.cancel();
			}
			
			timer.schedule(ratio);
		}
		else
		{
			lastExec = now;
			doAction();
		}
	}

	/**
	 * Executes the desired action. The invocation ratio of 
	 * this method is controlled by the <code>ratio</code>
	 * field of this instance.
	 */
	protected abstract void doAction();

}