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
	xmlns:adv="http://www.sysmap.com.br/crux/advanced-widgets/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="pagedDataGrid" match="adv:pagedDataGrid">
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:call-template name="widgetAttributes" />
			<xsl:if test="string-length(@pageSize) > 0">
				<xsl:attribute name="_pageSize" select="@pageSize" />
			</xsl:if>
			<xsl:if test="string-length(@dataSource) > 0">
				<xsl:attribute name="_dataSource" select="@dataSource" />
			</xsl:if>
			<xsl:if test="string-length(@rowSelection) > 0">
				<xsl:attribute name="_rowSelection" select="@rowSelection" />
			</xsl:if>
			<xsl:if test="string-length(@cellSpacing) > 0">
				<xsl:attribute name="_cellSpacing" select="@cellSpacing" />
			</xsl:if>
			<xsl:if test="string-length(@autoLoadData) > 0">
				<xsl:attribute name="_autoLoadData" select="@autoLoadData" />
			</xsl:if>
			<xsl:if test="string-length(@stretchColumns) > 0">
				<xsl:attribute name="_stretchColumns" select="@stretchColumns" />
			</xsl:if>
			
			<xsl:if test="string-length(@onRowRender) > 0">
				<xsl:attribute name="_onrowrender" select="@onRowRender" />
			</xsl:if>
			<xsl:if test="string-length(@onRowClick) > 0">
				<xsl:attribute name="_onrowclick" select="@onRowClick" />
			</xsl:if>
			<xsl:if test="string-length(@onRowDoubleClick) > 0">
				<xsl:attribute name="_onrowdoubleclick" select="@onRowDoubleClick" />
			</xsl:if>
			
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:attribute name="_columnType" select="local-name()" />
					<xsl:if test="string-length(@width) > 0">
						<xsl:attribute name="_width" select="@width" />
					</xsl:if>
					<xsl:if test="string-length(@visible) > 0">
						<xsl:attribute name="_visible" select="@visible" />
					</xsl:if>
					<xsl:if test="string-length(@label) > 0">
						<xsl:attribute name="_label" select="@label" />
					</xsl:if>
					<xsl:if test="string-length(@key) > 0">
						<xsl:attribute name="_key" select="@key" />
					</xsl:if>
					<xsl:if test="string-length(@formatter) > 0">
						<xsl:attribute name="_formatter" select="@formatter" />
					</xsl:if>
					<xsl:if test="string-length(@horizontalAlignment) > 0">
						<xsl:attribute name="_horizontalAlignment" select="@horizontalAlignment" />
					</xsl:if>
					<xsl:if test="string-length(@verticalAlignment) > 0">
						<xsl:attribute name="_verticalAlignment" select="@verticalAlignment" />
					</xsl:if>
					<xsl:if test="local-name() = 'widgetColumn'">
						<xsl:apply-templates select="child::*" />
					</xsl:if>
					<xsl:value-of select="' '"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
		<xsl:value-of select="' '"/>
	</xsl:template>	
</xsl:stylesheet>