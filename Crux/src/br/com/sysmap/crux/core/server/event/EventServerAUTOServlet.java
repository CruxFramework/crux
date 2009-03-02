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
