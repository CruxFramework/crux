/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.datebox.gwtoverride;

import java.util.Date;

import org.cruxframework.crux.widgets.client.datebox.gwtoverride.CruxDateBox.DefaultFormat;

/**
 * Implemented by a delegate to handle the parsing and formating of date
 * values. The default {@link CruxFormat} uses a new {@link DefaultFormat}
 * instance.
 * @author samuel.cardoso
 */
public interface CruxFormat {

  /**
   * Formats the provided date. Note, a null date is a possible input.
   *
   * @param dateBox the date box you are formatting
   * @param date the date to format
   * @return the formatted date as a string
   */
  String format(CruxDateBox dateBox, Date date);

  /**
   * Parses the provided string as a date.
   *
   * @param dateBox the date box
   * @param text the string representing a date
   * @param reportError should the formatter indicate a parse error to the
   *          user?
   * @return the date created, or null if there was a parse error
   */
  Date parse(CruxDateBox dateBox, String text, boolean reportError);

  /**
   * If the format did any modifications to the date box's styling, reset them
   * now.
   *
   * @param abandon true when the current format is being replaced by another
   * @param dateBox the date box
   */
  void reset(CruxDateBox dateBox, boolean abandon);
}