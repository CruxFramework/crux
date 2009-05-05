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
package br.com.sysmap.crux.core.client.component;

import com.google.gwt.dom.client.Element;


/**
 * Base class for any interface object in Crux.
 * @author Thiago Bustamante
 */
public class UIObject 
{
	protected com.google.gwt.user.client.ui.UIObject uiobject;

	protected UIObject(com.google.gwt.user.client.ui.UIObject uiobject) 
	{
		if (uiobject == null) throw new NullPointerException();
		this.uiobject = uiobject;
	}

	/**
	 * Set component's width
	 * @return
	 */
	public void setWidth(String width) 
	{
		uiobject.setWidth(width);
	}

	/**
	 * Return component's className
	 * @return
	 */
	public void getStyleName()
	{
		uiobject.getStyleName();
	}

	/**
	 * Set component's className
	 * @return
	 */
	public void setStyleName(String className) 
	{
		uiobject.setStyleName(className);
	}


	/**
	 * Gets the primary style name associated with the object.
	 * 
	 * @return the object's primary style name
	 * @see #setStyleName(String)
	 * @see #addStyleName(String)
	 * @see #removeStyleName(String)
	 */
	public String getStylePrimaryName() 
	{	
		return uiobject.getStylePrimaryName();
	}

	/**
	 * Set component's height
	 * @return
	 */
	public void setHeight(String height) 
	{
		uiobject.setHeight(height);
	}

	/**
	 * Return the component's visibility
	 * @return
	 */
	public boolean isVilible()
	{
		return uiobject.isVisible();
	}

	/**
	 * Set the component's visibility
	 * @return
	 */
	public void setVisible(boolean visible)
	{
		uiobject.setVisible(visible);
	}

	/**
	 * Return component's tooltip
	 * @return
	 */
	public String getTitle() 
	{
		return uiobject.getTitle();
	}

	/**
	 * Set component's tooltip
	 * @return
	 */
	public void setTitle(String tooltip) 
	{
		uiobject.setTitle(tooltip);
	}

	/**
	 * Return the component's offset width in pixels
	 * @return
	 */
	public int getOffsetWidth()
	{
		return uiobject.getOffsetWidth();
	}

	/**
	 * Return the component's offset height in pixels
	 * @return
	 */
	public int getOffsetHeight() 
	{	
		return uiobject.getOffsetHeight();
	}

	/**
	 * Return the object's absolute left position in pixels
	 * @return
	 */
	public int getAbsoluteLeft() 
	{
		return uiobject.getAbsoluteLeft();
	}

	/**
	 * Return the object's absolute top position in pixels
	 * @return
	 */
	public int getAbsoluteTop() 
	{
		return uiobject.getAbsoluteTop();
	}

	/**
	 * Adds a dependent style name by specifying the style name's suffix. The
	 * actual form of the style name that is added is:
	 * 
	 * <pre class="code">
	 * getStylePrimaryName() + '-' + styleSuffix
	 * </pre>
	 * 
	 * @param styleSuffix the suffix of the dependent style to be added.
	 * @see #setStylePrimaryName(String)
	 * @see #removeStyleDependentName(String)
	 * @see #addStyleName(String)
	 */
	public void addStyleDependentName(String styleSuffix) 
	{
		uiobject.addStyleDependentName(styleSuffix);
	}

