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
package br.com.sysmap.crux.core.declarativeui.template;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class TemplateException extends RuntimeException
{
	private static final long serialVersionUID = 7025740896654216172L;

	public TemplateException()
	{
		super();
	}

	public TemplateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TemplateException(String message)
	{
		super(message);
	}

	public TemplateException(Throwable cause)
	{
		super(cause);
	}
}
