/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.tools.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.tools.compile.CompilerException;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;


/**
 * A tool for merge legacy code into core classes. It is only for internal use. Used to generate a legacy version
 * of Crux jars, for backward compatibility, during Crux distribution build.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LegacyHandler
{
	private static final String[] CRUX_SIMPLE_FILE_EXTENSIONS = {".xsd", ".txt", ".gif", ".png", ".jpg"};
	
	public static void main(String[] args) throws CompilerException, IOException
    {
		if (args == null || args.length < 2)
		{
			System.out.println("usage: LegacyHandler <destinationFolder> <sourceFolderList ...>");
			System.exit(1);
		}
		
		LegacyHandler legacyHandler = new LegacyHandler();
		File destinationFolder = new File(args[0]);
		FileUtils.recursiveDelete(destinationFolder);
		legacyHandler.setDestinationFolder(destinationFolder);
		
		for (int i=1; i<args.length; i++)
		{
			legacyHandler.addSourceFolder(new File(args[i]));
		}
		
		legacyHandler.scanSourceFiles();
    }
	
	private List<File> sourceFolders = new ArrayList<File>();
	private File destinationFolder;
	
	public void addSourceFolder(File srcFolder)
	{
		sourceFolders.add(srcFolder);
	}
	
	public Iterator<File> sourceFolders()
	{
		return sourceFolders.iterator();
	}
	
	public File getDestinationFolder()
    {
    	return destinationFolder;
    }

	public void setDestinationFolder(File destinationFolder)
    {
    	this.destinationFolder = destinationFolder;
    	if (!destinationFolder.exists())
    	{
    		destinationFolder.mkdirs();
    	}
    }

	/**
	 * 
	 * @throws CompilerException
	 * @throws IOException
	 */
	public void scanSourceFiles() throws CompilerException, IOException
	{
		for (File folder : sourceFolders)
        {
            scanJavaFiles(folder);
            scanXmlFiles(folder);
            scanCssFiles(folder);
			scanCruxSimpleFiles(folder);
        }
	}

	private void scanCruxSimpleFiles(File folder)
    {
	    for (String extension : CRUX_SIMPLE_FILE_EXTENSIONS)
	    {
	    	List<File> files = FileUtils.scanFiles(folder, extension);
	    	for (File file : files)
	    	{
	    		String relativePath = file.getAbsolutePath().substring(folder.getAbsolutePath().length()+1);
	    		saveFile(file, relativePath, false);
	    	}
	    }
    }

	private void scanJavaFiles(File folder)
    {
	    List<File> files = FileUtils.scanFiles(folder, ".java");
	    for (File file : files)
	    {
	        String path = file.getAbsolutePath();
	    	if (!path.endsWith("package-info.java"))
	        {
	        	handleSourceFile(folder, file);
	        }
	    }
    }
	
	private void scanXmlFiles(File folder)
    {
	    List<File> files = FileUtils.scanFiles(folder, ".xml");
	    for (File file : files)
	    {
	        String path = file.getAbsolutePath();
	    	if (path.endsWith(".gwt.xml"))
	        {
	        	handleModuleFile(folder, file);
	        }
	    	else
	    	{
	    		String relativePath = file.getAbsolutePath().substring(folder.getAbsolutePath().length()+1);
	    		saveFile(file, relativePath, false);
	    	}
	    }
    }

	private void scanCssFiles(File folder)
    {
		List<File> files = FileUtils.scanFiles(folder, ".css");
		for (File file : files)
		{
			try
			{
				String relativePath = file.getAbsolutePath().substring(folder.getAbsolutePath().length()+1);
				boolean isLegacy = relativePath.endsWith(".legacy.css");
				if (isLegacy)
				{
					String targetModuleName = relativePath.replace(".legacy.css", "");
					File targetSourceFile = getTargetSourceFile(targetModuleName, ".css", true);
					String content = SourceFileHandler.mergeCssFile(targetSourceFile, file);
					saveFile(targetModuleName+".css", content, true);
				}
				else
				{
					saveFile(file, relativePath, false);
				}
			}
			catch (Exception e) 
			{
				throw new SourceAnalyserException("Error handling source file ["+file.getAbsolutePath()+"]", e);
			}
		}
    }

	private void handleModuleFile(File folder, File file)
    {
		try
		{
			String relativePath = file.getAbsolutePath().substring(folder.getAbsolutePath().length()+1);
			boolean isLegacy = relativePath.endsWith(".legacy.gwt.xml");
			if (isLegacy)
			{
				String targetModuleName = relativePath.replace(".legacy.gwt.xml", "");
				File targetSourceFile = getTargetSourceFile(targetModuleName, ".gwt.xml", true);
				String content = SourceFileHandler.mergeModuleFile(targetSourceFile, file);
				saveFile(targetModuleName+".gwt.xml", content, true);
			}
			else
			{
				saveFile(file, relativePath, false);
			}
		}
		catch (Exception e) 
		{
			throw new SourceAnalyserException("Error handling source file ["+file.getAbsolutePath()+"]", e);
		}
    }

	private void handleSourceFile(File sourceFolder, File sourceFile)
	{
		try
		{
			String relativePath = sourceFile.getAbsolutePath().substring(sourceFolder.getAbsolutePath().length()+1);
			
			CompilationUnitInfo compilationUnitInfo = getCompilationUnitInfo(sourceFile);
			LegacySourceAnalyser legacyAnalyser = new LegacySourceAnalyser(compilationUnitInfo.javacTrees); 
			compilationUnitInfo.tree.accept(legacyAnalyser);
			LegacyInfo legacyInfo = legacyAnalyser.getLegacyInfo();
			if (legacyInfo.isLegacy())
			{
				String targetClassName = legacyInfo.getTargetClassName();
				if (StringUtils.isEmpty(targetClassName))
				{
					saveFile(sourceFile, relativePath, false);
				}
				else
				{
					File targetSourceFile = getTargetSourceFile(targetClassName, ".java", false);
					CompilationUnitInfo targetCompilationUnitInfo = getCompilationUnitInfo(targetSourceFile);
					CruxClassAnalyser targetclassAnalyser = new CruxClassAnalyser(targetCompilationUnitInfo.javacTrees, null);
					targetCompilationUnitInfo.tree.accept(targetclassAnalyser);

					CruxClassAnalyser classAnalyser = new CruxClassAnalyser(compilationUnitInfo.javacTrees, targetclassAnalyser.getClassInfo());
					compilationUnitInfo.tree.accept(classAnalyser);
					saveSourceFile(classAnalyser.getClassInfo(), true);
				}
			}
			else
			{
				saveFile(sourceFile, relativePath, false);
			}
		}
		catch (Exception e) 
		{
			throw new SourceAnalyserException("Error handling source file ["+sourceFile.getAbsolutePath()+"]", e);
		}
	}

	private File getTargetSourceFile(String targetClassName, String extension, boolean acceptsNull)
    {
		String targetFileName = targetClassName.replace('.', '/')+extension;
		
		for (File folder: sourceFolders)
        {
	        File file = new File(folder,targetFileName);
			if (file.exists())
			{
				return file;
			}
        }
		if (acceptsNull)
		{
			return null;
		}
		throw new SourceAnalyserException("Can not found the source file ["+targetFileName+"].");
    }

	private CompilationUnitInfo getCompilationUnitInfo(File sourceFile) throws IOException
	{
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(sourceFile);
		CompilationTask task = compiler.getTask(null, null, null, null, null, fileObjects);
		com.sun.tools.javac.api.JavacTaskImpl javacTask = (com.sun.tools.javac.api.JavacTaskImpl) task;

		Iterable<? extends CompilationUnitTree> trees = javacTask.parse();
		JCCompilationUnit tree = (JCCompilationUnit) trees.iterator().next();
		JavacTrees javacTrees = JavacTrees.instance(javacTask);
		return new CompilationUnitInfo(tree, javacTrees);
	}
	
	private void saveFile(File sourceFile, String relativePath, boolean override)
    {
		try
        {
			File targetFile = new File(destinationFolder, relativePath);
			if (override || !targetFile.exists())
			{
				FileUtils.copyFile(sourceFile, targetFile);
			}
        }
        catch (IOException e)
        {
        	throw new SourceAnalyserException("Error saving source file ["+relativePath+"]", e);
        }
    }

	private void saveFile(String fileName, String content, boolean override)
    {
		try
        {
			File targetFile = new File(destinationFolder, fileName);
			if (override || !targetFile.exists())
			{
				FileUtils.write(content, targetFile);
			}
        }
        catch (IOException e)
        {
        	throw new SourceAnalyserException("Error saving source file ["+fileName+"]", e);
        }
    }

	private void saveSourceFile(ClassInfo classInfo, boolean override)
    {
		String targetFileName = classInfo.getPackageName().replace('.', '/')+"/"+classInfo.getClassName()+".java";
		File targetFile = new File(destinationFolder, targetFileName);
		
		if (override || !targetFile.exists())
		{
			File parentFile = targetFile.getParentFile();
			if (!parentFile.exists())
			{
				parentFile.mkdirs();
			}
			try
            {
				PrintWriter writer = new PrintWriter(new FileOutputStream(targetFile));
				writer.print(classInfo.toString());
				writer.close();
            }
            catch (FileNotFoundException e)
            {
            	throw new SourceAnalyserException("Error saving source file ["+targetFileName+"]", e);
            }
		}
    }
	
	private static class CompilationUnitInfo
	{
		private JCCompilationUnit tree;
		private JavacTrees javacTrees;
		
		public CompilationUnitInfo(JCCompilationUnit tree, JavacTrees javacTrees)
        {
			this.tree = tree;
			this.javacTrees = javacTrees;
        }
	}
}
