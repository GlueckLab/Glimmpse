package edu.cudenver.bios.glimmpse.client;

import java.util.ArrayList;
import java.util.List;

public class ListUtilities
{	
	public static List<String[]> getPairs(List<String> values)
	{
		ArrayList<String[]> pairs = new ArrayList<String[]>();
		
		if (values != null)
		{
			int startIndex = 1;
			for(String value: values)
			{
				for(int i = startIndex; i < values.size(); i++)
				{
					String[] pair = new String[2];
					pair[0] = value;
					pair[1] = values.get(i);
					pairs.add(pair);
				}
				startIndex++;
			}
		}
		return pairs;
	}
}
