package br.com.sysmap.crux.scannotation.archiveiterator;

import java.net.URL;


/**
 * @author Thiago da Rosa de Bustamante - <code>thiago@sysmap.com.br</code>
 *
 */
public interface URLIterator
{
   URL next();
   void close();
}
