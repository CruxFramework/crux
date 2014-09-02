/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.tools.codeserver.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CompilationMessage extends JavaScriptObject 
{
	public static enum CompilerOperation { START, END }

	protected CompilationMessage(){}

	public final CompilerOperation getOperation()
	{
		return CompilerOperation.valueOf(getOp());
	}
	
	public final native String getModule()/*-{
		return this.module;
	}-*/;
	
	public final native boolean getStatus()/*-{
		if (this.status)
		{
			return true;
		}
		return false;
	}-*/;
	
	private native String getOp()/*-{
		return this.op;
	}-*/;
}
