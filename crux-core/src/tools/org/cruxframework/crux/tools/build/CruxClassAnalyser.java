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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxClassAnalyser extends AbstractSourceAnalyser 
{
	private ClassInfo classInfo;
	private JCClassDecl rootType;
	private final JavacTrees javacTrees;

	public CruxClassAnalyser(JavacTrees javacTrees, ClassInfo classInfo)
    {
		this.javacTrees = javacTrees;
		this.classInfo = classInfo;
    }
	
	public ClassInfo getClassInfo()
	{
		return classInfo;
	}
	
	@Override
	public void doVisitTopLevel(JCCompilationUnit compilationUnit)
	{
		rootType = getRootType(compilationUnit, false);
        if (classInfo == null)
        {
        	classInfo = createClassInfo();
        }
        else
        {
        	processInheritance(rootType, classInfo);        	
        }
	}

	@Override
	public void visitImport(JCImport importDecl)
	{
		classInfo.addImport(importDecl.toString());
	    super.visitImport(importDecl);
	}
	
	@Override
	public void visitClassDef(JCClassDecl classDecl)
	{
		TreePath path = javacTrees.getPath(compilationUnit, classDecl);
		boolean isRootType = path.getLeaf().equals(rootType);
		boolean firstGeneration = !isRootType && path.getParentPath().getLeaf().equals(rootType);
		
		Position pos = getClassDeclPos(classDecl);
		int startLine = lineMap.getLineNumber(pos.start);
		int endLine = lineMap.getLineNumber(pos.end);
		
		if (startLine == endLine)
		{
			visitSingleLineClass(classDecl, isRootType, firstGeneration);
		}
		else
		{
			visitMultiLineClass(classDecl, isRootType, firstGeneration);
		}
	}

	@Override
	public void visitMethodDef(JCMethodDecl methodDecl)
	{
		PrintWriter printer = getPrinter();
		boolean rootClass = printer == null;
		if (rootClass)
		{
			StringWriter writer = new StringWriter();
			printer = new PrintWriter(writer);
			checkJavadoc(methodDecl, printer);
			String methodSource = getSourceString(methodDecl);
			boolean constructor = methodDecl.getReturnType() == null;
			if (constructor)
			{
				methodSource = methodSource.replace(rootType.getSimpleName().toString(), classInfo.getClassName());
			}
			printer.println(methodSource);

			String methodDesc = getMethodDescriptor(methodDecl);
			classInfo.addMember(methodDesc, writer.toString());
		}
		else
		{
			checkJavadoc(methodDecl, printer);
			printer.println(getSourceString(methodDecl));
		}
	}

	@Override
	public void visitVarDef(JCVariableDecl varDecl)
	{
		PrintWriter printer = getPrinter();
		boolean rootClass = printer == null;
		if (rootClass)
		{
			StringWriter writer = new StringWriter();
			printer = new PrintWriter(writer);
			checkJavadoc(varDecl, printer);
			printer.println(getSourceString(varDecl));

			String varDesc = varDecl.getName().toString();
			classInfo.addMember(varDesc, writer.toString());
		}
		else
		{
			checkJavadoc(varDecl, printer);
			printer.println(getSourceString(varDecl));
		}
	}

	private void visitMultiLineClass(JCClassDecl classDecl, boolean isRootType, boolean firstGeneration)
    {
	    StringWriter writer = null;
	    if (!isRootType)
	    {
	    	if (firstGeneration)
	    	{
	    		writer = createPrinter();
	    	}
	    	PrintWriter printer = getPrinter();
	    	printer.println();
	    	checkJavadoc(classDecl, printer);
	    	String indent = getIndentationOnSourceFile(classDecl);
	    	printer.println(indent+getClassDeclaration(classDecl));
	    }

	    super.visitClassDef(classDecl);

	    if (!isRootType)
	    {
	    	String indent = getIndentationOnSourceFile(classDecl);
	    	PrintWriter printer = getPrinter();
	    	printer.println(indent+"}");
	    	if (firstGeneration)
	    	{
	    		finishPrinter();
	    		classInfo.addMember(getClassDeclaration(classDecl), writer.toString());//it does not allow innerclasses with legacy methods
	    	}
	    }
    }

	private void visitSingleLineClass(JCClassDecl classDecl, boolean isRootType, boolean firstGeneration)
    {
		if (!isRootType)
		{
			StringWriter writer = null;
			if (firstGeneration)
			{
				writer = createPrinter();
			}
			int startPos = lineMap.getStartPosition(lineMap.getLineNumber(classDecl.getStartPosition()));
			int endPos = lineMap.getStartPosition(lineMap.getLineNumber(classDecl.getEndPosition(compilationUnit.endPositions))+1);
			PrintWriter printer = getPrinter();
			printer.println(fileContent.subSequence(startPos, endPos).toString());
			if (firstGeneration)
			{
				finishPrinter();
				classInfo.addMember(getClassDeclaration(classDecl), writer.toString());
			}
	    }
    }
	
	private ClassInfo createClassInfo()
    {
		ClassInfo classInfo = createClassInfo(rootType);
		
		int commentEndPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(compilationUnit.getPackageName().getStartPosition()));
		String comment = fileContent.subSequence(0, commentEndPos).toString();
		classInfo.setFileComment(comment);
	    classInfo.setPackageName(compilationUnit.getPackageName().toString());
	    return classInfo;
    }

	private void checkJavadoc(JCTree methodDecl, PrintWriter printer)
    {
		String javadoc = getJavadoc(methodDecl);
		if (javadoc != null && javadoc.length() > 0)
		{
			printer.print(javadoc);
		}
    }
	
	protected String getMethodDescriptor(JCMethodDecl methodDecl)
    {
	    StringBuilder builder = new StringBuilder(methodDecl.getName());
	    builder.append("(");
	    for (JCVariableDecl param : methodDecl.getParameters())
	    {
	        builder.append(param.toString());
	    }
	    builder.append("(");
	    String methodDesc = builder.toString();
	    return methodDesc;
    }

	private java.util.LinkedList<PrintWriter> printers = new LinkedList<PrintWriter>();
	
	protected StringWriter createPrinter()
	{
		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		printers.addFirst(printer);
		return writer;
	}
	
	protected void finishPrinter()
	{
		printers.removeFirst();
	}
	
	protected PrintWriter getPrinter()
	{
		return printers.size()>0?printers.getFirst():null;
	}
}