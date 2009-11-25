package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Expose;

public abstract class AbstractGridSourcesController extends SourcesController {
	
	private boolean dtoLoaded;
	private boolean serviceImplLoaded;
	
	protected abstract String getDtoFilePath();
	protected abstract String getServiceImplFilePath();	
	
	@Expose
	public void loadDTO() {
		
		if(!dtoLoaded)
		{
			dtoLoaded = true;
			loadFile("client/dto/" + getDtoFilePath(), "contact");
		}
	}	
	
	@Expose
	public void loadServiceImpl() {
		
		if(!serviceImplLoaded)
		{
			serviceImplLoaded = true;
			loadFile("server/" + getServiceImplFilePath(), "serviceImpl");
		}
	}
}