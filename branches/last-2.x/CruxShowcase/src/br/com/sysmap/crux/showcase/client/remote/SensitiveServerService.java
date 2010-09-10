package br.com.sysmap.crux.showcase.client.remote;

import br.com.sysmap.crux.core.client.rpc.st.UseSynchronizerToken;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SensitiveServerService extends RemoteService
{
	@UseSynchronizerToken
	String sensitiveMethod();
	
	@UseSynchronizerToken(blocksUserInteraction=false)
	String sensitiveMethodNoBlock();
}
