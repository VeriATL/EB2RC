package fr.loria.mosel.rodin.eb2rc.ui.preference;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
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
		
	}

}
