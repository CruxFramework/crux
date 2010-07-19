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
package br.com.sysmap.crux.widgets.client.wizard;

import br.com.sysmap.crux.core.client.controller.crossdoc.CrossDocument;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface CruxInternalWizardPageControllerCrossDoc extends CrossDocument
{
	boolean onLeave(String wizardId, String wizardDataId, String nextStep);
	void onEnter(String previousStep);
	void onCommand(String commandId);
	WizardCommandData[] listCommands();
	void cancel(String wizardId);
	boolean finish(String wizardId);
	boolean first(String wizardId);
	int getStepOrder(String wizardId, String stepId);
	boolean next(String wizardId);
	boolean back(String wizardId);
	boolean selectStep(String wizardId, String stepId, boolean ignoreLeaveEvents);
	String getButtonsHeight(String wizardId);
	String getButtonsStyle(String wizardId);
	String getButtonsWidth(String wizardId);
	String getCancelLabel(String wizardId);
	String getFinishLabel(String wizardId);
	String getNextLabel(String wizardId);
	String getBackLabel(String wizardId);
	int getSpacing(String wizardId);
	boolean isVertical(String wizardId);
	void setButtonsHeight(String wizardId, String buttonHeight);
	void setButtonsStyle(String wizardId, String buttonStyle);
	void setButtonsWidth(String wizardId, String buttonWidth);
	void setCancelLabel(String wizardId, String cancelLabel);
	void setFinishLabel(String wizardId, String finishLabel);
	void setNextLabel(String wizardId, String nextLabel);
	void setBackLabel(String wizardId, String backLabel);
	void setSpacing(String wizardId, int spacing);
	String getCommandId(String wizardId, String commandId);
	String getCommandLabel(String wizardId, String commandId);
	int getCommandOrder(String wizardId, String commandId);
	boolean isCommandEnabled(String wizardId, String commandId);
	void setCommandEnabled(String wizardId, String commandId, boolean enabled);
	void setCommandLabel(String wizardId, String commandId, String label);
	void setCommandOrder(String wizardId, String commandId, int order);
	String getCommandStyleName(String wizardId, String commandId);
	int getOffsetHeight(String wizardId, String commandId);
	int getOffsetWidth(String wizardId, String commandId);
	void setCommandStyleName(String wizardId, String commandId, String styleName);
	void setCommandHeight(String wizardId, String commandId, String height);
	void setCommandWidth(String wizardId, String commandId, String width);
}
