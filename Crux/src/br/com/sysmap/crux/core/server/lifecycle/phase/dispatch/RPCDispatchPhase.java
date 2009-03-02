package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.server.json.JsonResult;
import br.com.sysmap.crux.core.server.json.JsonSerializer;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

/**
 * Dispatcher for handle crux RPC events at the server side.
 * @author Thiago
 *
 */
public class RPCDispatchPhase extends AbstractDispatchPhase
{
	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		try 
		{
			DispatchData dispatchData = context.getDispatchData();
			String evtCall = dispatchData.getEvtCall();
			String[] call = RegexpPatterns.REGEXP_DOT.split(evtCall);
				
			Object controller = getController(call[0]);
			bindControllerParameters(controller, dispatchData);

			Class<?>[] parametersTypes = {};
			Object[] parametersValues = {};
			Object result = dispatch(controller, call[1], parametersTypes, parametersValues);
			context.setCycleResult(new JsonResult(JsonResult.CODE_SUCCESS,JsonSerializer.objMarshall(result)));
			context.setDto(controller);
		} 
		catch (ValidateException e) 
		{
			context.interruptCycle(e.getLocalizedMessage());
		} 
		catch (Throwable e) 
		{
			context.setCycleResult(null);
			throw new PhaseException(e);
		}
	}
}
