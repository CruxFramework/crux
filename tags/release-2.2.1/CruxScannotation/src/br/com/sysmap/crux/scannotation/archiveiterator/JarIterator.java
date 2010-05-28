package br.com.sysmap.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;

/**
 * @author Thiago da Rosa de Bustamante - <code>thiago@sysmap.com.br</code>
 */
public class JarIterator implements URLIterator
{
   URL jar;
   JarInputStream jarStream;
   JarEntry next;
   Filter filter;
   boolean initial = true;
   boolean closed = false;

   public JarIterator(File file, Filter filter) throws IOException
   {
	      this.filter = filter;
	      this.jar = toJarURL(file);
	      this.jarStream = new JarInputStream(new FileInputStream(file));
   }

   public JarIterator(URL jar, Filter filter) throws IOException
   {
      this.filter = filter;
      this.jar = jar;
      this.jarStream = new JarInputStream(jar.openStream());
   }

   private void setNext()
   {
      initial = true;
      try
      {
         if (next != null) jarStream.closeEntry();
         next = null;
         do
         {
            next = jarStream.getNextJarEntry();
         } while (next != null && (next.isDirectory() || (filter == null || !filter.accepts(next.getName()))));
         if (next == null)
         {
            close();
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("failed to browse jar", e);
      }
   }

   public URL next()
   {
      if (closed || (next == null && !initial)) return null;
      setNext();
      if (next == null) return null;
      
      URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler("jar");
      return handler.getChildResource(jar, next.getName());
   }

   public void close()
   {
      try
      {
         closed = true;
         jarStream.close();
      }
      catch (IOException ignored)
      {

      }
   }
   
   static URL toJarURL(File file) throws MalformedURLException
   {
	   return new URL("jar:"+file.toURI().toURL().toString()+"!/");
   }
}
