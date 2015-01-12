/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *  A component filled with editors for all editable properties 
 *  of an object.
 */
@SuppressWarnings("serial")
public class PropertySheet extends JPanel
{
	private static Map<Class<?>, Class<?>> editors;

	private ArrayList<ChangeListener> aChangeListeners = new ArrayList<>();
	
	static
	{  
	      editors = new HashMap<>();
	      editors.put(String.class, StringEditor.class);
	}
	
	/**
     * Constructs a property sheet that shows the editable
     * properties of a given object.
     * @param pBean the object whose properties are being edited
	 */
	public PropertySheet(Object pBean)
	{
		try
		{
			BeanInfo info = Introspector.getBeanInfo(pBean.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();      
			Arrays.sort(descriptors, new Comparator<PropertyDescriptor>()
					{
						public int compare(PropertyDescriptor pDescriptor1, PropertyDescriptor pDescriptor2)
						{
							Integer p1 = (Integer)pDescriptor1.getValue("priority");
							Integer p2 = (Integer)pDescriptor2.getValue("priority");
							if(p1 == null && p2 == null)
							{
								return 0;
							}
							if(p1 == null)
							{
								return 1;
							}
							if(p2 == null)
							{
								return -1;
							}
							return p1.intValue() - p2.intValue();
						}
					});
			setLayout(new FormLayout());
			for(int i = 0; i < descriptors.length; i++)
			{
				PropertyEditor editor = getEditor(pBean, descriptors[i]);
				if(editor != null)
				{
					add(new JLabel(descriptors[i].getName()));
					add(getEditorComponent(editor));
				}
			}		
		}
		catch (IntrospectionException exception)
		{
			exception.printStackTrace();
		}
	}

	/**
     * Gets the property editor for a given property,
     * and wires it so that it updates the given object.
     * @param pBean the object whose properties are being edited
     * @param pDescriptor the descriptor of the property to be edited
     * @return a property editor that edits the property
     *  with the given descriptor and updates the given object
	 */
	public PropertyEditor getEditor(final Object pBean, PropertyDescriptor pDescriptor)
	{
		try
		{
			final Method getter = pDescriptor.getReadMethod();
			final Method setter = pDescriptor.getWriteMethod();
			if(getter == null || setter == null )
			{
				return null;
			}
			
			Class<?> type = pDescriptor.getPropertyType();
			final PropertyEditor editor;
			Class<?> editorClass = pDescriptor.getPropertyEditorClass();
			if(editorClass == null && editors.containsKey(type))
			{
				editorClass = (Class<?>) editors.get(type);
			}
			if(editorClass != null)
			{
				editor = (PropertyEditor) editorClass.newInstance();
			}
			else
			{
				editor = PropertyEditorManager.findEditor(type);
			}
			if(editor == null)
			{
				return null;
			}

			Object value = getter.invoke(pBean, new Object[] {});
			editor.setValue(value);
			editor.addPropertyChangeListener(new PropertyChangeListener()
            	{
					public void propertyChange(PropertyChangeEvent pEvent)
					{
						try
						{	
							setter.invoke(pBean, new Object[] { editor.getValue() });
							fireStateChanged(null);
						}
						catch(IllegalAccessException | InvocationTargetException exception)
						{
							exception.printStackTrace();
						}
					}
            	});
			return editor;
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException exception)
		{
			exception.printStackTrace();
			return null;
		}
	}

	/**
     * Wraps a property editor into a component.
     * @param pEditor the editor to wrap
     * @return a button (if there is a custom editor), 
     * combo box (if the editor has tags), or text field (otherwise)
	 */      
	public Component getEditorComponent(final PropertyEditor pEditor)   
	{      
		String[] tags = pEditor.getTags();
		String text = pEditor.getAsText();
		if(pEditor.supportsCustomEditor())
		{
			return pEditor.getCustomEditor();         
         
		}
		else if(tags != null)
		{
			// make a combo box that shows all tags
			final JComboBox<String> comboBox = new JComboBox<>(tags);
			comboBox.setSelectedItem(text);
			comboBox.addItemListener(new ItemListener()
            	{
					public void itemStateChanged(ItemEvent pEvent)
					{
						if(pEvent.getStateChange() == ItemEvent.SELECTED)
						{
							pEditor.setAsText((String)comboBox.getSelectedItem());
						}
					}
            	});
			return comboBox;
		}
		else 
		{
			final JTextField textField = new JTextField(text, 10);
			textField.getDocument().addDocumentListener(new DocumentListener()
            	{
					public void insertUpdate(DocumentEvent pEvent) 
					{
						pEditor.setAsText(textField.getText());	
					}
					public void removeUpdate(DocumentEvent pEvent) 
					{
						pEditor.setAsText(textField.getText());
					}
					public void changedUpdate(DocumentEvent pEvent) 
					{}
            	});
			return textField;
		}
	}

	/**
     * Adds a change listener to the list of listeners.
     * @param pListener the listener to add
	 */
	public void addChangeListener(ChangeListener pListener)
	{
		aChangeListeners.add(pListener);
	}

	/**
     * Notifies all listeners of a state change.
     * @param pEvent the event to propagate
	 */
	private void fireStateChanged(ChangeEvent pEvent)
	{
		for(ChangeListener listener : aChangeListeners)
		{
			listener.stateChanged(pEvent);
		}
	}
   
	// workaround for Web Start bug TODO Remove eventually
	// CSOFF:
	public static class StringEditor extends PropertyEditorSupport
	{
		public String getAsText() 
		{ return (String) getValue(); }
		
		public void setAsText(String pString) 
		{ setValue(pString); }      
	}
	// CSON:
}

