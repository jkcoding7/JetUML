/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.
 ******************************************************************************/
package org.jetuml.rendering;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.edges.EdgeViewer;
import org.jetuml.viewers.edges.NoteEdgeViewer;
import org.jetuml.viewers.nodes.NodeViewer;
import org.jetuml.viewers.nodes.NoteNodeViewer;
import org.jetuml.viewers.nodes.PointNodeViewer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Default implementation of the rendering operations.
 */
public abstract class AbstractDiagramRenderer implements DiagramRenderer
{
	private final IdentityHashMap<Class<? extends DiagramElement>, DiagramElementRenderer> aRenderers = new IdentityHashMap<>();
	private final Diagram aDiagram;

	/*
	 * Add renderers for elements that are present in all diagrams. 
	 */
	protected AbstractDiagramRenderer(Diagram pDiagram)
	{
		aDiagram = pDiagram;
		addElementRenderer(NoteNode.class, new NoteNodeViewer(this));
		addElementRenderer(PointNode.class, new PointNodeViewer(this));
		addElementRenderer(NoteEdge.class, new NoteEdgeViewer(this));
	}

	protected void addElementRenderer(Class<? extends DiagramElement> pElementClass,
			DiagramElementRenderer pElementRenderer)
	{
		aRenderers.put(pElementClass, pElementRenderer);
	}
	
	@Override
	public DiagramElementRenderer rendererFor(Class<? extends DiagramElement> pClass)
	{
		assert aRenderers.containsKey(pClass);
		return aRenderers.get(pClass);
	}

	@Override
	public void draw(GraphicsContext pGraphics)
	{
		assert pGraphics != null;
		activateNodeStorages();
		aDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		aDiagram.edges().forEach(edge -> draw(edge, pGraphics));
		deactivateAndClearNodeStorages();
	}

	/**
	 * Activates all the NodeStorages of the NodeViewers present in the renderer.
	 */
	protected void activateNodeStorages()
	{
		aRenderers.values().stream().filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
				.map(NodeViewer.class::cast).forEach(NodeViewer::activateNodeStorage);
	}

	/**
	 * Deactivates and clears all the NodeStorages of the NodeViewers present in the renderer.
	 */
	protected void deactivateAndClearNodeStorages()
	{
		aRenderers.values().stream().filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
				.map(NodeViewer.class::cast).forEach(NodeViewer::deactivateAndClearNodeStorage);
	}

	protected void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		draw(pNode, pGraphics);
		pNode.getChildren().forEach(node -> drawNode(node, pGraphics));
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		aRenderers.get(pElement.getClass()).draw(pElement, pGraphics);
	}

	@Override
	public Optional<Edge> edgeAt(Point pPoint)
	{
		assert pPoint != null;
		return aDiagram.edges().stream()
				.filter(edge -> contains(edge, pPoint))
				.findFirst();
	}

	@Override
	public Optional<Node> nodeAt(Point pPoint)
	{
		assert pPoint != null;
		return aDiagram.rootNodes().stream()
				.map(node -> deepFindNode(node, pPoint))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce((first, second) -> second);
	}

	protected Optional<Node> deepFindNode(Node pNode, Point pPoint)
	{
		assert pNode != null && pPoint != null;

		return pNode.getChildren().stream()
				.map(node -> deepFindNode(node, pPoint))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.or(() -> Optional.of(pNode).filter(originalNode -> contains(originalNode, pPoint)));
	}

	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = null;
		for (Node node : aDiagram.rootNodes())
		{
			if (bounds == null)
			{
				bounds = getBounds(node);
			}
			else
			{
				bounds = bounds.add(getBounds(node));
			}
		}
		for (Edge edge : aDiagram.edges())
		{
			bounds = bounds.add(getBounds(edge));
		}
		if (bounds == null)
		{
			return new Rectangle(0, 0, 0, 0);
		}
		else
		{
			return new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
	}

	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		return aRenderers.get(pElement.getClass()).contains(pElement, pPoint);
	}

	@Override
	public Canvas createIcon(DiagramElement pElement)
	{
		assert pElement != null;
		return aRenderers.get(pElement.getClass()).createIcon(DiagramType.USECASE, pElement);
	}

	@Override
	public void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics)
	{
		assert pElement != null && pGraphics != null;
		aRenderers.get(pElement.getClass()).drawSelectionHandles(pElement, pGraphics);
	}

	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		assert pElement != null;
		return aRenderers.get(pElement.getClass()).getBounds(pElement);
	}

	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		assert pEdge != null;
		return ((EdgeViewer) aRenderers.get(pEdge.getClass())).getConnectionPoints(pEdge);
	}

	@Override
	public Point getConnectionPoints(Node pNode, Direction pDirection)
	{
		assert pNode != null && pDirection != null;
		return ((NodeViewer) aRenderers.get(pNode.getClass())).getConnectionPoint(pNode, pDirection);
	}
	
	@Override
	public Optional<Node> selectableNodeAt(Point pPoint)
	{
		return nodeAt(pPoint);
	}
	
	@Override
	public final Diagram diagram()
	{
		return aDiagram;
	}
	
	@Override
	public Rectangle getBoundsIncludingParents(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		assert pElements.iterator().hasNext();
		Iterator<DiagramElement> elements = pElements.iterator();
		DiagramElement next = elements.next();
		Rectangle bounds = getBounds(next);
		bounds = addBounds(bounds, next);
		while( elements.hasNext() )
		{
			bounds = addBounds(bounds, elements.next());
		}
		return bounds;
	}
	
	// Recursively enlarge the current rectangle to include the selected DiagramElements
	private Rectangle addBounds(Rectangle pBounds, DiagramElement pElement)
	{
		if( pElement instanceof Node && ((Node) pElement).hasParent())
		{
			return addBounds(pBounds, ((Node) pElement).getParent());
		}
		else
		{
			return pBounds.add(getBounds(pElement));
		}
	}
}
