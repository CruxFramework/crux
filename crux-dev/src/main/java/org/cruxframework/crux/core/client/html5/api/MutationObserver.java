/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.client.html5.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Node;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

/**
 * MutationObserver provides developers a way to react to changes in a DOM. 
 * It is designed as a replacement for Mutation Events defined in the DOM3 Events specification.
 * @author Thiago da Rosa de Bustamante
 */
public class MutationObserver extends JavaScriptObject
{
	/**
	 * Default constructor
	 */
	protected MutationObserver(){}
	
	/**
	 * Stops the MutationObserver instance from receiving notifications of DOM mutations. 
	 * Until the observe() method is used again, observer's callback will not be invoked.
	 */
	public final native void disconnect()/*-{
		this.disconnect();
	}-*/;
	
	/**
	 * Observe the given node for modifications
	 * 
	 * Adding an observer to an element is just like addEventListener, if you observe the element 
	 * multiple times it does not make a difference. Meaning if you observe element twice, the 
	 * observe callback does not fire twice, nor will you have to run disconnect() twice. In other words, 
	 * once an element is observed, observing it again with the same observer instance will do nothing.
	 * However if the callback object is different it will of course add another observer to it.
	 * @param node the node to be monitored.
	 * @param children if true, start monitoring changes on children list.
	 * @param attributes if true, start monitoring changes on node attributes.
	 * @param characterData if true, start monitoring changes on node data.
	 */
	public final native void observe(Node node, boolean children, boolean attributes, boolean characterData)/*-{
		this.observe(node, {'attributes': attributes, 'childList': children, 'characterData': characterData});	
	}-*/;
	
	/**
	 * Observe the given node for modifications on its attributes.
	 * @param node the node to be monitored.
	 */
	public final void observeAttributes(Node node)
	{
		observe(node, false, true, false);
	}

	/**
	 * Observe the given node for modifications on its children.
	 * @param node the node to be monitored.
	 */
	public final void observeChildren(Node node)
	{
		observe(node, true, false, false);
	}
	
	/**
	 * Verify if the current browser supports the MutationObserver API.
	 * @return true if supported.
	 */
	public static native boolean isSupported()/*-{
		return !!$wnd.MutationObserver;
	}-*/;
	
	/**
	 * Verify if the current browser supports the WeakMap API.
	 * @return true if supported.
	 */
	public static native boolean isWeakMapSupported()/*-{
		return !!$wnd.WeakMap;
	}-*/;
	
	/**
	 * If current browser supports the MutationObserver API, create a new Observer.
	 * @param callback A handler for the mutations.
	 * @return the MutationObserver
	 */
	public static void load(final Callback callback, final LoadCallback loadCallback)
	{
		if (isSupported())
		{
			loadCallback.onLoaded(create(callback));
		}
		else
		{
			if(!isWeakMapSupported())
			{
				injectPolyfill(Polyfill.INSTANCE.weakMap(), new ResourceCallback<TextResource>()
				{
					@Override
					public void onSuccess(TextResource resource)
					{
						injectPolyfill(Polyfill.INSTANCE.mutationObserver(), new ResourceCallback<TextResource>()
						{
							@Override
							public void onSuccess(TextResource resource)
							{
								loadCallback.onLoaded(create(callback));
							}
							
							@Override
							public void onError(ResourceException e)
							{
								loadCallback.onError(e.getMessage());
							}
						});
					}
					
					@Override
					public void onError(ResourceException e)
					{
						loadCallback.onError(e.getMessage());
					}
				});
			}
			else
			{
				injectPolyfill(Polyfill.INSTANCE.mutationObserver(), new ResourceCallback<TextResource>()
				{
					@Override
					public void onSuccess(TextResource resource)
					{
						loadCallback.onLoaded(create(callback));
					}
					
					@Override
					public void onError(ResourceException e)
					{
						loadCallback.onError(e.getMessage());
					}
				});
			}
		}
	}
	
