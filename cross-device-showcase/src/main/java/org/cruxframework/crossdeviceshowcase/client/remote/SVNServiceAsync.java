package org.cruxframework.crossdeviceshowcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SVNServiceAsync
{
	void getXmlFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
	void getJavaFile(String fileName, boolean escapeHtml, AsyncCallback<String> callback);
}
