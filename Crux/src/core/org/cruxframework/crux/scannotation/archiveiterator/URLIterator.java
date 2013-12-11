package org.cruxframework.crux.scannotation.archiveiterator;

import java.net.URL;


/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public interface URLIterator
{
   URL next();
   void close();
}
