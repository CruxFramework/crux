package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;

public interface BaseSourcesControllerCrossDoc extends CrossDocument {
	
	public void setSourceTabs(ArrayList<SourceTab> tabs);
	
}