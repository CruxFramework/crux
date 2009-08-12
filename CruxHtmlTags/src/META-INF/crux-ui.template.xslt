<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:crux="http://www.sysmap.com.br/crux/basic-widgets/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:exsl="http://exslt.org/common"
	exclude-result-prefixes="#all"
	extension-element-prefixes="html crux exsl xsl">

	${imports}

	<xsl:output method="xml" encoding="${charset}" indent="${indent}" exclude-result-prefixes="#all" omit-xml-declaration="yes" />

	<!-- 
	=====================================================================
	  Starts the transformation over the root element (HTML)
	=====================================================================
	-->
	<xsl:template match="/">
		<xsl:call-template name="handleInnerHtml" />
	</xsl:template>

	<!-- 
	=====================================================================
	  Recursively navigates through the document nodes, copying the 
	  HTML nodes and delegating crux nodes to their specific handlers
	=====================================================================
	-->
	<xsl:template name="handleInnerHtml">
		<xsl:for-each select="child::*">
			<xsl:choose>
				<xsl:when test="string-length(namespace-uri()) > 0 and not(contains(namespace-uri(), 'http://www.w3.org/1999/xhtml'))">
					<xsl:apply-templates select="current()" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="count(child::*) = 0 and string-length(local-name()) > 0">
							<xsl:copy copy-namespaces="no">
								<xsl:copy-of select="@*"/>
								<xsl:choose>
									<xsl:when test="string-length(text()) > 0">
										<xsl:value-of select="text()"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="' '"></xsl:value-of>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:copy>
						</xsl:when>
						<xsl:when test="count(child::*) = 0 and string-length(local-name()) = 0">
							<xsl:copy-of select="current()"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy copy-namespaces="no">
								<xsl:copy-of select="@*"/>
								<xsl:call-template name="handleInnerHtml" />
							</xsl:copy>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>