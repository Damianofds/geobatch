<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet 
version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:tsk="http://www.geo-solutions.it/tsk">

    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="password">
                <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

    <xsl:template match="schema">
                <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

    <xsl:template match="user">
                <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

    <xsl:template match="database">
                <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

    <xsl:template match="host">
                <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

    <xsl:template match="port">
                 <xsl:value-of select="concat(' ', . , ' ')"/>
    </xsl:template>

</xsl:stylesheet>
