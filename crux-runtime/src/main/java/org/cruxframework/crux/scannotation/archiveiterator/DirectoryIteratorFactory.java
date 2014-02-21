package org.cruxframework.crux.scannotation.archiveiterator;

import java.io.IOException;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface DirectoryIteratorFactory
{
   URLIterator create(URL url, Filter filter) throws IOException;
}
