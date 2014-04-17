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

import org.cruxframework.crux.core.client.Crux;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Contains common operations for handling styles on HTML elements.
 *  
 * @author Gesse S. F. Dafe
 */
public class StyleUtils
{
	/**
	 * Adds a dependent style name by specifying the style name's suffix. The
	 * actual form of the style name that is added is:
	 * 
	 * <pre class="code"> getStylePrimaryName() + '-' + styleSuffix </pre>
	 * 
	 * @param styleSuffix
	 *            the suffix of the dependent style to be added.
	 * @see #setStylePrimaryName(String)
	 * @see #removeStyleDependentName(String)
	 * @see #addStyleName(String)
	 */
	public static void addStyleDependentName(Element elem, String styleSuffix)
	{
		addStyleName(elem, getStylePrimaryName(elem) + '-' + styleSuffix);
	}

	/**
	 * Removes a dependent style name by specifying the style name's suffix.
	 * 
	 * @param styleSuffix
	 *            the suffix of the dependent style to be removed
	 * @see #setStylePrimaryName(Element, String)
	 * @see #addStyleDependentName(String)
	 * @see #addStyleName(String)
	 */
	public static void removeStyleDependentName(Element elem, String styleSuffix)
	{
		removeStyleName(elem, getStylePrimaryName(elem) + '-' + styleSuffix);
	}

	/**
	 * Removes a style name. This method is typically used to remove secondary
	 * style names, but it can be used to remove primary stylenames as well.
	 * That use is not recommended.
	 * @param elem 
	 * 
	 * @param style
	 *            the secondary style name to be removed
	 * @see #addStyleName(String)
	 */
	public static void removeStyleName(Element elem, String style)
	{
		setStyleName(elem, style, false);
	}

	/**
	 * Adds a secondary or dependent style name to this object. A secondary
	 * style name is an additional style name that is, in HTML/CSS terms,
	 * included as a space-separated token in the value of the CSS
	 * <code>class</code> attribute for this object's root element.
	 * 
	 * <p>
	 * The most important use for this method is to add a special kind of
	 * secondary style name called a <i>dependent style name</i>. To add a
	 * dependent style name, use {@link #addStyleDependentName(String)}, which
	 * will prefix the 'style' argument with the result of
	 * {@link #getStylePrimaryName()} (followed by a '-'). For example, suppose
	 * the primary style name is <code>gwt-TextBox</code>. If the following
	 * method is called as <code>obj.setReadOnly(true)</code>:
	 * </p>
	 * 
	 * <pre class="code"> public void setReadOnly(boolean readOnly) {
	 * isReadOnlyMode = readOnly;
	 * 
	 * // Create a dependent style name. String readOnlyStyle = "readonly";
	 * 
	 * if (readOnly) { addStyleDependentName(readOnlyStyle); } else {
	 * removeStyleDependentName(readOnlyStyle); } }</pre>
	 * 
	 * <p>
	 * then both of the CSS style rules below will be applied:
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * // This rule is based on the primary style name and is always active.
	 * .gwt-TextBox { font-size: 12pt; }
	 * 
	 * // This rule is based on a dependent style name that is only active //
	 * when the widget has called addStyleName(getStylePrimaryName() + //
	 * "-readonly"). .gwt-TextBox-readonly { background-color: lightgrey;
	 * border: none; }</pre>
	 * 
	 * <p>
	 * Dependent style names are powerful because they are automatically updated
	 * whenever the primary style name changes. Continuing with the example
	 * above, if the primary style name changed due to the following call:
	 * </p>
	 * 
	 * <pre class="code">setStylePrimaryName("my-TextThingy");</pre>
	 * 
	 * <p>
	 * then the object would be re-associated with following style rules,
	 * removing those that were shown above.
	 * </p>
	 * 
	 * <pre class="code"> .my-TextThingy { font-size: 20pt; }
	 * 
	 * .my-TextThingy-readonly { background-color: red; border: 2px solid
	 * yellow; }</pre>
	 * 
	 * <p>
	 * Secondary style names that are not dependent style names are not
	 * automatically updated when the primary style name changes.
	 * </p>
	 * 
	 * @param elem
	 * 
	 * @param style
	 *            the secondary style name to be added
	 * @see UIObject
	 * @see #removeStyleName(String)
	 */
	public static void addStyleName(Element elem, String style)
	{
		setStyleName(elem, style, true);
	}

