<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:template match="page">
    <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
    <html>
      <head>
        <meta charset="utf-8"/>
        <title>Redirect</title>
      </head>
      <body>
        <form name="autoform" action="{url}" method="POST">
          <xsl:for-each select="parameters/parameter">
            <input type="hidden" name="{name}" value="{value}"/>
          </xsl:for-each>
        </form>
        <script type="text/javascript">
		    	document.autoform.submit();
		    </script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
