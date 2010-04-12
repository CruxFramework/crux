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
package br.com.sysmap.crux.core.declarativeui;

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface DeclarativeUIMessages
{
	@DefaultServerMessage("[declarativeui 001] - Error initializing TemplateScanner.")
	String templatesScannerInitializationError(String localizedMessage);
	
	@DefaultServerMessage("[declarativeui 002] - Error creating xslt file from template. Template crux-ui.template.xslt not found")
	String transformerTemplateNotFound();
	
	@DefaultServerMessage("[declarativeui 003] - Error creating xslt file from template. Error reading Template crux-ui.template.xslt: {0}")
	String transformerErrorReadingTemplate(String message);
	
	@DefaultServerMessage("[declarativeui 004] - Error creating xsl transformer: {0}")
	String transformerErrorCreatingTransformer(String message);
	
	@DefaultServerMessage("[declarativeui 005] - Error obtaining screen resource. Screen id: {0}. Message: {1}")
	String cruxHtmlTagsScreenResolverError(String screenId, String message);

	@DefaultServerMessage("[declarativeui 006] - Generating core.xsd file")
	String schemaGeneratorCreatingCoreXSD();

	@DefaultServerMessage("[declarativeui 007] - Generating xsd file for library: {0}.")
	String schemaGeneratorCreatingLibraryXSD(String library);

	@DefaultServerMessage("[declarativeui 008] - XSD Files Generated.")
	String schemaGeneratorXSDFilesGenerated();

	@DefaultServerMessage("[declarativeui 009] - Error creating XSD File: Tag Name expected in processor class: {0}.")
	String schemaGeneratorErrorTagNameExpected(String className);

	@DefaultServerMessage("[declarativeui 010] - Error creating XSD File: ProcessChildren method not found.")
	String schemaGeneratorErrorProcessChildrenMethodNotFound();

	@DefaultServerMessage("[declarativeui 011] - Error creating XSD File: Error generating attributes for Processor.")
	String schemaGeneratorErrorGeneratingAttributesForProcessor();

	@DefaultServerMessage("[declarativeui 012] - Error creating XSD File: Error generating events for Processor.")
	String schemaGeneratorErrorGeneratingEventsForProcessor();

	@DefaultServerMessage("[declarativeui 013] - Error creating XSD File: Error generating children for Processor.")
	String schemaGeneratorErrorGeneratingChildrenForFactory();

	@DefaultServerMessage("[declarativeui 014] - Error creating XSD File: Error generating widgets reference list.")
	String transformerErrorGeneratingWidgetsReferenceList();

	@DefaultServerMessage("[declarativeui 015] - Error creating XSD File: Error generating widgets list.")
	String transformerErrorGeneratingWidgetsList();

	@DefaultServerMessage("[declarativeui 016] - Error creating XML Parser.")
	String templatesScannerErrorBuilderCanNotBeCreated();

	@DefaultServerMessage("[declarativeui 017] - Error parsing template file: {0}.")
	String templatesScannerErrorParsingTemplateFile(String fileName);

	@DefaultServerMessage("[declarativeui 018] - Searching for template files.")
	String templatesScannerSearchingTemplateFiles();

	@DefaultServerMessage("[declarativeui 019] - Error initializing templates pre-processor.")
	String templatesPreProcessorInitializingError();

	@DefaultServerMessage("[declarativeui 020] - Error pre-processing templates.")
	String templatesPreProcessorError();

	@DefaultServerMessage("[declarativeui 021] - Generating template.xsd file. ")
	String schemaGeneratorCreatingTemplateXSD();

	@DefaultServerMessage("[declarativeui 022] - Generating XSD file for library {0}. ")
	String schemaGeneratorCreatingTemplateLibrary(String library);

	@DefaultServerMessage("[declarativeui 023] - Error generating attributes for template {0}. ")
	String templateParserErrorExtractingAttributesForTemplate(String template);

	@DefaultServerMessage("[declarativeui 024] - Error initializing generator.")
	String templateParserErrorInitializingGenerator();

	@DefaultServerMessage("[declarativeui 025] - Error generating children for template {0}. ")
	String schemaGeneratorErrorGeneratingChildrenForTemplate(String localName);
	
	@DefaultServerMessage("[declarativeui 026] - Error scanning for templates. Scanned URL: {0}. Underlying error message: {1}")
	String templatesScanningURLError(String url, String localizedMessage);

	@DefaultServerMessage("[declarativeui 027] - The page {0} is not transformed... Accessing directly.")
	String htmlTagsDoesNotTransformPage(String pathInfo);

	@DefaultServerMessage("[declarativeui 028] - Duplicated template found. Library: {0}. Template: {1}.")
	String templateDuplicatedTemplate(String library, String templateId);

	@DefaultServerMessage("[declarativeui 029] - Template not found. Library: {0}. Template: {1}.")
	String templatesPreProcessorTemplateNotFound(String library, String template);

	@DefaultServerMessage("[declarativeui 030] - Template file modified: {0}.")
	String templatesHotDeploymentScannerTemplateFileModified(String fileName);

	@DefaultServerMessage("[declarativeui 031] - Error scanning dir: {0}.")
	String templatesHotDeploymentScannerErrorScanningDir(String name);
}