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
package org.cruxframework.crux.scanner.archiveiterator;

import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;



/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public abstract class URLIterator
{
	private List<ScannerRegistration> scanners;
	public URLIterator(List<ScannerRegistration> scanners)
    {
		this.scanners = scanners;
    }
	
	protected void consumeWhenAccepted(URL parentURL, URL url, String fileName)
	{
		for (ScannerRegistration scannerRegistration : scanners)
        {
	        Filter filter = scannerRegistration.getFilter();
			if (filter == null || filter.accepts(fileName))
			{
				scannerRegistration.addMatch(new ScannerMatch(parentURL, url));
			}
        }
	}

	public abstract void search();
}
