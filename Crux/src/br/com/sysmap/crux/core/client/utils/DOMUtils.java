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
package br.com.sysmap.crux.core.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * 
 * @author Thiago
 */
public class DOMUtils {

	/**
	 * Implementation of DOM's getElementsByTagName
	 * @param tagName
	 * @return
	 */
	public static Element[] getElementsByTagName(String tagName)
	{
		JavaScriptObject elements = getElementsByTagNameInternal(tagName);
		int length = getArrayLength(elements);
		Element[] result = new Element[length];
		for (int i=0; i<length; i++)
		{
			result[i] = getArrayElement(elements, i);
		}
		return result;
	}
	
	/**
	 * List all elements with the specified class. Try first the implementation of DOM's getElementsByClassName. 
	 * If not present try to use XPATH. If xpath is not supported searches DOM structure.
	 * 
	 * @param tagName
	 * @return
	 */
	public static Element[] getElementsByClassName(String className, Element parentElement)
	{
		JavaScriptObject elements = getElementsByClassNameInternal(className, parentElement);
		int length = getArrayLength(elements);
		Element[] result = new Element[length];
		for (int i=0; i<length; i++)
		{
			result[i] = getArrayElement(elements, i);
		}
		return result;
	}
	
	private static native int getArrayLength(JavaScriptObject array)/*-{
		return array.length;
	}-*/;
	
	private static native Element getArrayElement(JavaScriptObject array, int position)/*-{
		return (position>=0 && position<array.length?array[position]:null);
	}-*/;

	private static native JavaScriptObject getElementsByTagNameInternal(String tagName)/*-{
		return $doc.getElementsByTagName(tagName);
	}-*/;
	
	private static native JavaScriptObject getElementsByClassNameInternal(String className, Element parentElement)/*-{
		if ((parentElement && parentElement.getElementsByClassName) || $doc.getElementsByClassName)
		{
			  return (parentElement || $doc).getElementsByClassName(className);
		}
		else if (!!document.evaluate) 
		{
			  var expression = ".//*[contains(concat(' ', @class, ' '), ' " + className + " ')]";
              var results = [];
		      var query = $doc.evaluate(expression, parentElement || $doc, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
		      for (var i = 0, length = query.snapshotLength; i < length; i++)
		           results.push(query.snapshotItem(i));
		      return results;
		}
		else 
		{
			  var children = (parentElement || $doc.body).getElementsByTagName('*');
			  var elements = [], child, pattern = new RegExp("(^|\\s)" + className + "(\\s|$)");
			  for (var i = 0, length = children.length; i < length; i++) {
			    child = children[i];
			    var elementClassName = child.className;
			    if (elementClassName.length == 0) continue;
			    if (elementClassName == className || elementClassName.match(pattern))
			      elements.push(child);
			  }
			  return elements;
		}
	}-*/;

	public static native String getDocumentName()/*-{
		return $doc.location.href;
	}-*/;
}
