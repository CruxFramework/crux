package br.com.sysmap.crux.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gesse S. F. Dafe - <code>gesse@sysmap.com.br</code>
 */
public class FileUtils
{
	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int read = 0;
		byte[] buff = new byte[1024];
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		in.close();
		out.flush();
		out.close();
				
		return new String(out.toByteArray());
    }
	
	/**
	 * @param text
	 * @param f
	 * @throws IOException
	 */
	public static void write(String text, File f) throws IOException
    {
		FileOutputStream out = new FileOutputStream(f);
		out.write(text.getBytes());
		out.close();
    }
	
	/**
	 * @param in
	 * @param f
	 * @throws IOException
	 */
	public static void write(InputStream in, File f) throws IOException
    {
		FileOutputStream out = new FileOutputStream(f);
		
		int read = 0;
		byte[] buff = new byte[1024];
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		in.close();
		out.close();
    }

	/**
	 * @param file
	 */
	public static boolean recursiveDelete(File file)
	{
		boolean success = true;
		
		if(file != null && file.exists())
		{
			if(file.isDirectory())
			{
				File[] files = file.listFiles();
				
				if(files != null)
				{
					for (File child : files)
					{
						recursiveDelete(child);
					}
				}
			}
			
			success = file.delete();
		}
		
		return success;
	}
	
	/**
	 * @param sourceDir
	 * @param destDir
	 * @throws IOException 
	 */
	public static void copyFilesFromDir(File sourceDir, File destDir) throws IOException
	{
		copyFilesFromDir(sourceDir, destDir, null, 0);
	}			

	/**
	 * @param sourceDir
	 * @param destDir
	 * @param includes
	 * @param excludes
	 * @throws IOException
	 */
	public static void copyFilesFromDir(File sourceDir, File destDir, String includes, String excludes) throws IOException
	{
		FilePatternHandler handler = new FilePatternHandler(includes, excludes);
		copyFilesFromDir(sourceDir, destDir, handler, sourceDir.getCanonicalPath().length());
	}
	
	/**
	 * @param sourceDir
	 * @param destDir
	 * @param handler
	 * @throws IOException
	 */
	private static void copyFilesFromDir(File sourceDir, File destDir, FilePatternHandler handler, int inputDirNameLength) throws IOException
	{
		if(!destDir.exists())
		{
			destDir.mkdirs();
		}
		
		File[] files = sourceDir.listFiles();
		for (File file : files)
		{
			if (handler == null || handler.isValidEntry(getEntryName(file, inputDirNameLength)))
			{
				if(!file.isDirectory())
				{
					File destFile = new File(destDir, file.getName());
					FileInputStream stream = new FileInputStream(file);
					write(stream, destFile);
				}
				else
				{
					File dir = new File(destDir, file.getName());
					copyFilesFromDir(file, dir, handler, inputDirNameLength);
				}
			}
		}
	}

	/**
	 * @param source
	 * @param inputDirNameLength
	 * @return
	 * @throws IOException
	 */
	private static String getEntryName(File source, int inputDirNameLength) throws IOException
	{
		String name = source.getCanonicalPath().substring(inputDirNameLength).replace("\\", "/");
		if (name.startsWith("/"))
		{
			name = name.substring(1);
		}
		if (source.isDirectory() && !name.endsWith("/"))
		{
			name += "/";
		}
			
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getTempDir()
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\"))
		{
			tmpDir += File.separator;
		}
		return tmpDir;
	}
	
	/**
	 * 
	 * @return
	 */
	public static File getTempDirFile()
	{
		return new File(getTempDir());
	}
	
	/**
	 * @param sourceLocation
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException 
	{
		if (sourceLocation.isDirectory()) 
		{
			if (!targetLocation.exists()) 
			{
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) 
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} 
		else 
		{
			StreamUtils.write(new FileInputStream(sourceLocation), new FileOutputStream(targetLocation), true);
		}
	}	
}