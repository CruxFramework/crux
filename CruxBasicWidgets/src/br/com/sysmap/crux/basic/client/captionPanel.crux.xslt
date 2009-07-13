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

	

	<xsl:template name="captionPanel" match="crux:captionPanel">
	
		<xsl:element name="span" namespace="http://www.w3.org/1999/xhtml">

			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:for-each select="child::*">
				<xsl:choose>				
					<xsl:when test="local-name() = 'captionText'">
						<xsl:attribute name="_text" select="text()" />
					</xsl:when>					
					<xsl:when test="local-name() = 'captionHtml'">
						<xsl:element name="span" namespace="http://www.w3.org/1999/xhtml">
							<xsl:copy-of select="child::*" copy-namespaces="no"/>
						</xsl:element>
					</xsl:when>					
					<xsl:otherwise>
						<xsl:apply-templates select="child::*[1]" />
					</xsl:otherwise>					
				</xsl:choose>		
			</xsl:for-each>
			
			
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>
	
</xsl:stylesheet>