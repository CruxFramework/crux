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
package org.cruxframework.cruxdevtools.loginstrumentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class LogInstrumentation 
{
	//Set the root dir path
	private static final String DIR_PATH = "C:\\workspaces\\vivo360\\NFO-Vivo360-Lojas-26.1.0-safari\\src\\br\\com\\vivo\\nfo\\nfovivo360lojas\\client\\";
	
	private static Map<String, List<MethodLocation>> mapMethodLocation = new HashMap<String, List<MethodLocation>>();
	private static Map<String, VariableLocation> mapVariableLocation = new HashMap<String, VariableLocation>();
	private static final String NL = System.getProperty("line.separator");
	private static final boolean GENERATE_ALERT = true;
	private static final Logger LOG = Logger.getLogger(LogInstrumentation.class.getName());
	
	@SuppressWarnings("unused")
	private static class MethodLocation 
	{
		public MethodLocation(int lineNumber, int charNumber, String methodName) 
		{
			this.lineNumber = lineNumber;
			this.charNumber = charNumber;
			this.methodName = methodName;
		}

		private int charNumber;
		private int lineNumber;
		private String methodName;

		public int getLineNumber() 
		{
			return lineNumber;
		}
		public String getMethodName() 
		{
			return methodName;
		}
		public int getCharNumber() 
		{
			return charNumber;
		}
	}

	@SuppressWarnings("unused")
	private static class VariableLocation 
	{
		public VariableLocation(int lineNumber, int charNumber, String variableName, String className) 
		{
			this.charNumber = charNumber;
			this.lineNumber = lineNumber;
			this.variableName = variableName;
			this.className = className;
		}

		private int charNumber;
		private int lineNumber;
		private String variableName;
		private String className;

		public int getLineNumber() 
		{
			return lineNumber;
		}
		
		public String getVariableName() 
		{
			return variableName;
		}
		public int getCharNumber() 
		{
			return charNumber;
		}
		public String getClassName() 
		{
			return className;
		}
	}

	private static void parse(String str) 
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			private boolean logInserted = false;
			private String fileName;

			@Override
			public boolean visit(FieldDeclaration node) 
			{
				if(!logInserted)
				{
					logInserted = true;
					int lineNumber = cu.getLineNumber(node.getStartPosition());
					int charNumber = node.getStartPosition();
					mapVariableLocation.put(fileName, new VariableLocation(lineNumber-1, charNumber, node.toString(), fileName.substring(0, fileName.length()-".java".length())));
				}

				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) 
			{
				fileName = node.getName().getFullyQualifiedName()  + ".java";
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) 
			{
				List<MethodLocation> methodLocations = mapMethodLocation.get(fileName);
				if(methodLocations == null) 
				{
					methodLocations = new ArrayList<LogInstrumentation.MethodLocation>();
				}
				
				if(node != null && node.getBody() != null)
				{
					int indexOfSuper = node.getBody().toString().indexOf("super(");
					if(indexOfSuper < 0 || indexOfSuper > 5) {
						int lineNumber = cu.getLineNumber(node.getBody().getStartPosition());
						int charNumber = node.getStartPosition();
						methodLocations.add(new MethodLocation(lineNumber, charNumber, node.getName().getFullyQualifiedName()));
						mapMethodLocation.put(fileName, methodLocations);
					}
					
				}
				
				return super.visit(node);
			}
		});
	}

	private static String readFileToString(String filePath) throws IOException 
	{
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return  fileData.toString();	
	}

	private static void parseFiles(File root) throws IOException
	{
		File[] files = root.listFiles ( );
		String filePath = null;

		for (File f : files) 
		{
			filePath = f.getAbsolutePath();

			if(f.isFile())
			{
				parse(readFileToString(filePath));
			}

			if(f.isDirectory())
			{
				parseFiles(f);
			}
		}
	}

	private static void incFiles(File root) throws IOException
	{
		File[] files = root.listFiles ( );

		for (File f : files) 
		{
			if(f.isFile())
			{
				incFile(f, mapMethodLocation.get(f.getName()), mapVariableLocation.get(f.getName()));
			}

			if(f.isDirectory())
			{
				incFiles(f);
			}
		}
	}

	private static void incFile(File f, List<MethodLocation> list, VariableLocation variableLocation) throws IOException 
	{
		if(list == null || variableLocation == null)
		{
			return;
		}
		
		LOG.log(Level.INFO, "Incrementing file: " + f.getName());
		LOG.log(Level.INFO, "First variable location: " + variableLocation.getLineNumber());
		for(MethodLocation ml : list)
		{
			LOG.log(Level.INFO, "Method name: " + ml.getMethodName() + " -> " + ml.getLineNumber());
		}

    	ArrayList<String> lines = new ArrayList<String>();
    	String line;
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        while ((line = br.readLine()) != null)
        {
            lines.add(line);
        }
        fr.close();
        br.close();

        FileWriter fw = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fw);
        int lineNumber = 1;
        String logVariable = "LOG_" + System.currentTimeMillis();
        for(String s : lines)
        {
        	boolean hasWritten = false;
        	if(lineNumber == variableLocation.getLineNumber())
        	{
        		out.write(s + "private static final java.util.logging.Logger "+logVariable+" = java.util.logging.Logger.getLogger("+variableLocation.getClassName()+".class.getName());" + NL);
        		hasWritten = true;
        	}
        	
        	for(MethodLocation ml : list)
    		{
        		if(lineNumber == ml.getLineNumber())
        		{
        			String methodLog = variableLocation.getClassName()+": "+ml.getMethodName()+" -> Line:  " + (ml.getLineNumber() - 1);
					out.write(s + logVariable + ".log(java.util.logging.Level.FINE,\" " + methodLog + "  \");");
					if(GENERATE_ALERT)
					{
						out.write(" if(com.google.gwt.core.client.GWT.isClient()){ com.google.gwt.user.client.Window.alert(\" " + methodLog + "  \"); };" + NL);
					}
        			hasWritten = true;
        			break;
        		}
    		}
        	
        	if(!hasWritten)
        	{
        		out.write(s + NL);
        	}
        	lineNumber++;
        }
        out.flush();
        out.close();
	}
	
	public static void main(String[] args) throws IOException 
	{
		File root = new File(DIR_PATH);
		parseFiles(root);
		incFiles(root);
	}

}
