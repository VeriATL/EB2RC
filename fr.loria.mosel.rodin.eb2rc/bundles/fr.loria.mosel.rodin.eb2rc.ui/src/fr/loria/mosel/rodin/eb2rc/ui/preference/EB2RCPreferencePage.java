package fr.loria.mosel.rodin.eb2rc.ui.preference;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;



/**
 * The activator class controls the plug-in life cycle
 */
public class EB2RCPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	
	public EB2RCPreferencePage() {
		// display in GRID looks more natural
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		//TODO  init control var to first var find in the last refinement machine.
	
		// init preference page with value in the store
		setPreferenceStore(new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "fr.loria.mosel.eventb.eb2rc.config.ui"));
	}
	
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(EB2RCConstants.CV, "Name of Control Variable", getFieldEditorParent()));
		addField(new StringFieldEditor(EB2RCConstants.SIG, "Signature of Generated Algorithm", getFieldEditorParent()));
		addField(new StringFieldEditor(EB2RCConstants.START, "Name of Init Control Variable", getFieldEditorParent()));
		addField(new StringFieldEditor(EB2RCConstants.SYMBOL, "Symbol for Splitting Event Name", getFieldEditorParent()));	
		
		Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		  
		ListEditor formatOnSave = new EB2RCStringListEditor(
				EB2RCConstants.DEP, 
				"Project Dependencies", 
				"Specify External Dependencies", getFieldEditorParent());
		addField(formatOnSave);
			
		
	}

}
