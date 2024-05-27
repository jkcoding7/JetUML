/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.BooleanPreference;
import org.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * A collection of utility methods to draw shapes on the canvas.
 * 
 * The methods in this class assume that the default canvas line width is 0.6, 
 * the stroke color black, and the fill color white. Any changes to these 
 * attributes should be reverted.
 * 
 * In the method names, "draw" refers to stroke and fill.
 */
public final class RenderingUtils implements BooleanPreferenceChangeHandler
{
	private static Color aFill;
	private static Color aStroke;
	private static DropShadow aShadow;
	private static final Color DARK_MODE_FILL_COLOR = Color.web("#1C1C1F");
	private static final DropShadow DARK_MODE_DROPSHADOW = new DropShadow(3, 3, 3, Color.web("#2f2f34"));
	private static final DropShadow LIGHT_MODE_DROPSHADOW = new DropShadow(3, 3, 3, Color.LIGHTGRAY);
	private static final int ARC_SIZE = 20;
	
	/**
	 * RenderingUtils should be a unique object which manages the
	 * color scheme of nodes depending on whether dark mode is on or off. 
	 */
	public RenderingUtils()
	{
		if( UserPreferences.instance().getBoolean(BooleanPreference.darkMode) )
		{
			aFill = DARK_MODE_FILL_COLOR;
			aStroke = Color.WHITE;
			aShadow = DARK_MODE_DROPSHADOW;
		}
		else
		{
			aFill = Color.WHITE;
			aStroke = DARK_MODE_FILL_COLOR;
			aShadow = LIGHT_MODE_DROPSHADOW;
		}
		UserPreferences.instance().addBooleanPreferenceChangeHandler(this);
	}
	
	/**
	 * Getter for fill color.
	 * 
	 * @return The fill color.
	 */
	public static Color getFill()
	{
		return aFill;
	}
	
	/**
	 * Getter for stroke color.
	 * 
	 * @return The stroke color.
	 */
	public static Color getStroke()
	{
		return aStroke;
	}
	
	/**
	 * Getter for DropShadow.
	 * 
	 * @return The DropShadow.
	 */
	public static DropShadow getDropShadow()
	{
		return aShadow;
	}
	
	/**
	 * Draws a circle with default attributes, without a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pDiameter The diameter of the circle.
	 * @param pShadow True to include a drop shadow.
	 */
	public static void drawCircle(GraphicsContext pGraphics, int pX, int pY, int pDiameter, Paint pFill, boolean pShadow)
	{
		drawOval( pGraphics, pX, pY, pDiameter, pDiameter, pFill, pShadow);
	}
	