	/**
	 * This convenience method adds or removes a style name for a given element.
	 * This method is typically used to add and remove secondary style names,
	 * but it can be used to remove primary style names as well, but that is not
	 * recommended. See {@link #setStyleName(String)} for a description of how
	 * primary and secondary style names are used.
	 * 
	 * @param elem
	 *            the element whose style is to be modified
	 * @param style
	 *            the secondary style name to be added or removed
	 * @param add
	 *            <code>true</code> to add the given style, <code>false</code>
	 *            to remove it
	 */
	private static void setStyleName(Element elem, String style, boolean add)
	{
		if (elem == null)
		{
			throw new IllegalArgumentException(Crux.getMessages().nullElementAtSetStyleName());
		}

		style = style.trim();
		if (style.length() == 0)
		{
			throw new IllegalArgumentException(Crux.getMessages().emptyStringAsStyleNameValue());
		}

		// Get the current style string.
		String oldStyle = getStyleName(elem);
		int idx = oldStyle.indexOf(style);

		// Calculate matching index.
		while (idx != -1)
		{
			if (idx == 0 || oldStyle.charAt(idx - 1) == ' ')
			{
				int last = idx + style.length();
				int lastPos = oldStyle.length();
				if ((last == lastPos) || ((last < lastPos) && (oldStyle.charAt(last) == ' ')))
				{
					break;
				}
			}
			idx = oldStyle.indexOf(style, idx + 1);
		}

		if (add)
		{
			// Only add the style if it's not already present.
			if (idx == -1)
			{
				if (oldStyle.length() > 0)
				{
					oldStyle += " ";
				}
				DOM.setElementProperty(elem.<com.google.gwt.user.client.Element> cast(), "className", oldStyle + style);
			}
		}
		else
		{
			// Don't try to remove the style if it's not there.
			if (idx != -1)
			{
				// Get the leading and trailing parts, without the removed name.
				String begin = oldStyle.substring(0, idx).trim();
				String end = oldStyle.substring(idx + style.length()).trim();

				// Some contortions to make sure we don't leave extra spaces.
				String newClassName;
				if (begin.length() == 0)
				{
					newClassName = end;
				}
				else if (end.length() == 0)
				{
					newClassName = begin;
				}
				else
				{
					newClassName = begin + " " + end;
				}

				DOM.setElementProperty(elem.<com.google.gwt.user.client.Element> cast(), "className", newClassName);
			}
		}
	}

	/**
	 * Gets the element's primary style name.
	 * 
	 * @param elem
	 *            the element whose primary style name is to be retrieved
	 * @return the element's primary style name
	 */
	private static String getStylePrimaryName(Element elem)
	{
		String fullClassName = getStyleName(elem);
		int spaceIdx = fullClassName.indexOf(' ');
		if (spaceIdx >= 0)
		{
			return fullClassName.substring(0, spaceIdx);
		}
		return fullClassName;
	}

	/**
	 * Gets all of the element's style names, as a space-separated list.
	 * 
	 * @param elem
	 *            the element whose style is to be retrieved
	 * @return the objects's space-separated style names
	 */
	private static String getStyleName(Element elem)
	{
		return DOM.getElementProperty(elem.<com.google.gwt.user.client.Element> cast(), "className");
	}

	public static void addStyleProperty(Element element, String camelizedName, String value)
	{
		assert(element != null && camelizedName != null && value != null);
		try
		{
			addStylePropertyNative(element, camelizedName.trim(), value.trim());
		}
		catch (Throwable e) 
		{
			Crux.getErrorHandler().handleError(Crux.getMessages().styleErrorInvalidProperty(camelizedName, value));
			throw new RuntimeException(Crux.getMessages().styleErrorInvalidProperty(camelizedName, value));
		}
	}

	private static native void addStylePropertyNative(Element element, String camelizedName, String value)/*-{
		element.style[camelizedName] = value;
	}-*/;
}
