package org.cruxframework.crux.scanner.archiveiterator;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Filter
{
   boolean accepts(String filename);
}
