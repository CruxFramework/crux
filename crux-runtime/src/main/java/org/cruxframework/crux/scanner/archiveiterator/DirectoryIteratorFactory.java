package org.cruxframework.crux.scanner.archiveiterator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.cruxframework.crux.scanner.ScannerRegistration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface DirectoryIteratorFactory
{
   URLIterator create(URL url, List<ScannerRegistration> scanners) throws IOException;
}
