package edu.cudenver.bios.glimmpse.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class TextValidation
{
	public static int parseInteger(String str, int lowerBound, int upperBound)
	throws NumberFormatException
	{
        if (str == null || str.isEmpty()) throw new NumberFormatException("empty string");

        int n = Integer.parseInt(str);
        if (n > upperBound || n < lowerBound) throw new NumberFormatException("out of bounds");

        return n;
	}
	
	public static int parseInteger(String str, double bound, boolean lower)
	throws NumberFormatException
	{
	    if (str == null || str.isEmpty()) throw new NumberFormatException();

	    int n = Integer.parseInt(str);
	    if ((lower && n <= bound) || (!lower && n >= bound))
	        throw new NumberFormatException();

	    return n;
	}
	
	public static double parseDouble(String str, double lowerBound, double upperBound)
	throws NumberFormatException
	{
	    if (str == null || str.isEmpty()) throw new NumberFormatException();

	    double n = Double.parseDouble(str);
	    if (n > upperBound || n < lowerBound) throw new NumberFormatException();

	    return n;
	}
	
	public static double parseDouble(String str, double bound, boolean lower)
	throws NumberFormatException
	{
	    if (str == null || str.isEmpty()) throw new NumberFormatException();

	    double n = Double.parseDouble(str);
	    if ((lower && n < bound) || (!lower && n > bound))
	        throw new NumberFormatException();

	    return n;
	}
	
	
	
    public static void displayError(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.OKAY_STYLE);
        widget.removeStyleDependentName(GlimmpseConstants.ERROR_STYLE);

        widget.addStyleDependentName(GlimmpseConstants.ERROR_STYLE);
        widget.setHTML(msg);
    }
    
    public static void displayOkay(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.ERROR_STYLE);
        widget.removeStyleDependentName(GlimmpseConstants.OKAY_STYLE);

        widget.addStyleDependentName(GlimmpseConstants.OKAY_STYLE);
        widget.setHTML(msg);
    }
}
