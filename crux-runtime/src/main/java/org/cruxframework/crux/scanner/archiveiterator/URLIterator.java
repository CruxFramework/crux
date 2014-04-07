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
