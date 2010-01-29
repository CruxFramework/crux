package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SVNService extends RemoteService
{
	String getXmlFile(String fileName, boolean escapeHtml);
	String getJavaFile(String fileName, boolean escapeHtml);
}
