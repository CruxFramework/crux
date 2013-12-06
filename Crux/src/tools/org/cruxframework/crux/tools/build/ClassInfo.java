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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassInfo
{
	private Set<String> imports = new LinkedHashSet<String>();
	private Map<String, String> members = new LinkedHashMap<String, String>();
	private String className;
	private String fileComment;
	private boolean intf; 
	private boolean annotation;
	private boolean enumType;
	private String visibilityModifier;
	private String annotationsModifier;
	private boolean finalModifier;
	private boolean abstractModifier;
	private boolean staticModifier;
	private Set<String> implementList = new HashSet<String>();
	private List<String> typeParameters = new ArrayList<String>();
	private String extendsClause;
	private String packageName;
	private String javadoc;
	
	void addImport(String importDecl)
	{
		imports.add(importDecl);
	}

	public void addMember(String meberDesc, String memberString)
    {
		members.put(meberDesc, memberString);
    }

	public String getClassName()
    {
    	return className;
    }

	public void setClassName(String className)
    {
    	this.className = className;
    }

	public boolean isAnnotation()
    {
    	return annotation;
    }

	public void setAnnotation(boolean annotation)
    {
    	this.annotation = annotation;
    }

	public boolean isInterface()
    {
    	return intf;
    }

	public void setInterface(boolean intf)
    {
    	this.intf = intf;
    }

	public boolean isEnum()
    {
    	return enumType;
    }

	public void setEnum(boolean enumType)
    {
    	this.enumType = enumType;
    }

	public String getVisibility()
    {
    	return visibilityModifier;
    }

	public void setVisibility(String visibilityModifier)
    {
    	this.visibilityModifier = visibilityModifier;
    }

	public boolean isFinal()
    {
    	return finalModifier;
    }

	public void setFinal(boolean finalModifier)
    {
    	this.finalModifier = finalModifier;
    }

	public boolean isAbstract()
    {
    	return abstractModifier;
    }

	public void setAbstract(boolean abstractModifier)
    {
    	this.abstractModifier = abstractModifier;
    }

	public boolean isStatic()
    {
    	return staticModifier;
    }

	public void setStatic(boolean staticModifier)
    {
    	this.staticModifier = staticModifier;
    }

	public String getExtendsClause()
    {
    	return extendsClause;
    }

	public void setExtendsClause(String extendsClause)
    {
    	this.extendsClause = extendsClause;
    }
	
	public String getAnnotations()
    {
    	return annotationsModifier;
    }

	public void setAnnotations(String annotationsModifier)
    {
    	this.annotationsModifier = annotationsModifier;
    }

	public void addImplementsClause(String implementsClause)
    {
		this.implementList.add(implementsClause);
    }

	public void addTypeParameter(String typeParameter)
    {
		this.typeParameters.add(typeParameter);
    }
	
	public String getPackageName()
    {
    	return packageName;
    }

	public void setPackageName(String packageName)
    {
    	this.packageName = packageName;
    }
	
	public void setJavadoc(String javadoc)
    {
		this.javadoc = javadoc;
    }
	
	public String getJavadoc()
	{
		return javadoc;
	}
	
	public String getFileComment()
    {
    	return fileComment;
    }

	public void setFileComment(String fileComment)
    {
    	this.fileComment = fileComment;
    }

	@Override
	public String toString()
	{
	    StringBuilder builder = new StringBuilder();
	    builder.append(getFileComment());
	    builder.append("\n");
	    builder.append(getPackageDeclaration());
	    builder.append("\n");
	    builder.append(getImportsDeclaration());
	    builder.append("\n");
		builder.append(getClassDeclaration());
	    builder.append("\n");
		builder.append(getClassMembersDeclaration());
	    builder.append("}");
		return builder.toString();
	}

	public String getClassMembersDeclaration()
    {
		StringBuilder builder = new StringBuilder();
		for (String member : members.values())
        {
	        builder.append(member);
        }
		
		return builder.toString();
    }

	public String getImportsDeclaration()
    {
		StringBuilder builder = new StringBuilder();
		for (String importDecl : imports)
        {
	        builder.append(importDecl);
        }
		
		return builder.toString();
    }
	
	public String getPackageDeclaration()
    {
	    if (packageName != null && packageName.trim().length() > 0)
	    {
	    	return ("package "+packageName+";\n");
	    }
	    return "";
    }

	public String getClassDeclaration()
    {
	    StringBuilder builder = new StringBuilder();

		if (!StringUtils.isEmpty(javadoc))
		{
			builder.append(javadoc.trim()+"\n");
		}
		if (!StringUtils.isEmpty(annotationsModifier))
		{
			builder.append(annotationsModifier.trim()+"\n");
		}

		if (!StringUtils.isEmpty(visibilityModifier))
		{
			builder.append(visibilityModifier.trim()+" ");
		}
		
		if (isStatic())
		{
			builder.append("static ");
		}
		
		if (isAbstract())
		{
			builder.append("abstract ");
		}

		if (isFinal())
		{
			builder.append("final ");
		}

		builder.append(getTypeDeclaration());
		builder.append(className);
		builder.append(getTypeParametersString());
		builder.append(getExtendsClauseString());
		builder.append(getImplementationListString());
		builder.append("{\n");
		return builder.toString();
    }

	private String getTypeDeclaration()
    {
	    if (isAnnotation())
		{
			return "@interface ";
		}
		else if (isInterface())
		{
			return "interface ";
		}
		else if (isEnum())
		{
			return "enum ";
		}
	    return "class ";
    }

	private String getExtendsClauseString()
    {
		StringBuilder builder = new StringBuilder();
	    if (extendsClause != null)
		{
			builder.append(" extends "+extendsClause);
		}
	    return builder.toString();
    }

	private String getTypeParametersString()
    {
		StringBuilder builder = new StringBuilder();
	    if (typeParameters != null && typeParameters.size() > 0)
		{
			builder.append('<');
			boolean needsComma = false;
			for (String typeParameter : typeParameters)
            {
				if (needsComma)
				{
					builder.append(",");
				}
				else
				{
					needsComma = true;
				}
				builder.append(typeParameter);
            }
			builder.append('>');
		}
	    return builder.toString();
    }

	private String getImplementationListString()
    {
		StringBuilder builder = new StringBuilder();
	    if (implementList != null && implementList.size() > 0)
		{
			if (isInterface())
			{
				builder.append(" extends ");
			}
			else
			{
				builder.append(" implements ");
			}
			boolean needsComma = false;
			for (String expression : implementList)
			{
				if (needsComma)
				{
					builder.append(",");
				}
				else
				{
					needsComma = true;
				}
				builder.append(expression);
			}
		}
	    return builder.toString();
    }
}
