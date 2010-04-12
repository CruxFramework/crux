<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:crux="http://www.sysmap.com.br/crux"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:exsl="http://exslt.org/common"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:f="http://www.sysmap.com.br/functions"  
	exclude-result-prefixes="#all"
	extension-element-prefixes="html crux exsl xsl">

	<xsl:output method="html" encoding="${charset}" indent="${indent}" exclude-result-prefixes="#all" omit-xml-declaration="yes" />

	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>
	<xsl:param name="allWidgets" select="'${allWidgets}'"></xsl:param>
	<xsl:param name="referencedWidgets" select="'${referencedWidgets}'"></xsl:param>

	<!-- 
	=====================================================================
	  Utility functions
	=====================================================================
	-->

	<!-- indexOf -->
	<xsl:function name="f:index-of-string" as="xs:integer*" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  if (contains($arg, $substring))
	  then (string-length(substring-before($arg, $substring))+1,
	        for $other in
	           f:index-of-string(substring-after($arg, $substring),
	                               $substring)
	        return
	          $other +
	          string-length(substring-before($arg, $substring)) +
	          string-length($substring))
	  else ()
	 "/>	   
	</xsl:function>

	<!-- indexOfFirst -->
	<xsl:function name="f:index-of-string-first" as="xs:integer?" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  if (contains($arg, $substring))
	  then string-length(substring-before($arg, $substring))+1
	  else ()
	 "/>
	</xsl:function>

	<!-- indexOfLast -->
	<xsl:function name="f:index-of-string-last" as="xs:integer?" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  f:index-of-string($arg, $substring)[last()]
	 "/>
	</xsl:function>

	<!-- getTagType -->
	<xsl:function name="f:getTagType" as="xs:string?">
	  <xsl:param name="arg" as="xs:string?"/> 
      <xsl:variable name="internalIndex" select="(f:index-of-string-first($referencedWidgets, $arg) + string-length($arg) + 2)"></xsl:variable>
      <xsl:choose>
      	<xsl:when test="$internalIndex > 0">
      		<xsl:variable name="substringTag" select="substring($referencedWidgets, $internalIndex)"></xsl:variable>
      		<xsl:sequence select=" 
			  if (contains($referencedWidgets, $arg))
			  then substring($substringTag, 0, f:index-of-string-first($substringTag,'|'))
			  else ()
			 " />
      	</xsl:when>
      	<xsl:otherwise>
      		<xsl:sequence select="()"/>
      	</xsl:otherwise>
      </xsl:choose>
	</xsl:function>
	
	<!-- getTagName -->
	<xsl:function name="f:getTagName" as="xs:string?">
	  <xsl:param name="elem" as="node()*"/> 
	  <xsl:param name="elemName" as="xs:string?"/> 
	  
	  <xsl:variable name="libraryName" select="f:getLibraryName($elem/..)" />
	  
	  <xsl:choose>
	  	  <xsl:when test="string-length($libraryName) = 0 or f:isWidget($libraryName, $elem/../local-name())">
		  	  <xsl:sequence select="concat($libraryName, '_', $elem/../local-name(), '_', $elemName)"/>
	  	  </xsl:when>
	  	  <xsl:otherwise>
		  	  <xsl:sequence select="f:getTagName($elem/.., $elemName)"/>
	  	  </xsl:otherwise>
	  </xsl:choose>
	</xsl:function>

	<!-- isWidget -->
	<xsl:function name="f:isWidget" as="xs:boolean?">
	  <xsl:param name="libraryName" as="xs:string?"/> 
	  <xsl:param name="elemName" as="xs:string?"/> 
  	  <xsl:sequence select="contains($allWidgets, concat(',', $libraryName, '_', $elemName, ','))"/>
	</xsl:function>

	<!-- isReferencedWidget -->
	<xsl:function name="f:isReferencedWidget" as="xs:boolean?">
	  <xsl:param name="tagName" as="xs:string?"/> 
  	  <xsl:sequence select="contains($referencedWidgets, concat(',', $tagName, ','))"/>
	</xsl:function>

	<!-- getLibraryName -->
	<xsl:function name="f:getLibraryName" as="xs:string?">
	  <xsl:param name="elem" as="node()*"/> 
	
	  <xsl:choose>
	      <xsl:when test="string-length(namespace-uri($elem)) > 30 and (contains(namespace-uri($elem), 'http://www.sysmap.com.br/crux/'))">
	      	 <xsl:sequence select="substring(namespace-uri($elem),31)" />
	      </xsl:when>
	  	  <xsl:otherwise>
	  	  	 <xsl:sequence select="()" />
	  	  </xsl:otherwise>
	  </xsl:choose>
	</xsl:function>

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
	  Process Crux widgets core tags
	=====================================================================
	-->
	<xsl:template name="splashScreen" match="crux:splashScreen">
		<xsl:element name="div" namespace="{$xhtmlNS}">
			<xsl:attribute name="id" select="'cruxSplashScreen'"/>			
			<xsl:if test="string-length(@style) > 0">
				<xsl:attribute name="style" select="@style"/>			
			</xsl:if>
			<xsl:copy-of select="child::*" copy-namespaces="no"/>
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>

	<xsl:template name="screen" match="crux:screen">
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:attribute name="_type" select="'screen'" />	
			<xsl:for-each select="current()/@*">
				<xsl:if test="name() != 'id'">	
					<xsl:call-template name="writeAttribute"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:call-template name="handleInnerHtml"/>
			<xsl:value-of select="' '"/>
		</xsl:element> 
	</xsl:template>
	
	<!-- 
	=====================================================================
	  Write tag attribute
	=====================================================================
	-->
	<xsl:template name="writeAttribute">
		<xsl:param name="attrName" select="concat('_', name())"></xsl:param>
		<xsl:attribute name="{$attrName}" select="."/>
	</xsl:template>

	<!-- 
	=====================================================================
	  Process Crux widgets libraries
	=====================================================================
	-->
	<xsl:template name="cruxInnerTags">
		<xsl:param name="libraryName" select="f:getLibraryName(current())"></xsl:param>
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:if test="string-length(@id) > 0">
				<xsl:attribute name="id" select="@id" />
			</xsl:if>
			<xsl:variable name="tagName" select="f:getTagName(current(), local-name())" />
			<xsl:choose>
				<xsl:when test="f:isReferencedWidget($tagName)">
					<xsl:attribute name="_type" select="f:getTagType($tagName)" />	
				</xsl:when>
				<xsl:when test="f:isWidget($libraryName, local-name())">
					<xsl:attribute name="_type" select="concat($libraryName, '_', local-name())" />	
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="__tag" select="local-name()" />	
				</xsl:otherwise>
			</xsl:choose>			
			<xsl:for-each select="current()/@*">
				<xsl:if test="name() != 'id'">	
					<xsl:call-template name="writeAttribute"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:value-of select="text()"></xsl:value-of>
			<xsl:if test="count(child::*) = 0 and count(text()) = 0">
				<xsl:value-of select="' '"></xsl:value-of>
			</xsl:if>
			<xsl:call-template name="handleInnerHtml" />
		</xsl:element> 
	</xsl:template>

	<!-- 
	=====================================================================
	  Recursively navigates through the document nodes, copying the 
	  HTML nodes and delegating crux nodes to it handler
	=====================================================================
	-->
	<xsl:template name="handleInnerHtml">
		<xsl:for-each select="child::*">
			<xsl:choose>
				<xsl:when test="namespace-uri() = 'http://www.sysmap.com.br/crux'">
					<xsl:apply-templates select="current()" />
				</xsl:when>
				<xsl:when test="string-length(namespace-uri()) > 30 and (contains(namespace-uri(), 'http://www.sysmap.com.br/crux/'))">
					<!-- xsl:apply-templates select="current()" /-->
					<xsl:call-template name="cruxInnerTags" />
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