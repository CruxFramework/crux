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

	

	<xsl:template name="tree" match="crux:tree">
	
		<xsl:element name="span" namespace="http://www.w3.org/1999/xhtml">

			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:if test="string-length(@useLeafImages) > 0">
				<xsl:attribute name="_useLeafImages" select="@useLeafImages"/>
			</xsl:if>
			
			<xsl:if test="string-length(@openSelectedItem) > 0">
				<xsl:attribute name="_openSelectedItem" select="@openSelectedItem"/>
			</xsl:if>
			
			<xsl:for-each select="child::*">
				<xsl:apply-templates select="current()" />
			</xsl:for-each>
			
			<xsl:value-of select="' '"/>

		</xsl:element> 

	</xsl:template>
	
	
	
	<xsl:template name="treeItem" match="crux:item">
	
		<xsl:element name="span" namespace="http://www.w3.org/1999/xhtml">
	
			<xsl:if test="string-length(@selected) > 0">
				<xsl:attribute name="_selected" select="@selected"/>
			</xsl:if>
			
			<xsl:if test="string-length(@state) > 0">
				<xsl:attribute name="_state" select="@state"/>
			</xsl:if>
			
			<xsl:for-each select="child::*">
			
				<xsl:choose>
				
					<xsl:when test="local-name() = 'textTitle'">
						<xsl:element name="span" namespace="http://www.w3.org/1999/xhtml">
							<xsl:attribute name="_text" select="text()"/>
							<xsl:value-of select="' '" />
						</xsl:element>			
					</xsl:when>	
				
					<xsl:when test="local-name() = 'widgetTitle'">
						<xsl:apply-templates select="child::*[1]" />
					</xsl:when>
				
					<xsl:otherwise>
						<xsl:apply-templates select="current()" />
					</xsl:otherwise>
					
				</xsl:choose>
				
			</xsl:for-each>			
			
			<xsl:value-of select="' '"/>

		</xsl:element> 

	</xsl:template>
	
</xsl:stylesheet>