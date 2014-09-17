<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:tsk="http://www.geo-solutions.it/tsk">
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    
    <xsl:strip-space elements="*"/>   
 
    <xsl:template match="overwrite">
        <xsl:value-of select="concat(' -overwrite ', .)"/>
    </xsl:template>
    
    <xsl:template match="skipFailures">
        <xsl:value-of select="concat(' -skipFailures ', .)"/>
    </xsl:template>
	
    <xsl:template match="outputFormat">
        <xsl:value-of select="concat(' -f ', .)"/>
    </xsl:template>
	
    <xsl:template match="gdbZippedFile">
        <xsl:value-of select="concat(' ', .)"/>
    </xsl:template>
    
</xsl:stylesheet>
