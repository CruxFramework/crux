package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;

/**
 * Defines the contract to invoke the FrameController from another window. 
 */
public interface FrameControllerCrossDoc extends CrossDocument
{
	void setPerson(Person person);
}
