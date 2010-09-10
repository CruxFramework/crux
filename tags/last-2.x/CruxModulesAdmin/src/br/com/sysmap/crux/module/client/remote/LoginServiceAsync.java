package br.com.sysmap.crux.module.client.remote;

import br.com.sysmap.crux.module.client.dto.Repository;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface LoginServiceAsync
{
	void authenticate(String url, String user, String password, AsyncCallback<Void> callback);
	void authenticate(String[] urls, String[] users, String[] passwords, AsyncCallback<Void> callback);
	void getRegisteredRepositories(AsyncCallback<Repository[]> callback);
}
