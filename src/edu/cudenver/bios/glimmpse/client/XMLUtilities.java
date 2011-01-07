package edu.cudenver.bios.glimmpse.client;

import java.util.Properties;

public class XMLUtilities
{
	public static void matrixOpenTag(StringBuffer buffer, String name, int rows, int columns)
	{
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(name);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_ROWS);
		buffer.append("='");
		buffer.append(rows);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_COLUMNS);
		buffer.append("='");
		buffer.append(columns);
		buffer.append("' ");
		buffer.append(">");
	}
	
	public static void fixedRandomMatrixOpenTag(StringBuffer buffer, String name, boolean combineHorizontal)
	{
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(name);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_COMBINE_HORIZONTAL);
		buffer.append("='");
		buffer.append(combineHorizontal);
		buffer.append("' ");
		buffer.append(">");
	}
	
	public static void closeTag(StringBuffer buffer, String tagName)
	{
		buffer.append("</");
		buffer.append(tagName);
		buffer.append(">");
	}
	
	public static void openTag(StringBuffer buffer, String tagName)
	{
		buffer.append("<");
		buffer.append(tagName);
		buffer.append(">");
	}
	
	public static void openTag(StringBuffer buffer, String tagName, String attrs)
	{
		buffer.append("<");
		buffer.append(tagName);
		buffer.append(" ");
		buffer.append(attrs);
		buffer.append(">");
	}
}
