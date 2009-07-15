<!--
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
 *-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:crux="http://www.sysmap.com.br/crux/basic-widgets/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	
	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="richTextArea" match="crux:richTextArea">
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:call-template name="widgetAttributes"/>
			<xsl:if test="string-length(@backColor) > 0">
				<xsl:attribute name="_backColor" select="@backColor"/>	
			</xsl:if>
			<xsl:if test="string-length(@fontName) > 0">
				<xsl:attribute name="_fontName" select="@fontName"/>	
			</xsl:if>
			<xsl:if test="string-length(@foreColor) > 0">
				<xsl:attribute name="_foreColor" select="@foreColor"/>	
			</xsl:if>
			<xsl:if test="string-length(@fontSize) > 0">
				<xsl:attribute name="_fontSize" select="@fontSize"/>	
			</xsl:if>
			<xsl:if test="string-length(@justification) > 0">
				<xsl:attribute name="_justification" select="@justification"/>	
			</xsl:if>
			<xsl:if test="string-length(@bold) > 0">
				<xsl:attribute name="_bold" select="@bold"/>	
			</xsl:if>
			<xsl:if test="string-length(@italic) > 0">
				<xsl:attribute name="_italic" select="@italic"/>	
			</xsl:if>
			<xsl:if test="string-length(@subscript) > 0">
				<xsl:attribute name="_subscript" select="@subscript"/>	
			</xsl:if>
			<xsl:if test="string-length(@superscript) > 0">
				<xsl:attribute name="_superscript" select="@superscript"/>	
			</xsl:if>
			<xsl:if test="string-length(@underline) > 0">
				<xsl:attribute name="_underline" select="@underline"/>	
			</xsl:if>
			<xsl:if test="string-length(@strikethrough) > 0">
				<xsl:attribute name="_strikethrough" select="@strikethrough"/>	
			</xsl:if>
			<xsl:copy-of select="child::*" copy-namespaces="no"/>
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>
	
</xsl:stylesheet>