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
package org.cruxframework.crux.scanner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.archiveiterator.Filter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScannerRegistration
{
	private AbstractScanner scanner;
	private List<ScannerMatch> allMatches;
	private List<ScannerMatch> scanMatches;
	
	public ScannerRegistration(AbstractScanner scanner)
	{
		this.scanner = scanner;
	}

	public boolean isScanned()
	{
		return allMatches != null;
	}

	void setScanned()
	{
		if (allMatches == null)
		{
			allMatches = new ArrayList<ScannerMatch>();
		}
	}

	void startScanning()
	{
		if (scanMatches == null)
		{
			scanMatches = new ArrayList<ScannerMatch>();
		}
		else
		{
			scanMatches.clear();
		}
	}
	
	void endScanning()
	{
		scanMatches.clear();
	}
	
	public List<ScannerMatch> getAllMatches()
	{
		return allMatches;
	}
	
	List<ScannerMatch> getScanMatches()
	{
		return scanMatches;
	}

	public void addMatch(ScannerMatch scannerMatch)
	{
		scanMatches.add(scannerMatch);
		allMatches.add(scannerMatch);
	}

	public void resetScanner()
	{
		if (allMatches != null)
		{
			allMatches.clear();
			allMatches = null;
		}
		if (scanMatches != null)
		{
			scanMatches.clear();
			scanMatches = null;
		}
		scanner.resetScanner();
	}

	public Filter getFilter()
	{
		return scanner.getScannerFilter();
	}
	
	public ScannerCallback getCallback()
	{
		return scanner.getScannerCallback();
	}
	
	public Class<? extends AbstractScanner> getScannerClass()
	{
		return scanner.getClass();
	}
	
	public static class ScannerMatch
	{
		private URL parentURL;
		private URL match;

		public ScannerMatch(URL parentURL, URL match)
        {
			this.parentURL = parentURL;
			this.match = match;
        }
		
		public URL getMatch()
		{
			return match;
		}
		
		public URL getParentURL()
		{
			return parentURL;
		}
	}
}
