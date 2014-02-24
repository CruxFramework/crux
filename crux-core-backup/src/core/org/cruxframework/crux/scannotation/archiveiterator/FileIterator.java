package org.cruxframework.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public class FileIterator implements URLIterator
{
   private ArrayList<File> files;
   private int index = 0;

   public FileIterator(File file, Filter filter)
   {
      files = new ArrayList<File>();
      try
      {
         create(files, file, filter);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	protected static void create(List<File> list, File dir, Filter filter) throws Exception
	{
		File[] files = dir.listFiles();
		
		if(files != null)
		{		
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					create(list, files[i], filter);
				}
				else
				{
					if (filter == null || filter.accepts(files[i].toURI().toURL().toString()))
					{
						list.add(files[i]);
					}
				}
			}
		}
	}

   public URL next()
   {
      if (index >= files.size()) return null;
      File fp = (File) files.get(index++);
      try
      {
         return fp.toURI().toURL();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void close()
   {
   }
}
