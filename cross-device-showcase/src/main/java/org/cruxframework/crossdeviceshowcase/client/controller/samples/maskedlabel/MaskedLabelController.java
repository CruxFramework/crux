/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client.controller.samples.maskedlabel;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.maskedlabel.MaskedLabel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("maskedLabelController")
public class MaskedLabelController 
{
	@Expose
	public void setDate()
	{
		MaskedLabel widget = View.of(this).getWidget("birthDate", MaskedLabel.class);
		
		@SuppressWarnings("deprecation") //Don't try this at home! :D
		Date birth = new Date(60,12,23);
		
		widget.setUnformattedValue(birth);
	}
}
