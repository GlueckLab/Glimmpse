/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client;

import java.util.ArrayList;
import java.util.List;

/**
 * List utilities for the GLIMMPSE UI
 */
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
