package org.cruxframework.anttomaven.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class CruxFileUtils extends FileUtils {

	/**
	 * 
	 * The application's current working directory.
	 * 
	 */
	public static final File CURRENT_DIR = new File(".");

	public static void copy(File source, File destination) throws IOException 
	{
		if (source == null)
		{
			throw new NullPointerException("NullSource");
		}

		if (destination == null)
		{
			throw new NullPointerException("NullDestination");
		}

		if (source.isDirectory())
		{
			copyDirectory(source, destination);
		}
		else
		{
			copyFile(source, destination);
		}
	}

	public static void copyDirectory(File source, File destination) throws IOException 
	{
		copyDirectory(source, destination, null);
	}

	public static void copyDirectory(File source, File destination, FileFilter filter)
			throws IOException 
	{
		File nextDirectory = new File(destination, source.getName());

		//
		// create the directory if necessary...
		//
		if (!nextDirectory.exists() && !nextDirectory.mkdirs()) 
		{
			String message = "DirCopyFailed";
			throw new IOException(message);
		}

		File[] files = source.listFiles();

		//
		// and then all the items below the directory...
		//
		for (int n = 0; n < files.length; ++n) {
			if (filter == null || filter.accept(files[n])) {
				if (files[n].isDirectory())
				{
					copyDirectory(files[n], nextDirectory, filter);
				}
				else
				{
					copyFile(files[n], nextDirectory);
				}
			}
		}
	}
	
	public static boolean checkExtensionFile(File file, List<String> extensions)
	{
		for(String extension : extensions)
		{
			if(file.getName().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkExtensionDirectory(File file, List<String> extensions)
	{
		for(String extension : extensions)
		{
			if(file.getName().endsWith(extension) && file.isDirectory())
			{
				return true;
			}
		}
		return false;
	}
	
	public static String ensureDirNoLastBars(String value)
	{
		if(StringUtils.isEmpty(value))
		{
			return null;
		}

		if(value.endsWith("\\"))
		{
			return value.substring(0, value.length() - 2);
		}

		if(value.endsWith("/"))
		{
			return value.substring(0, value.length() - 1);
		}

		if(value.endsWith("//"))
		{
			return value.substring(0, value.length() - 2);
		}
		return value;
	}
	
	public static File search(File file, String name, boolean searchForDirectory) 
	{
		if(file.isDirectory()) 
		{
			if(file.canRead()) 
			{
				for(File temp : file.listFiles()) 
				{
					if(temp.isDirectory()) 
					{
						if(searchForDirectory && name.equals(temp.getName()))
						{
							return temp; 
						}
						return search(temp, name, searchForDirectory);
					} else 
					{
						if (!searchForDirectory && name.equals(temp.getName()))
						{
							return temp;
						}
					}
				}
			} else 
			{
				System.out.println(file.getAbsoluteFile() + "Permission Denied");
			}
		}
		
		return null;
	}
	
	public static String readFileContent(File file) throws IOException
	{
		BufferedReader reader = null;
		StringBuffer fileString = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null)
			{
				fileString.append(line);
			}
		} catch (IOException e)
		{
			throw new RuntimeException("Error reading file.", e);
		} finally
		{
			reader.close();	
		}
		return fileString.toString();
	}
}
