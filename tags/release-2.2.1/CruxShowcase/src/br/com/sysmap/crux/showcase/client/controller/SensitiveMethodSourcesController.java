package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("sensitiveMethodSourcesController")
public class SensitiveMethodSourcesController extends SourcesController {
	
	private boolean serviceLoaded;
	private boolean serviceImplLoaded;

	@Expose
	public void loadServiceSource() {
		
		if(!serviceLoaded){
			serviceLoaded = true;
			
			loadFile("client/remote/SensitiveServerService.java", "serviceInterfaceSource");
		}
	}

	@Expose
	public void loadServiceImplSource() {
		
		if(!serviceImplLoaded){
			serviceImplLoaded = true;
			
			loadFile("server/SensitiveServerServiceImpl.java", "ServiceImplementationSource");
		}
	}
}