	private static void injectPolyfill(ExternalTextResource resource, final ResourceCallback<TextResource> resourceCallback)
	{
        try
        {
        	resource.getText(new ResourceCallback<TextResource>()
            {
            	@Override
            	public void onError(ResourceException e)
            	{
            		resourceCallback.onError(e);						
            	}
            	
            	@Override
            	public void onSuccess(final TextResource resource)
            	{
            		ScriptInjector.fromString(resource.getText()).setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
            		Scheduler.get().scheduleDeferred(new ScheduledCommand()
            		{
            			@Override
            			public void execute()
            			{
            				resourceCallback.onSuccess(resource);	
            			}
            		});
            	}
            });
        }
        catch (ResourceException e)
        {
        	resourceCallback.onError(e);						
        }
	}
	
	protected static native MutationObserver create(Callback callback)/*-{
		return new $wnd.MutationObserver(function (mutations, observer) {
			callback.@org.cruxframework.crux.core.client.html5.api.MutationObserver.Callback::onChanged(Lcom/google/gwt/core/client/JsArray;Lorg/cruxframework/crux/core/client/html5/api/MutationObserver;)(mutations, observer);
        });
	}-*/;
	
	/**
	 * Define an handler for mutations observed.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback 
	{
		/**
		 * The observer will call this method when a mutation occurs on the observed node.
		 * @param mutations An array containing all the mutation records.
		 * @param observer the MutationObserver instance.
		 */
		void onChanged(JsArray<MutationRecord> mutations, MutationObserver observer);
	}
	
	/**
	 * Interface used to load a new MutationObserver object.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface LoadCallback
	{
		/**
		 * Called when MutationObserver is not supported on browser
		 * @param message error message 
		 */
		void onError(String message);
		/**
		 * Called when a MutationObserver is created successfully
		 * @param mutationObserver observer created
		 */
		void onLoaded(MutationObserver mutationObserver);
	}
	
	/**
	 * MutationRecord is the object that will be passed to the observer's callback.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class MutationRecord extends JavaScriptObject
	{
		/**
		 * Default Constructor
		 */
		protected MutationRecord() {}
		
		/**
		 * Return the nodes added. Will be an empty NodeList if no nodes were added.
		 * @return the nodes added.
		 */
		public final native JsArray<Node> getAddedNodes()/*-{
			return this.addedNodes;
		}-*/;
		
		/**
		 * Returns the local name of the changed attribute, or null.
		 * @return the attribute local name.
		 */
		public final native String getAttributeName()/*-{
			return this.attributeName;
		}-*/;
		
		/**
		 * Returns the namespace of the changed attribute, or null.
		 * @return the attribute namespace
		 */
		public final native String getAttributeNamespace()/*-{
			return this.attributeNamespace;
		}-*/;

		/**
		 * Return the next sibling of the added or removed nodes, or null.
		 * @return the next sibling node.
		 */
		public final native Node getNextSibling()/*-{
			return this.nextSibling;
		}-*/;

		/**
		 * The return value depends on the type. For attributes, it is the value of the changed 
		 * attribute before the change. For characterData, it is the data of the changed node 
		 * before the change. For childList, it is null.
		 * @return the value before the mutation occurs.
		 */
		public final native String getOldValue()/*-{
			return this.oldValue;
		}-*/;

		/**
		 * Return the previous sibling of the added or removed nodes, or null.
		 * @return the previous sibling node.
		 */
		public final native Node getPreviousSibling()/*-{
			return this.previousSibling;
		}-*/;
		
		/**
		 * Return the nodes removed. Will be an empty NodeList if no nodes were removed.
		 * @return the nodes removed.
		 */
		public final native JsArray<Node> getRemovedNodes()/*-{
			return this.removedNodes;
		}-*/;
		
		/**
		 * Returns the node the mutation affected, depending on the type. For attributes, it is 
		 * the element whose attribute changed. For characterData, it is the CharacterData node. 
		 * For childList, it is the node whose children changed.
		 * @return the node affected.
		 */
		public final native Node getTarget()/*-{
			return this.target;
		}-*/;
		
		/**
		 * Returns attributes if the mutation was an attribute mutation, characterData if 
		 * it was a mutation to a CharacterData node, and childList if it was a mutation 
		 * to the tree of nodes.
		 * @return mutation type.
		 */
		public final native String getType()/*-{
			return this.type;
		}-*/;
	}
}
