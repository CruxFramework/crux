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

	<xsl:template name="menuBar" match="crux:menuBar">
		
		<xsl:element name="span" namespace="{$xhtmlNS}">
			
			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:if test="string-length(@vertical) > 0">
				<xsl:attribute name="_vertical" select="@vertical"/>
			</xsl:if>
			
			<xsl:if test="string-length(@autoOpen) > 0">
				<xsl:attribute name="_autoOpen" select="@autoOpen"/>
			</xsl:if>
			
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:attribute name="_itemType" select="local-name()"/>
					<xsl:choose>
						<xsl:when test="local-name() = 'separator'">
							<xsl:value-of select="' '"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="menuItemType" select="local-name()"/>
							<xsl:if test="string-length(@onExecute) > 0">
								<xsl:attribute name="_onexecute" select="@onExecute"/>
							</xsl:if>
							<xsl:for-each select="child::*">
								<xsl:element name="span" namespace="{$xhtmlNS}">
									<xsl:choose>
										<xsl:when test="local-name() = 'caption'">
											<xsl:if test="$menuItemType = 'html'">
												<xsl:copy-of select="child::*" copy-namespaces="no" exclude-result-prefixes="#all"/>
											</xsl:if>
											<xsl:if test="$menuItemType = 'text'">
												<xsl:value-of select="text()"/>
											</xsl:if>
										</xsl:when>
										<xsl:when test="local-name() = 'items'">
											<xsl:for-each select="child::*">
												<xsl:call-template name="menuBar" />	
											</xsl:for-each>
										</xsl:when>
									</xsl:choose>
								</xsl:element>
							</xsl:for-each>
						</xsl:otherwise>						
					</xsl:choose>
				</xsl:element>
			</xsl:for-each>
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>	
</xsl:stylesheet>