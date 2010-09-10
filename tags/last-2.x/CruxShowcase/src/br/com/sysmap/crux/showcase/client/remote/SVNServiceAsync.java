package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SVNServiceAsync
{
	void getXmlFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
	void getJavaFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
}
