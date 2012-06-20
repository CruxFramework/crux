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

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.CancelHandler;
import org.cruxframework.crux.widgets.client.event.FinishEvent;
import org.cruxframework.crux.widgets.client.event.FinishHandler;
import org.cruxframework.crux.widgets.client.event.HasCancelHandlers;
import org.cruxframework.crux.widgets.client.event.HasFinishHandlers;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 */
public class Wizard<T extends Serializable> extends Composite implements HasCancelHandlers, HasFinishHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-Wizard";
	
	private WizardControlBar<T> controlBar;
	private int currentStep = -1;
	private DockPanel dockPanel;
	private boolean isChangingStep = false;

	private WizardNavigationBar<T> navigationBar;
	
	private FastList<WizardStepListener<T>> stepListeners = new FastList<WizardStepListener<T>>();
	private FastList<String> stepOrder = new FastList<String>();
	private FastMap<Step<T>> steps = new FastMap<Step<T>>();
	
	private DeckPanel stepsPanel;
	private WizardDataSerializer<T> wizardDataSerializer;
	
	
	/**
	 * Wizard Contructor
	 * 
	 * @param id widget identifier
	 * @param wizardDataSerializer serializer for Wizard Data class
	 */
    public Wizard(String id, WizardDataSerializer<T> wizardDataSerializer)
    {
		this.wizardDataSerializer = wizardDataSerializer;
		this.dockPanel = new DockPanel();
		this.dockPanel.setStyleName(DEFAULT_STYLE_NAME);
		
		this.stepsPanel = new DeckPanel();
		this.stepsPanel.setHeight("100%");
		this.stepsPanel.setWidth("100%");
		this.dockPanel.add(stepsPanel, DockPanel.CENTER);		
		this.dockPanel.getElement().setId(id);
		
		initWidget(dockPanel);
    }

    /**
	 * @see org.cruxframework.crux.widgets.client.event.HasCancelHandlers#addCancelHandler(org.cruxframework.crux.widgets.client.event.CancelHandler)
	 */
	public HandlerRegistration addCancelHandler(CancelHandler handler)
    {
		return addHandler(handler, CancelEvent.getType());
    }

	/**
	 * @see org.cruxframework.crux.widgets.client.event.HasFinishHandlers#addFinishHandler(org.cruxframework.crux.widgets.client.event.FinishHandler)
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
	public PageStep<T> addPageStep(String id, String label, String url)
	{
		return insertPageStep(id, label, url, stepOrder.size());
	}
	
	/**
	 * @param listener
	 */
	public void addStepListener(WizardStepListener<T> listener)
	{
		stepListeners.add(listener);
	}
	
	/**
	 * @param id
	 * @param widget
	 * @return
	 */
	public WidgetStep<T> addWidgetStep(String id, String label, Widget widget)
	{
		return insertWidgetStep(id, label, widget, stepOrder.size());
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
	public WizardControlBar<T> getControlBar()
    {
    	return controlBar;
    }

	/**
	 * @return
	 */
	public String getCurrentStep()
	{
		Step<T> step = getStep(currentStep);
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
	public WizardNavigationBar<T> getNavigationBar()
    {
    	return navigationBar;
    }

	/**
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public PageStep<T> getPageStep(int order)
	{
		Step<T> step = getStep(order);
		if (step == null)
		{
			return null;
		}
		return (PageStep<T>)step.getWidget();
	}
	
	/**
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public PageStep<T> getPageStep(String id)
	{
		Step<T> step = getStep(id);
		if (step == null)
		{
			return null;
		}
		return (PageStep<T>)step.getWidget();
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
	@SuppressWarnings("unchecked")
    public WidgetStep<T> getWidgetStep(int order)
	{
		Step<T> step = getStep(order);
		if (step == null)
		{
			return null;
		}
		return (WidgetStep<T>)step.getWidget();
	}
	
	/**
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public WidgetStep<T> getWidgetStep(String id)
	{
		Step<T> step = getStep(id);
		if (step == null)
		{
			return null;
		}
		return (WidgetStep<T>)step.getWidget();
	}

	/**
	 * @param id
	 * @param label
	 * @param url
	 * @param beforeIndex
	 * @return
	 */
	public PageStep<T> insertPageStep(String id, String label, String url, int beforeIndex)
	{
		PageStep<T> pageStep = new PageStep<T>(id, url, this.getElement().getId(), this.wizardDataSerializer);
		insertStep(new Step<T>(this, id, label, pageStep), beforeIndex);
		return pageStep;
	}

	/**
	 * @param id
	 * @param widget
	 * @param beforeIndex
	 * @return
	 */
	public WidgetStep<T> insertWidgetStep(String id, String label, Widget widget, int beforeIndex)
	{
		WidgetStep<T> widgetStep = new WidgetStep<T>(widget, this);
		insertStep(new Step<T>(this, id, label, widgetStep), beforeIndex);
		return widgetStep;
	}

	/**
	 * @param stepOrder
	 * @return
	 */
	public boolean isStepEnabled(int stepOrder)
	{
		Step<T> step = getStep(stepOrder);
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
		Step<T> step = getStep(stepId);
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
    public T readData()
	{
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
        return wizardDataSerializer.readObject();
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
			Step<T> step = steps.remove(id);
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
	public void removeStepListener(WizardStepListener<T> listener)
	{
		int index = stepListeners.indexOf(listener);
		if (index >= 0)
		{
			stepListeners.remove(index);
		}
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
		if (currentStep != step && step >= 0 && step < stepOrder.size())
		{
			Step<T> destinationStep = getStep(step);
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
	 * @param vertical
	 * @param position
	 * @param horizontalAlign
	 */
	public void setControlBar(ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
		setControlBar(new WizardControlBar<T>(), position, horizontalAlign);
    }

	/**
	 * @param vertical
	 * @param position
	 * @param verticalAlign
	 */
	public void setControlBar(ControlPosition position, ControlVerticalAlign verticalAlign)
    {
		setControlBar(new WizardControlBar<T>(), position, verticalAlign);
    }

	/**
	 * @param controlBar
	 * @param position
	 * @param horizontalAlign
	 */
	public void setControlBar(final WizardControlBar<T> controlBar, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
    	if (setControlBar(controlBar, position))
    	{
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
	public void setControlBar(final WizardControlBar<T> controlBar, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
    	if (setControlBar(controlBar, position))
    	{
			final VerticalAlignmentConstant align = getVerticalAlign(verticalAlign);
			this.controlBar.setCellVerticalAlignment(align);
			dockPanel.setCellVerticalAlignment(this.controlBar, align);
    	}
    }

	/**
	 * @param vertical
	 * @param showAllSteps
	 * @param position
	 * @param horizontalAlign
	 */
	public void setNavigationBar(boolean showAllSteps, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
		setNavigationBar(new WizardNavigationBar<T>(showAllSteps), position, horizontalAlign);
    }

	/**
	 * @param vertical
	 * @param showAllSteps
	 * @param position
	 * @param verticalAlign
	 */
	public void setNavigationBar(boolean showAllSteps, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
		setNavigationBar(new WizardNavigationBar<T>(showAllSteps), position, verticalAlign);
    }
	

	/**
	 * @param navigationBar
	 * @param position
	 * @param horizontalAlign
	 */
	public void setNavigationBar(WizardNavigationBar<T> navigationBar, ControlPosition position, ControlHorizontalAlign horizontalAlign)
    {
    	if (setNavigationBar(navigationBar, position))
    	{
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
	public void setNavigationBar(WizardNavigationBar<T> navigationBar, ControlPosition position, ControlVerticalAlign verticalAlign)
    {
    	if (setNavigationBar(navigationBar, position))
    	{
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
		Step<T> step = getStep(stepOrder);
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
		Step<T> step = getStep(stepId);
		if (step == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardStepNotFound(stepId));
		}
		
		step.setEnabled(enabled);
	}
	
	/**
	 * @param data
	 */
	public void updateData(T data)
	{
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
		wizardDataSerializer.writeObject(data);
	}
	
	T getResource()
	{
		if (wizardDataSerializer == null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardNoSerializerAssigned());
		}
		return wizardDataSerializer.getResource();
	}

	/**
	 * @param order
	 * @return
	 */
	Step<T> getStep(int order)
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
	Step<T> getStep(String id)
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
	 * Sets a css class on the parent element of the widget
	 * @param bar
	 * @param position
	 */
	private void applyCssStyleOnWizardBar(final Widget bar, final ControlPosition position, final String baseCssClassName)
	{
		Scheduler.get().scheduleDeferred
		(
			new ScheduledCommand()
			{
				public void execute()
				{
					Element parent = bar.getElement().getParentElement();
					StyleUtils.addStyleName(parent, baseCssClassName + StringUtils.toUpperCaseFirstChar(position.name()) + "Wrapper");
				}
			}
		);	
	}
	
	/**
	 * @param preivousStep
	 */
	@SuppressWarnings("unchecked")
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
	                    loaded = CruxInternalWizardPageController.isInternalPageLoaded(((PageStep<T>)widgetStep).getId());
						if (loaded)
						{
				    		notifyStepListeners(preivousStep);
				    		enterCurrentStep(preivousStep);
						}
                    }
                    catch (WizardException e)
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
	@SuppressWarnings("unchecked")
    private void enterCurrentStep(String previousStep)
    {
    	Step<T> entryStep = getStep(currentStep);
    	if (entryStep.getWidget() instanceof PageStep)
    	{
    		PageStep<T> source =(PageStep<T>)entryStep.getWidget();
    		source.fireEnterEvent(previousStep);
    	}
    	else
    	{
    		HasEnterHandlers<T> source =(HasEnterHandlers<T>)entryStep.getWidget();
    		EnterEvent.fire(source, new WidgetWizardProxy<T>(this), previousStep);
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
	private void insertStep(Step<T> step, int beforeIndex)
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
	@SuppressWarnings("unchecked")
    private boolean leavePreviousStep(String nextStep)
    {
		boolean leave = true;
	    if (currentStep >= 0)
	    {
	    	Step<T> previousStep = getStep(currentStep);
	    	if (previousStep.getWidget() instanceof PageStep)
	    	{
	    		PageStep<T> source =(PageStep<T>)previousStep.getWidget();
	    		LeaveEvent<T> leaveEvent = source.fireLeaveEvent(this, nextStep);
	    		leave = leaveEvent == null || !leaveEvent.isCanceled();
	    	}
	    	else
	    	{
	    		HasLeaveHandlers<T> source =(HasLeaveHandlers<T>)previousStep.getWidget();
	    		LeaveEvent<T> leaveEvent = LeaveEvent.fire(source, new WidgetWizardProxy<T>(this), nextStep);
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
		Step<T> previous = null;
		if (preivousStep != null)
		{
			previous = steps.get(preivousStep);
		}
		for (int i=0; i<stepListeners.size(); i++)
        {
			WizardStepListener<T> listener = stepListeners.get(i);
	        listener.stepChanged(getStep(currentStep), previous);
        }
    }
	
	/**
	 * @param controlBar
	 * @return true if a new controlBar was added
	 */
	private boolean setControlBar(WizardControlBar<T> controlBar, ControlPosition position)
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
			applyCssStyleOnWizardBar(this.controlBar, position, WizardControlBar.DEFAULT_STYLE_NAME);
			return true;
		}
		return false;
    }

	/**
	 * @param navigationBar
	 * @return true if a new controlBar was added
	 */
	private boolean setNavigationBar(WizardNavigationBar<T> navigationBar, ControlPosition position)
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
			applyCssStyleOnWizardBar(this.navigationBar, position, WizardNavigationBar.DEFAULT_STYLE_NAME);
			return true;
		}
		return false;
    }

	public static enum ControlHorizontalAlign{center, left, right}

	public static enum ControlPosition{north, south}	
	
	public static enum ControlVerticalAlign{bottom, middle, top}
	
	public static class NoData implements Serializable
	{
        private static final long serialVersionUID = -5573450207151298732L;
	}
	
}
