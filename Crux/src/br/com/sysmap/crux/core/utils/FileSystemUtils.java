/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class FileSystemUtils
{
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
