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
