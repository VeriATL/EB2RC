/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.loria.mosel.rodin.eb2rc.ui.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A field editor to edit strings.
 */
public class EB2RCStringListEditor extends ListEditor {


    /**
     * The special label text for directory chooser,
     * or <code>null</code> if none.
     */
    private String dialogLabelText;

    /**
     * Creates a new path field editor
     */
    protected EB2RCStringListEditor() {
    }

    /**
     * Creates a path field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param dirChooserLabelText the label text displayed for the directory chooser
     * @param parent the parent of the field editor's control
     */
    public EB2RCStringListEditor(String name, String labelText,
            String dialogLabelText, Composite parent) {
        init(name, labelText);
        this.dialogLabelText = dialogLabelText;
        createControl(parent);
    }

    @Override
	protected String createList(String[] items) {
        StringBuffer path = new StringBuffer("");//$NON-NLS-1$

        for (String item : items) {
            path.append(item);
            path.append("\n\r");
        }
        return path.toString();
    }

    @Override
	protected String getNewInputObject() {
    	InputDialog dialog = new InputDialog(getShell(), dialogLabelText, "Format: [py/rodin]|name|cv|sig|init|split", null, null);
   
        String param = null;
        final int dialogCode = dialog.open();
        if (dialogCode == 0) {
            param = dialog.getValue();
            if (param != null) {
                param = param.trim();
                if (param.length() == 0) {
                    return null;
                }
            }
        }
        
        return param;
    }

	@Override
	protected String[] parseString(String stringList) {
        StringTokenizer st = new StringTokenizer(stringList, "\n\r");
        ArrayList<Object> v = new ArrayList<>();
        while (st.hasMoreElements()) {
            v.add(st.nextElement());
        }
        return v.toArray(new String[v.size()]);
	}

}
