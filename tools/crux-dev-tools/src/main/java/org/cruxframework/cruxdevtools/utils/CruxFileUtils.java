/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.cruxdevtools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Samuel Almeida Cardoso
 * 
 */
public class CruxFileUtils extends FileUtils
{
	private static final int READ_BUFFER = 8192;

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	/** The file copy buffer size (30 MB) */
	private static final long FILE_COPY_BUFFER_SIZE = FileUtils.ONE_MB * 30;

	private static final String REGEX_FILE_SPLITTER = " ";

	/** The application's current working directory. */
	public static final File CURRENT_DIR = new File(".");

	
	/**
	 * @param source
	 * @param destination
	 * @param filter
	 * @param copyInnerContent
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination, 
		FileFilter filter, boolean copyInnerContent) throws IOException
	{
		copyDirectory(source, destination, filter, copyInnerContent, false);
	}
	
	/**
	 * @param source
	 * @param destination
	 * @param filter
	 * @param copyInnerContent
	 * @throws IOException
	 */
	public static void copyDirectory(File source, File destination, 
		FileFilter filter, boolean copyInnerContent, boolean convertToUtf8) 
		throws IOException
	{
		File nextDirectory = null;
		if (copyInnerContent)
		{
			nextDirectory = destination;
		} else
		{
			nextDirectory = new File(destination, source.getName());
		}
		copyInnerContent = false;

		File[] files = source.listFiles();

		/* and then all the items below the directory... */
		for (int n = 0; n < files.length; ++n)
		{
			if (files[n].isDirectory())
			{
				copyDirectory(files[n], nextDirectory, filter, copyInnerContent, 
					convertToUtf8);
			} else
			{
				if (filter == null || filter.accept(files[n]))
				{
					try
					{
						/* create the directory if necessary... */
						if (!nextDirectory.exists() && !nextDirectory.mkdirs())
						{
							String message = "DirCopyFailed";
							throw new IOException(message);
						}

						copyFileToDirectory(files[n], nextDirectory, convertToUtf8);
					} catch (Exception e)
					{
						String message = files[n].getName() + " not copied!";
						throw new IOException(message);
					}
				}
			}
		}
	}

//	public static String prettyFormat(String input, int indent)
//	{
//		try
//		{
//			Source xmlInput = new StreamSource(new StringReader(input));
//			StringWriter stringWriter = new StringWriter();
//			StreamResult xmlOutput = new StreamResult(stringWriter);
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			// This statement works with JDK 6
//			transformerFactory.setAttribute("indent-number", indent);
//
//			Transformer transformer = transformerFactory.newTransformer();
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.transform(xmlInput, xmlOutput);
//			return xmlOutput.getWriter().toString();
//		} catch (Throwable e)
//		{
//			// You'll come here if you are using JDK 1.5
//			// you are getting an the following exeption
//			// java.lang.IllegalArgumentException: Not supported: indent-number
//			// Use this code (Set the output property in transformer.
//			try
//			{
//				Source xmlInput = new StreamSource(new StringReader(input));
//				StringWriter stringWriter = new StringWriter();
//				StreamResult xmlOutput = new StreamResult(stringWriter);
//				TransformerFactory transformerFactory = TransformerFactory.newInstance();
//				Transformer transformer = transformerFactory.newTransformer();
//				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
//				transformer.transform(xmlInput, xmlOutput);
//				return xmlOutput.getWriter().toString();
//			} catch (Throwable t)
//			{
//				return input;
//			}
//		}
//	}

