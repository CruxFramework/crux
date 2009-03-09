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
package br.com.sysmap.crux.core.server.lifecycle.phase.render;

import java.io.PrintWriter;

import org.apache.commons.beanutils.BeanUtils;

import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;
import br.com.sysmap.crux.core.utils.HtmlUtils;

public abstract class AbstractRenderResponsePhase implements Phase
{
	/**
	 * Render all dirty properties
	 * @param dto
	 * @param dispatchData 
	 * @param writer
	 */
	protected void renderDtos(Object dto, DispatchData dispatchData, PrintWriter writer) 
	{
		for (String key : dispatchData.getParameters()) 
		{
			try
			{
				Object propValueAnt = dispatchData.getParameter(key);
				String propValue = BeanUtils.getProperty(dto, key);
				if (propValue == null && propValueAnt != null)
				{
					writer.println("<span id=\""+key+"\" value=\"\" ></span>");
				}
				else if (propValue != null && (propValueAnt == null || !propValueAnt.equals(propValue)))
				{
					writer.println("<span id=\""+key+"\" value=\""+HtmlUtils.filterValue(propValue)+"\" ></span>");
				}
			}
			catch (Throwable e) 
			{
				// Se o DTO não tiver uma propriedade declarada, isto não é um erro.
			}			
		}
	}
}
