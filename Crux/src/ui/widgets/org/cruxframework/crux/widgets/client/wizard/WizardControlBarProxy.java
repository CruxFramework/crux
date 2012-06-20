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

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
interface WizardControlBarProxy
{
	void finish();
	void cancel();
	void next();
	void back();
	void setSpacing(int spacing);
	int getSpacing();
	String getBackLabel();
	String getNextLabel();
	String getCancelLabel();
	String getFinishLabel();
	void setBackLabel(String backLabel);
	void setNextLabel(String nextLabel);
	void setCancelLabel(String cancelLabel);
	void setFinishLabel(String finishLabel);
	String getButtonsWidth();
	void setButtonsWidth(String buttonWidth);
	String getButtonsHeight();
	void setButtonsHeight(String buttonHeight);
	String getButtonsStyle();
	void setButtonsStyle(String buttonStyle);
	WizardCommandAccessor getCommand(String commandId);
}
