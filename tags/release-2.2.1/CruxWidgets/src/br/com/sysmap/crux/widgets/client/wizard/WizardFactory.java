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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.AllChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.event.CancelEvtBind;
import br.com.sysmap.crux.widgets.client.event.FinishEvtBind;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlHorizontalAlign;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlPosition;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlVerticalAlign;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@DeclarativeFactory(id="wizard", library="widgets")
public class WizardFactory extends WidgetFactory<Wizard>
{
	@Override
    public Wizard instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
    {
	    return new Wizard(widgetId);
    }

	@Override
	@TagEvents({
		@TagEvent(FinishEvtBind.class),
		@TagEvent(CancelEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<Wizard> context) throws InterfaceConfigException
	{
	    super.processEvents(context);
	}

	@Override
	public void postProcess(final WidgetFactoryContext<Wizard> context) throws InterfaceConfigException
	{
		context.getWidget().first();				
	}
	
	
	@Override
	@TagChildren({
		@TagChild(WizardChildrenProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<Wizard> context) throws InterfaceConfigException {}
	
	public static class WizardChildrenProcessor extends AllChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(NavigationBarProcessor.class),
			@TagChild(StepsProcessor.class),
			@TagChild(ControlBarProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="navigationBar", minOccurs="0")
	public static class NavigationBarProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="position", type=ControlPosition.class, defaultValue="north"),
			@TagAttributeDeclaration(value="showAllSteps", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="allowSelectStep", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=ControlVerticalAlign.class, defaultValue="top"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=ControlHorizontalAlign.class, defaultValue="left"),
			@TagAttributeDeclaration("labelStyleName"),
			@TagAttributeDeclaration("horizontalSeparatorStyleName"),
			@TagAttributeDeclaration("verticalSeparatorStyleName")
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException 
		{
			String positionAttr = context.getChildElement().getAttribute("_position");
			ControlPosition position = ControlPosition.north;
			if (!StringUtils.isEmpty(positionAttr))
			{
				position = ControlPosition.valueOf(positionAttr);
			}
			
			boolean vertical = position.equals(ControlPosition.east) || position.equals(ControlPosition.west);
			boolean showAllSteps = true;
			String showAllStepsAttr = context.getChildElement().getAttribute("_showAllSteps");
			if (!StringUtils.isEmpty(showAllStepsAttr ))
			{
				showAllSteps = Boolean.parseBoolean(showAllStepsAttr);
			}
			if (vertical)
			{
				ControlVerticalAlign verticalAlign = ControlVerticalAlign.top;
				String verticalAlignmentAttr = context.getChildElement().getAttribute("_verticalAlignment");
				if (!StringUtils.isEmpty(verticalAlignmentAttr))
				{
					verticalAlign = ControlVerticalAlign.valueOf(verticalAlignmentAttr);
				}
				context.getRootWidget().setNavigationBar(new WizardNavigationBar(vertical, showAllSteps), position, verticalAlign);
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.left;
				String horizontalAlignmentAttr = context.getChildElement().getAttribute("_horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				context.getRootWidget().setNavigationBar(new WizardNavigationBar(vertical, showAllSteps), position, horizontalAlign);
			}
			
			processNavigationBarAttributes(context);

		}

		private void processNavigationBarAttributes(WidgetChildProcessorContext<Wizard> context)
        {
	        String allowSelectStep = context.getChildElement().getAttribute("_allowSelectStep");
			if (!StringUtils.isEmpty(allowSelectStep))
			{
				context.getRootWidget().getNavigationBar().setAllowSelectStep(Boolean.parseBoolean(allowSelectStep));
			}
			String labelStyleName = context.getChildElement().getAttribute("_labelStyleName");
			if (!StringUtils.isEmpty(labelStyleName))
			{
				context.getRootWidget().getNavigationBar().setLabelStyleName(labelStyleName);
			}
			String horizontalSeparatorStyleName = context.getChildElement().getAttribute("_horizontalSeparatorStyleName");
			if (!StringUtils.isEmpty(horizontalSeparatorStyleName))
			{
				context.getRootWidget().getNavigationBar().setHorizontalSeparatorStyleName(horizontalSeparatorStyleName);
			}
			String verticalSeparatorStyleName = context.getChildElement().getAttribute("_verticalSeparatorStyleName");
			if (!StringUtils.isEmpty(verticalSeparatorStyleName))
			{
				context.getRootWidget().getNavigationBar().setVerticalSeparatorStyleName(verticalSeparatorStyleName);
			}
        }
	}
	
	@TagChildAttributes(tagName="steps")
	public static class StepsProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(WizardStepsProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(maxOccurs="unbounded")
	public static class WizardStepsProcessor extends ChoiceChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetStepProcessor.class),
			@TagChild(PageStepProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetStepProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class),
			@TagChild(CommandsProcessor.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onEnter"),
			@TagEventDeclaration("onLeave")
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException 
		{
			context.setAttribute("stepId", context.getChildElement().getAttribute("id"));
			context.setAttribute("stepLabel", context.getChildElement().getAttribute("_label"));
			context.setAttribute("stepOnEnter", context.getChildElement().getAttribute("_onEnter"));
			context.setAttribute("stepOnLeave", context.getChildElement().getAttribute("_onLeave"));
			context.setAttribute("enabled", context.getChildElement().getAttribute("_enabled"));
		}
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	public static class CommandsProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(WizardCommandsProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	public static class WizardCommandsProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="onCommand", required=true)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException 
		{
			String id = context.getChildElement().getAttribute("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getAttribute("_label"));
			int order = Integer.parseInt(context.getChildElement().getAttribute("_order"));
			
			final Event commandEvent = EvtBind.getWidgetEvent(context.getChildElement(), "onCommand");
			
			WizardCommandHandler handler = new WizardCommandHandler()
			{
				public void onCommand(WizardCommandEvent event)
				{
					Events.callEvent(commandEvent, event);
				}
			};
			
			WidgetStep widgetStep = context.getRootWidget().getWidgetStep((String)context.getAttribute("stepId"));
			widgetStep.addCommand(id, label, handler, order);
			
			String styleName = context.getChildElement().getAttribute("_styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				widgetStep.getCommand(id).setStyleName(styleName);
			}
			String width = context.getChildElement().getAttribute("_width");
			if (!StringUtils.isEmpty(width))
			{
				widgetStep.getCommand(id).setWidth(width);
			}
			String height = context.getChildElement().getAttribute("_height");
			if (!StringUtils.isEmpty(height))
			{
				widgetStep.getCommand(id).setHeight(height);
			}
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor extends WidgetChildProcessor<Wizard> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException
		{
			Widget childWidget = createChildWidget(context.getChildElement(), context.getChildElement().getId());
			
			String id = (String)context.getAttribute("stepId");
			String label = ScreenFactory.getInstance().getDeclaredMessage((String)context.getAttribute("stepLabel"));
			
			WidgetStep widgetStep = context.getRootWidget().addWidgetStep(id, label, childWidget);
			
			String onEnter = (String)context.getAttribute("stepOnEnter");
			final Event onEnterEvent = Events.getEvent("onEnter", onEnter);
			if (onEnterEvent != null)
			{
				widgetStep.addEnterHandler(new EnterHandler()
				{
					public void onEnter(EnterEvent event)
					{
						Events.callEvent(onEnterEvent, event);
					}
				});
			}
			
			String onLeave = (String)context.getAttribute("stepOnLeave");
			final Event onLeaveEvent = Events.getEvent("onLeave", onLeave);
			if (onLeaveEvent != null)
			{
				widgetStep.addLeaveHandler(new LeaveHandler()
				{
					public void onLeave(LeaveEvent event)
					{
						Events.callEvent(onLeaveEvent, event);
					}
				});
			}
			String enabled = (String)context.getAttribute("enabled");
			if (!StringUtils.isEmpty(enabled))
			{
				context.getRootWidget().setStepEnabled(id, Boolean.parseBoolean(enabled));
			}
		}
	}

	@TagChildAttributes(tagName="page")
	public static class PageStepProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true")
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException
		{
			String id = context.getChildElement().getAttribute("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getAttribute("_label"));
			String url = context.getChildElement().getAttribute("_url");
			context.getRootWidget().addPageStep(id, label, url);

			String enabled = context.getChildElement().getAttribute("_enabled");
			if (!StringUtils.isEmpty(enabled))
			{
				context.getRootWidget().setStepEnabled(id, Boolean.parseBoolean(enabled));
			}
		}
	}
	
	@TagChildAttributes(tagName="controlBar", minOccurs="0")
	public static class ControlBarProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="position", type=ControlPosition.class, defaultValue="south"),
			@TagAttributeDeclaration(value="verticalAlignment", type=ControlVerticalAlign.class, defaultValue="top"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=ControlHorizontalAlign.class, defaultValue="right"),
			@TagAttributeDeclaration(value="spacing", type=Integer.class),
			@TagAttributeDeclaration(value="buttonsWidth", type=String.class),
			@TagAttributeDeclaration(value="buttonsHeight", type=String.class),
			@TagAttributeDeclaration(value="buttonsStyle", type=String.class),
			@TagAttributeDeclaration(value="backLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="nextLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="cancelLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="finishLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="backOrder", type=Integer.class, defaultValue="0"),
			@TagAttributeDeclaration(value="nextOrder", type=Integer.class, defaultValue="1"),
			@TagAttributeDeclaration(value="finishOrder", type=Integer.class, defaultValue="2"),
			@TagAttributeDeclaration(value="cancelOrder", type=Integer.class, defaultValue="3")
		})
		@TagChildren({
			@TagChild(ControlBarCommandsProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException 
		{
			String positionAttr = context.getChildElement().getAttribute("_position");
			ControlPosition position = ControlPosition.south;
			if (!StringUtils.isEmpty(positionAttr))
			{
				position = ControlPosition.valueOf(positionAttr);
			}
			
			boolean vertical = position.equals(ControlPosition.east) || position.equals(ControlPosition.west);
			if (vertical)
			{
				ControlVerticalAlign verticalAlign = ControlVerticalAlign.top;
				String verticalAlignmentAttr = context.getChildElement().getAttribute("_verticalAlignment");
				if (!StringUtils.isEmpty(verticalAlignmentAttr))
				{
					verticalAlign = ControlVerticalAlign.valueOf(verticalAlignmentAttr);
				}
				context.getRootWidget().setControlBar(new WizardControlBar(vertical), position, verticalAlign);
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.right;
				String horizontalAlignmentAttr = context.getChildElement().getAttribute("_horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				context.getRootWidget().setControlBar(new WizardControlBar(vertical), position, horizontalAlign);
			}
			WizardControlBar controlBar = context.getRootWidget().getControlBar();
			
			processControlBarAttributes(context, controlBar);
		}

		private void processControlBarAttributes(WidgetChildProcessorContext<Wizard> context,
                WizardControlBar controlBar)
        {
	        String spacing = context.getChildElement().getAttribute("_spacing");
			if (!StringUtils.isEmpty(spacing))
			{
				controlBar.setSpacing(Integer.parseInt(spacing));
			}
			
			String buttonsWidth = context.getChildElement().getAttribute("_buttonsWidth");
			if (!StringUtils.isEmpty(buttonsWidth))
			{
				controlBar.setButtonsWidth(buttonsWidth);
			}

			String buttonsHeight = context.getChildElement().getAttribute("_buttonsHeight");
			if (!StringUtils.isEmpty(buttonsHeight))
			{
				controlBar.setButtonsHeight(buttonsHeight);
			}

			String buttonsStyle = context.getChildElement().getAttribute("_buttonsStyle");
			if (!StringUtils.isEmpty(buttonsStyle))
			{
				controlBar.setButtonsStyle(buttonsStyle);
			}

			String backLabel = context.getChildElement().getAttribute("_backLabel");
			if (!StringUtils.isEmpty(backLabel))
			{
				controlBar.setBackLabel(ScreenFactory.getInstance().getDeclaredMessage(backLabel));
			}

			String nextLabel = context.getChildElement().getAttribute("_nextLabel");
			if (!StringUtils.isEmpty(nextLabel))
			{
				controlBar.setNextLabel(ScreenFactory.getInstance().getDeclaredMessage(nextLabel));
			}

			String cancelLabel = context.getChildElement().getAttribute("_cancelLabel");
			if (!StringUtils.isEmpty(cancelLabel))
			{
				controlBar.setCancelLabel(ScreenFactory.getInstance().getDeclaredMessage(cancelLabel));
			}

			String finishLabel = context.getChildElement().getAttribute("_finishLabel");
			if (!StringUtils.isEmpty(finishLabel))
			{
				controlBar.setFinishLabel(ScreenFactory.getInstance().getDeclaredMessage(finishLabel));
			}

			String backOrder = context.getChildElement().getAttribute("_backOrder");
			if (!StringUtils.isEmpty(backOrder))
			{
				controlBar.setBackOrder(Integer.parseInt(backOrder));
			}

			String nextOrder = context.getChildElement().getAttribute("_nextOrder");
			if (!StringUtils.isEmpty(nextOrder))
			{
				controlBar.setNextOrder(Integer.parseInt(nextOrder));
			}

			String cancelOrder = context.getChildElement().getAttribute("_cancelOrder");
			if (!StringUtils.isEmpty(cancelOrder))
			{
				controlBar.setCancelOrder(Integer.parseInt(cancelOrder));
			}

			String finishOrder = context.getChildElement().getAttribute("_finishOrder");
			if (!StringUtils.isEmpty(finishOrder))
			{
				controlBar.setFinishOrder(Integer.parseInt(finishOrder));
			}
        }
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	public static class ControlBarCommandsProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagChildren({
			@TagChild(ControlBarCommandProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	public static class ControlBarCommandProcessor extends WidgetChildProcessor<Wizard>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="onCommand", required=true)
		})
		public void processChildren(WidgetChildProcessorContext<Wizard> context) throws InterfaceConfigException 
		{
			String id = context.getChildElement().getAttribute("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getAttribute("_label"));
			int order = Integer.parseInt(context.getChildElement().getAttribute("_order"));
			
			final Event commandEvent = EvtBind.getWidgetEvent(context.getChildElement(), "onCommand");
			
			WizardCommandHandler handler = new WizardCommandHandler()
			{
				public void onCommand(WizardCommandEvent event)
				{
					Events.callEvent(commandEvent, event);
				}
			};
			
			context.getRootWidget().getControlBar().addCommand(id, label, handler, order);
			
			String styleName = context.getChildElement().getAttribute("_styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				context.getRootWidget().getControlBar().getCommand(id).setStyleName(styleName);
			}
			String width = context.getChildElement().getAttribute("_width");
			if (!StringUtils.isEmpty(width))
			{
				context.getRootWidget().getControlBar().getCommand(id).setWidth(width);
			}
			String height = context.getChildElement().getAttribute("_height");
			if (!StringUtils.isEmpty(height))
			{
				context.getRootWidget().getControlBar().getCommand(id).setHeight(height);
			}
			
		}
	}
}
