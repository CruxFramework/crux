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
package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Timer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class Animation
{
	private int durationMillis;
	private CompleteCallback completeCallback;
	private Timer finishTimer;

	public static interface CompleteCallback
	{
		void onComplete();
	}
	
	public void start()
	{
		if(this.finishTimer != null)
		{
			finishTimer.cancel();
		}
		doStart();
		this.finishTimer = new Timer() 
		{
			public void run() 
			{
				finish();
			}
		};
		
		this.finishTimer.schedule(durationMillis+10);
		
	}

	public void finish()
	{
		doFinish();
		if (completeCallback != null)
		{
			completeCallback.onComplete();
		}
	}

	public void cancel() 
	{
		if(this.finishTimer != null)
		{
			finishTimer.cancel();
		}
		
		doCancel();
	}

	public void setDurationMillis(int durationMillis)
    {
    	this.durationMillis = durationMillis;
    }

	public void setCompleteCallback(CompleteCallback completeCallback)
    {
    	this.completeCallback = completeCallback;
    }

	public abstract void doCancel();
	public abstract void doFinish();
	protected abstract void doStart();

	protected int getDurationMillis() 
	{
		return durationMillis;
	}
}
