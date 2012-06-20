package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;

@Controller("frameController")
public class FrameController implements FrameControllerCrossDoc
{
	@Create
	protected Person person;
	
	/**
	 * @see org.cruxframework.crux.showcase.client.controller.FrameControllerCrossDoc#setPerson(org.cruxframework.crux.showcase.client.controller.Person)
	 */
	public void setPerson(Person person)
	{
		this.person = person;
	}
}
