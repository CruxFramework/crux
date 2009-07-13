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

	<xsl:template name="cellPanel">
		
			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:for-each select="child::*">
				
				<xsl:choose>
					
					<xsl:when test="local-name() = 'cell'">
					
						<xsl:element name="span" namespace="{$xhtmlNS}">
					
							<xsl:if test="string-length(@horizontalAlignment) > 0">
								<xsl:attribute name="_cellHorizontalAlignment" select="@horizontalAlignment"/>	
							</xsl:if>

							<xsl:if test="string-length(@verticalAlignment) > 0">
								<xsl:attribute name="_cellVerticalAlignment" select="@verticalAlignment"/>	
							</xsl:if>

							<xsl:if test="string-length(@width) > 0">
								<xsl:attribute name="_cellWidth" select="@width"/>	
							</xsl:if>

							<xsl:if test="string-length(@height) > 0">
								<xsl:attribute name="_cellHeight" select="@height"/>	
							</xsl:if>					
							
							<xsl:if test="string-length(@direction) > 0">
								<xsl:choose>
									<xsl:when test="@direction = 'lineEnd'">
										<xsl:attribute name="_direction" select="'line_end'"/>
									</xsl:when>
									<xsl:when test="@direction = 'lineStart'">
										<xsl:attribute name="_direction" select="'line_start'"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="_direction" select="@direction"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>

							<xsl:for-each select="child::*">
								<xsl:apply-templates select="current()"/>
							</xsl:for-each>

						</xsl:element>
						
					</xsl:when>
					
					<xsl:otherwise>
						<xsl:apply-templates select="current()"/>
					</xsl:otherwise>
					
				</xsl:choose>
			
			</xsl:for-each>
			
			<xsl:value-of select="' '"/>
		
	</xsl:template>
	
</xsl:stylesheet>