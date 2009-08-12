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
	xmlns:html="http://www.w3.org/1999/xhtml">

	<xsl:template name="widgetAttributes">
	
		<xsl:param name="skipWidgetAttributes" />
		<xsl:param name="widgetName"/>
	
		<!-- 
		================================================== 
		Common for all widgets 
		==================================================
		-->
		
		<xsl:if test="not($skipWidgetAttributes)">
			<xsl:attribute name="id" select="@id" />
			
			<xsl:choose>
				<xsl:when test="string-length($widgetName) > 0">
					<xsl:attribute name="_type" select="$widgetName" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="_type" select="local-name()" />	
				</xsl:otherwise>	
			</xsl:choose>		
		</xsl:if>
		
		<xsl:if test="string-length(@width) > 0">
			<xsl:attribute name="_width" select="@width"/>
		</xsl:if>
		
		<xsl:if test="string-length(@height) > 0">
			<xsl:attribute name="_height" select="@height"/>
		</xsl:if>
		
		<xsl:if test="string-length(@visible) > 0">
			<xsl:attribute name="_visible" select="@visible"/>
		</xsl:if>
		
		<xsl:if test="string-length(@tooltip) > 0">
			<xsl:attribute name="_tooltip" select="@tooltip"/>
		</xsl:if>
		
		<xsl:if test="string-length(@styleName) > 0">
			<xsl:attribute name="_styleName" select="@styleName"/>
		</xsl:if>
		
		<xsl:if test="string-length(@style) > 0">
			<xsl:attribute name="_style" select="@style"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Animation
		==================================================
		-->
		<xsl:if test="string-length(@animationEnabled) > 0">
			<xsl:attribute name="_animationEnabled" select="@animationEnabled"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Events
		==================================================
		-->
		<xsl:if test="string-length(@onClick) > 0">
			<xsl:attribute name="_onclick" select="@onClick"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onChange) > 0">
			<xsl:attribute name="_onchange" select="@onChange"/>
		</xsl:if>

		<xsl:if test="string-length(@onFocus) > 0">
			<xsl:attribute name="_onfocus" select="@onFocus"/>
		</xsl:if>

		<xsl:if test="string-length(@onBlur) > 0">
			<xsl:attribute name="_onblur" select="@onBlur"/>
		</xsl:if>

		<xsl:if test="string-length(@onLoad) > 0">
			<xsl:attribute name="_onload" select="@onLoad"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onLoadWidget) > 0">
			<xsl:attribute name="_onloadwidget" select="@onLoadWidget"/>
		</xsl:if>

		<xsl:if test="string-length(@onKeyDown) > 0">
			<xsl:attribute name="_onkeydown" select="@onKeyDown"/>
		</xsl:if>

		<xsl:if test="string-length(@onKeyPress) > 0">
			<xsl:attribute name="_onkeypress" select="@onKeyPress"/>
		</xsl:if>

		<xsl:if test="string-length(@onKeyUp) > 0">
			<xsl:attribute name="_onkeyup" select="@onKeyUp"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseDown) > 0">
			<xsl:attribute name="_onmousedown" select="@onMouseDown"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseMove) > 0">
			<xsl:attribute name="_onmousemove" select="@onMouseMove"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseUp) > 0">
			<xsl:attribute name="_onmouseup" select="@onMouseUp"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseOut) > 0">
			<xsl:attribute name="_onmouseout" select="@onMouseOut"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseOver) > 0">
			<xsl:attribute name="_onmouseover" select="@onMouseOver"/>
		</xsl:if>

		<xsl:if test="string-length(@onMouseWheel) > 0">
			<xsl:attribute name="_onmousewheel" select="@onMouseWheel"/>
		</xsl:if>

		<xsl:if test="string-length(@onOpen) > 0">
			<xsl:attribute name="_onopen" select="@onOpen"/>
		</xsl:if>

		<xsl:if test="string-length(@onClose) > 0">
			<xsl:attribute name="_onclose" select="@onClose"/>
		</xsl:if>

		<xsl:if test="string-length(@onClosing) > 0">
			<xsl:attribute name="_onclosing" select="@onClosing"/>
		</xsl:if>

		<xsl:if test="string-length(@onResized) > 0">
			<xsl:attribute name="_onresized" select="@onResized"/>
		</xsl:if>

		<xsl:if test="string-length(@onError) > 0">
			<xsl:attribute name="_onerror" select="@onError"/>
		</xsl:if>

		<xsl:if test="string-length(@onLoadOracle) > 0">
			<xsl:attribute name="_onloadoracle" select="@onLoadOracle"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onLoadImage) > 0">
			<xsl:attribute name="_onloadimage" select="@onLoadImage"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onLoadFormat) > 0">
			<xsl:attribute name="_onloadformat" select="@onLoadFormat"/>
		</xsl:if>
		
		
		<xsl:if test="string-length(@onHighlight) > 0">
			<xsl:attribute name="_onhighlight" select="@onHighlight"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onShowRange) > 0">
			<xsl:attribute name="_onshowrange" select="@onShowRange"/>
		</xsl:if>

		<xsl:if test="string-length(@onScroll) > 0">
			<xsl:attribute name="_onscroll" select="@onScroll"/>
		</xsl:if>

		<xsl:if test="string-length(@onBeforeSelection) > 0">
			<xsl:attribute name="_onbeforeselection" select="@onBeforeSelection"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onSelection) > 0">
			<xsl:attribute name="_onselection" select="@onSelection"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onLoadWidget) > 0">
			<xsl:attribute name="_onloadwidget" select="@onLoadWidget"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onExecute) > 0">
			<xsl:attribute name="_onexecute" select="@onExecute"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onSubmitComplete) > 0">
			<xsl:attribute name="_onsubmitcomplete" select="@onSubmitComplete"/>
		</xsl:if>
		
		<xsl:if test="string-length(@onSubmit) > 0">
			<xsl:attribute name="_onsubmit" select="@onSubmit"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Links
		==================================================
		-->
		<xsl:if test="string-length(@href) > 0">
			<xsl:attribute name="_href" select="@href"/>
		</xsl:if>
		
		<xsl:if test="string-length(@target) > 0">
			<xsl:attribute name="_target" select="@target"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Alignment
		==================================================
		-->
		<xsl:if test="string-length(@horizontalAlignment) > 0">
			<xsl:attribute name="_horizontalAlignment" select="@horizontalAlignment"/>
		</xsl:if>
		
		<xsl:if test="string-length(@verticalAlignment) > 0">
			<xsl:attribute name="_verticalAlignment" select="@verticalAlignment"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Text
		==================================================
		-->
		<xsl:if test="string-length(@direction) > 0">
			<xsl:attribute name="_direction" select="@direction"/>
		</xsl:if>
		
		<xsl:if test="string-length(@wordWrap) > 0">
			<xsl:attribute name="_wordWrap" select="@wordWrap"/>
		</xsl:if>
		
		<xsl:if test="string-length(@maxLength) > 0">
			<xsl:attribute name="_maxLength" select="@maxLength"/>
		</xsl:if>
		
		<xsl:if test="string-length(@visibleLength) > 0">
			<xsl:attribute name="_visibleLength" select="@visibleLength"/>
		</xsl:if>
		
		<xsl:if test="string-length(@readOnly) > 0">
			<xsl:attribute name="_readOnly" select="@readOnly"/>
		</xsl:if>
		
		<xsl:if test="string-length(@textAlignment) > 0">
			<xsl:attribute name="_textAlignment" select="@textAlignment"/>
		</xsl:if>
		
		<!-- 
		================================================== 
		Other
		==================================================
		-->
		<xsl:if test="string-length(@name) > 0">
			<xsl:attribute name="_name" select="@name"/>
		</xsl:if>
		
		<xsl:if test="string-length(@value) > 0">
			<xsl:attribute name="_value" select="@value"/>
		</xsl:if>
		
		<xsl:if test="string-length(@text) > 0">
			<xsl:attribute name="_text" select="@text"/>
		</xsl:if>
		
		<xsl:if test="string-length(@enabled) > 0">
			<xsl:attribute name="_enabled" select="@enabled"/>
		</xsl:if>
		
		<xsl:if test="string-length(@borderWidth)">
			<xsl:attribute name="_borderWidth" select="@borderWidth"/>	
		</xsl:if>
		
		<xsl:if test="string-length(@spacing)">
			<xsl:attribute name="_spacing" select="@spacing"/>	
		</xsl:if>
		
		<xsl:if test="string-length(@cellPadding)">
			<xsl:attribute name="_cellPadding" select="@cellPadding"/>	
		</xsl:if>
		
		<xsl:if test="string-length(@cellSpacing)">
			<xsl:attribute name="_cellSpacing" select="@cellSpacing"/>	
		</xsl:if>
		
		<xsl:if test="string-length(@url) > 0">
			<xsl:attribute name="_url" select="@url"/>
		</xsl:if>

	</xsl:template>
	
</xsl:stylesheet>