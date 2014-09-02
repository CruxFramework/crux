/*
 * Copyright 2009 Google Inc.
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
package org.cruxframework.crux.core.client.collection;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The root Array type that provides read-access to an array that might still be
 * mutable by another actor.
 * 
 * @param <E>
 *            The type stored in the array elements
 */
public class Array<E> extends JavaScriptObject
{

	protected Array()
	{
	}

	public final void add(E elem)
	{
		jsniAdd(elem);
	}

	public final void clear()
	{
		jsniClear();
	}

	public final E get(int index)
	{
	    assert 0 < size() : "Attempt to access an element in an empty array";
	    assert (index >= 0 && index < size()) : "Index " + index + " was not in the acceptable range [" + 0 + ", " + size() + ")";
		return jsniGet(index);
	}

	/**
	 * @param elem
	 * @return
	 */
	public final int indexOf(E elem)
	{
		if (elem == null)
		{
			return -1;
		}
		return jsniIndexOf(elem);
	}

	/**
	 * Inserts {@code elem} before the element residing at {@code index}.
	 * 
	 * @param index
	 *            in the range [0, this.size()], inclusive; if index is equal to
	 *            the array's current size, the result is equivalent to calling
	 *            {@link #add(Object)}
	 * @param elem
	 *            the element to insert or {@code null}
	 */
	public final void insert(int index, E elem)
	{
		assert (index >= 0 && index < size() + 1) : "Index " + index + " was not in the acceptable range [" + 0 + ", " + (size() + 1) + ")";
		jsniInsert(index, elem);
	}

	/**
	 * Removes the element at the specified index.
	 */
	public final void remove(int index)
	{
	    assert 0 < size() : "Attempt to access an element in an empty array";
	    assert (index >= 0 && index < size()) : "Index " + index + " was not in the acceptable range [" + 0 + ", " + size() + ")";
		jsniRemove(index);
	}

	/**
	 * Replaces the element at the specified index.
	 * 
	 * @param index
	 *            in the range [0, this.size()), exclusive
	 * @param elem
	 *            the element to insert or {@code null}
	 */
	public final void set(int index, E elem)
	{
	    assert 0 < size() : "Attempt to access an element in an empty array";
	    assert (index >= 0 && index < size()) : "Index " + index + " was not in the acceptable range [" + 0 + ", " + size() + ")";

		jsniSet(index, elem);
	}

	/**
	 * Changes the array size. If {@code newSize} is less than the current size,
	 * the array is truncated. If {@code newSize} is greater than the current
	 * size the array is grown and the new elements of the array filled up with
	 * {@code fillValue}.
	 */
	public final void setSize(int newSize, E fillValue)
	{
		jsniSetSize(newSize, fillValue);
	}

	public final native int size() /*-{
		return this ? this.length : 0;
	}-*/;

	private native void jsniAdd(E elem) /*-{
		this.push(elem);
	}-*/;

	private native void jsniClear() /*-{
		this.length = 0;
	}-*/;

	private native E jsniGet(int index) /*-{
		return this[index];
	}-*/;

	private native int jsniIndexOf(E elem)/*-{
		for (var i = 0, len = this.length; i < len; i++) {
			if (this[i] == elem){
				return i;
			}
		}
		return -1;
	}-*/;

	/**
	 * Inserts {@code element} before the element residing at {@code index}.
	 * 
	 * @param index
	 *            in the range [0, this.size()], inclusive; if index is equal to
	 *            the array's current size, the result is equivalent to calling
	 *            {@link #add(Object)}
	 * @param elem
	 *            the element to insert or {@code null}
	 */
	private native void jsniInsert(int index, E elem) /*-{
		this.splice(index, 0, elem);
	}-*/;

	/**
	 * Removes the element at the specified index.
	 */
	private native void jsniRemove(int index) /*-{
		this.splice(index, 1);
	}-*/;

	/**
	 * Replaces the element at the specified index.
	 * 
	 * @param index
	 *            in the range [0, this.size()), exclusive
	 * @param elem
	 *            the element to insert or {@code null}
	 */
	private native void jsniSet(int index, E elem) /*-{
		this[index] = elem;
	}-*/;

	/**
	 * Changes the array size. If {@code newSize} is less than the current size,
	 * the array is truncated. If {@code newSize} is greater than the current
	 * size the array is grown and the new elements of the array filled up with
	 * {@code fillValue}.
	 */
	private native void jsniSetSize(int newSize, E fillValue) /*-{
		if (fillValue == null) {
			this.length = newSize;
		} else {  
			for (var i = this.length; i < newSize; ++i) {
				this[i] = fillValue;
			}
		}
	}-*/;
}
