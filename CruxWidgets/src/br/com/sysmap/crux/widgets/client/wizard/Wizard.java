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
	
	private WizardControlBar controlBar;
	private int currentStep = -1;
	private InternalDockPanel dockPanel;
	private boolean isChangingStep = false;
	private WizardNavigationBar navigationBar;

	private List<WizardStepListener> stepListeners = new ArrayList<WizardStepListener>();
	
	private List<String> stepOrder = new ArrayList<String>();
	private Map<String, Step> steps = new HashMap<String, Step>();

	private DeckPanel stepsPanel; 
	
	/**
	 * 
	 */
	public Wizard(String id)
    {
		this.dockPanel = new InternalDockPanel(){
		};
		this.dockPanel.setStyleName(DEFAULT_STYLE_NAME);
		
		this.stepsPanel = new DeckPanel();
		this.stepsPanel.setHeight("100%");
		this.stepsPanel.setWidth("100%");
		
		this.dockPanel.add(stepsPanel, DockPanel.CENTER);
		this.dockPanel.setCellHeight(this.stepsPanel, "100%");
		this.dockPanel.setCellWidth(this.stepsPanel, "100%");
		
		this.dockPanel.getElement().setId(id);
		this.dockPanel.getBody().getStyle().setProperty("height", "100%");
		
		
		initWidget(dockPanel);
		Events.getRegisteredClientEventHandlers().registerEventHandler("__wizard", new CruxInternalWizardPageController());
		ContextManager.getContextHandler().eraseData("__Wizard."+getElement().getId());
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
	 * @param label
	 * @param url
	 * @return
	 */
	public PageStep addPageStep(String id, String label, String url)
	{
		return insertPageStep(id, label, url, steps.size());
	}
	
	/**
	 * @param listener
	 */
	public void addStepListener(WizardStepListener listener)
	{
		stepListeners.add(listener);
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
	 * @return
	 */
	public boolean back()
	{
		int destinationStep = currentStep-1;
		if (destinationStep >=0 )
		{
			while (!getStep(destinationStep).isEnabled())
			{
				destinationStep--;
				if (destinationStep <0)
				{
					return false;
				}
			}
		}
		
		
		return selectStep(destinationStep);
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
	 * @return
	 */
	public boolean first()
	{
		return selectStep(0);
	}

	/**
	 * @return
	 */
	public WizardControlBar getControlBar()
    {
    	return controlBar;
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
	 * @return
	 */
	public int getCurrentStepIndex()
	{
		return currentStep;
	}

	/**
	 * @return
	 */
	public WizardNavigationBar getNavigationBar()
    {
    	return navigationBar;
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
	 * @return
	 */
	public int getStepCount()
    {
		return stepOrder.size();
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
	 * @param stepOrder
	 * @return
	 */
	public boolean isStepEnabled(int stepOrder)
	{
		Step step = getStep(stepOrder);
		if (step == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardStepNotFound(stepOrder));
		}
		
		return step.isEnabled();
	}

	/**
	 * @param stepId
	 * @return
	 */
	public boolean isStepEnabled(String stepId)
	{
		Step step = getStep(stepId);
		if (step == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardStepNotFound(stepId));
		}
		
		return step.isEnabled();
	}

	/**
	 * @return
	 */
	public boolean next()
	{
		int destinationStep = currentStep+1;
		if (destinationStep < stepOrder.size())
		{
			while (!getStep(destinationStep).isEnabled())
			{
				destinationStep++;
				if (destinationStep >= stepOrder.size())
				{
					return false;
				}
			}
		}
		
		
		return selectStep(destinationStep);
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
	 * @param listener
	 */
	public void removeStepListener(WizardStepListener listener)
	{
		stepListeners.remove(listener);
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
			Step destinationStep = getStep(step);
			if (!destinationStep.isEnabled())
			{
				throw new WizardException(WidgetMsgFactory.getMessages().wizardInvalidStepSelected(step));
			}
			isChangingStep = true;

			String nextStep = destinationStep.getId();
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
	 * @param controlBar
	 * @param position
	 * @param horizontalAlign
	 */
	public void setControlBar(final WizardControlBar controlBar, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
    	if (setControlBar(controlBar, position))
    	{
			dockPanel.setCellHeight(this.controlBar, "0");
			final HorizontalAlignmentConstant align = getHorizontalAlign(horizontalAlign);
			this.controlBar.setCellHorizontalAlignment(align);
			dockPanel.setCellHorizontalAlignment(this.controlBar, align);
		}
    }
	
	/**
	 * @param controlBar
	 * @param position
	 * @param verticalAlign
	 */
	public void setControlBar(final WizardControlBar controlBar, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
    	if (setControlBar(controlBar, position))
    	{
			dockPanel.setCellWidth(this.controlBar, "0");
			dockPanel.setCellHeight(this.controlBar, "100%");
			final VerticalAlignmentConstant align = getVerticalAlign(verticalAlign);
			this.controlBar.setCellVerticalAlignment(align);
			dockPanel.setCellVerticalAlignment(this.controlBar, align);
    	}
    }

	/**
	 * @param navigationBar
	 * @param position
	 * @param horizontalAlign
	 */
	public void setNavigationBar(WizardNavigationBar navigationBar, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
    	if (setNavigationBar(navigationBar, position))
    	{
			dockPanel.setCellHeight(this.navigationBar, "0");
			HorizontalAlignmentConstant align = getHorizontalAlign(horizontalAlign);
			this.navigationBar.setCellHorizontalAlignment(align);
			dockPanel.setCellHorizontalAlignment(this.navigationBar, align);
		}
    }
	
	/**
	 * @param navigationBar
	 * @param position
	 * @param verticalAlign
	 */
	public void setNavigationBar(WizardNavigationBar navigationBar, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
    	if (setNavigationBar(navigationBar, position))
    	{
			dockPanel.setCellWidth(this.navigationBar, "0");
			dockPanel.setCellHeight(this.navigationBar, "100%");
			VerticalAlignmentConstant align = getVerticalAlign(verticalAlign);
			this.navigationBar.setCellVerticalAlignment(align);
			dockPanel.setCellVerticalAlignment(this.navigationBar, align);
		}
    }

	/**
	 * @param enabled
	 */
	public void setStepEnabled(int stepOrder, boolean enabled)
	{
		Step step = getStep(stepOrder);
		if (step == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardStepNotFound(stepOrder));
		}
		
		step.setEnabled(enabled);
	}
	
	/**
	 * @param enabled
	 */
	public void setStepEnabled(String stepId, boolean enabled)
	{
		Step step = getStep(stepId);
		if (step == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardStepNotFound(stepId));
		}
		
		step.setEnabled(enabled);
	}
	
	/**
	 * @param data
	 */
	public void updateContext(Object data)
	{
        ContextManager.getContextHandler().writeData("__Wizard."+getElement().getId(), data);
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
	 * @param id
	 * @return
	 */
	Step getStep(String id)
	{
		return steps.get(id);
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
	 * @param controlBar
	 * @return true if a new controlBar was added
	 */
	private boolean setControlBar(WizardControlBar controlBar, ControlPosition position)
    {
	    if (this.controlBar != null)
    	{
    		dockPanel.remove(this.controlBar);
    		removeStepListener(this.controlBar);
    	}
		this.controlBar = controlBar;
		if (this.controlBar != null)
		{
			this.controlBar.setWizard(this);
			addStepListener(this.controlBar);
			dockPanel.add(this.controlBar, getDockPosition(position));
			return true;
		}
		return false;
    }
	
	/**
	 * @param navigationBar
	 * @return true if a new controlBar was added
	 */
	private boolean setNavigationBar(WizardNavigationBar navigationBar, ControlPosition position)
    {
	    if (this.navigationBar != null)
    	{
    		dockPanel.remove(this.navigationBar);
    		removeStepListener(this.navigationBar);
    	}
		this.navigationBar = navigationBar;
		if (this.navigationBar != null)
		{
			this.navigationBar.setWizard(this);
			addStepListener(this.navigationBar);
			dockPanel.add(this.navigationBar, getDockPosition(position));
			return true;
		}
		return false;
    }

	public static enum ControlHorizontalAlign{center, left, right}

	public static enum ControlPosition{east, north, south, west}	
	
	public static enum ControlVerticalAlign{bottom, middle, top}	
	
	
	
}
