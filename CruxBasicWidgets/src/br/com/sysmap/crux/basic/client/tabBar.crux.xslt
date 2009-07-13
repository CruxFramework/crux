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
	xmlns:crux="http://www.sysmap.com.br/crux/ui/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	
	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="tabBar" match="crux:tabBar">
	
		<xsl:element name="span" namespace="{$xhtmlNS}">

			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:if test="string-length(@visibleTab) > 0">
				<xsl:attribute name="_visibleTab" select="@visibleTab"/>
			</xsl:if>
			
			<!-- for each stack item -->
			<xsl:for-each select="child::*">
				
				<xsl:element name="span" namespace="{$xhtmlNS}">
				
					<xsl:call-template name="widgetAttributes">
						<xsl:with-param name="skipWidgetAttributes" select="true()"/>
					</xsl:call-template>
					
					<xsl:if test="string-length(@enabled) > 0">
						<xsl:attribute name="_enabled" select="@enabled"/>
					</xsl:if>
					
					<xsl:if test="string-length(@wordWrap) > 0">
						<xsl:attribute name="_wordWrap" select="@wordWrap"/>
					</xsl:if>
					
					<xsl:choose>
						<xsl:when test="child::*[1]/local-name() = 'text'">
							<xsl:attribute name="_widgetTitle" select="child::*[1]/text()"/>
						</xsl:when>
						<xsl:when test="child::*[1]/local-name() = 'html'">
							<xsl:copy-of select="child::*[1]/child::*" copy-namespaces="no"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="child::*[1]/child::*"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:for-each>			
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>
	
</xsl:stylesheet>