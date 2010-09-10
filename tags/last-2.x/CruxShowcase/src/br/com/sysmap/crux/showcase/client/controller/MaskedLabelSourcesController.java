package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("maskedLabelSourcesController")
public class MaskedLabelSourcesController extends SourcesController {
	
	private boolean formatterLoaded;
	
	@Expose
	public void loadFormatterSource() {
		
		if(!formatterLoaded)
		{
			formatterLoaded = true;
			loadFile("client/formatter/BirthdayFormatter.java", "formatterSource");
		}
	}
}