/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.tools.quickstart.client.remote;

import br.com.sysmap.crux.tools.quickstart.client.dto.DirectoryInfo;
import br.com.sysmap.crux.tools.quickstart.client.dto.ProjectInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public interface QuickStartServiceAsync
{
	void getProjectInfoDefaultValues(AsyncCallback<ProjectInfo> callback);
	void getDirectoryInfo(String directoryPath, AsyncCallback<DirectoryInfo> callback);
	void generateProject(ProjectInfo projectInfo, AsyncCallback<Boolean> callback);
}
