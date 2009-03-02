package br.com.sysmap.crux.core.server.lifecycle;


/**
 * Abstraction for a phase in the request life cycle.
 * @author Thiago
 */
public interface Phase 
{
	void execute(PhaseContext context) throws PhaseException;
}
