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
package br.com.sysmap.crux.tools;

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public interface CompilerMessages
{
	@DefaultServerMessage("[compiler 001] - Error creating the CruxCompiler class: {0}.")
	String cruxCompilerFactoryErrorCreatingCompiler(String errMsg);

	@DefaultServerMessage("[compiler 002] - Error processing program parameters: {0}. Program aborted.")
	String cruxCompilerErrorProcessingParameters(String errMsg);

	@DefaultServerMessage("[compiler 003] - Error compiling files: {0}. Program aborted.")
	String cruxCompilerErrorCompilingFiles(String errMsg);
}
