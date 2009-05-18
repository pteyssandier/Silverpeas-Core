/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * WindowSogreah.java
 * 
 * Created on 10 octobre 2000, 16:11
 */

package com.stratelia.webactiv.util.viewGenerator.html.window;

/**
 * The default implementation of Window interface
 * @author neysseri
 * @version 1.0
 */
public class WindowWeb20 extends AbstractWindow
{

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public WindowWeb20()
    {
        super();
    }
    
    private String displayLine()
    {
        StringBuffer line = new StringBuffer();
        String iconsPath = getIconsPath();
        
        int nbCols = 1;
        if (getOperationPane().nbOperations() > 0)
            nbCols = 2;
        
        line.append("<tr>");
        line.append("<td width=\"100%\" colspan=\"").append(nbCols).append("\"><img src=\"").append(iconsPath).append("/1px.gif\" width=\"1\" height=\"2\"></td>");
        line.append("</tr>");
        line.append("<tr>");
        line.append("<td class=\"viewGeneratorLines\" width=\"100%\" colspan=\"").append(nbCols).append("\"><img src=\"").append(iconsPath).append("/1px.gif\" width=\"1\" height=\"1\"></td>");
        line.append("</tr>");
        line.append("<tr>");
        line.append("<td width=\"100%\" colspan=\"").append(nbCols).append("\"><img src=\"").append(iconsPath).append("/1px.gif\" width=\"1\" height=\"5\"></td>");
        line.append("</tr>");
        return line.toString();
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String printBefore()
    {
        StringBuffer	result		= new StringBuffer();
        String width = getWidth();

        int    nbCols = 1;

        if (getOperationPane().nbOperations() > 0)
        {
            nbCols = 2;
        }
        
        result.append("<a name=\"topPage\"></a>");
		result.append("<table width=\"").append(width).append("\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		result.append("<tr><td>");
        result.append(getBrowseBar().print());
        result.append("</td>");
        if (nbCols == 2)
        {
        	result.append("<td align=\"right\">");
        	result.append(getOperationPane().print());
        	result.append("</td>");
        }
        result.append("</tr>");
        result.append(displayLine());
        result.append("<tr><td width=\"100%\" valign=\"top\" colspan=\"2\">");
        result.append("<table border=\"0\" width=\"100%\" cellpadding=\"5\" cellspacing=\"5\"><tr><td align=\"center\" valign=\"top\">");
        return result.toString();
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String printAfter()
    {
        StringBuffer	result		= new StringBuffer();
        String iconsPath = getIconsPath();

        result.append("</td></tr></table>");
        result.append("</td>");
        /*if (getOperationPane().nbOperations() > 0)
        {
            result.append("<td valign=\"top\" align=\"right\">");
            result.append(getOperationPane().print());
            result.append("</td>");
        }*/
        result.append("</tr>");
        result.append("<tr><td>&nbsp;</td></tr>");
        result.append("</table>");
				result.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				result.append("<tr><td>");
				result.append("<div align=\"left\"><a href=\"#topPage\"><img src=\"").append(iconsPath).append("/goTop.gif\" border=\"0\"></a></div>");
				result.append("</td><td width=\"100%\">");
				result.append("&nbsp;");
				result.append("</td><td>");
				result.append("<div align=\"right\"><a href=\"#topPage\"><img src=\"").append(iconsPath).append("/goTop.gif\" border=\"0\"></a></div>");
				result.append("</td></tr></table>");

        return result.toString();
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String print()
    {
        StringBuffer	result		= new StringBuffer();

        result.append(printBefore());
        result.append(getBody());
        result.append(printAfter());

        return result.toString();
    }

}
