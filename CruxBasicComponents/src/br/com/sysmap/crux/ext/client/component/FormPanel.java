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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

/**
 * Represents a FormPanel.
 * @author Thiago Bustamante
 */
public class FormPanel extends SimplePanel
{
	protected com.google.gwt.user.client.ui.FormPanel formPanelWidget;
	
	public FormPanel(String id)
	{
		this(id, new com.google.gwt.user.client.ui.FormPanel());
	}
	
	public FormPanel(String id, Element element)
	{
		this(id, createFormWidget(element));
	}

	public FormPanel(String id, NamedFrame namedFrame)
	{
		this(id, new com.google.gwt.user.client.ui.FormPanel(
				(com.google.gwt.user.client.ui.NamedFrame)namedFrame.getComponentWidget(namedFrame)));
	}

	public FormPanel(String id, String target)
	{
		this(id, new com.google.gwt.user.client.ui.FormPanel(target));
	}

	protected FormPanel(String id, com.google.gwt.user.client.ui.FormPanel widget) 
	{
		super(id, widget);
		this.formPanelWidget = widget;
	}

	protected static com.google.gwt.user.client.ui.FormPanel createFormWidget(Element element)
	{
		String target = element.getAttribute("_target");
		if (target != null && target.length() >0)
		{
			return new com.google.gwt.user.client.ui.FormPanel(target);
		}
		else
		{
			return new com.google.gwt.user.client.ui.FormPanel();
		}
	}
	
	/**
	 * Gets the 'action' associated with this form. This is the URL to which it
	 * will be submitted.
	 * 
	 * @return the form's action
	 */
	public String getAction() 
	{
		return formPanelWidget.getAction();
	}

	/**
	 * Gets the encoding used for submitting this form. This should be either
	 * {@link #ENCODING_MULTIPART} or {@link #ENCODING_URLENCODED}.
	 * 
	 * @return the form's encoding
	 */
	public String getEncoding() 
	{
		return formPanelWidget.getEncoding();
	}

	/**
	 * Gets the HTTP method used for submitting this form. This should be either
	 * {@link #METHOD_GET} or {@link #METHOD_POST}.
	 * 
	 * @return the form's method
	 */
	public String getMethod() 
	{
		return formPanelWidget.getMethod();
	}

	/**
	 * Gets the form's 'target'. This is the name of the {@link NamedFrame} that
	 * will receive the results of submission, or <code>null</code> if none has
	 * been specified.
	 * 
	 * @return the form's target.
	 */
	public String getTarget()
	{
		return formPanelWidget.getTarget();
	}


	/**
	 * Resets the form, clearing all fields.
	 */
	public void reset() 
	{
		formPanelWidget.reset();
	}

	/**
	 * Sets the 'action' associated with this form. This is the URL to which it
	 * will be submitted.
	 * 
	 * @param url the form's action
	 */
	public void setAction(String url) 
	{
		formPanelWidget.setAction(url);
	}

	/**
	 * Sets the encoding used for submitting this form. This should be either
	 * {@link #ENCODING_MULTIPART} or {@link #ENCODING_URLENCODED}.
	 * 
	 * @param encodingType the form's encoding
	 */
	public void setEncoding(String encodingType) 
	{
		formPanelWidget.setEncoding(encodingType);
	}

	/**
	 * Sets the HTTP method used for submitting this form. This should be either
	 * {@link #METHOD_GET} or {@link #METHOD_POST}.
	 * 
	 * @param method the form's method
	 */
	public void setMethod(String method) 
	{
		formPanelWidget.setMethod(method);
	}

	/**
	 * Submits the form.
	 * 
	 * <p>
	 * The FormPanel must <em>not</em> be detached (i.e. removed from its parent
	 * or otherwise disconnected from a {@link RootPanel}) until the submission is
	 * complete. Otherwise, notification of submission will fail.
	 * </p>
	 */
	public void submit() 
	{
		formPanelWidget.submit();
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		
		String method = element.getAttribute("_method");
		if (method != null && method.length() > 0)
		{
			setMethod(method);
		}
		String encoding = element.getAttribute("_encoding");
		if (encoding != null && encoding.length() > 0)
		{
			setEncoding(encoding);
		}
		String action = element.getAttribute("_action");
		if (action != null && action.length() > 0)
		{
			setAction(action);
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		final Event eventSubmitComplete = EvtBind.getComponentEvent(element, "_onsubmitcomplete");
		if (eventSubmitComplete != null)
		{
			formPanelWidget.addSubmitCompleteHandler(new SubmitCompleteHandler()
			{
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) 
				{
					EventFactory.callEvent(eventSubmitComplete, getId());
				}
			});
		}
		
		final Event eventSubmit = EvtBind.getComponentEvent(element, "_onsubmit");
		if (eventSubmitComplete != null)
		{
			formPanelWidget.addSubmitHandler(new SubmitHandler()
			{
				@Override
				public void onSubmit(SubmitEvent event) 
				{
					EventFactory.callEvent(eventSubmit, getId());
				}
			});
		}
	}
}
