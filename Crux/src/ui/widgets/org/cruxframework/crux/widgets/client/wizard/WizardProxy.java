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
package org.cruxframework.crux.widgets.client.wizard;

import java.io.Serializable;

import org.cruxframework.crux.core.client.screen.JSWindow;


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
interface WizardProxy<T extends Serializable>
{
	boolean first();
	boolean next();
	boolean back();
	void cancel();
	boolean finish();
	boolean selectStep(String id, boolean ignoreLeaveEvent);
	int getStepOrder(String id);
	WizardControlBarAccessor getControlBar();
	void updateData(T data);
    T readData();
    T getResource();
	JSWindow getStepWindow(String stepId);
	void setStepEnabled(int stepOrder, boolean enabled);
	void setStepEnabled(String stepId, boolean enabled);
}
