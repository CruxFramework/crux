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
package org.cruxframework.crux.core.declarativeui;

import org.w3c.dom.Document;

/**
 * Defines a pre processor for crux view processor class. This is used during the processing
 * of all view files to perform operations on the original document. It is useful to process
 * templates, conditional fragments etc. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface CruxXmlPreProcessor
{
	Document preprocess(Document doc, String device);
}
