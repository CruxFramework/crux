package br.com.sysmap.crux.tools.projectgen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gessé Dafé - <code>gesse@sysmap.com.br</code>
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
	public static void recursiveDelete(File file)
	{
		File[] files = file.listFiles();

		if(files != null)
		{
			for (File child : files)
			{
				recursiveDelete(child);
			}
		}
		
		if(files == null || files.length == 0)
		{
			file.delete();
		}
	}
	
	/**
	 * @param sourceDir
	 * @param destDir
	 * @throws IOException 
	 */
	public static void copyFilesFromDir(File sourceDir, File destDir) throws IOException
	{
		if(!destDir.exists())
		{
			destDir.mkdirs();
		}
		
		File[] files = sourceDir.listFiles();
		for (File file : files)
		{
			if(!file.isDirectory())
			{
				FileInputStream stream = new FileInputStream(file);
				File destFile = new File(destDir, file.getName());
				write(stream, destFile);
			}
			else
			{
				File dir = new File(destDir, file.getName());
				copyFilesFromDir(file, dir);
			}
		}
	}
}