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
package br.com.sysmap.crux.core.server.event;

import br.com.sysmap.crux.core.server.lifecycle.BaseServlet;
import br.com.sysmap.crux.core.server.lifecycle.annotation.DispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.RenderResponsePhase;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.AutoDispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.phase.render.AutoRenderResponsePhase;

/**
 * Servlet to handle AUTO requests. This kind of event will make the server process a method call and send 
 * back to the client an update block for each modified component.
 * @author Thiago
 *
 */
@DispatchPhase(AutoDispatchPhase.class)
@RenderResponsePhase(AutoRenderResponsePhase.class)
public class EventServerAUTOServlet extends BaseServlet
{
	private static final long serialVersionUID = -7786236115297341839L;
}
