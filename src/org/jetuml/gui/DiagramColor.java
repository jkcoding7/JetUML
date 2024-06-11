package org.jetuml.gui;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.BooleanPreference;
import org.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * 
 */
public class DiagramColor implements BooleanPreferenceChangeHandler
{
	public static final Color DARK_MODE_FILL_COLOR = Color.web("#1f1f1f");
	public static final DropShadow LIGHT_MODE_DROPSHADOW = new DropShadow(3, 3, 3, Color.LIGHTGRAY);
	public static final DropShadow DARK_MODE_DROPSHADOW = new DropShadow(3, 3, 3, Color.web("#2f2f34"));
	public static final Color LIGHT_MODE_NOTE_COLOR = Color.color(0.9f, 0.9f, 0.6f); // Pale yellow
	public static final Color DARK_MODE_NOTE_COLOR = Color.web("#1e3f66");
	public static final Color DARK_MODE_CANVAS_COLOR = Color.web("#070707");
	public static final Color LIGHT_MODE_GRID_COLOR = Color.rgb(220, 220, 220);
	public static final Color DARK_MODE_GRID_COLOR = Color.web("#1f1f1f");
	
	private Color aFill = Color.WHITE;
	private Color aStroke = Color.BLACK;
	private DropShadow aShadow = LIGHT_MODE_DROPSHADOW;
	private Color aNoteColor = LIGHT_MODE_NOTE_COLOR;
	private Color aCanvasColor = Color.WHITE;
	
	/**
	 * 
	 */
	public DiagramColor() 
	{
		UserPreferences.instance().addBooleanPreferenceChangeHandler(this);
	}
	
	public Color getFillColor()
	{
		return aFill;
	}
	
	public Color getStrokeColor()
	{
		return aStroke;
	}
	
	public Color getNoteColor()
	{
		return aNoteColor;
	}
	
	public Color getCanvasColor()
	{
		return aCanvasColor;
	}
	
	public DropShadow getDropShadow()
	{
		return aShadow;
	}
	
	@Override
	public void booleanPreferenceChanged(BooleanPreference pPreference) 
	{
		if( pPreference == BooleanPreference.darkMode )
		{
			applyDarkModeColors(UserPreferences.instance().getBoolean(pPreference));
		}
	}
	
	private void applyDarkModeColors(Boolean pDarkMode)
	{
		if( pDarkMode )
		{
			aFill = DARK_MODE_FILL_COLOR;
			aStroke = Color.WHITE;
			aShadow = DARK_MODE_DROPSHADOW;
			aNoteColor = DARK_MODE_NOTE_COLOR;
			aCanvasColor = DARK_MODE_CANVAS_COLOR;
		}
		else
		{
			aFill = Color.WHITE;
			aStroke = Color.BLACK;
			aShadow = LIGHT_MODE_DROPSHADOW;
			aNoteColor = LIGHT_MODE_NOTE_COLOR;
			aCanvasColor = Color.WHITE;
		}
	}
}
