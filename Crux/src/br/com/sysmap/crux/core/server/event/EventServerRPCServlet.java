package br.com.sysmap.crux.core.server.event;

import br.com.sysmap.crux.core.server.lifecycle.BaseServlet;
import br.com.sysmap.crux.core.server.lifecycle.annotation.DispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.ParametersBindPhase;
import br.com.sysmap.crux.core.server.lifecycle.annotation.RenderResponsePhase;
import br.com.sysmap.crux.core.server.lifecycle.phase.bind.RPCParametersBindPhase;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.RPCDispatchPhase;
import br.com.sysmap.crux.core.server.lifecycle.phase.render.RPCRenderResponsePhase;

/**
 * Servlet para processamento de chamadas RPC (implementadas via protocolo JSON).
 * @author Thiago
 *
 */
@ParametersBindPhase(RPCParametersBindPhase.class)
@DispatchPhase(RPCDispatchPhase.class)
@RenderResponsePhase(RPCRenderResponsePhase.class)
public class EventServerRPCServlet extends BaseServlet
{
	private static final long serialVersionUID = -2881555465895895737L;
}
