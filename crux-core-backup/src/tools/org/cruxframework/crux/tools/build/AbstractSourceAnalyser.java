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

import java.io.IOException;
import java.util.regex.Pattern;

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.thirdparty.guava.common.base.Splitter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Position.LineMap;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractSourceAnalyser extends TreeScanner 
{
	protected LineMap lineMap;
	protected JCCompilationUnit compilationUnit;
	protected CharSequence fileContent;
	protected int lastLine;

	public AbstractSourceAnalyser()
	{
	}

	@Override
	public void visitTopLevel(JCCompilationUnit compilationUnit)
	{
		this.compilationUnit = compilationUnit;
		try
		{
			fileContent = compilationUnit.getSourceFile().getCharContent(true);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}

		lineMap = compilationUnit.getLineMap();
		lastLine = (int) lineMap.getLineNumber(fileContent.length() - 1);

		doVisitTopLevel(compilationUnit);
		super.visitTopLevel(compilationUnit);
	}

	protected abstract void doVisitTopLevel(JCCompilationUnit compilationUnit);

	protected String getClassDeclaration(JCClassDecl classDecl)
	{
		ClassInfo classInfo = createClassInfo(classDecl);
		return classInfo.getClassDeclaration();
	}

	protected JCClassDecl getRootType(JCCompilationUnit compilationUnit, boolean acceptsNull)
    {
	    List<JCTree> typeDecls = compilationUnit.getTypeDecls();
        if (typeDecls.size() == 0)
        {
        	if (acceptsNull)
        	{
        		return null;
        	}
        	throw new SourceAnalyserException("Can not found any Type in this compilation unit.");
        }
        else if (typeDecls.size() > 1)
        {
        	if (acceptsNull)
        	{
        		return null;
        	}
        	throw new SourceAnalyserException("More than one type is declared in this compilation unit.");
        }
        return (JCClassDecl) typeDecls.get(0);
    }

	protected String getIndentationOnSourceFile(JCClassDecl tree)
	{
		int memberStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(tree.getStartPosition()));
		return fileContent.subSequence(memberStartPos, tree.getStartPosition()).toString();
	}

	protected String getSourceString(JCMethodDecl tree)
	{
		int startPosition = getMethodStartPosition(tree);
		int memberStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(startPosition));
		int memberEndPos;
		JCBlock body = tree.getBody();
		if (body != null)
		{
			memberEndPos = getImplementedMethodEndPosition(tree);
		}
		else
		{
			if (isNativeJsMethod(tree))
			{
				memberEndPos = getNativeMethodEndPosition(memberStartPos);
			}
			else
			{   // abstract method
				memberEndPos = getAbstractMethodEndPosition(tree, memberStartPos);
			}
		}
		return fileContent.subSequence(memberStartPos, memberEndPos).toString();
	}

	private int getImplementedMethodEndPosition(JCMethodDecl method)
    {
		JCBlock body = method.getBody();
		if (body != null)
		{
			int memberEndPos = body.getEndPosition(compilationUnit.endPositions);
			int endPosLineNumber = (int) lineMap.getLineNumber(memberEndPos);
			if (endPosLineNumber < lastLine) {
				memberEndPos = (int) lineMap.getStartPosition(endPosLineNumber + 1);
			}
			return memberEndPos;
		}
		return -1;
    }

	private int getNativeMethodEndPosition(int memberStartPos)
    {
	    int memberEndPos;
	    memberEndPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(memberStartPos) + 1);
	    String contentLine = fileContent.subSequence(memberStartPos, memberEndPos).toString();
	    while (!contentLine.contains("}-*/"))
	    {
	    	int lineStart = memberEndPos;
	    	memberEndPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(memberEndPos) + 1);
	    	contentLine = fileContent.subSequence(lineStart, memberEndPos).toString();
	    }
		int endPosLineNumber = (int) lineMap.getLineNumber(memberEndPos);
		if (endPosLineNumber < lastLine) {
			memberEndPos = (int) lineMap.getStartPosition(endPosLineNumber + 1);
		}
	    return memberEndPos;
    }

	private int getAbstractMethodEndPosition(JCMethodDecl tree, int memberStartPos)
    {
	    int memberEndPos;
	    memberEndPos = (int) tree.getEndPosition(compilationUnit.endPositions);
	    String contentLine = fileContent.subSequence(memberStartPos, memberEndPos).toString();
	    while (!contentLine.contains(";"))
	    {
	    	int lineStart = memberEndPos;
	    	memberEndPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(memberEndPos) + 1);
	    	contentLine = fileContent.subSequence(lineStart, memberEndPos).toString();
	    }
	    return memberEndPos;
    }

	private int getMethodStartPosition(JCMethodDecl tree)
    {
		// There is a bug on javac parser. If a method does not have any modifier, tree.getStartPosition() returns -1;
	    int startPosition;
		if (StringUtils.isEmpty(tree.getModifiers().toString()))
		{
			if (tree.getReturnType()!= null)
			{
				//if we dont have modifiers, try to find the position of method's return type declaration
				startPosition = tree.getReturnType().getStartPosition();
			}
			else
			{
				//if we dont have return type we must have body (we are a constructor)...
				startPosition = (int) lineMap.getStartPosition(lineMap.getLineNumber(tree.getBody().getStartPosition())-1);
			}
		}
		else
		{
			// when we have modifiers, we have no bug..
			startPosition = tree.getStartPosition();
		}
	    return startPosition;
    }

	protected String getSourceString(JCVariableDecl tree)
	{
		int startPosition;
		boolean isEnum = (tree.mods.flags&Flags.ENUM)!=0;
		if (isEnum)
		{
			// There is a bug on javac parser. http://bugs.sun.com/view_bug.do?bug_id=6472751. getStartPosition() returns -1.
			startPosition = tree.pos;
		}
		else
		{
			startPosition = tree.getStartPosition();
		}
		int memberStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(startPosition));
		int memberEndPos = tree.getEndPosition(compilationUnit.endPositions);
		int endPosLineNumber = (int) lineMap.getLineNumber(memberEndPos);
		if (endPosLineNumber < lastLine) {
			memberEndPos = (int) lineMap.getStartPosition(endPosLineNumber + 1);
		}
		return fileContent.subSequence(memberStartPos, memberEndPos).toString();
	}

	protected boolean isNativeJsMethod(JCMethodDecl tree)
	{
		if (tree.getModifiers().toString().contains("native"))
		{
			int memberStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(tree.getStartPosition()));
			int memberEndPos = tree.getTree().getEndPosition(compilationUnit.endPositions);
			int endPosLineNumber = (int) lineMap.getLineNumber(memberEndPos);
			if (endPosLineNumber < lastLine) {
				memberEndPos = (int) lineMap.getStartPosition(endPosLineNumber + 1);
			}
			String methodDeclarationString = fileContent.subSequence(memberStartPos, memberEndPos).toString();
			return (methodDeclarationString.contains("/*-{"));
			}
		return false;
	}

	protected String getJavadoc(JCTree tree) 
	{
		if (compilationUnit.docComments.containsKey(tree)) {
			int startPos = tree.getStartPosition();
			final String docComment = compilationUnit.docComments.get(tree);
			final StringBuffer docRegex = new StringBuffer(".*" + Pattern.quote("/**"));
			for (String line : Splitter.on("\n").split(docComment)) 
			{
				docRegex.append("[\\s]*")  // each line can optionally start with whitespaces.
				/*
				 * The starting '*' is optional.  javadoc doesn't require each
				 * line to with '*'.  One-liner javadoc also doesn't have the
				 * leading '*'.
				 */
				.append(Pattern.quote("*") + "?")
				.append(Pattern.quote(line));
			}
			docRegex
			/*
			 * The final * is optional.  Single-line javadoc needs it, but
			 * multiline javadoc doesn't have it (the '*' on the last line accounts
			 * for the '*'.
			 */
			.append(Pattern.quote("*") + "?")
			.append(Pattern.quote("/"))
			.append(".*");  // in case there are other comments (line or block) after this.
			final Pattern docPattern = Pattern.compile(docRegex.toString(), Pattern.MULTILINE | Pattern.DOTALL);

			int commentStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(startPos));
			boolean matched = docPattern.matcher(fileContent.subSequence(commentStartPos, startPos)).matches();
			while (!matched && commentStartPos > 0) 
			{
				/*
				 * Keep going back, one line at a time, until we match the full javadoc.
				 * The 'commentStartPos > 0' sholdn't be necessary unless we have a bug.
				 */
				commentStartPos = (int) lineMap.getStartPosition(lineMap.getLineNumber(commentStartPos) - 1);
				matched = docPattern.matcher(fileContent.subSequence(commentStartPos, startPos)).matches();
			}
			if (commentStartPos >= 0 && commentStartPos <= startPos)
			{
				return StringUtils.rTrim(fileContent.subSequence(commentStartPos, startPos).toString())+"\n";
			}
        }
		return null;
	}
	
	protected ClassInfo createClassInfo(JCClassDecl classDecl)
    {
		ClassInfo classInfo = new ClassInfo();
		
	    classInfo.setClassName(classDecl.getSimpleName().toString());
	    classInfo.setExtendsClause(classDecl.getExtendsClause()!=null?classDecl.getExtendsClause().toString():null);
	    classInfo.setJavadoc(getJavadoc(classDecl));
	    processInheritance(classDecl, classInfo);
	    if (classDecl.getModifiers() != null)
	    {
	    	processClassModifiers(classInfo, classDecl.getModifiers());
	    }
	    processTypeParameters(classDecl, classInfo);
	    return classInfo;
    }

	protected void processTypeParameters(JCClassDecl classDecl, ClassInfo classInfo)
    {
	    List<JCTypeParameter> typeParameters = classDecl.getTypeParameters();
	    if (typeParameters != null)
	    {
	    	for (JCTypeParameter jcTypeParameter : typeParameters)
            {
	    		classInfo.addTypeParameter(jcTypeParameter.toString());
            }
	    }
    }

	protected void processInheritance(JCClassDecl classDecl, ClassInfo classInfo)
    {
	    classInfo.setExtendsClause(classDecl.getExtendsClause()!=null?classDecl.getExtendsClause().toString():null);
		List<JCExpression> implementsClause = classDecl.getImplementsClause();
	    if (implementsClause != null)
	    {
	    	for (JCExpression jcExpression : implementsClause)
	    	{
	    		classInfo.addImplementsClause(jcExpression.toString());
	    	}
	    }
    }

	protected void processClassModifiers(ClassInfo classInfo, JCModifiers jcModifiers)
    {
	    boolean isEnum = (jcModifiers.flags&Flags.ENUM)!=0;
	    boolean isInterface = (jcModifiers.flags&Flags.INTERFACE)!=0;
	    boolean isAnnotation = (jcModifiers.flags&Flags.ANNOTATION)!=0;
	    classInfo.setAnnotation(isAnnotation);
	    classInfo.setInterface(isInterface && !isAnnotation);
	    classInfo.setEnum(isEnum && !isInterface && !isAnnotation); 
	    if ((jcModifiers.flags & Flags.PUBLIC) != 0)
	    {
	    	classInfo.setVisibility("public");
	    }
	    else if ((jcModifiers.flags & Flags.PRIVATE) != 0)
	    {
	    	classInfo.setVisibility("private");
	    }
	    else if ((jcModifiers.flags & Flags.PROTECTED) != 0)
	    {
	    	classInfo.setVisibility("protected");
	    }
	    else
	    {
	    	classInfo.setVisibility("");
	    }
	    classInfo.setStatic((jcModifiers.flags&Flags.STATIC)!=0);
	    classInfo.setAbstract((jcModifiers.flags&Flags.ABSTRACT)!=0);
	    classInfo.setFinal((jcModifiers.flags&Flags.FINAL)!=0);
	    List<JCAnnotation> annotations = jcModifiers.annotations;
	    if (annotations != null)
	    {
	    	StringBuilder builder = new StringBuilder();
	    	for (JCAnnotation jcAnnotation : annotations)
	        {
	            builder.append(jcAnnotation.toString()+"\n");
	        }
	    	classInfo.setAnnotations(builder.toString());
	    }
    }	
	
	protected Position getClassDeclPos(JCClassDecl t) 
	{
		List<JCTree> p = t.getMembers();
		int s = t.getStartPosition();
		int e;
		if (p != null && p.size() > 0)
		{
			JCTree tree = p.last();
			if (tree instanceof JCVariableDecl)
			{
				e = tree.getEndPosition(compilationUnit.endPositions);
			}
			else if (tree instanceof JCClassDecl)
			{
				e = getClassDeclPos((JCClassDecl) tree).end;
			}
			else if (tree instanceof JCMethodDecl)
			{
				JCMethodDecl jcMethodDecl = (JCMethodDecl) tree;
				long flags = jcMethodDecl.getModifiers().flags;
				if ((flags & Flags.NATIVE) != 0)
				{
					e = getNativeMethodEndPosition(s);
				}
				else if ((flags & Flags.ABSTRACT) != 0)
				{
					e = getAbstractMethodEndPosition(jcMethodDecl, s);
				}
				else
				{
					e = getImplementedMethodEndPosition(jcMethodDecl);
				}
			}
			else
			{
				e = tree.getEndPosition(compilationUnit.endPositions);
			}
		}
		else
		{
			e = (int) lineMap.getStartPosition(lineMap.getLineNumber(s) + 1);
			String contentLine = fileContent.subSequence(s, e).toString();
			while (!contentLine.contains("}"))
			{
				int lineStart = e;
				if (e < lastLine) 
				{
					e = (int) lineMap.getStartPosition(lineMap.getLineNumber(e) + 1);
				}
				else
				{
					e = fileContent.length() -1;
				}
				contentLine = fileContent.subSequence(lineStart, e).toString();
			}
		}
		return new Position(s, e);
	}
	
	protected static class Position 
	{
		public final Integer start;
		public final Integer end;

		public Position(Integer start, Integer end) 
		{
			this.start = start;
			this.end = end;
		}
	}
}