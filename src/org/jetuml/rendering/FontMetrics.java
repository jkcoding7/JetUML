/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import org.jetuml.geom.Dimension;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A utility class to determine various font position metrics
 * for the particular font.
 * 
 * A visual diagram for why the bounds values are what they are
 * (with word "Thy"):   ____________________
 * getMinY() (ascent)  |*****  *           |
 *                     |  *    *           |
 *                     |  *    *****   *  *|
 *                     |  *    *   *   *  *|
 *                     |  *    *   *   ****|
 * (baseline)          |------------------*| x=getWidth()
 *                     |                  *|
 *                     |                  *| 
 * y = 0  (descent)    |                ***|
 *                     |                   |
 *                     |                   |
 * getMaxY() (leading) |-------------------|
 *
 * Hence, upon calling getHeight(), to get tight bounds, one should subtract
 * off the leading value (found by getting the max Y value of a one-lined text
 * box)
 */
public class FontMetrics 
{
	public static final int DEFAULT_FONT_SIZE = 12;
	public static final String DEFAULT_FONT_NAME = "System";
	private static final String SINGLE_LINED_TEXT = "One";
	private static final String TWO_LINED_TEXT = "One\nTwo";
	private Text aTextNode;

	/**
	 * Creates a new FontMetrics object.
	 * @param pFont The font to use.
	 */

	public FontMetrics(Font pFont)
	{
		assert pFont != null;
		
		aTextNode = new Text();
		aTextNode.setFont(pFont);
	}

	/**
	 * Returns the dimension of a given string.
	 * For the fonts supported in JetUML, the dimension includes the leading space in the height.
	 * However, this behavior is not consistent across all fonts.
	 * 
	 * @param pString The string to which the bounds pertain.
	 * @return The dimension of the string
	 */
	public Dimension getDimension(String pString)
	{
		assert pString != null;
		
		aTextNode.setText(pString);
		Bounds bounds = aTextNode.getLayoutBounds();
		return new Dimension((int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
	}
	
	/**
	 * Returns the distance between the top and bottom of a single lined text.
	 * 
	 * @param pString The string. 
	 * @return The height of a single lined text.
	 * @pre pString != null
	 */
	public int getHeight(String pString)
	{
		assert pString != null;
		
		aTextNode.setText(TWO_LINED_TEXT);
		double twoLineHeight = aTextNode.getLayoutBounds().getHeight();
		aTextNode.setText(SINGLE_LINED_TEXT);
		double singleLineHeight = aTextNode.getLayoutBounds().getHeight();
		return (int) Math.round(twoLineHeight - singleLineHeight);
	}
	
	/**
	 * Returns the distance between the top and baseline of a single lined text.
	 * 
	 * @param pBold Whether the font is bold.
	 * @param pItalic whether the font is italic.
	 * @return the distance above the baseline for a single lined text.
	 */
	public int getBaselineOffset()
	{
		aTextNode.setText(SINGLE_LINED_TEXT);
		return (int) Math.round(aTextNode.getBaselineOffset());
	}
} 