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

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.Legacy.NoTarget;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.util.List;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LegacySourceAnalyser extends AbstractSourceAnalyser 
{
	private JCClassDecl rootType;
	private final JavacTrees javacTrees;
	private LegacyInfo legacyInfo;
	private Map<String, String> imports = new HashMap<String, String>();
	
	public LegacySourceAnalyser(JavacTrees javacTrees)
    {
		this.javacTrees = javacTrees;
    }
	
	@Override
	protected void doVisitTopLevel(JCCompilationUnit compilationUnit)
    {
		rootType = getRootType(compilationUnit, true);
		legacyInfo = new LegacyInfo();
    }
	
	@Override
	public void visitImport(JCImport importDecl)
	{
		String className = importDecl.getQualifiedIdentifier().toString();
		int index = className.lastIndexOf('.');
		String simpleName;
		if (index > -1)
		{
			simpleName = className.substring(index+1);
		}
		else
		{
			simpleName = className;
		}
		imports.put(simpleName, className);
	}
	
	@Override
	public void visitAnnotation(JCAnnotation annotationDecl)
	{
		if (rootType != null)
		{
			TreePath path = javacTrees.getPath(compilationUnit, annotationDecl);
			boolean isRootType = path.getParentPath().getParentPath().getLeaf().equals(rootType);

			if (isRootType)
			{
				String annotation = annotationDecl.getAnnotationType().toString();
				if (Legacy.class.getSimpleName().equals(annotation))
				{
					legacyInfo.setLegacy(true);
					List<JCExpression> arguments = annotationDecl.getArguments();
					if (arguments != null && arguments.size() == 1)
					{
						// very simple and fragile implementation
						JCExpression jcExpression = arguments.get(0);
						String simpleName = jcExpression.toString().replaceFirst("value", "").trim().replaceFirst("=", "").trim().replace(".class", "").trim();
						String targetClassName = imports.containsKey(simpleName)? imports.get(simpleName): compilationUnit.getPackageName().toString()+"."+simpleName;
						if (!targetClassName.equals(NoTarget.class.getCanonicalName()))
						{
							legacyInfo.setTargetClassName(targetClassName);
						}
					}
				}
			}
		}
	    super.visitAnnotation(annotationDecl);
	}

	public LegacyInfo getLegacyInfo()
    {
	    return legacyInfo;
    }
}