	/**
	 * Draws a circle with default attributes, without a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate of the top-left of the circle.
	 * @param pY The y-coordinate of the top-left of the circle.
	 * @param pFill The color with which to fill the circle.
	 * @param pWidth The width of the oval to draw
	 * @param pHeight The height of the oval to draw.
	 * @param pShadow True to include a drop shadow.
	 */
	public static void drawOval(GraphicsContext pGraphics, int pX, int pY, int pWidth, int pHeight, Paint pFill, boolean pShadow)
	{
		assert pWidth > 0 && pHeight > 0 && pFill != null && pGraphics != null;
		Paint oldFill = pGraphics.getFill();
		pGraphics.setFill(pFill);
		pGraphics.setStroke(aStroke);
		if( pShadow )
		{
			pGraphics.setEffect(aShadow);
		}
		pGraphics.fillOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.strokeOval(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.setFill(oldFill);
		pGraphics.setEffect(null);
	}
	
	/**
	 * Draws a white rounded rectangle with a drop shadow.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pRectangle The rectangle to draw.
	 */
	public static void drawRoundedRectangle(GraphicsContext pGraphics, Rectangle pRectangle)
	{
		assert pGraphics != null && pRectangle != null;
		pGraphics.setFill(aFill);
		pGraphics.setStroke(aStroke);
		pGraphics.setEffect(aShadow);
		pGraphics.fillRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE );
		pGraphics.setEffect(null);
		pGraphics.strokeRoundRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, 
				pRectangle.width(), pRectangle.height(), ARC_SIZE, ARC_SIZE);
	}

	/**
	 * Strokes and fills a rectangle, originally in integer coordinates, so that it aligns precisely
	 * with the JavaFX coordinate system, which is 0.5 away from the pixel. See
	 * the documentation for javafx.scene.shape.Shape for details.
	 * 
	 * @param pGraphics The graphics context on which to draw the rectangle.
	 * @param pStroke The stroke (border) color for the rectangle.
	 * @param pFill The fill (background) color for the rectangle.
	 * @param pX The x-coordinate of the origin.
	 * @param pY The y-coordinate of the origin.
	 * @param pWidth The width.
	 * @param pHeight The height.
	 */
	public static void drawRectangle(GraphicsContext pGraphics, Paint pStroke, Paint pFill, 
			int pX, int pY, int pWidth, int pHeight)
	{
		Paint oldFill = pGraphics.getFill();
		Paint oldStroke = pGraphics.getStroke();
		pGraphics.setFill(pFill);
		pGraphics.setStroke(pStroke);
		pGraphics.fillRect(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.strokeRect(pX + 0.5, pY + 0.5, pWidth, pHeight);
		pGraphics.setFill(oldFill);
		pGraphics.setStroke(oldStroke);
	}
	
	/**
	 * Draws a rectangle with default attributes.
	 * 
	 * @param pGraphics The graphics context on which to draw the rectangle.
	 * @param pRectangle The rectangle to draw.
	 */
	public static void drawRectangle( GraphicsContext pGraphics, Rectangle pRectangle)
	{
		assert pGraphics != null && pRectangle != null;
		pGraphics.setFill(aFill);
		pGraphics.setStroke(aStroke);
		pGraphics.setEffect(aShadow);
		pGraphics.fillRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
		pGraphics.setEffect(null);
		pGraphics.strokeRect(pRectangle.x() + 0.5, pRectangle.y() + 0.5, pRectangle.width(), pRectangle.height());
	}
	
	/**
	 * Draws a line with default attributes and a specified line style.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX1 The x-coordinate of the first point
	 * @param pY1 The y-coordinate of the first point
	 * @param pX2 The x-coordinate of the second point
	 * @param pY2 The y-coordinate of the second point
	 * @param pStyle The line style for the path.
	 */
	public static void drawLine(GraphicsContext pGraphics, int pX1, int pY1, int pX2, int pY2, LineStyle pStyle)
	{
		double[] oldDash = pGraphics.getLineDashes();
		pGraphics.setStroke(aStroke);
		pGraphics.setLineDashes(pStyle.getLineDashes());
		pGraphics.strokeLine(pX1 + 0.5, pY1 + 0.5, pX2 + 0.5, pY2 + 0.5);
		pGraphics.setLineDashes(oldDash);
	}
	
	/**
	 * Draw pText in black with the given font, at point pX, pY.
	 * 
	 * @param pGraphics The graphics context.
	 * @param pX The x-coordinate where to draw the text.
	 * @param pY The y-coordinate where to draw the text.
	 * @param pText The text to draw.
	 * @param pFont The font to use.
	 */
	public static void drawText(GraphicsContext pGraphics, int pX, int pY, String pText, Font pFont)
	{
		Font font = pGraphics.getFont();
		pGraphics.setFont(pFont);
		pGraphics.setFill(aStroke);
		pGraphics.fillText(pText, pX + 0.5, pY + 0.5);
		pGraphics.setFont(font);
		pGraphics.setFill(Color.WHITE);
	}

	@Override
	public void booleanPreferenceChanged(BooleanPreference pPreference) 
	{
		if( pPreference == BooleanPreference.darkMode )
		{
			if( UserPreferences.instance().getBoolean(pPreference) )
			{
				aFill = DARK_MODE_FILL_COLOR;
				aStroke = Color.WHITE;
				aShadow = DARK_MODE_DROPSHADOW;
			}
			else
			{
				aFill = Color.WHITE;
				aStroke = DARK_MODE_FILL_COLOR;
				aShadow = LIGHT_MODE_DROPSHADOW;
			}
		}
	}
}
