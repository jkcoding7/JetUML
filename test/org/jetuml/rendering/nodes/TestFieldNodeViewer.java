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
package org.jetuml.rendering.nodes;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.jetuml.testutils.GeometryUtils.osDependent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jetuml.JavaFXLoader;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ObjectDiagramRenderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.jetuml.testutils.GeometryUtils.osDependent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jetuml.JavaFXLoader;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ObjectDiagramRenderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFieldNodeViewer
{
	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	private ObjectNode aObjectNode1;
	private FieldNode aFieldNode1;
	private Graphics2D aGraphics;
	private Diagram aDiagram;
	private ObjectDiagramRenderer aRenderer;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aObjectNode1 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aFieldNode1.setName("");
		aFieldNode1.setValue("");
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aDiagram = new Diagram(DiagramType.OBJECT);
		aDiagram.addRootNode(aObjectNode1);
		aRenderer = new ObjectDiagramRenderer(aDiagram);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testDimensionsUnattachedWithNameString()
	{
		aFieldNode1.setName("XXXXX");
		assertEquals(osDependent(42, 44, 53), FieldNodeRenderer.leftWidth(aFieldNode1));    // The length of the string
		assertEquals(osDependent(37, 36, 37), FieldNodeRenderer.rightWidth(aFieldNode1));   // Half the default width + mid offset.
		assertEquals(osDependent(20, 20, 23), FieldNodeRenderer.getHeight(aFieldNode1));    // The height of the string
	}
	
	@Test
	public void testDimensionsUnattachedWithValueString()
	{
		aFieldNode1.setValue("XXXXX");
		assertEquals(osDependent(7, 6, 7), FieldNodeRenderer.leftWidth(aFieldNode1));    	// Just the length of the mid offset
		assertEquals(osDependent(42, 44, 53), FieldNodeRenderer.rightWidth(aFieldNode1));  	// The length of the string
		assertEquals(osDependent(20, 20, 23), FieldNodeRenderer.getHeight(aFieldNode1));    	// The height of the string
	}
	
	@Test
	public void testGetBoundsUnattachedNoStrings()
	{
		// x = axis (30) - offset (6)
		// y = 0
		// w = default length (30)/2 + 2* offset (6) = 42
		// h = default height = 20
		assertEquals( new Rectangle(osDependent(23, 24, 23),0,osDependent(44, 42, 44), osDependent(20,20,26)), aRenderer.getBounds(aFieldNode1));
	}
	
	@Test
	public void testGetBoundsUnattachedNameValueString()
	{
		aFieldNode1.setName("XXXXX");
		aFieldNode1.setValue("XXXXX");
		// x = axis (30) - offset + length (47)  = -17
		// y = 0
		// w = 47 * 2
		// h = text height 22
		assertEquals( new Rectangle(osDependent(-12, -14, -23), 0, osDependent(84, 88, 106), osDependent(20, 20, 23)), aRenderer.getBounds(aFieldNode1));
	}
	
	@Test
	public void testDimensionsAttachedNoStrings()
	{
		aObjectNode1.addChild(aFieldNode1);
		assertEquals(osDependent(7, 6, 7), FieldNodeRenderer.leftWidth(aFieldNode1));    // Just the length of the mid offset
		assertEquals(osDependent(37, 36, 37), FieldNodeRenderer.rightWidth(aFieldNode1));  // Half the default width + mid offset.
		assertEquals(osDependent(20, 20,26), FieldNodeRenderer.getHeight(aFieldNode1));   // Default height
	}
	
	@Test
	public void testDimensionsAttachedObjectString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.setName("XXXXXXXXXXXXXXXXXXX");
		assertEquals(osDependent(7, 6, 7), FieldNodeRenderer.leftWidth(aFieldNode1));    // Just the length of the mid offset
		assertEquals(osDependent(37, 36, 37), FieldNodeRenderer.rightWidth(aFieldNode1));  // Half the default width + mid offset.
		assertEquals(osDependent(20,20,26), FieldNodeRenderer.getHeight(aFieldNode1));   // Default height
	}
	
	@Test
	public void testDimensionsAttachedWithNameString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aFieldNode1.setName("XXXXX");
		assertEquals(osDependent(42, 44, 53), FieldNodeRenderer.leftWidth(aFieldNode1));    // The length of the string
		assertEquals(osDependent(37, 36, 37), FieldNodeRenderer.rightWidth(aFieldNode1));   // Half the default width + mid offset.
		assertEquals(osDependent(20, 20, 23), FieldNodeRenderer.getHeight(aFieldNode1));    // The height of the string
	}
	
	@Test
	public void testDimensionsAttachedWithValueString()
	{
		aObjectNode1.addChild(aFieldNode1);
		aFieldNode1.setValue("XXXXX");
		assertEquals(osDependent(7, 6, 7), FieldNodeRenderer.leftWidth(aFieldNode1));    	// Just the length of the mid offset
		assertEquals(osDependent(42, 44, 53), FieldNodeRenderer.rightWidth(aFieldNode1));  	// The length of the string
		assertEquals(osDependent(20, 20, 23), FieldNodeRenderer.getHeight(aFieldNode1));    	// The height of the string
	}
	
	@Test
	public void testGetBoundsAttachedNoStrings()
	{
		aObjectNode1.addChild(aFieldNode1);
		// x = axis (45) - offset (6) = 39
		// y = top node height
		// w = left + right
		// h = default height
		assertEquals( new Rectangle(5,70,70, osDependent(20,20,26)), aRenderer.getBounds(aFieldNode1));
	}
	
	@AfterEach
	public void teardown()
	{
		aGraphics.dispose();
	}
}
