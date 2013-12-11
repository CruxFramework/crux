package org.cruxframework.anttomaven.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class CruxFileUtils extends FileUtils {

	private static final String REGEX_FILE_SPLITTER = " ";
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

	public static void copyDirectory(File source, File destination, FileFilter filter, boolean copyInnerContent)
			throws IOException 
			{
		File nextDirectory = null;
		if(copyInnerContent)
		{
			nextDirectory = destination;
		} else
		{
			nextDirectory = new File(destination, source.getName());
		}
		copyInnerContent = false;

		File[] files = source.listFiles();

		//
		// and then all the items below the directory...
		//
		for (int n = 0; n < files.length; ++n) 
		{
			if (files[n].isDirectory())
			{
				copyDirectory(files[n], nextDirectory, filter, copyInnerContent);
			}
			else
			{
				if (filter == null || filter.accept(files[n])) 
				{
					try {
						//
						// create the directory if necessary...
						//
						if (!nextDirectory.exists() && !nextDirectory.mkdirs()) 
						{
							String message = "DirCopyFailed";
							throw new IOException(message);
						}
						
			            copyFileToDirectory(files[n], nextDirectory);
			        } catch (Exception e) 
			        {
			        	String message = files[n].getName() + " not copied!";
						throw new IOException(message);
			        }
				}
			}
		}
	}

	public static String prettyFormat(String input, int indent) {
        try
        {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // This statement works with JDK 6
            transformerFactory.setAttribute("indent-number", indent);
             
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        }
        catch (Throwable e)
        {
            // You'll come here if you are using JDK 1.5
            // you are getting an the following exeption
            // java.lang.IllegalArgumentException: Not supported: indent-number
            // Use this code (Set the output property in transformer.
            try
            {
                Source xmlInput = new StreamSource(new StringReader(input));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            }
            catch(Throwable t)
            {
                return input;
            }
        }
    }
	
	public static boolean checkExtensionFile(File file, String extensionsStr)
	{
		if(StringUtils.isEmpty(extensionsStr))
		{
			return false;
		}

		String[] extensions = extensionsStr.split(REGEX_FILE_SPLITTER);

		if(extensions == null)
		{
			return false;
		}

		if(extensions.length == 1)
		{
			if(file.getName().endsWith(extensions[0]))
			{
				return true;
			}
			return false;
		}

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
						//Root
						if(searchForDirectory && name.equals(temp.getName()))
						{
							return temp; 
						}
						//Deep search
						File found = search(temp, name, searchForDirectory);
						if(found != null) 
						{
							return found;
						}
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