	/**
	 * @param file
	 * @param extensionsStr
	 * @return
	 */
	public static boolean checkExtensionFile(File file, String extensionsStr)
	{
		if (StringUtils.isEmpty(extensionsStr))
		{
			return false;
		}

		String[] extensions = extensionsStr.split(REGEX_FILE_SPLITTER);

		if (extensions == null)
		{
			return false;
		}

		if (extensions.length == 1)
		{
			if (file.getName().endsWith(extensions[0]))
			{
				return true;
			}
			return false;
		}

		for (String extension : extensions)
		{
			if (file.getName().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param file
	 * @param extensions
	 * @return
	 */
	public static boolean checkExtensionDirectory(File file, List<String> extensions)
	{
		for (String extension : extensions)
		{
			if (file.getName().endsWith(extension) && file.isDirectory())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param value
	 * @return
	 */
	public static String ensureDirNoLastBars(String value)
	{
		if (StringUtils.isEmpty(value))
		{
			return null;
		}

		if (value.endsWith("\\"))
		{
			return value.substring(0, value.length() - 2);
		}

		if (value.endsWith("/"))
		{
			return value.substring(0, value.length() - 1);
		}

		if (value.endsWith("//"))
		{
			return value.substring(0, value.length() - 2);
		}
		return value;
	}

	/**
	 * @param file
	 * @param name
	 * @param searchForDirectory
	 * @return
	 */
	public static File search(File file, String name, boolean searchForDirectory)
	{
		if (file.isDirectory())
		{
			if (file.canRead())
			{
				for (File temp : file.listFiles())
				{
					if (temp.isDirectory())
					{
						// Root
						if (searchForDirectory && name.equals(temp.getName()))
						{
							return temp;
						}
						// Deep search
						File found = search(temp, name, searchForDirectory);
						if (found != null)
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

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFileContent(File file) throws IOException
	{
		BufferedReader reader = null;
		StringBuffer fileString = new StringBuffer();
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				fileString.append(line);
			}
		} catch (IOException e)
		{
			throw new RuntimeException("Error reading file.", e);
		} finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		return fileString.toString();
	}
	
	/**
	 * Get a String representation of a text file
	 * 
	 * @param file text file name
	 * @return String representation
	 * @throws IOException @see {@link Reader#read(char[], int, int)}
	 */
	public static String readFile(String file) throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		try
		{
			return readFile(stream);
		} finally
		{
			stream.close();
		}
	}

	/**
	 * Get a String representation of a text file
	 * 
	 * @param file text file
	 * @return String representation
	 * @throws IOException @see {@link Reader#read(char[], int, int)}
	 */
	public static String readFile(FileInputStream stream) throws IOException
	{
		try
		{
			Reader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[READ_BUFFER];
			int read;
			while ((read = reader.read(buffer, 0, buffer.length)) > 0)
			{
				builder.append(buffer, 0, read);
			}
			return builder.toString();
		} finally
		{
			stream.close();
		}
	}

	/***********************************************
	 * 
	 ***********************************************/

	
	public static void copyFileToDirectory(final File source, 
		final File destinationDirectory) throws IOException
	{
		copyFileToDirectory(source, destinationDirectory, false);
	}
	
	/**
	 * Copy file from source to destination. If <code>destinationDirectory</code>
	 * does not exist, it
	 * (and any parent directories) will be created. If a file <code>source</code>
	 * in <code>destinationDirectory</code> exists, it will be overwritten.
	 * 
	 * @param source An existing <code>File</code> to copy.
	 * @param destinationDirectory A directory to copy <code>source</code> into.
	 * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal
	 *           file.
	 * @throws IllegalArgumentException if <code>destinationDirectory</code> isn't
	 *           a directory.
	 * @throws IOException if <code>source</code> does not exist, the file in
	 *           <code>destinationDirectory</code> cannot be written to, or an IO
	 *           error occurs during copying.
	 */
	public static void copyFileToDirectory(final File source, 
		final File destinationDirectory, boolean convertToUtf8) throws IOException
	{
		if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
		{
			throw new IllegalArgumentException("Destination is not a directory");
		}

		copyFile(source, new File(destinationDirectory, source.getName()), 
			convertToUtf8);
	}

	/**
	 * Copy file from source to destination. The directories up to 
	 * <code>destination</code> will be  created if they don't already exist. 
	 * <code>destination</code> will be overwritten if it  already exists.
	 * 
	 * @param source An existing non-directory <code>File</code> to copy bytes 
	 * from.
	 * 
	 * @param destination A non-directory <code>File</code> to write bytes to 
	 * (possibly overwriting).
	 * 
	 * @throws IOException if <code>source</code> does not exist, 
	 * <code>destination</code> cannot be written to, or an IO error occurs 
	 * during copying.
	 * 
	 * @throws java.io.FileNotFoundException if <code>destination</code> is a 
	 * directory (use {@link #copyFileToDirectory}).
	 */
	public static void copyFile(final File source, final File destination) 
		throws IOException
	{
		copyFile(source, destination, false); 
	}

	/**
	 * @param source
	 * @param destination
	 * @param convertUtf8
	 * @throws IOException
	 */
	public static void copyFile(final File source, final File destination, 
		boolean convertUtf8) throws IOException
	{
		// check source exists
		if (!source.exists())
		{
			final String message = "File " + source + " does not exist";
			throw new IOException(message);
		}

		/* check source != destination, see PLXUTILS-10 */
		if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
		{
			/* if they are equal, we can exit the method without doing any work */
			return;
		}
		mkdirsFor(destination);
		
		if (convertUtf8)
		{
			convertFileToUtf8(source, destination);
		} else
		{
			doCopyFile(source, destination);
		}

		if (!convertUtf8 && source.length() != destination.length())
		{
			final String message = "Failed to copy full contents from " + source + 
				" to " + destination;
			throw new IOException(message);
		}
	}

	/***********************************************
	 * 
	 ***********************************************/
	
	private static void mkdirsFor(File destination)
	{
		// does destination directory exist ?
		if (destination.getParentFile() != null && 
			!destination.getParentFile().exists())
		{
			destination.getParentFile().mkdirs();
		}
	}

	private static void doCopyFile(File source, File destination) 
		throws IOException
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel input = null;
		FileChannel output = null;
		try
		{
			fis = new FileInputStream(source);
			fos = new FileOutputStream(destination);
			input = fis.getChannel();
			output = fos.getChannel();
			long size = input.size();
			long pos = 0;
			long count = 0;
			while (pos < size)
			{
				count = size - pos > FILE_COPY_BUFFER_SIZE ? 
					FILE_COPY_BUFFER_SIZE : size - pos;
				
				pos += output.transferFrom(input, pos, count);
			}
		} finally
		{
			IOUtil.close(output);
			IOUtil.close(fos);
			IOUtil.close(input);
			IOUtil.close(fis);
		}
	}

	private static void convertFileToUtf8(File source, File destination) 
		throws IOException
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try
		{
			fis = new FileInputStream(source);
			fos = new FileOutputStream(destination);

			String fileContents = readFile(fis);
			byte[] utfBytes = fileContents.getBytes(UTF8_CHARSET);
			fos.write(utfBytes);
		} finally
		{
			IOUtil.close(fos);
			IOUtil.close(fis);
		}
	}
}
