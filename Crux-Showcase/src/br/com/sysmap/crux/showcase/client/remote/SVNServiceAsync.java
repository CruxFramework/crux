package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SVNServiceAsync
{
	void getJavaFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
	void getXmlFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
}
