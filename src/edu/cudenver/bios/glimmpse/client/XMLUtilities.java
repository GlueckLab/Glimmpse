package edu.cudenver.bios.glimmpse.client;

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
}
