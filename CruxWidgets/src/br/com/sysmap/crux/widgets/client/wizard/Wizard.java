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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.context.ContextManager;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.CancelHandler;
import br.com.sysmap.crux.widgets.client.event.FinishEvent;
import br.com.sysmap.crux.widgets.client.event.FinishHandler;
import br.com.sysmap.crux.widgets.client.event.HasCancelHandlers;
import br.com.sysmap.crux.widgets.client.event.HasFinishHandlers;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 */
public class Wizard extends Composite implements HasCancelHandlers, HasFinishHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-Wizard";
	
	private Map<String, Step> steps = new HashMap<String, Step>();
	private List<String> stepOrder = new ArrayList<String>();
	private DockPanel dockPanel;
	private DeckPanel stepsPanel;
	private int currentStep = -1;

	private boolean isChangingStep = false;
	
	private WizardControlBar controlBar;
	private WizardNavigationBar navigationBar;

	private List<WizardStepListener> stepListeners = new ArrayList<WizardStepListener>(); 
	
	public static enum ControlPosition{north, south, east, west}
	public static enum ControlVerticalAlign{top, middle, bottom}
	public static enum ControlHorizontalAlign{left, center, right}
	
	/**
	 * 
	 */
	public Wizard(String id)
    {
		this.dockPanel = new DockPanel();
		this.dockPanel.setStyleName(DEFAULT_STYLE_NAME);
		
		this.stepsPanel = new DeckPanel();
		this.stepsPanel.setHeight("100%");
		this.stepsPanel.setWidth("100%");
		
		this.dockPanel.add(stepsPanel, DockPanel.CENTER);
		this.dockPanel.setCellHeight(this.stepsPanel, "100%");
		this.dockPanel.setCellWidth(this.stepsPanel, "100%");
		
		this.dockPanel.getElement().setId(id);
		
		initWidget(dockPanel);
		Events.getRegisteredClientEventHandlers().registerEventHandler("__wizard", new CruxInternalWizardPageController());
		ContextManager.getContextHandler().eraseData("__Wizard."+getElement().getId());
    }
	
	/**
	 * @param id
	 * @param label
	 * @param url
	 * @return
	 */
	public PageStep addPageStep(String id, String label, String url)
	{
		return insertPageStep(id, label, url, steps.size());
	}
	
	/**
	 * @param id
	 * @param label
	 * @param url
	 * @param beforeIndex
	 * @return
	 */
	public PageStep insertPageStep(String id, String label, String url, int beforeIndex)
	{
		PageStep pageStep = new PageStep(id, url);
		insertStep(new Step(this, id, label, pageStep), beforeIndex);
		return pageStep;
	}

	/**
	 * @param id
	 * @param widget
	 * @return
	 */
	public WidgetStep addWidgetStep(String id, String label, Widget widget)
	{
		return insertWidgetStep(id, label, widget, steps.size());
	}
	
	/**
	 * @param id
	 * @param widget
	 * @param beforeIndex
	 * @return
	 */
	public WidgetStep insertWidgetStep(String id, String label, Widget widget, int beforeIndex)
	{
		WidgetStep widgetStep = new WidgetStep(widget, this);
		insertStep(new Step(this, id, label, widgetStep), beforeIndex);
		return widgetStep;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public boolean removeStep(String id)
	{
		boolean ret = false;
		if (steps.containsKey(id))
		{
			int stepIndex = getStepOrder(id);
			Step step = steps.remove(id);
			ret = stepsPanel.remove(step.getWidget());
			stepOrder.remove(stepIndex);
			if (currentStep == stepIndex)
			{
				selectStep(currentStep-1, true);
			}
			else if (currentStep > stepIndex)
			{
				currentStep--;
			}
		}
		return ret;
	}
	
	/**
	 * @return
	 */
	public boolean first()
	{
		return selectStep(0);
	}

	/**
	 * @return
	 */
	public boolean next()
	{
		return selectStep(currentStep+1);
	}

	/**
	 * @return
	 */
	public boolean back()
	{
		return selectStep(currentStep-1);
	}

	/**
	 * @return
	 */
	public void cancel()
	{
		CancelEvent.fire(this);
	}

	/**
	 * @return
	 */
	public boolean finish()
	{
		FinishEvent finishEvent = FinishEvent.fire(this);
		return !finishEvent.isCanceled();
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.HasCancelHandlers#addCancelHandler(br.com.sysmap.crux.widgets.client.event.CancelHandler)
	 */
	public HandlerRegistration addCancelHandler(CancelHandler handler)
    {
		return addHandler(handler, CancelEvent.getType());
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.HasFinishHandlers#addFinishHandler(br.com.sysmap.crux.widgets.client.event.FinishHandler)
	 */
	public HandlerRegistration addFinishHandler(FinishHandler handler)
    {
		return addHandler(handler, FinishEvent.getType());
    }

	/**
	 * @param id
	 * @return
	 */
	public boolean selectStep(String id)
	{
		return selectStep(id, false);
	}
	
	/**
	 * @param id
	 * @param ignoreLeaveEvent
	 * @return
	 */
	public boolean selectStep(String id, boolean ignoreLeaveEvent)
	{
		boolean ret = false;
		int index = getStepOrder(id);
		if (index > -1)
		{
			ret = selectStep(index, ignoreLeaveEvent);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public int getStepOrder(String id)
	{
		return (id==null?-1:stepOrder.indexOf(id));
	}

	/**
	 * @return
	 */
	public WizardControlBar getControlBar()
    {
    	return controlBar;
    }

	/**
	 * @param controlBar
	 * @param position
	 * @param verticalAlign
	 */
	public void setControlBar(WizardControlBar controlBar, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
    	setControlBar(controlBar);
		if (this.controlBar != null)
		{
			this.controlBar.setWizard(this);
			addStepListener(this.controlBar);
			dockPanel.add(this.controlBar, getDockPosition(position));
			dockPanel.setCellWidth(this.controlBar, "0");
			dockPanel.setCellVerticalAlignment(this.controlBar, getVerticalAlign(verticalAlign));
		}
    }

	/**
	 * @param controlBar
	 * @param position
	 * @param horizontalAlign
	 */
	public void setControlBar(WizardControlBar controlBar, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
    	setControlBar(controlBar);
		if (this.controlBar != null)
		{
			this.controlBar.setWizard(this);
			addStepListener(this.controlBar);
			dockPanel.add(this.controlBar, getDockPosition(position));
			dockPanel.setCellHeight(this.controlBar, "0");
			dockPanel.setCellHorizontalAlignment(this.controlBar, getHorizontalAlign(horizontalAlign));
		}
    }

	/**
	 * @return
	 */
	public WizardNavigationBar getNavigationBar()
    {
    	return navigationBar;
    }

	/**
	 * @param navigationBar
	 * @param position
	 */
	public void setNavigationBar(WizardNavigationBar navigationBar, ControlPosition position)
    {
    	if (this.navigationBar != null)
    	{
    		dockPanel.remove(this.navigationBar);
    		removeStepListener(this.navigationBar);
    	}
		this.navigationBar = navigationBar;
    	addStepListener(navigationBar);
    	dockPanel.add(navigationBar, getDockPosition(position));
    }

	/**
	 * @param listener
	 */
	public void addStepListener(WizardStepListener listener)
	{
		stepListeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeStepListener(WizardStepListener listener)
	{
		stepListeners.remove(listener);
	}
	
	/**
	 * @return
	 */
	public int getStepCount()
    {
		return stepOrder.size();
    }

	/**
	 * @param id
	 * @return
	 */
	public WidgetStep getWidgetStep(String id)
	{
		Step step = getStep(id);
		if (step == null)
		{
			return null;
		}
		return (WidgetStep)step.getWidget();
	}
	
	/**
	 * @param order
	 * @return
	 */
	public WidgetStep getWidgetStep(int order)
	{
		Step step = getStep(order);
		if (step == null)
		{
			return null;
		}
		return (WidgetStep)step.getWidget();
	}

	/**
	 * @param id
	 * @return
	 */
	public PageStep getPageStep(String id)
	{
		Step step = getStep(id);
		if (step == null)
		{
			return null;
		}
		return (PageStep)step.getWidget();
	}
	
	/**
	 * @param order
	 * @return
	 */
	public PageStep getPageStep(int order)
	{
		Step step = getStep(order);
		if (step == null)
		{
			return null;
		}
		return (PageStep)step.getWidget();
	}

	/**
	 * @return
	 */
	public int getCurrentStepIndex()
	{
		return currentStep;
	}
	
	/**
	 * @return
	 */
	public String getCurrentStep()
	{
		Step step = getStep(currentStep);
		if (step != null)
		{
			return step.getId();
		}
		return null;
	}
	
	/**
	 * @param step
	 * @return
	 */
	public boolean selectStep(int step)
    {
		return selectStep(step, false);
    }

	/**
	 * @param step
	 * @param ignoreLeaveEvent
	 * @return
	 */
	public boolean selectStep(int step, boolean ignoreLeaveEvent)
    {
		boolean ret = false;
		if (currentStep != step && step >= 0 && step < steps.size())
		{
			isChangingStep = true;

			String nextStep = (step < getStepCount())?getStep(step).getId():null;
			if (ignoreLeaveEvent || leavePreviousStep(nextStep))			
			{
				String preivousStep = (currentStep >=0)?getStep(currentStep).getId():null;
				currentStep = step;
				stepsPanel.showWidget(currentStep);
				changeStep(preivousStep);
				ret = true;
			}
			else
			{
				isChangingStep = false;
			}
		}
		return ret;
    }

	/**
	 * @param data
	 */
	public void updateContext(Object data)
	{
        ContextManager.getContextHandler().writeData("__Wizard."+getElement().getId(), data);
	}
	
	/**
	 * @param <T>
	 * @param dataType
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public <T> T readContext(Class<T> dataType)
	{
        return (T)ContextManager.getContextHandler().readData("__Wizard."+getElement().getId());
	}
	
	/**
	 * @param id
	 * @return
	 */
	Step getStep(String id)
	{
		return steps.get(id);
	}
	
	/**
	 * @param order
	 * @return
	 */
	Step getStep(int order)
	{
		if (order >= 0 && order < stepOrder.size())
		{
			return steps.get(stepOrder.get(order));
		}
		
		return null;
	}
	
	/**
	 * @return
	 */
	boolean isChangingStep()
	{
		return this.isChangingStep;
	}
	
	/**
	 * @param preivousStep
	 */
	private void changeStep(final String preivousStep)
    {
		final Widget widgetStep = getStep(currentStep).getWidget();
		if (widgetStep instanceof PageStep)
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
			{
				public boolean execute()
				{
					boolean loaded;
                    try
                    {
	                    loaded = CruxInternalWizardPageController.isInternalPageLoaded(((PageStep)widgetStep).getId());
						if (loaded)
						{
				    		notifyStepListeners(preivousStep);
				    		enterCurrentStep(preivousStep);
						}
                    }
                    catch (ModuleComunicationException e)
                    {
                    	Crux.getErrorHandler().handleError(e.getMessage(), e);
                    	loaded = true;
                    }
					return !loaded;
				}
			}, 10);
		}
		else
		{
    		notifyStepListeners(preivousStep);
    		enterCurrentStep(preivousStep);
		}
    }

	/**
	 * @param step
	 * @param beforeIndex
	 * @return
	 */
	private void insertStep(Step step, int beforeIndex)
	{
		if (!steps.containsKey(step.getId()))
		{
			steps.put(step.getId(), step);
			stepsPanel.insert(step.getWidget(), beforeIndex);
			stepOrder.add(beforeIndex, step.getId());
		}
		else
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardDuplicatedStep(step.getId()));
		}
	}

	/**
	 * 
	 */
	private void notifyStepListeners(String preivousStep)
    {
		Step previous = null;
		if (preivousStep != null)
		{
			previous = steps.get(preivousStep);
		}
		for (WizardStepListener listener : stepListeners)
        {
	        listener.stepChanged(getStep(currentStep), previous);
        }
    }

	/**
	 * @param previousStep
	 */
	private void enterCurrentStep(String previousStep)
    {
    	Step entryStep = getStep(currentStep);
    	if (entryStep.getWidget() instanceof PageStep)
    	{
    		PageStep source =(PageStep)entryStep.getWidget();
    		source.fireEnterEvent(getElement().getId(), previousStep);
    	}
    	else
    	{
    		HasEnterHandlers source =(HasEnterHandlers)entryStep.getWidget();
    		EnterEvent.fire(source, new WidgetWizardProxy(this), previousStep);
    	}
		isChangingStep = false;
    }

	/**
	 * @param nextStep 
	 * @return
	 */
	private boolean leavePreviousStep(String nextStep)
    {
		boolean leave = true;
	    if (currentStep >= 0)
	    {
	    	Step previousStep = getStep(currentStep);
	    	if (previousStep.getWidget() instanceof PageStep)
	    	{
	    		PageStep source =(PageStep)previousStep.getWidget();
	    		LeaveEvent leaveEvent = source.fireLeaveEvent(getElement().getId(), nextStep);
	    		leave = leaveEvent == null || !leaveEvent.isCanceled();
	    	}
	    	else
	    	{
	    		HasLeaveHandlers source =(HasLeaveHandlers)previousStep.getWidget();
	    		LeaveEvent leaveEvent = LeaveEvent.fire(source, new WidgetWizardProxy(this), nextStep);
	    		leave = !leaveEvent.isCanceled();
	    	}
	    }
	    return leave;
    }
	
	/**
	 * @param position
	 * @return
	 */
	private DockLayoutConstant getDockPosition(ControlPosition position)
    {
	    switch (position)
        {
        	case north:
        		return DockPanel.NORTH;
        	case south:
    	        return DockPanel.SOUTH;
        	case east:
    	        return DockPanel.EAST;
        	case west:
    	        return DockPanel.WEST;
        }
	    return DockPanel.SOUTH;
    }
	
	private HorizontalAlignmentConstant getHorizontalAlign(ControlHorizontalAlign horizontalAlign)
    {
		switch (horizontalAlign)
        {
        	case center:
        		return HasHorizontalAlignment.ALIGN_CENTER;
        	case left:
        		return HasHorizontalAlignment.ALIGN_LEFT;
        	case right:
        		return HasHorizontalAlignment.ALIGN_RIGHT;
        }
		
		return HasHorizontalAlignment.ALIGN_RIGHT;
    }

	private VerticalAlignmentConstant getVerticalAlign(ControlVerticalAlign verticalAlign)
    {
		switch (verticalAlign)
        {
        	case middle:
        		return HasVerticalAlignment.ALIGN_MIDDLE;
        	case top:
        		return HasVerticalAlignment.ALIGN_TOP;
        	case bottom:
        		return HasVerticalAlignment.ALIGN_BOTTOM;
        }
		
		return HasVerticalAlignment.ALIGN_TOP;
    }

	/**
	 * @param controlBar
	 */
	private void setControlBar(WizardControlBar controlBar)
    {
	    if (this.controlBar != null)
    	{
    		dockPanel.remove(this.controlBar);
    		removeStepListener(this.controlBar);
    	}
		this.controlBar = controlBar;
    }	
}
