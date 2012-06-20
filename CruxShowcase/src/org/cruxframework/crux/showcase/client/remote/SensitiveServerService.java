package org.cruxframework.crux.showcase.client.remote;

import org.cruxframework.crux.core.client.rpc.st.UseSynchronizerToken;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Both methods in this interface are sensitive, but only the first one blocks the user's interface during processing.
 */
public interface SensitiveServerService extends RemoteService {
	
	@UseSynchronizerToken
	String sensitiveMethod();
	
	@UseSynchronizerToken(blocksUserInteraction=false)
	String sensitiveMethodNoBlock();
}
