package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Expose;

public abstract class AbstractGridSourcesController extends SourcesController {
	
	private boolean dtoLoaded;
	private boolean serviceImplLoaded;
	private boolean dsLoaded;
	
	protected abstract String getDtoFilePath();
	protected abstract String getServiceImplFilePath();	
	protected abstract String getDSFilePath();	
	
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

	@Expose
	public void loadDS() {
		
		if(!dsLoaded)
		{
			dsLoaded = true;
			loadFile("client/datasource/" + getDSFilePath(), "dataSource");
		}
	}
}