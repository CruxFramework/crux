package br.com.sysmap.crux.module.client.remote;

import br.com.sysmap.crux.module.client.dto.Repository;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface LoginService extends RemoteService
{
	void authenticate(String url, String user, String password) throws LoginException;
	void authenticate(String[] urls, String[] users, String[] passwords) throws LoginException;
	Repository[] getRegisteredRepositories();
}
