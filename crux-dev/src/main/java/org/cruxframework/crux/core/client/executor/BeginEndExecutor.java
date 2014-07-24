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
 * An executor that only executes the first and the last call to a given action.
 * "Last call" means "the last call in N milliseconds", where N can be
 * parameterized at the constructor. If there was only one call (begin call), a
 * second one (end call) is emulated.
 * 
 * @author Gesse Dafe
 */
public abstract class BeginEndExecutor
{
	private int maxIntervalBetweenStartAndEnd;
	private long lastExec = new Date().getTime();
	boolean isRunning = false;
	private boolean doEndActionExecuted = false;

	/**
	 * Creates an executor that only executes the first and the last call to a
	 * given action.
	 * 
	 * @param end
	 *            if more than <code>maxIntervalBetweenStartAndEnd</code>
	 *            milliseconds has passed since the previous call, considers
	 *            that the current call is a begin call.
	 */
	public BeginEndExecutor(int maxIntervalBetweenStartAndEnd)
	{
		this.maxIntervalBetweenStartAndEnd = maxIntervalBetweenStartAndEnd;
	}

	/**
	 * Used for scheduling the end call
	 */
	private Timer timer = new Timer(){
		public void run()
		{
			execute();
		}
	};

	/**
	 * Throttles the invocations on the executor
	 */
	public void execute()
	{
		if (!isRunning)
		{
			isRunning = true;
			doBeginAction();
		} else
		{
			doEndAction();
			isRunning = false;
			doEndActionExecuted = true;
		}

	}

	/**
	 * Called when the first call is made for the executor
	 */
	protected abstract void doEndAction();

	/**
	 * Called when the last call is made for the executor
	 */
	protected abstract void doBeginAction();
}