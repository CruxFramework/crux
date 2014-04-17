package org.cruxframework.crossdeviceshowcase.client.remote.showcase;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SVNServiceAsync
{
	void getSourceFile(String path, AsyncCallback<String> callback);
	void listSourceFilesForView(String viewName, AsyncCallback<List<String>> callback);
}
