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
package org.cruxframework.crux.core.client.utils;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScriptTagHandler
{
	private static ScriptInjector injector = null;
	private static ArrayList<Element> scripts;

	/**
	 * Evaluates any script inserted on the given element using element.innerHTML.
	 * @param element
	 */
	public static void evaluateScripts(Element element, ScriptLoadCallback callback)
	{
		if (scripts == null)
		{
			 scripts = new ArrayList<Element>();
		}
		NodeList<Element> scriptElements = element.getElementsByTagName("script");
		
		if (scriptElements != null)
		{
			for (int i = 0; i < scriptElements.getLength(); i++)
			{
				scripts.add(scriptElements.getItem(i));
			}
		}
		processNextScript(callback);
	}
	
	private static void processNextScript(final ScriptLoadCallback callback)
	{
		if (scripts.size() > 0)
		{
			final ScriptElement script = scripts.remove(0).cast();
			final ScriptElement cloneScript = cloneScript(script); // if cloned using node.cloneNode(), browser does not evaluate the 
																   // inner javascript when tag is attached
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					processScriptTag(script, cloneScript, callback);
				}
			});
		}
		else if (callback != null)
		{
			callback.onLoaded();
		}
	}
	
	private static void processScriptTag(final ScriptElement script, final ScriptElement cloneScript, final ScriptLoadCallback callback)
    {
		handleDocWriteFunction();
		String src = script.getSrc();
		ScriptLoadCallback tagCallback = new ScriptLoadCallback()
		{
			@Override
			public void onLoaded()
			{
				String content = getDocWrittenContent();
				restoreDocWriteFunction();
				processNextScript(callback);
				final DivElement wrapperElement = Document.get().createDivElement();
				wrapperElement.setInnerHTML(content);
				cloneScript.getParentElement().insertAfter(wrapperElement, cloneScript);
				Scheduler.get().scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						evaluateScripts(wrapperElement, null);
					}
				});
			}
		};

		// If it is a cross domain script, the content loading is assynchronous
		boolean xDomain = isXDomain(src);
		if (xDomain)
		{
			handleXDomainScriptLoad(cloneScript, tagCallback);
		}
		
        script.getParentElement().replaceChild(cloneScript, script);
        
        if (!xDomain)
        {
        	tagCallback.onLoaded();
        }
    }

	private static native boolean isXDomain(String src)/*-{
		if (!src)
		{
			return false;
		}
		var parts = /^(\w+:)?\/\/([^\/?#]+)/.exec(src);
		return parts && ( parts[1] && parts[1] != location.protocol || parts[2] != location.host );
	}-*/;
	
	private static ScriptElement cloneScript(ScriptElement script)
	{
		Document doc = Document.get();
		ScriptElement cloneScript = doc.createScriptElement();
		cloneScript.setType("text/javascript");
		cloneScript.setLang("javascript");
		if (script.hasAttribute("src"))
		{
			cloneScript.setSrc(script.getSrc());
		}
		else
		{
			getScriptInjector().copyScriptContent(script, cloneScript);
		}
		
		return cloneScript;
	}
	
	private static native void handleDocWriteFunction()/*-{
		$wnd.__crux_content = '';
		$wnd.__crux_originalWrite = $doc.write;
		$doc.write = function(s) {
		    $wnd.__crux_content += s;
		};
	}-*/;

	
	private static native String getDocWrittenContent()/*-{
		return $wnd.__crux_content;
	}-*/;
	
	
	private static native void restoreDocWriteFunction()/*-{
		$wnd.__crux_content = '';
		$doc.write = $wnd.__crux_originalWrite;
	}-*/;

	private static native void handleXDomainScriptLoad(ScriptElement script, ScriptLoadCallback callback)/*-{
		script.onload = script.onreadystatechange = function(){
			if (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") {
				if (callback)
				{
					callback.@org.cruxframework.crux.core.client.utils.ScriptTagHandler.ScriptLoadCallback::onLoaded()();
				}

				script.onload = script.onreadystatechange = null;
				script.parentNode.removeChild( script );
			}
		};		
	}-*/;

	private static ScriptInjector getScriptInjector()
	{
		if (injector == null)
		{
			injector = GWT.create(ScriptInjector.class);
		}
		return injector;
	}

	public static interface ScriptLoadCallback
	{
		void onLoaded();
	}
	
	static class ScriptInjector
	{
		protected void copyScriptContent(ScriptElement script, ScriptElement cloneScript)
		{
			String text = script.getInnerHTML();
			if (StringUtils.isEmpty(text))
			{
				text = script.getText();
			}
			cloneScript.appendChild(Document.get().createTextNode(text));
		}
	}
	
	static class IEScriptInjector extends ScriptInjector
	{
		protected void copyScriptContent(ScriptElement script, ScriptElement cloneScript)
		{
			cloneScript.setText(script.getText());
		}
	}
}
