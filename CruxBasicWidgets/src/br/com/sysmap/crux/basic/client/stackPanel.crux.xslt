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

	<xsl:template name="stackPanel" match="crux:stackPanel">
	
		<xsl:element name="span" namespace="{$xhtmlNS}">

			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:if test="string-length(@visibleStack) > 0">
				<xsl:attribute name="_visibleStack" select="@visibleStack"/>
			</xsl:if>
			
			<!-- for each stack item -->
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:choose>
						<xsl:when test="count(child::node()) > 1">
							<xsl:choose>
							
								<!-- has text title -->
								<xsl:when test="child::*[1]/local-name() = 'textTitle'">
									<xsl:attribute name="_widgetTitle" select="child::*[1]/text()"/>
									<xsl:apply-templates select="child::*[2]/child::*"/>
								</xsl:when>
								
								<!-- has HTML title -->
								<xsl:when test="child::*[1]/local-name() = 'htmlTitle'">
									<xsl:element name="span" namespace="{$xhtmlNS}">
										<xsl:copy-of select="child::*[1]/child::*" copy-namespaces="no"/>
									</xsl:element>
									<xsl:apply-templates select="child::*[2]/child::*"/>									
								</xsl:when>
								
								<!-- has text title -->
								<xsl:otherwise>
									<xsl:apply-templates select="child::*[1]/child::*"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="child::*[1]"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:for-each>			
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>
	
</xsl:stylesheet>