	/**
	 * Adds a secondary or dependent style name to this object. A secondary style
	 * name is an additional style name that is, in HTML/CSS terms, included as a
	 * space-separated token in the value of the CSS <code>class</code>
	 * attribute for this object's root element.
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
	 * <pre class="code">
	 * public void setReadOnly(boolean readOnly) {
	 *   isReadOnlyMode = readOnly;
	 *   
	 *   // Create a dependent style name.
	 *   String readOnlyStyle = "readonly";
	 *    
	 *   if (readOnly) {
	 *     addStyleDependentName(readOnlyStyle);
	 *   } else {
	 *     removeStyleDependentName(readOnlyStyle);
	 *   }
	 * }</pre>
	 * 
	 * <p>
	 * then both of the CSS style rules below will be applied:
	 * </p>
	 * 
	 * <pre class="code">
	 *
	 * // This rule is based on the primary style name and is always active.
	 * .gwt-TextBox {
	 *   font-size: 12pt;
	 * }
	 * 
	 * // This rule is based on a dependent style name that is only active
	 * // when the widget has called addStyleName(getStylePrimaryName() +
	 * // "-readonly").
	 * .gwt-TextBox-readonly {
	 *   background-color: lightgrey;
	 *   border: none;
	 * }</pre>
	 * 
	 * <p>
	 * Dependent style names are powerful because they are automatically updated
	 * whenever the primary style name changes. Continuing with the example above,
	 * if the primary style name changed due to the following call:
	 * </p>
	 * 
	 * <pre class="code">setStylePrimaryName("my-TextThingy");</pre>
	 * 
	 * <p>
	 * then the object would be re-associated with following style rules, removing
	 * those that were shown above.
	 * </p>
	 * 
	 * <pre class="code">
	 * .my-TextThingy {
	 *   font-size: 20pt;
	 * }
	 * 
	 * .my-TextThingy-readonly {
	 *   background-color: red;
	 *   border: 2px solid yellow;
	 * }</pre>
	 * 
	 * <p>
	 * Secondary style names that are not dependent style names are not
	 * automatically updated when the primary style name changes.
	 * </p>
	 * 
	 * @param style the secondary style name to be added
	 * @see UIObject
	 * @see #removeStyleName(String)
	 */
	public void addStyleName(String style) 
	{
		uiobject.addStyleName(style);
	}

	/**
	 * Removes a dependent style name by specifying the style name's suffix.
	 * 
	 * @param styleSuffix the suffix of the dependent style to be removed
	 * @see #setStylePrimaryName(Element, String)
	 * @see #addStyleDependentName(String)
	 * @see #addStyleName(String)
	 */
	public void removeStyleDependentName(String styleSuffix) 
	{
		uiobject.removeStyleDependentName(styleSuffix);
	}

	/**
	 * Removes a style name. This method is typically used to remove secondary
	 * style names, but it can be used to remove primary stylenames as well. That
	 * use is not recommended.
	 * 
	 * @param style the secondary style name to be removed
	 * @see #addStyleName(String)
	 */
	public void removeStyleName(String style) 
	{
		uiobject.removeStyleName(style);
	}

	/**
	 * Sets the object's size, in pixels, not including decorations such as
	 * border, margin, and padding.
	 * 
	 * @param width the object's new width, in pixels
	 * @param height the object's new height, in pixels
	 */
	public void setPixelSize(int width, int height) 
	{
		uiobject.setPixelSize(width, height);
	}


	/**
	 * Sets the object's size. This size does not include decorations such as
	 * border, margin, and padding.
	 * 
	 * @param width the object's new width, in CSS units (e.g. "10px", "1em")
	 * @param height the object's new height, in CSS units (e.g. "10px", "1em")
	 */
	public void setSize(String width, String height) 
	{
		uiobject.setSize(width, height);
	}

	/**
	 * Sets the object's primary style name and updates all dependent style names.
	 * 
	 * @param style the new primary style name
	 * @see #addStyleName(String)
	 * @see #removeStyleName(String)
	 */
	public void setStylePrimaryName(String style) 
	{
		uiobject.setStylePrimaryName(style);
	}

	/**
	 * Provide access to uiobject's UIObject. Used for component subclasses that
	 * need to access uiobjects of their children. 
	 * @param uiobject
	 * @return
	 */
	protected com.google.gwt.user.client.ui.UIObject getWrappedUIObject(UIObject uiobject)
	{
		return (uiobject.uiobject!=null?uiobject.uiobject:null);
	}	
}
