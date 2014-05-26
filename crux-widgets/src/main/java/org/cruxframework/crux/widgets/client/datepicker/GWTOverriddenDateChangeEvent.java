/*
 * Copyright 2008 Google Inc.
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

package org.cruxframework.crux.widgets.client.datepicker;

import java.util.Date;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Creates a new value every time a date is accessed.
 */
public class GWTOverriddenDateChangeEvent extends ValueChangeEvent<Date> {

  /**
   * Fires value change event if the old value is not equal to the new value.
   * Use this call rather than making the decision to short circuit yourself for
   * safe handling of null.
   * 
   * @param <S> The event source
   * @param source the source of the handlers
   * @param oldValue the oldValue, may be null
   * @param newValue the newValue, may be null
   */
  public static <S extends HasValueChangeHandlers<Date> & HasHandlers> void fireIfNotEqualDates(
      S source, Date oldValue, Date newValue) {
    if (ValueChangeEvent.shouldFire(source, oldValue, newValue)) {
      source.fireEvent(new GWTOverriddenDateChangeEvent(newValue));
    }
  }

  /**
   * Creates a new date value change event.
   * 
   * @param value the value
   */
  protected GWTOverriddenDateChangeEvent(Date value) {
    // The date must be copied in case one handler causes it to change.
    super(GWTOverriddenCalendarUtil.copyDate(value));
  }

  @Override
  public Date getValue() {
    return GWTOverriddenCalendarUtil.copyDate(super.getValue());
  }
}
